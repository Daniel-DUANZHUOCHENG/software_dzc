package edu.neu.oaas.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.neu.oaas.pojo.Information;
import edu.neu.oaas.service.InformationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class InformationControllerTest {

    @Mock
    private InformationService informationService;

    @InjectMocks
    private InformationController informationController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(informationController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void testGetInformationByPathPrefix_Success() throws Exception {
        // 准备测试数据
        Information info1 = new Information();
        info1.setId(1);
        info1.setTitle("Test Title 1");
        info1.setPath("/test/path1");
        info1.setContent("Test Content 1");
        info1.setAuthor("Test Author");
        info1.setTenantId(1);

        Information info2 = new Information();
        info2.setId(2);
        info2.setTitle("Test Title 2");
        info2.setPath("/test/path2");
        info2.setContent("Test Content 2");
        info2.setAuthor("Test Author");
        info2.setTenantId(1);

        List<Information> infoList = Arrays.asList(info1, info2);

        when(informationService.findByPathPrefix("/test")).thenReturn(infoList);

        // 执行测试并验证结果
        mockMvc.perform(get("/api/information/path/test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(2))
                .andExpect(jsonPath("$.informationList[0].id").value(1))
                .andExpect(jsonPath("$.informationList[0].title").value("Test Title 1"))
                .andExpect(jsonPath("$.informationList[1].id").value(2))
                .andExpect(jsonPath("$.informationList[1].title").value("Test Title 2"));

        verify(informationService).findByPathPrefix("/test");
    }

    @Test
    void testGetInformationByPathPrefix_Empty() throws Exception {
        when(informationService.findByPathPrefix("/test")).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/information/path/test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(0))
                .andExpect(jsonPath("$.informationList").isEmpty());

        verify(informationService).findByPathPrefix("/test");
    }

    @Test
    void testGetInformationByPathPrefix_Error() throws Exception {
        when(informationService.findByPathPrefix("/test")).thenThrow(new RuntimeException("Database error"));

        mockMvc.perform(get("/api/information/path/test"))
                .andExpect(status().isInternalServerError());

        verify(informationService).findByPathPrefix("/test");
    }

    @Test
    void testSearchInformation_Success() throws Exception {
        // 准备测试数据
        Information info = new Information();
        info.setId(1);
        info.setTitle("Test Title");
        info.setPath("/test/path");
        info.setContent("Test Content");
        info.setAuthor("Test Author");
        info.setTenantId(1);

        List<Information> infoList = Collections.singletonList(info);

        when(informationService.searchByTitleAndAuthor(anyString(), anyString(), anyString())).thenReturn(infoList);

        // 执行测试并验证结果
        mockMvc.perform(get("/api/information/search")
                .param("pathPrefix", "/test")
                .param("title", "Test")
                .param("author", "Author"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(1))
                .andExpect(jsonPath("$.informationList[0].id").value(1))
                .andExpect(jsonPath("$.informationList[0].title").value("Test Title"));

        verify(informationService).searchByTitleAndAuthor("/test", "Test", "Author");
    }

    @Test
    void testSearchInformation_Empty() throws Exception {
        when(informationService.searchByTitleAndAuthor(anyString(), anyString(), anyString()))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/information/search")
                .param("pathPrefix", "/test")
                .param("title", "Test")
                .param("author", "Author"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(0))
                .andExpect(jsonPath("$.informationList").isEmpty());

        verify(informationService).searchByTitleAndAuthor("/test", "Test", "Author");
    }

    @Test
    void testSearchInformation_Error() throws Exception {
        when(informationService.searchByTitleAndAuthor(anyString(), anyString(), anyString()))
                .thenThrow(new RuntimeException("Search failed"));

        mockMvc.perform(get("/api/information/search")
                .param("pathPrefix", "/test")
                .param("title", "Test")
                .param("author", "Author"))
                .andExpect(status().isInternalServerError());

        verify(informationService).searchByTitleAndAuthor("/test", "Test", "Author");
    }

    @Test
    void testCreateInformation_Success() throws Exception {
        // 准备测试数据
        Information info = new Information();
        info.setTitle("New Information");
        info.setPath("/test/path");
        info.setContent("New Content");
        info.setAuthor("Author");
        info.setTenantId(1);

        doNothing().when(informationService).insertInformation(any(Information.class));

        // 执行测试并验证结果
        mockMvc.perform(post("/api/information")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(info)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("资讯创建成功"));

        verify(informationService).insertInformation(any(Information.class));
    }

    @Test
    void testCreateInformation_Error() throws Exception {
        // 准备测试数据
        Information info = new Information();
        info.setTitle("New Information");
        info.setPath("/test/path");
        info.setContent("New Content");
        info.setAuthor("Author");
        info.setTenantId(1);

        doThrow(new RuntimeException("Failed to create information")).when(informationService).insertInformation(any(Information.class));

        // 执行测试并验证结果
        mockMvc.perform(post("/api/information")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(info)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("资讯创建失败"));

        verify(informationService).insertInformation(any(Information.class));
    }

    @Test
    void testUpdateInformation_Success() throws Exception {
        // 准备测试数据
        Information info = new Information();
        info.setId(1);
        info.setTitle("Updated Information");
        info.setPath("/test/path");
        info.setContent("Updated Content");
        info.setAuthor("Author");
        info.setTenantId(1);

        when(informationService.updateInformation(any(Information.class))).thenReturn(1);

        // 执行测试并验证结果
        mockMvc.perform(put("/api/information/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(info)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("资讯修改成功"));

        verify(informationService).updateInformation(any(Information.class));
    }

    @Test
    void testUpdateInformation_TenantAdmin_Success() throws Exception {
        // 准备测试数据
        Information info = new Information();
        info.setId(1);
        info.setTitle("Updated Information");
        info.setPath("/test/path");
        info.setContent("Updated Content");
        info.setAuthor("Author");
        info.setTenantId(1);

        Information originalInfo = new Information();
        originalInfo.setId(1);
        originalInfo.setTenantId(1);

        when(informationService.getInformationById(1)).thenReturn(originalInfo);
        when(informationService.updateInformation(any(Information.class))).thenReturn(1);

        // 执行测试并验证结果
        mockMvc.perform(put("/api/information/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(info))
                .header("User-Role", "TAdmin")
                .header("User-Tenant-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("资讯修改成功"));

        verify(informationService).updateInformation(any(Information.class));
    }

    @Test
    void testUpdateInformation_TenantAdmin_Forbidden() throws Exception {
        // 准备测试数据
        Information info = new Information();
        info.setId(1);
        info.setTitle("Updated Information");
        info.setPath("/test/path");
        info.setContent("Updated Content");
        info.setAuthor("Author");
        info.setTenantId(2);

        Information originalInfo = new Information();
        originalInfo.setId(1);
        originalInfo.setTenantId(2);

        when(informationService.getInformationById(1)).thenReturn(originalInfo);

        // 执行测试并验证结果
        mockMvc.perform(put("/api/information/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(info))
                .header("User-Role", "TAdmin")
                .header("User-Tenant-Id", "1"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("权限不足，只能修改本租户资讯"));

        verify(informationService, never()).updateInformation(any(Information.class));
    }

    @Test
    void testUpdateInformation_NotFound() throws Exception {
        Information info = new Information();
        info.setId(1);
        info.setTitle("Updated Information");
        info.setPath("/test/path");
        info.setContent("Updated Content");
        info.setAuthor("Author");
        info.setTenantId(1);

        when(informationService.updateInformation(any(Information.class))).thenReturn(0);

        mockMvc.perform(put("/api/information/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(info)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("资讯修改失败"));

        verify(informationService).updateInformation(any(Information.class));
    }

    @Test
    void testUpdateInformation_Error() throws Exception {
        Information info = new Information();
        info.setId(1);
        info.setTitle("Updated Information");
        info.setPath("/test/path");
        info.setContent("Updated Content");
        info.setAuthor("Author");
        info.setTenantId(1);

        when(informationService.updateInformation(any(Information.class)))
                .thenThrow(new RuntimeException("Update failed"));

        mockMvc.perform(put("/api/information/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(info)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("资讯修改失败"));

        verify(informationService).updateInformation(any(Information.class));
    }

    @Test
    void testDeleteInformation_Success() throws Exception {
        doNothing().when(informationService).deleteInformation(1);

        // 执行测试并验证结果
        mockMvc.perform(delete("/api/information/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("资讯删除成功"));

        verify(informationService).deleteInformation(1);
    }

    @Test
    void testDeleteInformation_Error() throws Exception {
        doThrow(new RuntimeException("Delete failed")).when(informationService).deleteInformation(1);

        mockMvc.perform(delete("/api/information/1"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("资讯删除失败"));

        verify(informationService).deleteInformation(1);
    }

    @Test
    void testDeleteInformation_TenantAdmin_Success() throws Exception {
        Information info = new Information();
        info.setId(1);
        info.setTenantId(1);

        when(informationService.getInformationById(1)).thenReturn(info);
        doNothing().when(informationService).deleteInformation(1);

        // 执行测试并验证结果
        mockMvc.perform(delete("/api/information/1")
                .header("User-Role", "TAdmin")
                .header("User-Tenant-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("资讯删除成功"));

        verify(informationService).deleteInformation(1);
    }

    @Test
    void testDeleteInformation_TenantAdmin_Forbidden() throws Exception {
        Information info = new Information();
        info.setId(1);
        info.setTenantId(2);

        when(informationService.getInformationById(1)).thenReturn(info);

        // 执行测试并验证结果
        mockMvc.perform(delete("/api/information/1")
                .header("User-Role", "TAdmin")
                .header("User-Tenant-Id", "1"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("权限不足，只能删除本租户资讯"));

        verify(informationService, never()).deleteInformation(1);
    }

    @Test
    void testGetInformationById_Success() throws Exception {
        // 准备测试数据
        Information info = new Information();
        info.setId(1);
        info.setTitle("Test Information");
        info.setPath("/test/path");
        info.setContent("Test Content");
        info.setAuthor("Author");
        info.setTenantId(1);

        when(informationService.getInformationById(1)).thenReturn(info);

        // 执行测试并验证结果
        mockMvc.perform(get("/api/information/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Test Information"));

        verify(informationService).getInformationById(1);
    }

    @Test
    void testGetInformationById_NotFound() throws Exception {
        when(informationService.getInformationById(1)).thenReturn(null);

        // 执行测试并验证结果
        mockMvc.perform(get("/api/information/1"))
                .andExpect(status().isNotFound());

        verify(informationService).getInformationById(1);
    }

    @Test
    void testGetInformationByTenantId_Success() throws Exception {
        // 准备测试数据
        Information info = new Information();
        info.setId(1);
        info.setTitle("Test Information");
        info.setPath("/test/path");
        info.setContent("Test Content");
        info.setAuthor("Author");
        info.setTenantId(1);

        List<Information> infoList = Collections.singletonList(info);

        when(informationService.getInformationByTenantId(1)).thenReturn(infoList);

        // 执行测试并验证结果
        mockMvc.perform(get("/api/information/tenant/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(1))
                .andExpect(jsonPath("$.informationList[0].id").value(1))
                .andExpect(jsonPath("$.informationList[0].title").value("Test Information"));

        verify(informationService).getInformationByTenantId(1);
    }

    @Test
    void testGetPendingInformation_Success() throws Exception {
        // 准备测试数据
        Information info = new Information();
        info.setId(1);
        info.setTitle("Pending Information");
        info.setPath("/test/path");
        info.setContent("Test Content");
        info.setAuthor("Author");
        info.setTenantId(1);

        List<Information> infoList = Collections.singletonList(info);

        when(informationService.getPendingInformation()).thenReturn(infoList);

        // 执行测试并验证结果
        mockMvc.perform(get("/api/information/pending"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(1))
                .andExpect(jsonPath("$.informationList[0].id").value(1))
                .andExpect(jsonPath("$.informationList[0].title").value("Pending Information"));

        verify(informationService).getPendingInformation();
    }

    @Test
    void testGetInformationByApprovalStatus_Success() throws Exception {
        // 准备测试数据
        Information info = new Information();
        info.setId(1);
        info.setTitle("Approved Information");
        info.setPath("/test/path");
        info.setContent("Test Content");
        info.setAuthor("Author");
        info.setTenantId(1);

        List<Information> infoList = Collections.singletonList(info);

        when(informationService.getInformationByApprovalStatus("approved")).thenReturn(infoList);

        // 执行测试并验证结果
        mockMvc.perform(get("/api/information/status/approved"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(1))
                .andExpect(jsonPath("$.informationList[0].id").value(1))
                .andExpect(jsonPath("$.informationList[0].title").value("Approved Information"));

        verify(informationService).getInformationByApprovalStatus("approved");
    }

    @Test
    void testApproveInformation_Success() throws Exception {
        when(informationService.approveInformation(eq(1), eq("approved"), anyString())).thenReturn(1);

        // 执行测试并验证结果
        mockMvc.perform(post("/api/information/1/approve")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"approvalStatus\":\"approved\",\"rejectionReason\":\"\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("资讯审核通过"))
                .andExpect(jsonPath("$.status").value("success"));

        verify(informationService).approveInformation(eq(1), eq("approved"), anyString());
    }

    @Test
    void testApproveInformation_Reject() throws Exception {
        when(informationService.approveInformation(eq(1), eq("rejected"), eq("内容不合适"))).thenReturn(1);

        // 执行测试并验证结果
        mockMvc.perform(post("/api/information/1/approve")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"approvalStatus\":\"rejected\",\"rejectionReason\":\"内容不合适\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("资讯审核拒绝"))
                .andExpect(jsonPath("$.status").value("success"));

        verify(informationService).approveInformation(eq(1), eq("rejected"), eq("内容不合适"));
    }

    @Test
    void testGetApprovedInformation_Success() throws Exception {
        // 准备测试数据
        Information info = new Information();
        info.setId(1);
        info.setTitle("Approved Information");
        info.setPath("/test/path");
        info.setContent("Test Content");
        info.setAuthor("Author");
        info.setTenantId(1);

        List<Information> infoList = Collections.singletonList(info);

        when(informationService.getApprovedInformation()).thenReturn(infoList);

        // 执行测试并验证结果
        mockMvc.perform(get("/api/information/approved"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(1))
                .andExpect(jsonPath("$.informationList[0].id").value(1))
                .andExpect(jsonPath("$.informationList[0].title").value("Approved Information"));

        verify(informationService).getApprovedInformation();
    }

    @Test
    void testGetAllInformation_Success() throws Exception {
        // 准备测试数据
        Information info1 = new Information();
        info1.setId(1);
        info1.setTitle("Information 1");
        info1.setPath("/test/path1");
        info1.setContent("Test Content 1");
        info1.setAuthor("Author 1");
        info1.setTenantId(1);

        Information info2 = new Information();
        info2.setId(2);
        info2.setTitle("Information 2");
        info2.setPath("/test/path2");
        info2.setContent("Test Content 2");
        info2.setAuthor("Author 2");
        info2.setTenantId(1);

        List<Information> infoList = Arrays.asList(info1, info2);

        when(informationService.getAllInformation()).thenReturn(infoList);

        // 执行测试并验证结果
        mockMvc.perform(get("/api/information"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(2))
                .andExpect(jsonPath("$.informationList[0].id").value(1))
                .andExpect(jsonPath("$.informationList[0].title").value("Information 1"))
                .andExpect(jsonPath("$.informationList[1].id").value(2))
                .andExpect(jsonPath("$.informationList[1].title").value("Information 2"));

        verify(informationService).getAllInformation();
    }

    @Test
    void testGetAllInformation_Error() throws Exception {
        when(informationService.getAllInformation()).thenThrow(new RuntimeException("Failed to get information"));

        // 执行测试并验证结果
        mockMvc.perform(get("/api/information"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.total").value(0))
                .andExpect(jsonPath("$.informationList").isArray())
                .andExpect(jsonPath("$.informationList").isEmpty())
                .andExpect(jsonPath("$.error").value("获取资讯数据失败: Failed to get information"));

        verify(informationService).getAllInformation();
    }
} 