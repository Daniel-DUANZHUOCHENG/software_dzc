package edu.neu.oaas.service;

import edu.neu.oaas.mapper.MeetingApplicationMapper;
import edu.neu.oaas.pojo.MeetingApplication;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Meeting Application Service Tests")
class MeetingApplicationServiceTest {

    @Mock
    private MeetingApplicationMapper meetingApplicationMapper;

    @InjectMocks
    private MeetingApplicationService meetingApplicationService;

    private MeetingApplication createSampleApplication() {
        MeetingApplication application = new MeetingApplication();
        application.setId(1);
        application.setMeetingId(1);
        application.setApplicantId(1);
        application.setApplicantName("John Doe");
        application.setStatus("pending");
        application.setApplicationTime(LocalDateTime.now());
        return application;
    }

    @Nested
    @DisplayName("Submit Application Tests")
    class SubmitApplicationTests {

        @Test
        @DisplayName("Should successfully submit new application")
        void testSubmitMeetingApplication_Success() {
            // Arrange
            MeetingApplication application = createSampleApplication();
            when(meetingApplicationMapper.checkExistingApplication(anyInt(), anyInt())).thenReturn(0);
            when(meetingApplicationMapper.insertMeetingApplication(any())).thenReturn(1);

            // Act
            int result = meetingApplicationService.submitMeetingApplication(application);

            // Assert
            assertEquals(1, result);
            assertNotNull(application.getApplicationTime());
            assertEquals("pending", application.getStatus());
            verify(meetingApplicationMapper).insertMeetingApplication(application);
        }

        @Test
        @DisplayName("Should throw exception for duplicate application")
        void testSubmitMeetingApplication_DuplicateApplication() {
            // Arrange
            MeetingApplication application = createSampleApplication();
            when(meetingApplicationMapper.checkExistingApplication(anyInt(), anyInt())).thenReturn(1);

            // Act & Assert
            assertThrows(RuntimeException.class, () -> 
                meetingApplicationService.submitMeetingApplication(application));
            verify(meetingApplicationMapper, never()).insertMeetingApplication(any());
        }
    }

    @Nested
    @DisplayName("Application Status Update Tests")
    class ApplicationStatusUpdateTests {

        @Test
        @DisplayName("Should successfully update application status")
        void testUpdateApplicationStatus_Success() {
            // Arrange
            when(meetingApplicationMapper.updateMeetingApplication(any())).thenReturn(1);

            // Act
            int result = meetingApplicationService.updateApplicationStatus(1, "approved", null, 2, "Admin");

            // Assert
            assertEquals(1, result);
            verify(meetingApplicationMapper).updateMeetingApplication(argThat(app -> {
                return app.getId() == 1 &&
                       "approved".equals(app.getStatus()) &&
                       app.getApproverId() == 2 &&
                       "Admin".equals(app.getApproverName()) &&
                       app.getApprovalTime() != null;
            }));
        }

        @Test
        @DisplayName("Should successfully update application status with rejection reason")
        void testUpdateApplicationStatus_WithRejectionReason() {
            // Arrange
            when(meetingApplicationMapper.updateMeetingApplication(any())).thenReturn(1);

            // Act
            int result = meetingApplicationService.updateApplicationStatus(1, "rejected", "Invalid request", 2, "Admin");

            // Assert
            assertEquals(1, result);
            verify(meetingApplicationMapper).updateMeetingApplication(argThat(app -> {
                return app.getId() == 1 &&
                       "rejected".equals(app.getStatus()) &&
                       "Invalid request".equals(app.getRejectionReason());
            }));
        }
    }

    @Nested
    @DisplayName("Batch Operations Tests")
    class BatchOperationsTests {

        @Test
        @DisplayName("Should successfully batch update application status")
        void testBatchUpdateApplicationStatus_Success() {
            // Arrange
            List<Integer> applicationIds = Arrays.asList(1, 2, 3);
            when(meetingApplicationMapper.updateMeetingApplication(any())).thenReturn(1);

            // Act
            int result = meetingApplicationService.batchUpdateApplicationStatus(applicationIds, "approved", 1, "Admin");

            // Assert
            assertEquals(3, result);
            verify(meetingApplicationMapper, times(3)).updateMeetingApplication(any());
        }
    }

    @Nested
    @DisplayName("Statistics Tests")
    class StatisticsTests {

        @Test
        @DisplayName("Should successfully get application statistics")
        void testGetApplicationStatistics_Success() {
            // Arrange
            when(meetingApplicationMapper.getTenantApplicationCountByStatus(eq(1), eq("pending"))).thenReturn(5);
            when(meetingApplicationMapper.getTenantApplicationCountByStatus(eq(1), eq("approved"))).thenReturn(10);
            when(meetingApplicationMapper.getTenantApplicationCountByStatus(eq(1), eq("rejected"))).thenReturn(3);

            // Act
            Map<String, Object> stats = meetingApplicationService.getApplicationStatistics(1);

            // Assert
            assertEquals(5, stats.get("pendingCount"));
            assertEquals(10, stats.get("approvedCount"));
            assertEquals(3, stats.get("rejectedCount"));
            assertEquals(18, stats.get("totalCount"));
        }
    }

