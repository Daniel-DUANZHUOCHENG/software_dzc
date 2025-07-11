package edu.neu.oaas.service;

import com.google.gson.Gson;
import edu.neu.oaas.utils.MockResponseHelper;
import okhttp3.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AI Service Tests")
class AiServiceTest {

    @Mock
    private OkHttpClient mockClient;

    @Mock
    private Call mockCall;

    private AiService aiService;
    private final Gson gson = new Gson();

    @BeforeEach
    void setUp() {
        aiService = new AiService(mockClient);
        ReflectionTestUtils.setField(aiService, "apiKey", "test-api-key");
        ReflectionTestUtils.setField(aiService, "apiUrl", "https://api.test.com/v1");
        when(mockClient.newCall(any(Request.class))).thenReturn(mockCall);
    }

    private Map<String, Object> createSuccessResponse(Map<String, Object> data) {
        Map<String, Object> message = new HashMap<>();
        message.put("role", "assistant");
        message.put("content", gson.toJson(data));
        
        Map<String, Object> choice = new HashMap<>();
        choice.put("message", message);
        
        Map<String, Object> response = new HashMap<>();
        response.put("choices", Arrays.asList(choice));
        
        return response;
    }

    @Nested
    @DisplayName("User Form Parsing Tests")
    class UserFormParsingTests {

        @Test
        @DisplayName("Should successfully parse user form text")
        void testParseTextForUserForm_Success() throws IOException {
            // Prepare test data
            Map<String, Object> userData = new HashMap<>();
            userData.put("username", "johndoe");
            userData.put("nickname", "John Doe");
            userData.put("position", "developer");
            userData.put("departmentName", "IT");

            // Mock response
            Response mockResponse = MockResponseHelper.createMockResponse(true, gson.toJson(createSuccessResponse(userData)));
            when(mockCall.execute()).thenReturn(mockResponse);

            // Execute test
            Map<String, Object> result = aiService.parseTextForUserForm("Create a user named John Doe");

            // Verify results
            assertNotNull(result);
            assertEquals("johndoe", result.get("username"));
            assertEquals("John Doe", result.get("nickname"));
            assertEquals("developer", result.get("position"));
            assertEquals("IT", result.get("departmentName"));
        }

        @Test
        @DisplayName("Should handle API error response")
        void testParseTextForUserForm_ApiError() throws IOException {
            // Mock error response
            Response mockResponse = MockResponseHelper.createMockResponse(false, "Error message");
            when(mockCall.execute()).thenReturn(mockResponse);

            // Execute test
            Map<String, Object> result = aiService.parseTextForUserForm("Create a user");

            // Verify results
            assertNotNull(result);
            assertTrue(result.containsKey("error"));
            assertTrue(result.get("error").toString().contains("Failed to call AI API"));
        }

        @Test
        @DisplayName("Should handle invalid JSON response")
        void testParseTextForUserForm_InvalidJson() throws IOException {
            // Mock invalid JSON response
            Response mockResponse = MockResponseHelper.createMockResponse(true, "Invalid JSON");
            when(mockCall.execute()).thenReturn(mockResponse);

            // Execute test
            Map<String, Object> result = aiService.parseTextForUserForm("Create a user");

            // Verify results
            assertNotNull(result);
            assertTrue(result.containsKey("error"));
        }
    }

    @Nested
    @DisplayName("Tenant Form Parsing Tests")
    class TenantFormParsingTests {

        @Test
        @DisplayName("Should successfully parse tenant form text")
        void testParseTextForTenantForm_Success() throws IOException {
            // Prepare test data
            Map<String, Object> tenantData = new HashMap<>();
            tenantData.put("tenantName", "Acme Corp");
            tenantData.put("contactPerson", "John Doe");

            // Mock response
            Response mockResponse = MockResponseHelper.createMockResponse(true, gson.toJson(createSuccessResponse(tenantData)));
            when(mockCall.execute()).thenReturn(mockResponse);

            // Execute test
            Map<String, Object> result = aiService.parseTextForTenantForm("Create a tenant named Acme Corp");

            // Verify results
            assertNotNull(result);
            assertEquals("Acme Corp", result.get("tenantName"));
            assertEquals("John Doe", result.get("contactPerson"));
        }

