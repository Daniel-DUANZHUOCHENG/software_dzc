package edu.neu.oaas.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.neu.oaas.pojo.MeetingApplication;
import edu.neu.oaas.service.MeetingApplicationService;
import edu.neu.oaas.utils.PermissionValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class MeetingApplicationControllerTest {

    @Mock
    private MeetingApplicationService meetingApplicationService;

    @Mock
    private PermissionValidator permissionValidator;

    @InjectMocks
    private MeetingApplicationController meetingApplicationController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(meetingApplicationController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void testSubmitMeetingApplication_Success() throws Exception {
        // 准备测试数据
        MeetingApplication application = new MeetingApplication();
        application.setMeetingId(1);
        application.setApplicantId(1);
        application.setTenantId(1);
        application.setStatus("pending");
        application.setApplicationTime(LocalDateTime.now());

        when(permissionValidator.canApplyForMeeting(anyString())).thenReturn(true);
        when(meetingApplicationService.submitMeetingApplication(any(MeetingApplication.class))).thenReturn(1);

        // 执行测试并验证结果
        mockMvc.perform(post("/api/meeting-applications/submit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(application))
                .header("User-Role", "User")
                .header("User-Id", "1")
                .header("User-Tenant-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("申请提交成功"));

        verify(meetingApplicationService).submitMeetingApplication(any(MeetingApplication.class));
    }

    @Test
    void testSubmitMeetingApplication_NoPermission() throws Exception {
        // 准备测试数据
        MeetingApplication application = new MeetingApplication();
        application.setMeetingId(1);
        application.setApplicantId(1);
        application.setTenantId(1);
        application.setStatus("pending");
        application.setApplicationTime(LocalDateTime.now());

        when(permissionValidator.canApplyForMeeting(anyString())).thenReturn(false);

        // 执行测试并验证结果
        mockMvc.perform(post("/api/meeting-applications/submit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(application))
                .header("User-Role", "Guest")
                .header("User-Id", "1")
                .header("User-Tenant-Id", "1"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("权限不足，无法申请会议"));

        verify(meetingApplicationService, never()).submitMeetingApplication(any(MeetingApplication.class));
    }

    @Test
    void testCheckApplicationExists_Success() throws Exception {
        when(meetingApplicationService.checkExistingApplication(1, 1)).thenReturn(1);

        // 执行测试并验证结果
        mockMvc.perform(get("/api/meeting-applications/check")
                .param("meetingId", "1")
                .param("applicantId", "1")
                .header("User-Role", "User")
                .header("User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.hasApplied").value(true));

        verify(meetingApplicationService).checkExistingApplication(1, 1);
    }

    @Test
    void testCheckApplicationExists_NoPermission() throws Exception {
        // 执行测试并验证结果
        mockMvc.perform(get("/api/meeting-applications/check")
                .param("meetingId", "1")
                .param("applicantId", "2")
                .header("User-Role", "User")
                .header("User-Id", "1"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("权限不足"));

        verify(meetingApplicationService, never()).checkExistingApplication(anyInt(), anyInt());
    }

    @Test
    void testGetMeetingApplications_AdminSuccess() throws Exception {
        // 准备测试数据
        MeetingApplication application = new MeetingApplication();
        application.setId(1);
        application.setMeetingId(1);
        application.setApplicantId(1);
        application.setTenantId(1);
        application.setStatus("pending");
        application.setApplicationTime(LocalDateTime.now());

        when(permissionValidator.isAdminUser(anyString())).thenReturn(true);
        when(meetingApplicationService.getMeetingApplications(1)).thenReturn(Collections.singletonList(application));

        // 执行测试并验证结果
        mockMvc.perform(get("/api/meeting-applications/meeting/1")
                .header("User-Role", "Admin"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.applications[0].id").value(1))
                .andExpect(jsonPath("$.total").value(1));

        verify(meetingApplicationService).getMeetingApplications(1);
    }

    @Test
    void testGetMeetingApplications_TenantAdminSuccess() throws Exception {
        // 准备测试数据
        MeetingApplication application = new MeetingApplication();
        application.setId(1);
        application.setMeetingId(1);
        application.setApplicantId(1);
        application.setTenantId(1);
        application.setStatus("pending");
        application.setApplicationTime(LocalDateTime.now());

        when(permissionValidator.isAdminUser(anyString())).thenReturn(true);
        when(meetingApplicationService.getMeetingApplicationsByTenant(1, 1)).thenReturn(Collections.singletonList(application));

        // 执行测试并验证结果
        mockMvc.perform(get("/api/meeting-applications/meeting/1")
                .header("User-Role", "TAdmin")
                .header("User-Tenant-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.applications[0].id").value(1))
                .andExpect(jsonPath("$.total").value(1));

        verify(meetingApplicationService).getMeetingApplicationsByTenant(1, 1);
    }

    @Test
    void testGetMeetingApplications_NoPermission() throws Exception {
        when(permissionValidator.isAdminUser(anyString())).thenReturn(false);

        // 执行测试并验证结果
        mockMvc.perform(get("/api/meeting-applications/meeting/1")
                .header("User-Role", "User"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("权限不足，无法查看申请列表"));

        verify(meetingApplicationService, never()).getMeetingApplications(anyInt());
    }

    @Test
    void testGetMeetingApplicationsByApplicantId_Success() throws Exception {
        // 准备测试数据
        MeetingApplication application = new MeetingApplication();
        application.setId(1);
        application.setMeetingId(1);
        application.setApplicantId(1);
        application.setTenantId(1);
        application.setStatus("pending");
        application.setApplicationTime(LocalDateTime.now());

        when(meetingApplicationService.getMeetingApplicationsByApplicantId(1)).thenReturn(Collections.singletonList(application));

        // 执行测试并验证结果
        mockMvc.perform(get("/api/meeting-applications/applicant/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.applications[0].id").value(1))
                .andExpect(jsonPath("$.total").value(1));

        verify(meetingApplicationService).getMeetingApplicationsByApplicantId(1);
    }

    @Test
    void testApproveMeetingApplication_Success() throws Exception {
        // 准备测试数据
        Map<String, Object> approvalData = new HashMap<>();
        approvalData.put("status", "approved");
        approvalData.put("rejectionReason", "");
        approvalData.put("approverId", 1);
        approvalData.put("approverName", "Admin");

        when(meetingApplicationService.approveMeetingApplication(eq(1), eq("approved"), eq(""), eq(1), eq("Admin"))).thenReturn(1);

        // 执行测试并验证结果
        mockMvc.perform(post("/api/meeting-applications/1/approve")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(approvalData)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("申请已通过"));

        verify(meetingApplicationService).approveMeetingApplication(eq(1), eq("approved"), eq(""), eq(1), eq("Admin"));
    }

    @Test
    void testApproveMeetingApplication_Reject() throws Exception {
        // 准备测试数据
        Map<String, Object> approvalData = new HashMap<>();
        approvalData.put("status", "rejected");
        approvalData.put("rejectionReason", "不符合要求");
        approvalData.put("approverId", 1);
        approvalData.put("approverName", "Admin");

        when(meetingApplicationService.approveMeetingApplication(eq(1), eq("rejected"), eq("不符合要求"), eq(1), eq("Admin"))).thenReturn(1);

        // 执行测试并验证结果
        mockMvc.perform(post("/api/meeting-applications/1/approve")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(approvalData)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("申请已拒绝"));

        verify(meetingApplicationService).approveMeetingApplication(eq(1), eq("rejected"), eq("不符合要求"), eq(1), eq("Admin"));
    }

    @Test
    void testGetMeetingApplicationById_Success() throws Exception {
        // 准备测试数据
        MeetingApplication application = new MeetingApplication();
        application.setId(1);
        application.setMeetingId(1);
        application.setApplicantId(1);
        application.setTenantId(1);
        application.setStatus("pending");
        application.setApplicationTime(LocalDateTime.now());

        when(meetingApplicationService.getMeetingApplicationById(1)).thenReturn(application);

        // 执行测试并验证结果
        mockMvc.perform(get("/api/meeting-applications/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.application.id").value(1));

        verify(meetingApplicationService).getMeetingApplicationById(1);
    }

    @Test
    void testGetMeetingApplicationById_NotFound() throws Exception {
        when(meetingApplicationService.getMeetingApplicationById(1)).thenReturn(null);

        // 执行测试并验证结果
        mockMvc.perform(get("/api/meeting-applications/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("申请不存在"));

        verify(meetingApplicationService).getMeetingApplicationById(1);
    }

    @Test
    void testGetPendingApplicationCount_Success() throws Exception {
        when(meetingApplicationService.getPendingApplicationCount(1)).thenReturn(5);

        // 执行测试并验证结果
        mockMvc.perform(get("/api/meeting-applications/pending/count")
                .param("tenantId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.count").value(5));

        verify(meetingApplicationService).getPendingApplicationCount(1);
    }

    @Test
    void testGetTenantPendingApplications_Success() throws Exception {
        // 准备测试数据
        MeetingApplication application = new MeetingApplication();
        application.setId(1);
        application.setMeetingId(1);
        application.setApplicantId(1);
        application.setTenantId(1);
        application.setStatus("pending");
        application.setApplicationTime(LocalDateTime.now());

        when(meetingApplicationService.getTenantPendingApplications(1)).thenReturn(Collections.singletonList(application));

        // 执行测试并验证结果
        mockMvc.perform(get("/api/meeting-applications/tenant/1/pending"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.applications[0].id").value(1))
                .andExpect(jsonPath("$.total").value(1));

        verify(meetingApplicationService).getTenantPendingApplications(1);
    }

    @Test
    void testDeleteMeetingApplication_Success() throws Exception {
        when(meetingApplicationService.deleteMeetingApplication(1)).thenReturn(1);

        // 执行测试并验证结果
        mockMvc.perform(delete("/api/meeting-applications/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("申请删除成功"));

        verify(meetingApplicationService).deleteMeetingApplication(1);
    }

    @Test
    void testGetAllMeetingApplications_Success() throws Exception {
        // 准备测试数据
        MeetingApplication application1 = new MeetingApplication();
        application1.setId(1);
        application1.setMeetingId(1);
        application1.setApplicantId(1);
        application1.setTenantId(1);
        application1.setStatus("pending");
        application1.setApplicationTime(LocalDateTime.now());

        MeetingApplication application2 = new MeetingApplication();
        application2.setId(2);
        application2.setMeetingId(2);
        application2.setApplicantId(2);
        application2.setTenantId(1);
        application2.setStatus("approved");
        application2.setApplicationTime(LocalDateTime.now());

        when(meetingApplicationService.getAllMeetingApplications()).thenReturn(Arrays.asList(application1, application2));

        // 执行测试并验证结果
        mockMvc.perform(get("/api/meeting-applications/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.applications[0].id").value(1))
                .andExpect(jsonPath("$.applications[1].id").value(2))
                .andExpect(jsonPath("$.total").value(2));

        verify(meetingApplicationService).getAllMeetingApplications();
    }

    @Test
    void testGetMeetingApplicationsByStatus_Success() throws Exception {
        // 准备测试数据
        MeetingApplication application = new MeetingApplication();
        application.setId(1);
        application.setMeetingId(1);
        application.setApplicantId(1);
        application.setTenantId(1);
        application.setStatus("pending");
        application.setApplicationTime(LocalDateTime.now());

        when(meetingApplicationService.getMeetingApplicationsByStatus("pending")).thenReturn(Collections.singletonList(application));

        // 执行测试并验证结果
        mockMvc.perform(get("/api/meeting-applications/status/pending"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.applications[0].id").value(1))
                .andExpect(jsonPath("$.total").value(1));

        verify(meetingApplicationService).getMeetingApplicationsByStatus("pending");
    }
} 