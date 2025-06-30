package edu.neu.oaas.controller;

import edu.neu.oaas.pojo.Department;
import edu.neu.oaas.pojo.Tenant;
import edu.neu.oaas.pojo.User;
import edu.neu.oaas.service.TenantService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class TenantControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
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
    }

    @Test
    public void testGetAll() throws Exception {
        List<Tenant> tenants = Arrays.asList(tenant);
        when(tenantService.getAll()).thenReturn(tenants);

        mockMvc.perform(get("/tenants/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tenantList").isArray())
                .andExpect(jsonPath("$.tenantList[0].adminUsername").value("admin"))
                .andExpect(jsonPath("$.isOK").value(true));
    }

    @Test
    public void testGetByName() throws Exception {
//        List<Tenant> tenants = Arrays.asList(tenant);
//        when(tenantService.getByName("TestTenant")).thenReturn(tenants);
//
//        mockMvc.perform(get("/tenants/search").param("tenantName", "TestTenant"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.tenantList").isArray())
//                .andExpect(jsonPath("$.tenantList[0].adminUsername").value("admin"))
//                .andExpect(jsonPath("$.isOK").value(true));
    }

    @Test
    public void testGetById() throws Exception {
        when(tenantService.getById(1)).thenReturn(tenant);

        mockMvc.perform(get("/tenants/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tenant.adminUsername").value("admin"))
                .andExpect(jsonPath("$.isOK").value(true));
    }

    @Test
    public void testInsert() throws Exception {
        when(tenantService.insertTenant(any(Tenant.class), any(Department.class), any(User.class))).thenReturn(true);

        String requestBody = "{\"tenant\":{\"adminUsername\":\"admin\",\"password\":\"password\",\"contactPerson\":\"John Doe\",\"phone\":\"1234567890\",\"tenantName\":\"TestTenant\",\"createdAt\":\"2023-07-04T10:15:30\",\"icon\":\"icon.png\",\"remark\":\"Remark\",\"rootDepartmentId\":1}," +
                "\"department\":{\"departmentName\":\"TestDepartment\",\"createdAt\":\"2023-07-04T10:15:30\",\"parentDepartment\":1,\"tenantId\":1}," +
                "\"user\":{\"username\":\"admin\",\"password\":\"password\",\"email\":\"admin@example.com\",\"phoneNumber\":\"1234567890\",\"nickname\":\"Admin\",\"role\":\"Admin\",\"status\":\"Active\",\"createdAt\":\"2023-07-04T10:15:30\",\"tenantId\":1}}";

        mockMvc.perform(post("/tenants/insert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isOK").value(true));
    }

    @Test
    public void testReset() throws Exception {
        when(tenantService.updateTenant(any(Tenant.class))).thenReturn(true);

        String json = "{\"adminUsername\":\"admin\",\"password\":\"password\",\"contactPerson\":\"John Doe\",\"phone\":\"1234567890\",\"tenantName\":\"TestTenant\",\"createdAt\":\"2023-07-04T10:15:30\",\"icon\":\"icon.png\",\"remark\":\"Remark\",\"rootDepartmentId\":1}";

        mockMvc.perform(post("/tenants/reset")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isOK").value(true));
    }

    @Test
    public void testDelete() throws Exception {
        when(tenantService.delete(anyInt())).thenReturn(true);

        mockMvc.perform(post("/tenants/delete").param("id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isOK").value(true));
    }

    @Test
    public void testAddTenant() throws Exception {

        String json = "{\"adminUsername\":\"admin\",\"password\":\"password\",\"contactPerson\":\"John Doe\",\"phone\":\"1234567890\",\"tenantName\":\"TestTenant\",\"createdAt\":\"2023-07-04T10:15:30\",\"icon\":\"icon.png\",\"remark\":\"Remark\",\"rootDepartmentId\":1}";

        mockMvc.perform(post("/tenants/insert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("Tenant added successfully"));
    }

    @Test
    public void testGetAllTenants() throws Exception {
        List<Tenant> tenants = Arrays.asList(tenant);
        when(tenantService.getAll()).thenReturn(tenants);

        mockMvc.perform(get("/tenants"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].adminUsername").value("admin"));
    }
}
