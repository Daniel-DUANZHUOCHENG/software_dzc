package edu.neu.oaas.pojo;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ConferenceTest {

    /**
     * 测试默认构造函数
     */
    @Test
    void testDefaultConstructor() {
        Conference conference = new Conference();
        assertEquals("pending", conference.getApprovalStatus());
    }

    /**
     * 测试带参数构造函数
     */
    @Test
    void testParameterizedConstructor() {
        Conference conference = new Conference(1, "Test Conference", "admin", 
            "Room 101", "cover.jpg", "content.txt", "2024-03-20 10:00", 
            "2024-03-20 12:00", "Active", 1);

        assertEquals(Integer.valueOf(1), conference.getConferenceID());
        assertEquals("Test Conference", conference.getConferencename());
        assertEquals("admin", conference.getCreator());
        assertEquals("Room 101", conference.getSituation());
        assertEquals("cover.jpg", conference.getCoverpath());
        assertEquals("content.txt", conference.getContentspath());
        assertEquals("2024-03-20 10:00", conference.getStarttime());
        assertEquals("2024-03-20 12:00", conference.getEndtime());
        assertEquals("Active", conference.getState());
        assertEquals(Integer.valueOf(1), conference.getTenantID());
        assertEquals("pending", conference.getApprovalStatus());
    }

    /**
     * 测试所有属性的Getter和Setter
     */
    @Test
    void testGettersAndSetters() {
        Conference conference = new Conference();

        conference.setConferenceID(1);
        assertEquals(Integer.valueOf(1), conference.getConferenceID());

        conference.setConferencename("Test Conference");
        assertEquals("Test Conference", conference.getConferencename());

        conference.setCreator("admin");
        assertEquals("admin", conference.getCreator());

        conference.setSituation("Room 101");
        assertEquals("Room 101", conference.getSituation());

        conference.setCoverpath("cover.jpg");
        assertEquals("cover.jpg", conference.getCoverpath());

        conference.setContentspath("content.txt");
        assertEquals("content.txt", conference.getContentspath());

        conference.setStarttime("2024-03-20 10:00");
        assertEquals("2024-03-20 10:00", conference.getStarttime());

        conference.setEndtime("2024-03-20 12:00");
        assertEquals("2024-03-20 12:00", conference.getEndtime());

        conference.setState("Active");
        assertEquals("Active", conference.getState());

        conference.setTenantID(1);
        assertEquals(Integer.valueOf(1), conference.getTenantID());

        conference.setApprovalStatus("approved");
        assertEquals("approved", conference.getApprovalStatus());

        conference.setRejectionReason("Schedule conflict");
        assertEquals("Schedule conflict", conference.getRejectionReason());
    }

    /**
     * 测试边界值
     */
    @Test
    void testBoundaryValues() {
        Conference conference = new Conference();

        // 测试空字符串
        conference.setConferencename("");
        assertEquals("", conference.getConferencename());

        conference.setCreator("");
        assertEquals("", conference.getCreator());

        // 测试null值
        conference.setConferenceID(null);
        assertNull(conference.getConferenceID());

        conference.setTenantID(null);
        assertNull(conference.getTenantID());

        conference.setRejectionReason(null);
        assertNull(conference.getRejectionReason());
    }
} 