    @Nested
    @DisplayName("Query Operations Tests")
    class QueryOperationsTests {

        @Test
        @DisplayName("Should successfully get meeting applications")
        void testGetMeetingApplications_Success() {
            // Arrange
            List<MeetingApplication> expectedApplications = Arrays.asList(createSampleApplication());
            when(meetingApplicationMapper.getMeetingApplications(anyInt())).thenReturn(expectedApplications);

            // Act
            List<MeetingApplication> result = meetingApplicationService.getMeetingApplications(1);

            // Assert
            assertEquals(expectedApplications, result);
            verify(meetingApplicationMapper).getMeetingApplications(1);
        }

        @Test
        @DisplayName("Should successfully get tenant meeting applications")
        void testGetMeetingApplicationsByTenant_Success() {
            // Arrange
            List<MeetingApplication> expectedApplications = Arrays.asList(createSampleApplication());
            when(meetingApplicationMapper.getMeetingApplicationsByTenant(anyInt(), anyInt())).thenReturn(expectedApplications);

            // Act
            List<MeetingApplication> result = meetingApplicationService.getMeetingApplicationsByTenant(1, 1);

            // Assert
            assertEquals(expectedApplications, result);
            verify(meetingApplicationMapper).getMeetingApplicationsByTenant(1, 1);
        }

        @Test
        @DisplayName("Should successfully get application by ID")
        void testGetMeetingApplicationById_Success() {
            // Arrange
            MeetingApplication expectedApplication = createSampleApplication();
            when(meetingApplicationMapper.getMeetingApplicationById(anyInt())).thenReturn(expectedApplication);

            // Act
            MeetingApplication result = meetingApplicationService.getMeetingApplicationById(1);

            // Assert
            assertEquals(expectedApplication, result);
            verify(meetingApplicationMapper).getMeetingApplicationById(1);
        }

        @Test
        @DisplayName("Should successfully get user applications")
        void testGetUserApplications_Success() {
            // Arrange
            List<MeetingApplication> expectedApplications = Arrays.asList(createSampleApplication());
            when(meetingApplicationMapper.getUserApplications(anyInt())).thenReturn(expectedApplications);

            // Act
            List<MeetingApplication> result = meetingApplicationService.getUserApplications(1);

            // Assert
            assertEquals(expectedApplications, result);
            verify(meetingApplicationMapper).getUserApplications(1);
        }

        @Test
        @DisplayName("Should successfully get tenant pending applications")
        void testGetTenantPendingApplications_Success() {
            // Arrange
            List<MeetingApplication> expectedApplications = Arrays.asList(createSampleApplication());
            when(meetingApplicationMapper.getTenantPendingApplications(anyInt())).thenReturn(expectedApplications);

            // Act
            List<MeetingApplication> result = meetingApplicationService.getTenantPendingApplications(1);

            // Assert
            assertEquals(expectedApplications, result);
            verify(meetingApplicationMapper).getTenantPendingApplications(1);
        }

        @Test
        @DisplayName("Should successfully get all tenant applications")
        void testGetTenantAllApplications_Success() {
            // Arrange
            List<MeetingApplication> expectedApplications = Arrays.asList(createSampleApplication());
            when(meetingApplicationMapper.getTenantAllApplications(anyInt())).thenReturn(expectedApplications);

            // Act
            List<MeetingApplication> result = meetingApplicationService.getTenantAllApplications(1);

            // Assert
            assertEquals(expectedApplications, result);
            verify(meetingApplicationMapper).getTenantAllApplications(1);
        }
    }

    @Nested
    @DisplayName("Delete Operations Tests")
    class DeleteOperationsTests {

        @Test
        @DisplayName("Should successfully delete application")
        void testDeleteMeetingApplication_Success() {
            // Arrange
            when(meetingApplicationMapper.deleteMeetingApplication(anyInt())).thenReturn(1);

            // Act
            int result = meetingApplicationService.deleteMeetingApplication(1);

            // Assert
            assertEquals(1, result);
            verify(meetingApplicationMapper).deleteMeetingApplication(1);
        }
    }

    @Nested
    @DisplayName("Application Check Tests")
    class ApplicationCheckTests {

        @Test
        @DisplayName("Should return true when application exists")
        void testHasAppliedForMeeting_Exists() {
            // Arrange
            when(meetingApplicationMapper.checkExistingApplication(anyInt(), anyInt())).thenReturn(1);

            // Act
            boolean result = meetingApplicationService.hasAppliedForMeeting(1, 1);

            // Assert
            assertTrue(result);
            verify(meetingApplicationMapper).checkExistingApplication(1, 1);
        }

        @Test
        @DisplayName("Should return false when application does not exist")
        void testHasAppliedForMeeting_NotExists() {
            // Arrange
            when(meetingApplicationMapper.checkExistingApplication(anyInt(), anyInt())).thenReturn(0);

            // Act
            boolean result = meetingApplicationService.hasAppliedForMeeting(1, 1);

            // Assert
            assertFalse(result);
            verify(meetingApplicationMapper).checkExistingApplication(1, 1);
        }
    }
} 