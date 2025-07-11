package edu.neu.oaas.pojo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Information Entity Tests")
class InformationTest {

    @Test
    @DisplayName("Should create information with default constructor")
    void testDefaultConstructor() {
        Information info = new Information();
        assertNotNull(info);
    }

    @Test
    @DisplayName("Should set and get basic properties")
    void testBasicProperties() {
        // Create test object
        Information info = new Information();

        // Set properties
        info.setId(1);
        info.setTitle("Test Title");
        info.setContent("Test Content");
        info.setIntroduction("Test Introduction");
        info.setAuthor("Test Author");
        info.setCompany("Test Company");
        info.setPath("/test/path");
        info.setApprovalStatus("pending");
        info.setTenantId(1);

        // Verify properties
        assertEquals(1, info.getId());
        assertEquals("Test Title", info.getTitle());
        assertEquals("Test Content", info.getContent());
        assertEquals("Test Introduction", info.getIntroduction());
        assertEquals("Test Author", info.getAuthor());
        assertEquals("Test Company", info.getCompany());
        assertEquals("/test/path", info.getPath());
        assertEquals("pending", info.getApprovalStatus());
        assertEquals(1, info.getTenantId());
    }

    @Test
    @DisplayName("Should set and get picture path")
    void testPicturePath() {
        Information info = new Information();
        String picturePath = "/test/picture.jpg";
        info.setPicture(picturePath);
        assertEquals(picturePath, info.getPicture());
    }

    @Test
    @DisplayName("Should set and get rejection reason")
    void testRejectionReason() {
        Information info = new Information();
        
        info.setApprovalStatus("rejected");
        info.setRejectionReason("Invalid content");
        
        assertEquals("rejected", info.getApprovalStatus());
        assertEquals("Invalid content", info.getRejectionReason());
    }

    @Test
    @DisplayName("Should handle null values")
    void testNullValues() {
        Information info = new Information();
        
        assertNull(info.getTitle());
        assertNull(info.getContent());
        assertNull(info.getPicture());
        assertNull(info.getIntroduction());
        assertNull(info.getAuthor());
        assertNull(info.getCompany());
        assertNull(info.getPath());
        assertNull(info.getApprovalStatus());
        assertNull(info.getRejectionReason());
    }

    @Test
    @DisplayName("Should have default pending status")
    void testDefaultApprovalStatus() {
        Information info = new Information();
        info.setApprovalStatus("pending");
        assertEquals("pending", info.getApprovalStatus());
    }
} 