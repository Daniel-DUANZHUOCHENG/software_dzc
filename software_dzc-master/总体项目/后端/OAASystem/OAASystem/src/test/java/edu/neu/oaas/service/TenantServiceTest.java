package edu.neu.oaas.service;

import edu.neu.oaas.mapper.TenantMapper;
import edu.neu.oaas.mapper.UserMapper;
import edu.neu.oaas.pojo.Department;
import edu.neu.oaas.pojo.Tenant;
import edu.neu.oaas.pojo.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

import org.springframework.web.multipart.MultipartFile;

public class TenantServiceTest {

    @Mock
    private TenantMapper tenantMapper;

    @Mock
    private UserMapper userMapper;

    @Mock
    private DepartmentService departmentService;

    @Mock
    private UserService userService;

    @InjectMocks
    private TenantService tenantService;

    private Tenant tenant;
    private Department department;
    private User user;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        tenant = new Tenant();
        tenant.setId(1);
        tenant.setAdminUsername("admin");
        tenant.setPassword("password");
        tenant.setContactPerson("John Doe");
        tenant.setPhone("1234567890");
        tenant.setTenantName("TestTenant");
        tenant.setCreatedAt(LocalDateTime.now());
        tenant.setIcon("icon.png");
        tenant.setRemark("Remark");
        tenant.setRootDepartmentId(1);

        department = new Department();
        department.setId(1);
        department.setDepartmentName("TestDepartment");
        department.setCreatedAt(LocalDateTime.now());
        department.setParentDepartment(1);
        department.setTenantId(1);
        department.setPath("/1");
        department.setStatus("Active");