        @Test
        @DisplayName("Should handle API error response")
        void testParseTextForTenantForm_ApiError() throws IOException {
            // Mock error response
            Response mockResponse = MockResponseHelper.createMockResponse(false, "Error message");
            when(mockCall.execute()).thenReturn(mockResponse);

            // Execute test
            Map<String, Object> result = aiService.parseTextForTenantForm("Create a tenant");

            // Verify results
            assertNotNull(result);
            assertTrue(result.containsKey("error"));
        }
    }

    @Nested
    @DisplayName("Department Form Parsing Tests")
    class DepartmentFormParsingTests {

        @Test
        @DisplayName("Should successfully parse department form text")
        void testParseTextForDepartmentForm_Success() throws IOException {
            // Prepare test data
            Map<String, Object> deptData = new HashMap<>();
            deptData.put("departmentName", "IT");
            deptData.put("manager", "John Doe");
            deptData.put("managerEmail", "john@example.com");
            deptData.put("status", "Active");

            // Mock response
            Response mockResponse = MockResponseHelper.createMockResponse(true, gson.toJson(createSuccessResponse(deptData)));
            when(mockCall.execute()).thenReturn(mockResponse);

            // Execute test
            Map<String, Object> result = aiService.parseTextForDepartmentForm("Create IT department");

            // Verify results
            assertNotNull(result);
            assertEquals("IT", result.get("departmentName"));
            assertEquals("John Doe", result.get("manager"));
            assertEquals("john@example.com", result.get("managerEmail"));
            assertEquals("Active", result.get("status"));
        }

        @Test
        @DisplayName("Should handle API error response")
        void testParseTextForDepartmentForm_ApiError() throws IOException {
            // Mock error response
            Response mockResponse = MockResponseHelper.createMockResponse(false, "Error message");
            when(mockCall.execute()).thenReturn(mockResponse);

            // Execute test
            Map<String, Object> result = aiService.parseTextForDepartmentForm("Create a department");

            // Verify results
            assertNotNull(result);
            assertTrue(result.containsKey("error"));
        }
    }

    @Nested
    @DisplayName("Tenant Remark Generation Tests")
    class TenantRemarkGenerationTests {

        @Test
        @DisplayName("Should successfully generate tenant remark")
        void testGenerateTenantRemark_Success() throws IOException {
            // Prepare test data
            Map<String, Object> remarkData = new HashMap<>();
            remarkData.put("remark", "Professional company description");

            // Mock response
            Response mockResponse = MockResponseHelper.createMockResponse(true, gson.toJson(createSuccessResponse(remarkData)));
            when(mockCall.execute()).thenReturn(mockResponse);

            // Execute test
            Map<String, Object> result = aiService.generateTenantRemark("Acme Corp", "A technology company");

            // Verify results
            assertNotNull(result);
            assertTrue(result.containsKey("remark"));
        }

        @Test
        @DisplayName("Should handle API error response")
        void testGenerateTenantRemark_ApiError() throws IOException {
            // Mock error response
            Response mockResponse = MockResponseHelper.createMockResponse(false, "Error message");
            when(mockCall.execute()).thenReturn(mockResponse);

            // Execute test
            Map<String, Object> result = aiService.generateTenantRemark("Acme Corp", "Description");

            // Verify results
            assertNotNull(result);
            assertTrue(result.containsKey("error"));
        }

        @Test
        @DisplayName("Should handle empty response")
        void testGenerateTenantRemark_EmptyResponse() throws IOException {
            // Mock empty response
            Response mockResponse = MockResponseHelper.createMockResponse(true, "{}");
            when(mockCall.execute()).thenReturn(mockResponse);

            // Execute test
            Map<String, Object> result = aiService.generateTenantRemark("Acme Corp", "Description");

            // Verify results
            assertNotNull(result);
            assertTrue(result.containsKey("error"));
        }
    }
}