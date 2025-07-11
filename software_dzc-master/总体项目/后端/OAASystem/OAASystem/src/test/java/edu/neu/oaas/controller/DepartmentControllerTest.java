package edu.neu.oaas.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.neu.oaas.pojo.Department;
import edu.neu.oaas.service.DepartmentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DepartmentController.class)
public class DepartmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DepartmentService departmentService;

    private Department department;
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        department = new Department();
        department.setId(1);
        department.setDepartmentName("Test Department");
        department.setParentDepartment(0);
        department.setPath("/1");
        department.setStatus("Active");
        department.setTenantId(1);
        department.setManager("Test Manager");
        department.setManagerPhone("1234567890");
        department.setManagerEmail("test@example.com");
        department.setCreatedAt(LocalDateTime.now());

        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules(); // 注册所有模块，包括 JSR310
    }

    // 1. 核心功能测试 - 部门查询
    @Test
    public void testGetDepartmentsByTenantId() throws Exception {
        List<Department> departments = Arrays.asList(department);
        when(departmentService.getDepartmentById(1)).thenReturn(department);
        when(departmentService.getAllByPrefix("/1")).thenReturn(departments);

        mockMvc.perform(get("/departments")
                        .param("departementId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].departmentName").value("Test Department"));
    }

    @Test
    public void testGetAllByPrefix() throws Exception {
        List<Department> departments = Arrays.asList(department);
        when(departmentService.getAllByPrefix("/1")).thenReturn(departments);

        mockMvc.perform(get("/departments/getall")
                        .param("path", "/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isOK").value(true))
                .andExpect(jsonPath("$.departmentList[0].departmentName").value("Test Department"));
    }

    @Test
    public void testGetDepartmentById() throws Exception {
        when(departmentService.getDepartmentsById(1)).thenReturn(department);

        mockMvc.perform(get("/departments/get/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isOK").value(true))
                .andExpect(jsonPath("$.department.departmentName").value("Test Department"));
    }

    @Test
    public void testGetDepartmentByIdNotFound() throws Exception {
        when(departmentService.getDepartmentsById(999)).thenReturn(null);

        mockMvc.perform(get("/departments/get/{id}", 999))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isOK").value(false))
                .andExpect(jsonPath("$.msg").value("好像没找到"));
    }

    // 2. 核心功能测试 - 部门管理
    @Test
    public void testInsertDepartment() throws Exception {
        when(departmentService.insertDepartment(any(Department.class))).thenReturn(true);

        mockMvc.perform(post("/departments/insert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(department)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isOK").value(true));
    }

    @Test
    public void testInsertDepartmentFailure() throws Exception {
        when(departmentService.insertDepartment(any(Department.class))).thenReturn(false);

        mockMvc.perform(post("/departments/insert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(department)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isOK").value(false))
                .andExpect(jsonPath("$.msg").value("系统故障"));
    }

    @Test
    public void testUpdateDepartment() throws Exception {
        when(departmentService.updateDepartment(any(Department.class))).thenReturn(true);

        mockMvc.perform(put("/departments/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(department)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isOK").value(true));
    }

    @Test
    public void testUpdateDepartmentFailure() throws Exception {
        when(departmentService.updateDepartment(any(Department.class))).thenReturn(false);

        mockMvc.perform(put("/departments/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(department)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isOK").value(false))
                .andExpect(jsonPath("$.msg").value("系统故障"));
    }

    @Test
    public void testDeleteDepartment() throws Exception {
        when(departmentService.delete(1)).thenReturn(true);

        mockMvc.perform(delete("/departments/delete")
                        .param("id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isOK").value(true));
    }

    @Test
    public void testDeleteDepartmentFailure() throws Exception {
        when(departmentService.delete(1)).thenReturn(false);

        mockMvc.perform(delete("/departments/delete")
                        .param("id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isOK").value(false))
                .andExpect(jsonPath("$.msg").value("系统故障"));
    }

    // 3. 核心功能测试 - 部门搜索
    @Test
    public void testSearchDepartments() throws Exception {
        List<Department> departments = Arrays.asList(department);
        when(departmentService.getDepartmentsById(1)).thenReturn(department);
        when(departmentService.getAllByPrefixAndName("/1", "Test", "Active")).thenReturn(departments);

        mockMvc.perform(get("/departments/search")
                        .param("departmentName", "Test")
                        .param("status", "Active")
                        .param("departementId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isOK").value(true))
                .andExpect(jsonPath("$.departmentList[0].departmentName").value("Test Department"));
    }

    // 4. 核心功能测试 - 租户部门管理
    @Test
    public void testGetDepartmentsByTenant() throws Exception {
        List<Department> departments = Arrays.asList(department);
        when(departmentService.getDepartmentsByTenantId(1)).thenReturn(departments);

        mockMvc.perform(get("/departments/getByTenantId")
                        .param("tenantId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isOK").value(true))
                .andExpect(jsonPath("$.departmentList[0].departmentName").value("Test Department"));
    }

    @Test
    public void testGetDepartmentsByTenantFailure() throws Exception {
        when(departmentService.getDepartmentsByTenantId(1)).thenThrow(new RuntimeException("Database error"));

        mockMvc.perform(get("/departments/getByTenantId")
                        .param("tenantId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isOK").value(false))
                .andExpect(jsonPath("$.msg").value("获取部门失败: Database error"));
    }

    @Test
    public void testGetDepartmentByDepartmentId() throws Exception {
        when(departmentService.getDepartmentById(1)).thenReturn(department);

        mockMvc.perform(get("/departments/getByDepartmentId")
                        .param("departmentId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isOK").value(true))
                .andExpect(jsonPath("$.departmentList[0].departmentName").value("Test Department"));
    }

    @Test
    public void testGetDepartmentByDepartmentIdNotFound() throws Exception {
        when(departmentService.getDepartmentById(999)).thenReturn(null);

        mockMvc.perform(get("/departments/getByDepartmentId")
                        .param("departmentId", "999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isOK").value(true))
                .andExpect(jsonPath("$.departmentList").isEmpty());
    }
} 