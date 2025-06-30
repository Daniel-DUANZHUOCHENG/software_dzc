package edu.neu.oaas.service;

import edu.neu.oaas.mapper.TenantMapper;
import edu.neu.oaas.mapper.UserMapper;
import edu.neu.oaas.pojo.Department;
import edu.neu.oaas.pojo.Tenant;
import edu.neu.oaas.pojo.User;
import edu.neu.oaas.service.DepartmentService;
import edu.neu.oaas.service.TenantService;
import edu.neu.oaas.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
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
        department.setDepartmentName("TestDepartment");
        department.setCreatedAt(LocalDateTime.now());
        department.setParentDepartment(1);
        department.setTenantId(1);

        user = new User();
        user.setUsername("admin");
        user.setPassword("password");
        user.setEmail("admin@example.com");
        user.setPhoneNumber("1234567890");
        user.setNickname("Admin");
        user.setRole("Admin");
        user.setStatus("Active");
        user.setCreatedAt(LocalDateTime.now());
        user.setTenantId(1);
    }

    @Test
    public void testGetAll() {
        when(tenantMapper.getAll()).thenReturn(List.of(tenant));
        List<Tenant> tenants = tenantService.getAll();
        assertFalse(tenants.isEmpty());
        verify(tenantMapper, times(1)).getAll();
    }

    @Test
    public void testGetByName() {
        when(tenantMapper.getByName("TestTenant")).thenReturn(List.of(tenant));
        List<Tenant> tenants = tenantService.getByName("TestTenant");
        assertFalse(tenants.isEmpty());
        verify(tenantMapper, times(1)).getByName("TestTenant");
    }

    @Test
    public void testGetById() {
        when(tenantMapper.getById(1)).thenReturn(tenant);
        Tenant found = tenantService.getById(1);
        assertNotNull(found);
        verify(tenantMapper, times(1)).getById(1);
    }

    @Test
    public void testInsertTenant() {
        // Simulate insertTenant2 setting the ID on the tenant
        doAnswer(invocation -> {
            Tenant t = invocation.getArgument(0);
            t.setId(1); // simulate setting the generated ID
            return null;
        }).when(tenantMapper).insertTenant2(any(Tenant.class));

        // Simulate reget method returning the tenant with the set ID
        when(tenantMapper.reget(any(Tenant.class))).thenReturn(tenant);

        // Simulate insertDepartment method (void return type)
//        doNothing().when(departmentService).insertDepartment(any(Department.class));

        // Simulate reget method on departmentService returning the department
        when(departmentService.reget(1, "TestDepartment")).thenReturn(department);

        // Simulate insertUser method (void return type)
//        doNothing().when(userService).insertUser(any(User.class));

        // Call the method under test
        boolean result = tenantService.insertTenant(tenant, department, user);

        // Verify and assert
        assertTrue(result);
        verify(tenantMapper, times(1)).insertTenant2(tenant);
        verify(departmentService, times(1)).insertDepartment(department);
        verify(userService, times(1)).insertUser(user);
    }


    @Test
    public void testUpdateTenant() {
        doNothing().when(tenantMapper).updateTenant(tenant);
        boolean result = tenantService.updateTenant(tenant);
        assertTrue(result);
        verify(tenantMapper, times(1)).updateTenant(tenant);
    }

    @Test
    public void testDelete() {
        when(tenantMapper.getById(1)).thenReturn(tenant);
//        doNothing().when(departmentService).delete(1);
//        doNothing().when(tenantMapper).deleteById(1);

        boolean result = tenantService.delete(1);
        assertTrue(result);
        verify(tenantMapper, times(1)).getById(1);
        verify(departmentService, times(1)).delete(1);
        verify(tenantMapper, times(1)).deleteById(1);
    }

    @Test
    public void testAddTenant() {
        doNothing().when(tenantMapper).insertTenant2(tenant);
        tenantService.insertTenant2(tenant);
        verify(tenantMapper, times(1)).insertTenant2(tenant);
    }

    @Test
    public void testGetAllTenants() {
        when(tenantMapper.findAll()).thenReturn(List.of(tenant));
        List<Tenant> tenants = tenantService.getAll();
        assertFalse(tenants.isEmpty());
        verify(tenantMapper, times(1)).findAll();
    }

    @Test
    public void testRegisterTenantAndUser() {
        when(tenantMapper.getTenantByName("TestTenant")).thenReturn(null);
        when(userMapper.getUserByUsername("admin")).thenReturn(null);
        when(userMapper.getUserByEmail("admin@example.com")).thenReturn(null);
        doNothing().when(tenantMapper).insertTenant2(tenant);
        doNothing().when(userMapper).insertUser2(user);

        tenantService.registerTenantAndUser("TestTenant", "John Doe", "1234567890", "admin@example.com", "admin", "password");
        verify(tenantMapper, times(1)).insertTenant2(any(Tenant.class));
        verify(userMapper, times(1)).insertUser2(any(User.class));
    }
}
