package edu.neu.oaas.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.neu.oaas.pojo.Conference;
import edu.neu.oaas.service.ConferenceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ConferenceController.class)
public class ConferenceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ConferenceService conferenceService;

    private Conference conference;
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        conference = new Conference();
        conference.setConferenceID(1);
        conference.setConferencename("Test Conference");
        conference.setCreator("Test Creator");
        conference.setStarttime(LocalDateTime.now().toString());
        conference.setEndtime(LocalDateTime.now().plusHours(2).toString());
        conference.setSituation("Test Location");
        conference.setContentspath("Test Description");
        conference.setApprovalStatus("pending");
        conference.setCoverpath("/covers/test.jpg");
        conference.setState("Active");
        conference.setTenantID(1);

        objectMapper = new ObjectMapper();
    }

    // 1. 核心功能测试 - 会议查询
    @Test
    public void testGetConferences() throws Exception {
        List<Conference> conferences = Arrays.asList(conference);
        when(conferenceService.getAllConferences()).thenReturn(conferences);

        mockMvc.perform(get("/conferences"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.meetings[0].conferencename").value("Test Conference"))
                .andExpect(jsonPath("$.total").value(1));
    }

    @Test
    public void testGetConferenceById() throws Exception {
        when(conferenceService.getConferenceById(1)).thenReturn(conference);

        mockMvc.perform(get("/conferences/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.conferencename").value("Test Conference"));
    }

    @Test
    public void testGetConferenceByIdNotFound() throws Exception {
        when(conferenceService.getConferenceById(999)).thenReturn(null);

        mockMvc.perform(get("/conferences/999"))
                .andExpect(status().isNotFound());
    }

    // 2. 核心功能测试 - 会议管理
    @Test
    public void testCreateConference() throws Exception {
        Map<String, String> response = new HashMap<>();
        response.put("message", "会议创建成功");

        mockMvc.perform(post("/conferences")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(conference)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("会议创建成功"));
    }

    @Test
    public void testCreateConferenceFailure() throws Exception {
        doThrow(new RuntimeException("创建失败")).when(conferenceService).createConference(any(Conference.class));

        mockMvc.perform(post("/conferences")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(conference)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("会议创建失败"));
    }

    @Test
    public void testUploadCover() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.jpg",
                "image/jpeg",
                "test image content".getBytes()
        );

        when(conferenceService.saveCover(any())).thenReturn("/covers/test.jpg");

        mockMvc.perform(multipart("/conferences/upload-cover")
                        .file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.url").value("/covers/test.jpg"));
    }

    @Test
    public void testUpdateConference() throws Exception {
        when(conferenceService.updateConference(any(Conference.class))).thenReturn(1);

        mockMvc.perform(put("/conferences/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(conference)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("会议修改成功"));
    }

    @Test
    public void testUpdateConferenceFailure() throws Exception {
        when(conferenceService.updateConference(any(Conference.class))).thenReturn(0);

        mockMvc.perform(put("/conferences/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(conference)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("会议修改失败"));
    }

    @Test
    public void testDeleteConference() throws Exception {
        mockMvc.perform(delete("/conferences/1"))
                .andExpect(status().isOk());
    }

    // 3. 核心功能测试 - 会议搜索
    @Test
    public void testSearchConferences() throws Exception {
        List<Conference> conferences = Arrays.asList(conference);
        when(conferenceService.searchConferences("Test", "Creator", "2024-01-01"))
                .thenReturn(conferences);

        mockMvc.perform(get("/conferences/search")
                        .param("conferencename", "Test")
                        .param("creator", "Creator")
                        .param("starttime", "2024-01-01"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.meetings[0].conferencename").value("Test Conference"))
                .andExpect(jsonPath("$.total").value(1));
    }

    // 4. 核心功能测试 - 会议审批
    @Test
    public void testGetPendingConferences() throws Exception {
        List<Conference> conferences = Arrays.asList(conference);
        when(conferenceService.getPendingConferences()).thenReturn(conferences);

        mockMvc.perform(get("/conferences/pending"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.meetings[0].conferencename").value("Test Conference"))
                .andExpect(jsonPath("$.total").value(1));
    }

    @Test
    public void testGetConferencesByApprovalStatus() throws Exception {
        List<Conference> conferences = Arrays.asList(conference);
        when(conferenceService.getConferencesByApprovalStatus("pending")).thenReturn(conferences);

        mockMvc.perform(get("/conferences/status/pending"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.meetings[0].conferencename").value("Test Conference"))
                .andExpect(jsonPath("$.total").value(1));
    }

    @Test
    public void testApproveConference() throws Exception {
        Map<String, String> approvalData = new HashMap<>();
        approvalData.put("approvalStatus", "approved");
        approvalData.put("rejectionReason", "");

        when(conferenceService.approveConference(eq(1), eq("approved"), eq("")))
                .thenReturn(1);

        mockMvc.perform(post("/conferences/1/approve")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(approvalData)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("会议审核通过"))
                .andExpect(jsonPath("$.status").value("success"));
    }

    @Test
    public void testRejectConference() throws Exception {
        Map<String, String> approvalData = new HashMap<>();
        approvalData.put("approvalStatus", "rejected");
        approvalData.put("rejectionReason", "不符合要求");

        when(conferenceService.approveConference(eq(1), eq("rejected"), eq("不符合要求")))
                .thenReturn(1);

        mockMvc.perform(post("/conferences/1/approve")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(approvalData)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("会议审核拒绝"))
                .andExpect(jsonPath("$.status").value("success"));
    }

    @Test
    public void testApproveConferenceFailure() throws Exception {
        Map<String, String> approvalData = new HashMap<>();
        approvalData.put("approvalStatus", "approved");
        approvalData.put("rejectionReason", "");

        when(conferenceService.approveConference(eq(1), eq("approved"), eq("")))
                .thenReturn(0);

        mockMvc.perform(post("/conferences/1/approve")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(approvalData)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("审核失败"))
                .andExpect(jsonPath("$.status").value("error"));
    }

    @Test
    public void testGetApprovedConferences() throws Exception {
        List<Conference> conferences = Arrays.asList(conference);
        when(conferenceService.getApprovedConferences()).thenReturn(conferences);

        mockMvc.perform(get("/conferences/approved"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.meetings[0].conferencename").value("Test Conference"))
                .andExpect(jsonPath("$.total").value(1));
    }
} 