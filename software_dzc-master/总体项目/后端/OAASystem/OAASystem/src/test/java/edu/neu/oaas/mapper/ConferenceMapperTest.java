package edu.neu.oaas.mapper;

import edu.neu.oaas.pojo.Conference;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@DisplayName("Conference Mapper Tests")
class ConferenceMapperTest {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Autowired
    private ConferenceMapper conferenceMapper;

    private Conference createTestConference(String name, String creator) {
        Conference conference = new Conference();
        conference.setConferencename(name);
        conference.setCreator(creator);
        conference.setState("active");
        conference.setStarttime(LocalDateTime.now().format(FORMATTER));
        conference.setEndtime(LocalDateTime.now().plusHours(2).format(FORMATTER));
        conference.setTenantID(1);
        conference.setApprovalStatus("pending");
        return conference;
    }

    @Test
    @DisplayName("Should create and retrieve conference")
    void testCreateAndRetrieveConference() {
        // Create test conference
        Conference conference = createTestConference("Test Conference", "John Doe");
        
        // Insert conference
        assertDoesNotThrow(() -> conferenceMapper.insertConference(conference));

        // Retrieve all conferences
        List<Conference> conferences = conferenceMapper.getAllConferences();
        assertFalse(conferences.isEmpty());

        // Find the inserted conference
        Conference found = conferences.stream()
                .filter(c -> c.getConferencename().equals("Test Conference"))
                .findFirst()
                .orElse(null);

        // Verify conference details
        assertNotNull(found);
        assertEquals("John Doe", found.getCreator());
        assertEquals("active", found.getState());
        assertEquals("pending", found.getApprovalStatus());
    }

    @Test
    @DisplayName("Should update conference")
    void testUpdateConference() {
        // Create and insert test conference
        Conference conference = createTestConference("Initial Conference", "John Doe");
        assertDoesNotThrow(() -> conferenceMapper.insertConference(conference));

        // Get the inserted conference
        List<Conference> conferences = conferenceMapper.getAllConferences();
        Conference toUpdate = conferences.stream()
                .filter(c -> c.getConferencename().equals("Initial Conference"))
                .findFirst()
                .orElse(null);

        assertNotNull(toUpdate);

        // Update conference
        toUpdate.setConferencename("Updated Conference");
        toUpdate.setCreator("Jane Doe");
        toUpdate.setState("inactive");
        int result = conferenceMapper.updateConference(toUpdate);

        // Verify update
        assertEquals(1, result);
        Conference updated = conferenceMapper.getConferenceById(toUpdate.getConferenceID());
        assertEquals("Updated Conference", updated.getConferencename());
        assertEquals("Jane Doe", updated.getCreator());
        assertEquals("inactive", updated.getState());
    }

    @Test
    @DisplayName("Should handle approval workflow")
    void testApprovalWorkflow() {
        // Create and insert test conference
        Conference conference = createTestConference("Approval Test", "John Doe");
        assertDoesNotThrow(() -> conferenceMapper.insertConference(conference));

        // Get pending conferences
        List<Conference> pendingConferences = conferenceMapper.selectPendingConferences();
        assertFalse(pendingConferences.isEmpty());
        assertTrue(pendingConferences.stream()
                .anyMatch(c -> c.getConferencename().equals("Approval Test")));

        // Get the conference to approve
        Conference toApprove = pendingConferences.stream()
                .filter(c -> c.getConferencename().equals("Approval Test"))
                .findFirst()
                .orElse(null);

        assertNotNull(toApprove);

        // Approve conference
        int result = conferenceMapper.approveConference(
                toApprove.getConferenceID(),
                "approved",
                null
        );

        assertEquals(1, result);

        // Verify approval
        List<Conference> approvedConferences = conferenceMapper.selectApprovedConferences();
        assertTrue(approvedConferences.stream()
                .anyMatch(c -> c.getConferencename().equals("Approval Test")));

        // Verify by approval status
        List<Conference> approvedStatusConferences = conferenceMapper.selectConferencesByApprovalStatus("approved");
        assertTrue(approvedStatusConferences.stream()
                .anyMatch(c -> c.getConferencename().equals("Approval Test")));
    }

    @Test
    @DisplayName("Should search conferences")
    void testSearchConferences() {
        // Create and insert test conferences
        Conference conf1 = createTestConference("Spring Meeting", "John");
        Conference conf2 = createTestConference("Spring Workshop", "Jane");
        Conference conf3 = createTestConference("Summer Meeting", "John");

        assertDoesNotThrow(() -> {
            conferenceMapper.insertConference(conf1);
            conferenceMapper.insertConference(conf2);
            conferenceMapper.insertConference(conf3);
        });

        // Search by name
        List<Conference> springConferences = conferenceMapper.searchConferences("Spring", null, null);
        assertEquals(2, springConferences.size());

        // Search by creator
        List<Conference> johnConferences = conferenceMapper.searchConferences(null, "John", null);
        assertEquals(2, johnConferences.size());

        // Search by date
        String today = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        List<Conference> todayConferences = conferenceMapper.searchConferences(null, null, today);
        assertEquals(3, todayConferences.size());
    }

    @Test
    @DisplayName("Should delete conference")
    void testDeleteConference() {
        // Create and insert test conference
        Conference conference = createTestConference("To Delete", "John Doe");
        assertDoesNotThrow(() -> conferenceMapper.insertConference(conference));

        // Get the inserted conference
        List<Conference> conferences = conferenceMapper.getAllConferences();
        Conference toDelete = conferences.stream()
                .filter(c -> c.getConferencename().equals("To Delete"))
                .findFirst()
                .orElse(null);

        assertNotNull(toDelete);

        // Delete conference
        assertDoesNotThrow(() -> conferenceMapper.deleteConference(toDelete.getConferenceID()));

        // Verify deletion
        Conference deleted = conferenceMapper.getConferenceById(toDelete.getConferenceID());
        assertNull(deleted);
    }
}