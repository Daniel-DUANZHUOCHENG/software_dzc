package edu.neu.oaas.service;

import edu.neu.oaas.mapper.CourseMapper;
import edu.neu.oaas.pojo.Course;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Course Service Tests")
class CourseServiceTest {

    @Mock
    private CourseMapper courseMapper;

    @InjectMocks
    private CourseService courseService;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        // Set the base paths to our temporary directory
        ReflectionTestUtils.setField(courseService, "COVER_BASE_PATH", tempDir.toString() + "/cover/");
        ReflectionTestUtils.setField(courseService, "VIDEO_BASE_PATH", tempDir.toString() + "/video/");
    }

    private Course createSampleCourse() {
        Course course = new Course();
        course.setCourseID(1);
        course.setCoursename("Test Course");
        course.setNumber("CS101");
        course.setOwner("John Doe");
        course.setCourseintro("Test Description");
        course.setApprovalStatus("pending");
        return course;
    }

    @Nested
    @DisplayName("Course CRUD Tests")
    class CourseCRUDTests {

        @Test
        @DisplayName("Should get all courses")
        void testGetAllCourses() {
            // Arrange
            List<Course> expectedCourses = Arrays.asList(createSampleCourse());
            when(courseMapper.selectAllCourses()).thenReturn(expectedCourses);

            // Act
            List<Course> result = courseService.getAllCourses();

            // Assert
            assertEquals(expectedCourses, result);
            verify(courseMapper).selectAllCourses();
        }

        @Test
        @DisplayName("Should search courses")
        void testSearchCourses() {
            // Arrange
            List<Course> expectedCourses = Arrays.asList(createSampleCourse());
            when(courseMapper.searchCourses(anyString(), anyString())).thenReturn(expectedCourses);

            // Act
            List<Course> result = courseService.searchCourses("Test", "CS101");

            // Assert
            assertEquals(expectedCourses, result);
            verify(courseMapper).searchCourses("Test", "CS101");
        }

        @Test
        @DisplayName("Should get course by ID")
        void testGetCourseById() {
            // Arrange
            Course expectedCourse = createSampleCourse();
            when(courseMapper.selectCourseById(anyInt())).thenReturn(expectedCourse);

            // Act
            Course result = courseService.getCourseById(1);

            // Assert
            assertEquals(expectedCourse, result);
            verify(courseMapper).selectCourseById(1);
        }

        @Test
        @DisplayName("Should add course")
        void testAddCourse() {
            // Arrange
            Course course = createSampleCourse();
            doReturn(1).when(courseMapper).insertCourse(any());

            // Act
            courseService.addCourse(course);

            // Assert
            verify(courseMapper).insertCourse(course);
        }

        @Test
        @DisplayName("Should update course")
        void testUpdateCourse() {
            // Arrange
            Course course = createSampleCourse();
            when(courseMapper.updateCourse(any())).thenReturn(1);

            // Act
            int result = courseService.updateCourse(course);

            // Assert
            assertEquals(1, result);
            verify(courseMapper).updateCourse(course);
        }

        @Test
        @DisplayName("Should delete course")
        void testDeleteCourse() {
            // Arrange
            doReturn(1).when(courseMapper).deleteCourse(anyInt());

            // Act
            courseService.deleteCourse(1);

            // Assert
            verify(courseMapper).deleteCourse(1);
        }
    }

    @Nested
    @DisplayName("Course Media Tests")
    class CourseMediaTests {

        @Test
        @DisplayName("Should save cover successfully")
        void testSaveCover_Success() throws IOException {
            // Arrange
            MockMultipartFile file = new MockMultipartFile(
                "cover", "test.jpg", "image/jpeg", "test image".getBytes()
            );

            // Act
            String result = courseService.saveCover(file);

            // Assert
            assertNotNull(result);
            assertTrue(result.startsWith("/CourseCover/"));
            assertTrue(result.endsWith("_test.jpg"));
        }

        @Test
        @DisplayName("Should create cover directory if not exists")
        void testSaveCover_CreateDirectory() throws IOException {
            // Arrange
            MockMultipartFile file = new MockMultipartFile(
                "cover", "test.jpg", "image/jpeg", "test image".getBytes()
            );
            Files.deleteIfExists(tempDir.resolve("cover"));

            // Act
            String result = courseService.saveCover(file);

            // Assert
            assertTrue(Files.exists(tempDir.resolve("cover")));
            assertNotNull(result);
            assertTrue(result.startsWith("/CourseCover/"));
        }

        @Test
        @DisplayName("Should handle cover IO exception")
        void testSaveCover_IOError() {
            // Arrange
            MockMultipartFile file = new MockMultipartFile(
                "cover", "test.jpg", "image/jpeg", "test image".getBytes()
            );
            tempDir.toFile().setWritable(false);

            // Act & Assert
            assertThrows(IOException.class, () -> courseService.saveCover(file));

            // Cleanup
            tempDir.toFile().setWritable(true);
        }

        @Test
        @DisplayName("Should save video successfully")
        void testSaveVideo_Success() throws IOException {
            // Arrange
            MockMultipartFile file = new MockMultipartFile(
                "video", "test.mp4", "video/mp4", "test video".getBytes()
            );

            // Act
            String result = courseService.saveVideo(file);

            // Assert
            assertNotNull(result);
            assertTrue(result.startsWith("/Video/"));
            assertTrue(result.endsWith("_test.mp4"));
            assertTrue(Files.exists(tempDir.resolve("video").resolve(result.substring(7))));
        }

        @Test
        @DisplayName("Should throw exception for empty video")
        void testSaveVideo_EmptyFile() {
            // Arrange
            MockMultipartFile file = new MockMultipartFile(
                "video", "test.mp4", "video/mp4", new byte[0]
            );

            // Act & Assert
            assertThrows(IOException.class, () -> courseService.saveVideo(file));
        }

        @Test
        @DisplayName("Should create video directory if not exists")
        void testSaveVideo_CreateDirectory() throws IOException {
            // Arrange
            MockMultipartFile file = new MockMultipartFile(
                "video", "test.mp4", "video/mp4", "test video".getBytes()
            );
            Files.deleteIfExists(tempDir.resolve("video"));

            // Act
            String result = courseService.saveVideo(file);

            // Assert
            assertTrue(Files.exists(tempDir.resolve("video")));
            assertNotNull(result);
            assertTrue(result.startsWith("/Video/"));
        }

        @Test
        @DisplayName("Should handle video IO exception")
        void testSaveVideo_IOError() {
            // Arrange
            MockMultipartFile file = new MockMultipartFile(
                "video", "test.mp4", "video/mp4", "test video".getBytes()
            );
            tempDir.toFile().setWritable(false);

            // Act & Assert
            assertThrows(IOException.class, () -> courseService.saveVideo(file));

            // Cleanup
            tempDir.toFile().setWritable(true);
        }
    }

    @Nested
    @DisplayName("Course Approval Tests")
    class CourseApprovalTests {

        @Test
        @DisplayName("Should get pending courses")
        void testGetPendingCourses() {
            // Arrange
            List<Course> expectedCourses = Arrays.asList(createSampleCourse());
            when(courseMapper.selectPendingCourses()).thenReturn(expectedCourses);

            // Act
            List<Course> result = courseService.getPendingCourses();

            // Assert
            assertEquals(expectedCourses, result);
            verify(courseMapper).selectPendingCourses();
        }

        @Test
        @DisplayName("Should get courses by approval status")
        void testGetCoursesByApprovalStatus() {
            // Arrange
            List<Course> expectedCourses = Arrays.asList(createSampleCourse());
            when(courseMapper.selectCoursesByApprovalStatus(anyString()))
                .thenReturn(expectedCourses);

            // Act
            List<Course> result = courseService.getCoursesByApprovalStatus("approved");

            // Assert
            assertEquals(expectedCourses, result);
            verify(courseMapper).selectCoursesByApprovalStatus("approved");
        }

        @Test
        @DisplayName("Should approve course")
        void testApproveCourse() {
            // Arrange
            when(courseMapper.approveCourse(anyInt(), anyString(), anyString()))
                .thenReturn(1);

            // Act
            int result = courseService.approveCourse(1, "approved", null);

            // Assert
            assertEquals(1, result);
            verify(courseMapper).approveCourse(1, "approved", null);
        }

        @Test
        @DisplayName("Should reject course with reason")
        void testRejectCourse() {
            // Arrange
            when(courseMapper.approveCourse(anyInt(), anyString(), anyString()))
                .thenReturn(1);

            // Act
            int result = courseService.approveCourse(1, "rejected", "Invalid request");

            // Assert
            assertEquals(1, result);
            verify(courseMapper).approveCourse(1, "rejected", "Invalid request");
        }

        @Test
        @DisplayName("Should get approved courses")
        void testGetApprovedCourses() {
            // Arrange
            List<Course> expectedCourses = Arrays.asList(createSampleCourse());
            when(courseMapper.selectApprovedCourses()).thenReturn(expectedCourses);

            // Act
            List<Course> result = courseService.getApprovedCourses();

            // Assert
            assertEquals(expectedCourses, result);
            verify(courseMapper).selectApprovedCourses();
        }
    }
} 