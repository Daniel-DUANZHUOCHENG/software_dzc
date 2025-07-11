package edu.neu.oaas.service;

import edu.neu.oaas.mapper.ConferenceMapper;
import edu.neu.oaas.pojo.Conference;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ConferenceServiceTest {

    @Mock
    private ConferenceMapper conferenceMapper;

    @InjectMocks
    private ConferenceService conferenceService;

    private Conference conference;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        conference = new Conference();
        conference.setConferenceID(1);
        conference.setConferencename("Test Conference");
        conference.setContentspath("Test Content");
        conference.setStarttime(LocalDateTime.now().toString());
        conference.setEndtime(LocalDateTime.now().plusHours(2).toString());
        conference.setSituation("Room 101");
        conference.setState("Active");
        conference.setTenantID(1);
        conference.setCreator("admin");
        conference.setApprovalStatus("pending");
    }

    /**
     * 正常情况：创建会议
     */
    @Test
    void testCreateConference() {
        doNothing().when(conferenceMapper).insertConference(any(Conference.class));
        
        conferenceService.createConference(conference);
        
        verify(conferenceMapper).insertConference(conference);
    }

    /**
     * 正常情况：获取所有会议
     */
    @Test
    void testGetAllConferences() {
        when(conferenceMapper.getAllConferences()).thenReturn(Arrays.asList(conference));
        
        List<Conference> conferences = conferenceService.getAllConferences();
        
        assertFalse(conferences.isEmpty());
        assertEquals(1, conferences.size());
        assertEquals("Test Conference", conferences.get(0).getConferencename());
    }

    /**
     * 边界情况：无会议记录
     */
    @Test
    void testGetAllConferencesEmpty() {
        when(conferenceMapper.getAllConferences()).thenReturn(Collections.emptyList());
        
        List<Conference> conferences = conferenceService.getAllConferences();
        
        assertTrue(conferences.isEmpty());
    }

    /**
     * 正常情况：根据ID获取会议
     */
    @Test
    void testGetConferenceById() {
        when(conferenceMapper.getConferenceById(1)).thenReturn(conference);
        
        Conference result = conferenceService.getConferenceById(1);
        
        assertNotNull(result);
        assertEquals("Test Conference", result.getConferencename());
    }

    /**
     * 边界情况：会议不存在
     */
    @Test
    void testGetConferenceByIdNotFound() {
        when(conferenceMapper.getConferenceById(999)).thenReturn(null);
        
        Conference result = conferenceService.getConferenceById(999);
        
        assertNull(result);
    }

    /**
     * 正常情况：更新会议
     */
    @Test
    void testUpdateConference() {
        when(conferenceMapper.updateConference(any(Conference.class))).thenReturn(1);
        
        int result = conferenceService.updateConference(conference);
        
        assertEquals(1, result);
        verify(conferenceMapper).updateConference(conference);
    }

    /**
     * 异常情况：更新不存在的会议
     */
    @Test
    void testUpdateConferenceNotFound() {
        when(conferenceMapper.updateConference(any(Conference.class))).thenReturn(0);
        
        int result = conferenceService.updateConference(conference);
        
        assertEquals(0, result);
    }

    /**
     * 正常情况：删除会议
     */
    @Test
    void testDeleteConference() {
        doNothing().when(conferenceMapper).deleteConference(1);
        
        conferenceService.deleteConference(1);
        
        verify(conferenceMapper).deleteConference(1);
    }

    /**
     * 正常情况：搜索会议
     */
    @Test
    void testSearchConferences() {
        when(conferenceMapper.searchConferences("Test", "admin", "2024")).thenReturn(Arrays.asList(conference));
        
        List<Conference> conferences = conferenceService.searchConferences("Test", "admin", "2024");
        
        assertFalse(conferences.isEmpty());
        assertEquals(1, conferences.size());
        assertEquals("Test Conference", conferences.get(0).getConferencename());
    }

    /**
     * 边界情况：搜索无结果
     */
    @Test
    void testSearchConferencesEmpty() {
        when(conferenceMapper.searchConferences("NonExistent", "unknown", "2025")).thenReturn(Collections.emptyList());
        
        List<Conference> conferences = conferenceService.searchConferences("NonExistent", "unknown", "2025");
        
        assertTrue(conferences.isEmpty());
    }

    /**
     * 正常情况：获取待审核会议
     */
    @Test
    void testGetPendingConferences() {
        when(conferenceMapper.selectPendingConferences()).thenReturn(Arrays.asList(conference));
        
        List<Conference> conferences = conferenceService.getPendingConferences();
        
        assertFalse(conferences.isEmpty());
        assertEquals("pending", conferences.get(0).getApprovalStatus());
    }

    /**
     * 正常情况：根据审核状态获取会议
     */
    @Test
    void testGetConferencesByApprovalStatus() {
        when(conferenceMapper.selectConferencesByApprovalStatus("approved")).thenReturn(Arrays.asList(conference));
        
        List<Conference> conferences = conferenceService.getConferencesByApprovalStatus("approved");
        
        assertFalse(conferences.isEmpty());
    }

    /**
     * 正常情况：审核会议
     */
    @Test
    void testApproveConference() {
        when(conferenceMapper.approveConference(1, "approved", null)).thenReturn(1);
        
        int result = conferenceService.approveConference(1, "approved", null);
        
        assertEquals(1, result);
    }

    /**
     * 正常情况：获取已审核通过的会议
     */
    @Test
    void testGetApprovedConferences() {
        conference.setApprovalStatus("approved");
        when(conferenceMapper.selectApprovedConferences()).thenReturn(Arrays.asList(conference));
        
        List<Conference> conferences = conferenceService.getApprovedConferences();
        
        assertFalse(conferences.isEmpty());
        assertEquals("approved", conferences.get(0).getApprovalStatus());
    }

    /**
     * 正常情况：保存会议封面
     */
    @Test
    void testSaveCover() throws IOException {
        MockMultipartFile file = new MockMultipartFile(
            "file",
            "test.jpg",
            "image/jpeg",
            "test image content".getBytes()
        );
        
        String path = conferenceService.saveCover(file);
        
        assertNotNull(path);
        assertTrue(path.startsWith("/ConferenceCover/"));
        assertTrue(path.endsWith("_test.jpg"));
    }
} 