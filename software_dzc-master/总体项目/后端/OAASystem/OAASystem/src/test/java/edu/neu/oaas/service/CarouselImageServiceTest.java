package edu.neu.oaas.service;

import edu.neu.oaas.mapper.CarouselImageMapper;
import edu.neu.oaas.pojo.CarouselImage;
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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Carousel Image Service Tests")
class CarouselImageServiceTest {

    @Mock
    private CarouselImageMapper carouselImageMapper;

    @InjectMocks
    private CarouselImageService carouselImageService;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        // Set the image base path to our temporary directory
        ReflectionTestUtils.setField(carouselImageService, "IMAGE_BASE_PATH", tempDir.toString() + "/");
    }

    private CarouselImage createSampleImage() {
        CarouselImage image = new CarouselImage();
        image.setId(1L);
        image.setName("test.jpg");
        image.setUrl("/images/test.jpg");
        return image;
    }

    @Nested
    @DisplayName("Image Upload Tests")
    class ImageUploadTests {

        @Test
        @DisplayName("Should successfully save image")
        void testSaveImage_Success() throws IOException {
            // Arrange
            MockMultipartFile file = new MockMultipartFile(
                "image", "test.jpg", "image/jpeg", "test image".getBytes()
            );
            doNothing().when(carouselImageMapper).insertImage(any());

            // Act
            CarouselImage result = carouselImageService.saveImage(file);

            // Assert
            assertNotNull(result);
            assertEquals("test.jpg", result.getName());
            assertTrue(result.getUrl().startsWith("/images/"));
            assertTrue(result.getUrl().endsWith("_test.jpg"));
            verify(carouselImageMapper).insertImage(any());
        }

        @Test
        @DisplayName("Should create directory if not exists")
        void testSaveImage_CreateDirectory() throws IOException {
            // Arrange
            MockMultipartFile file = new MockMultipartFile(
                "image", "test.jpg", "image/jpeg", "test image".getBytes()
            );
            doNothing().when(carouselImageMapper).insertImage(any());

            // Delete the temp directory to test directory creation
            Files.deleteIfExists(tempDir);

            // Act
            CarouselImage result = carouselImageService.saveImage(file);

            // Assert
            assertTrue(Files.exists(tempDir));
            assertNotNull(result);
            verify(carouselImageMapper).insertImage(any());
        }

        @Test
        @DisplayName("Should handle IO exception")
        void testSaveImage_IOError() {
            // Arrange
            MockMultipartFile file = new MockMultipartFile(
                "image", "test.jpg", "image/jpeg", "test image".getBytes()
            );
            // Make the directory read-only to cause an IO error
            tempDir.toFile().setWritable(false);

            // Act & Assert
            assertThrows(IOException.class, () -> carouselImageService.saveImage(file));
            verify(carouselImageMapper, never()).insertImage(any());

            // Cleanup
            tempDir.toFile().setWritable(true);
        }
    }

    @Nested
    @DisplayName("Image Retrieval Tests")
    class ImageRetrievalTests {

        @Test
        @DisplayName("Should get all images")
        void testGetAllImages_Success() {
            // Arrange
            List<CarouselImage> expectedImages = Arrays.asList(createSampleImage());
            when(carouselImageMapper.getAllImages()).thenReturn(expectedImages);

            // Act
            List<CarouselImage> result = carouselImageService.getAllImages();

            // Assert
            assertEquals(expectedImages, result);
            verify(carouselImageMapper).getAllImages();
        }

        @Test
        @DisplayName("Should return default image when no images exist")
        void testGetAllImages_Empty() {
            // Arrange
            when(carouselImageMapper.getAllImages()).thenReturn(new ArrayList<>());

            // Act
            List<CarouselImage> result = carouselImageService.getAllImages();

            // Assert
            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals("/images/banner/banner1.jpg", result.get(0).getUrl());
            assertEquals("默认轮播图", result.get(0).getName());
            verify(carouselImageMapper).getAllImages();
        }

        @Test
        @DisplayName("Should get image by ID")
        void testGetImageById_Success() {
            // Arrange
            CarouselImage expectedImage = createSampleImage();
            when(carouselImageMapper.getImageById(anyLong())).thenReturn(expectedImage);

            // Act
            CarouselImage result = carouselImageService.getImageById(1L);

            // Assert
            assertEquals(expectedImage, result);
            verify(carouselImageMapper).getImageById(1L);
        }

        @Test
        @DisplayName("Should return null when image not found")
        void testGetImageById_NotFound() {
            // Arrange
            when(carouselImageMapper.getImageById(anyLong())).thenReturn(null);

            // Act
            CarouselImage result = carouselImageService.getImageById(1L);

            // Assert
            assertNull(result);
            verify(carouselImageMapper).getImageById(1L);
        }
    }

    @Nested
    @DisplayName("Image Deletion Tests")
    class ImageDeletionTests {

        @Test
        @DisplayName("Should successfully delete image")
        void testDeleteImage_Success() throws IOException {
            // Arrange
            CarouselImage image = createSampleImage();
            when(carouselImageMapper.getImageById(anyLong())).thenReturn(image);
            doNothing().when(carouselImageMapper).deleteImage(anyLong());

            // Create a test file
            Path imagePath = tempDir.resolve("test.jpg");
            Files.write(imagePath, "test image".getBytes());

            // Act
            carouselImageService.deleteImage(1L);

            // Assert
            assertFalse(Files.exists(imagePath));
            verify(carouselImageMapper).getImageById(1L);
            verify(carouselImageMapper).deleteImage(1L);
        }

        @Test
        @DisplayName("Should handle non-existent image")
        void testDeleteImage_NonExistent() {
            // Arrange
            when(carouselImageMapper.getImageById(anyLong())).thenReturn(null);

            // Act
            carouselImageService.deleteImage(1L);

            // Assert
            verify(carouselImageMapper).getImageById(1L);
            verify(carouselImageMapper, never()).deleteImage(anyLong());
        }

        @Test
        @DisplayName("Should handle non-existent file")
        void testDeleteImage_NonExistentFile() {
            // Arrange
            CarouselImage image = createSampleImage();
            when(carouselImageMapper.getImageById(anyLong())).thenReturn(image);
            doNothing().when(carouselImageMapper).deleteImage(anyLong());

            // Act
            carouselImageService.deleteImage(1L);

            // Assert
            verify(carouselImageMapper).getImageById(1L);
            verify(carouselImageMapper).deleteImage(1L);
        }
    }
}