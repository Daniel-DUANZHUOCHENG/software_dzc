package edu.neu.oaas.service;

import edu.neu.oaas.mapper.InformationMapper;
import edu.neu.oaas.pojo.Information;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Information Service Tests")
class InformationServiceTest {

    @Mock
    private InformationMapper informationMapper;

    @InjectMocks
    private InformationService informationService;

    @Test
    @DisplayName("Should create information")
    void testCreateInformation() {
        // Prepare test data
        Information info = new Information();
        info.setTitle("Test Title");
        info.setContent("Test Content");
        info.setAuthor("Test Author");
        info.setPath("/test/path");

        doNothing().when(informationMapper).insertInformation(any(Information.class));

        // Execute test
        informationService.insertInformation(info);

        // Verify
        verify(informationMapper).insertInformation(info);
        assertEquals("pending", info.getApprovalStatus());
    }

    @Test
    @DisplayName("Should get information by ID")
    void testGetInformationById() {
        // Prepare test data
        Information expected = new Information();
        expected.setId(1);
        expected.setTitle("Test Title");

        when(informationMapper.getInformationById(1)).thenReturn(expected);

        // Execute test
        Information result = informationService.getInformationById(1);

        // Verify
        assertNotNull(result);
        assertEquals(expected.getTitle(), result.getTitle());
        verify(informationMapper).getInformationById(1);
    }

    @Test
    @DisplayName("Should approve information")
    void testApproveInformation() {
        // Prepare test data
        int infoId = 1;
        String status = "approved";
        String remarks = "Approved by test";

        when(informationMapper.approveInformation(infoId, status, remarks)).thenReturn(1);

        // Execute test
        informationService.approveInformation(infoId, status, remarks);

        // Verify
        verify(informationMapper).approveInformation(infoId, status, remarks);
    }

    @Test
    @DisplayName("Should search information")
    void testSearchInformation() {
        // Prepare test data
        String prefix = "/test";
        String title = "Test";
        String author = "Author";

        Information info = new Information();
        info.setTitle("Test Title");
        List<Information> expected = Arrays.asList(info);

        when(informationMapper.searchByTitleAndAuthor(prefix, title, author)).thenReturn(expected);

        // Execute test
        List<Information> result = informationService.searchByTitleAndAuthor(prefix, title, author);

        // Verify
        assertFalse(result.isEmpty());
        assertEquals(expected.size(), result.size());
        verify(informationMapper).searchByTitleAndAuthor(prefix, title, author);
    }
}