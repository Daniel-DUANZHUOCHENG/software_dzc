package edu.neu.oaas.controller;

import edu.neu.oaas.service.AiService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AiControllerTest {

    @Mock
    private AiService aiService;

    @InjectMocks
    private AiController aiController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(aiController).build();
    }

    @Test
    void testParseUserForm_Success() throws Exception {
        // 准备测试数据
        Map<String, Object> mockResponse = new HashMap<>();
        mockResponse.put("username", "testUser");
        mockResponse.put("email", "test@example.com");
        
        // 模拟服务层行为
        when(aiService.parseTextForUserForm(anyString())).thenReturn(mockResponse);

        // 执行测试并验证结果
        mockMvc.perform(post("/api/ai/parse-form/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"text\": \"Create a user named testUser with email test@example.com\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testUser"))
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    void testParseUserForm_Error() throws Exception {
        // 准备错误响应
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", "Failed to parse user form");
        
        // 模拟服务层错误情况
        when(aiService.parseTextForUserForm(anyString())).thenReturn(errorResponse);

        // 执行测试并验证错误结果
        mockMvc.perform(post("/api/ai/parse-form/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"text\": \"invalid input\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.error").value("Failed to parse user form"));
    }

    @Test
    void testParseTenantForm_Success() throws Exception {
        // 准备测试数据
        Map<String, Object> mockResponse = new HashMap<>();
        mockResponse.put("tenantName", "TestCompany");
        mockResponse.put("contactPerson", "John Doe");
        
        // 模拟服务层行为
        when(aiService.parseTextForTenantForm(anyString())).thenReturn(mockResponse);

        // 执行测试并验证结果
        mockMvc.perform(post("/api/ai/parse-form/tenant")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"text\": \"Create a tenant named TestCompany with contact person John Doe\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tenantName").value("TestCompany"))
                .andExpect(jsonPath("$.contactPerson").value("John Doe"));
    }

    @Test
    void testParseTenantForm_Error() throws Exception {
        // 准备错误响应
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", "Failed to parse tenant form");
        
        // 模拟服务层错误情况
        when(aiService.parseTextForTenantForm(anyString())).thenReturn(errorResponse);

        // 执行测试并验证错误结果
        mockMvc.perform(post("/api/ai/parse-form/tenant")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"text\": \"invalid input\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.error").value("Failed to parse tenant form"));
    }

    @Test
    void testParseDepartmentForm_Success() throws Exception {
        // 准备测试数据
        Map<String, Object> mockResponse = new HashMap<>();
        mockResponse.put("departmentName", "IT Department");
        mockResponse.put("manager", "Jane Smith");
        
        // 模拟服务层行为
        when(aiService.parseTextForDepartmentForm(anyString())).thenReturn(mockResponse);

        // 执行测试并验证结果
        mockMvc.perform(post("/api/ai/parse-form/department")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"text\": \"Create an IT Department managed by Jane Smith\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.departmentName").value("IT Department"))
                .andExpect(jsonPath("$.manager").value("Jane Smith"));
    }

    @Test
    void testParseDepartmentForm_Error() throws Exception {
        // 准备错误响应
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", "Failed to parse department form");
        
        // 模拟服务层错误情况
        when(aiService.parseTextForDepartmentForm(anyString())).thenReturn(errorResponse);

        // 执行测试并验证错误结果
        mockMvc.perform(post("/api/ai/parse-form/department")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"text\": \"invalid input\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.error").value("Failed to parse department form"));
    }

    @Test
    void testGenerateTenantRemark_Success() throws Exception {
        // 准备测试数据
        Map<String, Object> mockResponse = new HashMap<>();
        mockResponse.put("remark", "A professional IT services company");
        
        // 模拟服务层行为
        when(aiService.generateTenantRemark(anyString(), anyString())).thenReturn(mockResponse);

        // 执行测试并验证结果
        mockMvc.perform(post("/api/ai/generate/tenant-remark")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"tenantName\": \"Tech Corp\", \"description\": \"IT services company\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.remark").value("A professional IT services company"));
    }

    @Test
    void testGenerateTenantRemark_Error() throws Exception {
        // 准备错误响应
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", "Failed to generate tenant remark");
        
        // 模拟服务层错误情况
        when(aiService.generateTenantRemark(anyString(), anyString())).thenReturn(errorResponse);

        // 执行测试并验证错误结果
        mockMvc.perform(post("/api/ai/generate/tenant-remark")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"tenantName\": \"\", \"description\": \"\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.error").value("Failed to generate tenant remark"));
    }
} 