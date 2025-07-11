package edu.neu.oaas.controller;

import edu.neu.oaas.pojo.Department;
import edu.neu.oaas.pojo.Tenant;
import edu.neu.oaas.pojo.User;
import edu.neu.oaas.service.TenantService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TenantController.class)
public class TenantControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TenantService tenantService;

    private Tenant tenant;
    private Department department;
    private User user;
    private String tenantJson;
    private String fullRequestJson;

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

        tenantJson = "{\"adminUsername\":\"admin\",\"password\":\"password\",\"contactPerson\":\"John Doe\",\"phone\":\"1234567890\",\"tenantName\":\"TestTenant\",\"icon\":\"icon.png\",\"remark\":\"Remark\",\"rootDepartmentId\":1}";
        
        fullRequestJson = "{\"tenant\":" + tenantJson + "," +
                "\"department\":{\"departmentName\":\"TestDepartment\",\"parentDepartment\":1,\"tenantId\":1}," +
                "\"user\":{\"username\":\"admin\",\"password\":\"password\",\"email\":\"admin@example.com\",\"phoneNumber\":\"1234567890\",\"nickname\":\"Admin\",\"role\":\"Admin\",\"status\":\"Active\",\"tenantId\":1}}";
    }

    @Test
    public void testGetAll() throws Exception {
        List<Tenant> tenants = Arrays.asList(tenant);
        when(tenantService.getAll()).thenReturn(tenants);

        mockMvc.perform(get("/api/tenants/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tenantList").isArray())
                .andExpect(jsonPath("$.tenantList[0].adminUsername").value("admin"))
                .andExpect(jsonPath("$.isOK").value(true));

        verify(tenantService, times(1)).getAll();
    }

    @Test
    public void testGetAllWhenEmpty() throws Exception {
        when(tenantService.getAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/tenants/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tenantList").isArray())
                .andExpect(jsonPath("$.tenantList").isEmpty())
                .andExpect(jsonPath("$.isOK").value(true));

        verify(tenantService, times(1)).getAll();
    }

    @Test
    public void testGetByName() throws Exception {
        List<Tenant> tenants = Arrays.asList(tenant);
        when(tenantService.searchTenants("TestTenant", null, null, null, null)).thenReturn(tenants);

        mockMvc.perform(get("/api/tenants/search")
                .param("tenantName", "TestTenant"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tenantList").isArray())
                .andExpect(jsonPath("$.total").value(1));

        verify(tenantService, times(1)).searchTenants("TestTenant", null, null, null, null);
    }

    @Test
    public void testGetByNameWhenNotFound() throws Exception {
        when(tenantService.searchTenants("NonExistent", null, null, null, null))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/tenants/search")
                .param("tenantName", "NonExistent"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tenantList").isArray())
                .andExpect(jsonPath("$.tenantList").isEmpty());

        verify(tenantService, times(1)).searchTenants("NonExistent", null, null, null, null);
    }

    @Test
    public void testGetById() throws Exception {
        when(tenantService.getById(1)).thenReturn(tenant);

        mockMvc.perform(get("/api/tenants/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tenant.adminUsername").value("admin"))
                .andExpect(jsonPath("$.isOK").value(true));

        verify(tenantService, times(1)).getById(1);
    }

    @Test
    public void testGetByIdWhenNotFound() throws Exception {
        when(tenantService.getById(999)).thenReturn(null);

        mockMvc.perform(get("/api/tenants/999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tenant").isEmpty());

        verify(tenantService, times(1)).getById(999);
    }

    @Test
    public void testInsertSuccess() throws Exception {
        when(tenantService.insertTenant(any(Tenant.class), any(Department.class), any(User.class))).thenReturn(true);

        mockMvc.perform(post("/api/tenants/insert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(fullRequestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isOK").value(true));

        verify(tenantService, times(1)).insertTenant(any(Tenant.class), any(Department.class), any(User.class));
    }

    @Test
    public void testInsertFailure() throws Exception {
        when(tenantService.insertTenant(any(Tenant.class), any(Department.class), any(User.class))).thenReturn(false);

        mockMvc.perform(post("/api/tenants/insert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(fullRequestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isOK").value(false))
                .andExpect(jsonPath("$.msg").value("系统故障"));

        verify(tenantService, times(1)).insertTenant(any(Tenant.class), any(Department.class), any(User.class));
    }

    @Test
    public void testResetSuccess() throws Exception {
        when(tenantService.updateTenant(any(Tenant.class))).thenReturn(true);

        mockMvc.perform(put("/api/tenants/reset")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(tenantJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isOK").value(true));

        verify(tenantService, times(1)).updateTenant(any(Tenant.class));
    }

    @Test
    public void testResetFailure() throws Exception {
        when(tenantService.updateTenant(any(Tenant.class))).thenReturn(false);

        mockMvc.perform(put("/api/tenants/reset")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(tenantJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isOK").value(false))
                .andExpect(jsonPath("$.msg").value("系统故障"));

        verify(tenantService, times(1)).updateTenant(any(Tenant.class));
    }

    @Test
    public void testDeleteSuccess() throws Exception {
        when(tenantService.delete(1)).thenReturn(true);

        mockMvc.perform(delete("/api/tenants/delete")
                .param("id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isOK").value(true));

        verify(tenantService, times(1)).delete(1);
    }

    @Test
    public void testDeleteFailure() throws Exception {
        when(tenantService.delete(1)).thenReturn(false);

        mockMvc.perform(delete("/api/tenants/delete")
                .param("id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isOK").value(false))
                .andExpect(jsonPath("$.msg").value("系统故障"));

        verify(tenantService, times(1)).delete(1);
    }

    @Test
    public void testDeleteWithInvalidId() throws Exception {
        when(tenantService.delete(999)).thenReturn(false);

        mockMvc.perform(delete("/api/tenants/delete")
                .param("id", "999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isOK").value(false))
                .andExpect(jsonPath("$.msg").value("系统故障"));

        verify(tenantService, times(1)).delete(999);
    }

    @Test
    public void testDeleteWithNonExistentId() throws Exception {
        when(tenantService.deleteTenant(999)).thenThrow(new RuntimeException("Tenant not found"));

        mockMvc.perform(delete("/api/tenants/delete/{id}", 999))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.isOK").value(false))
                .andExpect(jsonPath("$.msg").value("系统故障：Tenant not found"));

        verify(tenantService, times(1)).deleteTenant(999);
    }
}