        user = new User();
        user.setId(1);
        user.setUsername("admin");
        user.setPassword("password");
        user.setEmail("admin@example.com");
        user.setPhoneNumber("1234567890");
        user.setNickname("Admin");
        user.setRole("Admin");
        user.setStatus("Active");
        user.setCreatedAt(LocalDateTime.now());
        user.setTenantId(1);
        user.setDepartmentId(1);
        user.setPath("/1");
    }

    @Test
    public void testGetAll() {
        when(tenantMapper.getAll()).thenReturn(Arrays.asList(tenant));
        List<Tenant> tenants = tenantService.getAll();
        assertFalse(tenants.isEmpty());
        verify(tenantMapper).getAll();
    }

    @Test
    public void testGetByName() {
        when(tenantMapper.getByName("TestTenant")).thenReturn(Arrays.asList(tenant));
        List<Tenant> tenants = tenantService.getByName("TestTenant");
        assertFalse(tenants.isEmpty());
        verify(tenantMapper).getByName("TestTenant");
    }

    @Test
    public void testGetById() {
        when(tenantMapper.getById(1)).thenReturn(tenant);
        Tenant found = tenantService.getById(1);
        assertNotNull(found);
        verify(tenantMapper).getById(1);
    }

    @Test
    public void testInsertTenant() {
        // 模拟insertTenant2返回成功
        doNothing().when(tenantMapper).insertTenant2(any(Tenant.class));

        // 模拟reget返回tenant
        when(tenantMapper.reget(any(Tenant.class))).thenReturn(tenant);

        // 模拟department相关操作
        when(departmentService.reget(1, "TestDepartment")).thenReturn(department);
        when(departmentService.insertDepartment(any(Department.class))).thenReturn(true);

        // 模拟user相关操作
        when(userService.insertUser(any(User.class))).thenReturn(true);

        // 执行测试
        boolean result = tenantService.insertTenant(tenant, department, user);

        // 验证结果
        assertTrue(result);
        verify(tenantMapper).insertTenant2(tenant);
        verify(departmentService).insertDepartment(any(Department.class));
        verify(userService).insertUser(any(User.class));
    }

    @Test
    public void testUpdateTenant() {
        doNothing().when(tenantMapper).updateTenant(tenant);
        boolean result = tenantService.updateTenant(tenant);
        assertTrue(result);
        verify(tenantMapper).updateTenant(tenant);
    }

    @Test
    public void testDelete() {
        // 模拟获取tenant
        when(tenantMapper.getById(1)).thenReturn(tenant);
        
        // 模拟删除操作
        when(departmentService.delete(1)).thenReturn(true);
        doNothing().when(tenantMapper).deleteById(1);

        // 执行测试
        boolean result = tenantService.delete(1);
        
        // 验证结果
        assertTrue(result);
        verify(tenantMapper).getById(1);
        verify(departmentService).delete(1);
        verify(tenantMapper).deleteById(1);
    }

    @Test
    public void testDeleteWithNonExistentTenant() {
        // Mock tenant not found
        when(tenantMapper.getById(999)).thenReturn(null);
        
        // Mock department service to ensure it's not called
        when(departmentService.delete(anyInt())).thenReturn(true);
        
        // Mock user service to ensure it's not called
        when(userService.deleteById(anyInt())).thenReturn(true);
        
        // Execute test
        boolean result = tenantService.delete(999);
        
        // Verify result and interactions
        assertFalse(result, () -> "Should return false when tenant does not exist");
        verify(tenantMapper).getById(999);
        verify(departmentService, never()).delete(anyInt());
        verify(tenantMapper, never()).deleteById(anyInt());
        verify(userService, never()).deleteById(anyInt());
    }

    @Test
    public void testInsertTenant2() {
        // Setup test data
        Tenant newTenant = new Tenant();
        newTenant.setTenantName("NewTenant");
        newTenant.setContactPerson("Jane Doe");
        newTenant.setPhone("9876543210");

        // Mock tenant insertion
        doNothing().when(tenantMapper).insertTenant(any(Tenant.class));

        // Execute test
        tenantService.insertTenant2(newTenant);

        // Verify correct method was called
        verify(tenantMapper).insertTenant(newTenant);
        verify(tenantMapper, never()).insertTenant2(any(Tenant.class));
    }

    @Test
    public void testGetAllTenants() {
        // Setup test data
        List<Tenant> tenants = Arrays.asList(tenant);
        when(tenantMapper.getAll()).thenReturn(tenants);

        // Execute test
        List<Tenant> result = tenantService.getAll();

        // Verify result
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        verify(tenantMapper).getAll();
        verify(tenantMapper, never()).findAll();
    }

    @Test
    public void testRegisterTenantAndUser() {
        // Mock tenant creation
        doAnswer(invocation -> {
            Tenant t = invocation.getArgument(0);
            t.setId(1);
            return null;
        }).when(tenantMapper).insertTenant2(any(Tenant.class));

        // Mock user creation
        doNothing().when(userMapper).insertUser2(any(User.class));

        // Execute test
        tenantService.registerTenantAndUser(
            "TestTenant",
            "John Doe",
            "1234567890",
            "admin@example.com",
            "admin",
            "password"
        );

        // Verify interactions
        verify(tenantMapper).insertTenant2(argThat(t -> 
            t.getTenantName().equals("TestTenant") &&
            t.getContactPerson().equals("John Doe") &&
            t.getPhone().equals("1234567890")
        ));
        verify(userMapper).insertUser2(argThat(u ->
            u.getUsername().equals("admin") &&
            u.getEmail().equals("admin@example.com")
        ));
    }

    @Test
    public void testRegisterTenantAndUserWithExistingTenant() {
        when(tenantMapper.getTenantByName("TestTenant")).thenReturn(tenant);

        assertThrows(IllegalArgumentException.class, () ->
            tenantService.registerTenantAndUser(
                "TestTenant",
                "John Doe",
                "1234567890",
                "admin@example.com",
                "admin",
                "password"
            )
        );

        verify(tenantMapper, never()).insertTenant2(any(Tenant.class));
        verify(userMapper, never()).insertUser2(any(User.class));
    }

    @Test
    public void testRegisterTenantAndUserWithExistingUsername() {
        when(tenantMapper.getTenantByName("TestTenant")).thenReturn(null);
        when(userMapper.getUserByUsername("admin")).thenReturn(user);

        assertThrows(IllegalArgumentException.class, () ->
            tenantService.registerTenantAndUser(
                "TestTenant",
                "John Doe",
                "1234567890",
                "admin@example.com",
                "admin",
                "password"
            )
        );

        verify(tenantMapper, never()).insertTenant2(any(Tenant.class));
        verify(userMapper, never()).insertUser2(any(User.class));
    }

    @Test
    public void testRegisterTenantAndUserWithExistingEmail() {
        when(tenantMapper.getTenantByName("TestTenant")).thenReturn(null);
        when(userMapper.getUserByUsername("admin")).thenReturn(null);
        when(userMapper.getUserByEmail("admin@example.com")).thenReturn(user);

        assertThrows(IllegalArgumentException.class, () ->
            tenantService.registerTenantAndUser(
                "TestTenant",
                "John Doe",
                "1234567890",
                "admin@example.com",
                "admin",
                "password"
            )
        );

        verify(tenantMapper, never()).insertTenant2(any(Tenant.class));
        verify(userMapper, never()).insertUser2(any(User.class));
    }

    @Test
    public void testDeleteTenantSuccess() {
        // Setup
        when(tenantMapper.getById(1)).thenReturn(tenant);
        doNothing().when(departmentService).deleteDepartmentByTenantId(1);
        doNothing().when(userService).deleteUserByTenantId(1);
        doNothing().when(tenantMapper).deleteById(1);

        // Execute
        boolean result = tenantService.deleteTenant(1);

        // Verify
        assertTrue(result);
        verify(tenantMapper).getById(1);
        verify(departmentService).deleteDepartmentByTenantId(1);
        verify(userService).deleteUserByTenantId(1);
        verify(tenantMapper).deleteById(1);
    }

    @Test
    public void testDeleteTenantNonExistent() {
        // Setup
        when(tenantMapper.getById(999)).thenReturn(null);

        // Execute & Verify
        assertThrows(IllegalArgumentException.class, () -> tenantService.deleteTenant(999));
        verify(tenantMapper).getById(999);
        verify(departmentService, never()).deleteDepartmentByTenantId(anyInt());
        verify(userService, never()).deleteUserByTenantId(anyInt());
        verify(tenantMapper, never()).deleteById(anyInt());
    }

    @Test
    public void testUpdateTenant3Success() {
        // Setup
        Tenant updateTenant = new Tenant();
        updateTenant.setId(1);
        updateTenant.setTenantName("UpdatedTenant");
        doNothing().when(tenantMapper).updateTenant(updateTenant);

        // Execute
        boolean result = tenantService.updateTenant3(updateTenant);

        // Verify
        assertTrue(result);
        verify(tenantMapper).updateTenant(updateTenant);
    }

    @Test
    public void testUpdateTenant3Null() {
        assertThrows(IllegalArgumentException.class, () -> tenantService.updateTenant3(null));
        verify(tenantMapper, never()).updateTenant(any());
    }

    @Test
    public void testSearchTenantsAllParameters() {
        // Setup
        LocalDate startDate = LocalDate.now().minusDays(7);
        LocalDate endDate = LocalDate.now();
        List<Tenant> expectedTenants = Arrays.asList(tenant);
        when(tenantMapper.searchTenants("TestTenant", "John", "123", startDate, endDate))
            .thenReturn(expectedTenants);

        // Execute
        List<Tenant> result = tenantService.searchTenants("TestTenant", "John", "123", startDate, endDate);

        // Verify
        assertNotNull(result);
        assertEquals(expectedTenants, result);
        verify(tenantMapper).searchTenants("TestTenant", "John", "123", startDate, endDate);
    }

    @Test
    public void testSearchTenantsPartialParameters() {
        // Setup
        List<Tenant> expectedTenants = Arrays.asList(tenant);
        when(tenantMapper.searchTenants("TestTenant", null, null, null, null))
            .thenReturn(expectedTenants);

        // Execute
        List<Tenant> result = tenantService.searchTenants("TestTenant", null, null, null, null);

        // Verify
        assertNotNull(result);
        assertEquals(expectedTenants, result);
        verify(tenantMapper).searchTenants("TestTenant", null, null, null, null);
    }

    @Test
    public void testSaveIconSuccess() throws IOException {
        // Setup
        MultipartFile mockFile = mock(MultipartFile.class);
        when(mockFile.getOriginalFilename()).thenReturn("test.png");
        
        // Execute
        String result = tenantService.saveIcon(mockFile);

        // Verify
        assertNotNull(result);
        assertTrue(result.startsWith("/icons/"));
        assertTrue(result.endsWith("_test.png"));
        verify(mockFile).transferTo(any(File.class));
    }

    @Test
    public void testSaveIconIOException() throws IOException {
        // Setup
        MultipartFile mockFile = mock(MultipartFile.class);
        when(mockFile.getOriginalFilename()).thenReturn("test.png");
        doThrow(new IOException("Test exception")).when(mockFile).transferTo(any(File.class));

        // Execute & Verify
        assertThrows(IOException.class, () -> tenantService.saveIcon(mockFile));
    }

    @Test
    public void testGetAllTenantNames() {
        // Setup
        List<Tenant> expectedTenants = Arrays.asList(tenant);
        when(tenantMapper.getAllTenantNames()).thenReturn(expectedTenants);

        // Execute
        List<Tenant> result = tenantService.getAllTenantNames();

        // Verify
        assertNotNull(result);
        assertEquals(expectedTenants, result);
        verify(tenantMapper).getAllTenantNames();
    }
}
