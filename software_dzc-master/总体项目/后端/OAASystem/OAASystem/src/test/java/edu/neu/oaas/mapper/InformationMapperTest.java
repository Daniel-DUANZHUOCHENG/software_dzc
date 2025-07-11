package edu.neu.oaas.mapper;

import edu.neu.oaas.pojo.Information;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@DisplayName("Information Mapper Tests")
class InformationMapperTest {

    @Autowired
    private InformationMapper informationMapper;

    private Information createTestInformation(String title, String author, String path) {
        Information info = new Information();
        info.setTitle(title);
        info.setPicture("/test/picture.jpg");
        info.setContent("Test content");
        info.setIntroduction("Test introduction");
        info.setAuthor(author);
        info.setCompany("Test Company");
        info.setTenantId(1);
        info.setPath(path);
        info.setApprovalStatus("pending");
        return info;
    }

    @Test
    @DisplayName("Should insert and retrieve information")
    void testInsertAndRetrieveInformation() {
        // Create test information
        Information info = createTestInformation("Test Information", "Test Author", "/test/path");

        // Insert information
        assertDoesNotThrow(() -> informationMapper.insertInformation(info));

        // Retrieve all information
        List<Information> allInfo = informationMapper.getAllInformation();
        assertFalse(allInfo.isEmpty());

        // Find the inserted information
        Information found = allInfo.stream()
                .filter(i -> i.getTitle().equals("Test Information"))
                .findFirst()
                .orElse(null);

        // Verify information details
        assertNotNull(found);
        assertEquals("Test Author", found.getAuthor());
        assertEquals("Test Company", found.getCompany());
        assertEquals("/test/path", found.getPath());
        assertEquals("pending", found.getApprovalStatus());
    }

    @Test
    @DisplayName("Should update information")
    void testUpdateInformation() {
        // Create and insert test information
        Information info = createTestInformation("Initial Title", "Initial Author", "/test/path");
        assertDoesNotThrow(() -> informationMapper.insertInformation(info));

        // Get the inserted information
        List<Information> allInfo = informationMapper.getAllInformation();
        Information toUpdate = allInfo.stream()
                .filter(i -> i.getTitle().equals("Initial Title"))
                .findFirst()
                .orElse(null);

        assertNotNull(toUpdate);

        // Update information
        toUpdate.setTitle("Updated Title");
        toUpdate.setContent("Updated content");
        toUpdate.setAuthor("Updated Author");
        int result = informationMapper.updateInformation(toUpdate);

        // Verify update
        assertEquals(1, result);
        Information updated = informationMapper.getInformationById(toUpdate.getId());
        assertEquals("Updated Title", updated.getTitle());
        assertEquals("Updated content", updated.getContent());
        assertEquals("Updated Author", updated.getAuthor());
    }

    @Test
    @DisplayName("Should handle approval workflow")
    void testApprovalWorkflow() {
        // Create and insert test information
        Information info = createTestInformation("Approval Test", "Test Author", "/test/path");
        assertDoesNotThrow(() -> informationMapper.insertInformation(info));

        // Get pending information
        List<Information> pendingInfo = informationMapper.selectPendingInformation();
        assertFalse(pendingInfo.isEmpty());
        assertTrue(pendingInfo.stream()
                .anyMatch(i -> i.getTitle().equals("Approval Test")));

        // Get the information to approve
        Information toApprove = pendingInfo.stream()
                .filter(i -> i.getTitle().equals("Approval Test"))
                .findFirst()
                .orElse(null);

        assertNotNull(toApprove);

        // Approve information
        int result = informationMapper.approveInformation(
                toApprove.getId(),
                "approved",
                null
        );

        assertEquals(1, result);

        // Verify approval
        List<Information> approvedInfo = informationMapper.selectInformationByApprovalStatus("approved");
        assertTrue(approvedInfo.stream()
                .anyMatch(i -> i.getTitle().equals("Approval Test")));
    }

    @Test
    @DisplayName("Should search by path prefix")
    void testSearchByPathPrefix() {
        // Create and insert test information
        Information info1 = createTestInformation("Test 1", "Author 1", "/test/path1");
        Information info2 = createTestInformation("Test 2", "Author 2", "/test/path2");
        Information info3 = createTestInformation("Test 3", "Author 3", "/other/path");

        assertDoesNotThrow(() -> {
            informationMapper.insertInformation(info1);
            informationMapper.insertInformation(info2);
            informationMapper.insertInformation(info3);
        });

        // Search by path prefix
        List<Information> testPathInfo = informationMapper.findByPathPrefix("/test");

        // Verify
        assertEquals(2, testPathInfo.size());
        assertTrue(testPathInfo.stream().allMatch(i -> i.getPath().startsWith("/test")));
    }

    @Test
    @DisplayName("Should delete information")
    void testDeleteInformation() {
        // Create and insert test information
        Information info = createTestInformation("To Delete", "Test Author", "/test/path");
        assertDoesNotThrow(() -> informationMapper.insertInformation(info));

        // Get the inserted information
        List<Information> allInfo = informationMapper.getAllInformation();
        Information toDelete = allInfo.stream()
                .filter(i -> i.getTitle().equals("To Delete"))
                .findFirst()
                .orElse(null);

        assertNotNull(toDelete);

        // Delete information
        assertDoesNotThrow(() -> informationMapper.deleteInformation(toDelete.getId()));

        // Verify deletion
        Information deleted = informationMapper.getInformationById(toDelete.getId());
        assertNull(deleted);
    }

    @Test
    @DisplayName("Should search by title and author")
    void testSearchByTitleAndAuthor() {
        // Create and insert test information
        Information info1 = createTestInformation("Spring Guide", "John", "/docs/spring");
        Information info2 = createTestInformation("Spring Tutorial", "Jane", "/docs/spring");
        Information info3 = createTestInformation("Java Guide", "John", "/docs/java");

        assertDoesNotThrow(() -> {
            informationMapper.insertInformation(info1);
            informationMapper.insertInformation(info2);
            informationMapper.insertInformation(info3);
        });

        // Search by title
        List<Information> springDocs = informationMapper.searchByTitleAndAuthor("/docs", "Spring", null);
        assertEquals(2, springDocs.size());

        // Search by author
        List<Information> johnDocs = informationMapper.searchByTitleAndAuthor("/docs", null, "John");
        assertEquals(2, johnDocs.size());

        // Search by both
        List<Information> springJohnDocs = informationMapper.searchByTitleAndAuthor("/docs", "Spring", "John");
        assertEquals(1, springJohnDocs.size());
    }

    @Test
    @DisplayName("Should delete by path prefix")
    void testDeleteByPathPrefix() {
        // Create and insert test information
        Information info1 = createTestInformation("Test 1", "Author 1", "/test/path1");
        Information info2 = createTestInformation("Test 2", "Author 2", "/test/path2");
        Information info3 = createTestInformation("Test 3", "Author 3", "/other/path");

        assertDoesNotThrow(() -> {
            informationMapper.insertInformation(info1);
            informationMapper.insertInformation(info2);
            informationMapper.insertInformation(info3);
        });

        // Delete by path prefix
        assertDoesNotThrow(() -> informationMapper.deleteByPathPrefix("/test"));

        // Verify deletion
        List<Information> remainingInfo = informationMapper.getAllInformation();
        assertEquals(1, remainingInfo.size());
        assertTrue(remainingInfo.stream().allMatch(i -> i.getPath().startsWith("/other")));
    }
}