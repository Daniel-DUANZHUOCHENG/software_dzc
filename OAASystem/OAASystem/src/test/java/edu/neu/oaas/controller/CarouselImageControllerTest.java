package edu.neu.oaas.controller;

import edu.neu.oaas.pojo.CarouselImage;
import edu.neu.oaas.service.CarouselImageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class CarouselImageControllerTest {

    @Mock
    private CarouselImageService carouselImageService;

    @InjectMocks
    private CarouselImageController carouselImageController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(carouselImageController).build();
    }

    @Test
    void testUploadImage_Success() throws Exception {
        // 准备测试数据
        MockMultipartFile file = new MockMultipartFile(
            "file",
            "test-image.jpg",
            MediaType.IMAGE_JPEG_VALUE,
            "test image content".getBytes()
        );

        CarouselImage savedImage = new CarouselImage();
        savedImage.setId(1L);
        savedImage.setName("test-image.jpg");
        savedImage.setUrl("/images/test-image.jpg");

        when(carouselImageService.saveImage(any())).thenReturn(savedImage);

        // 执行测试并验证结果
        mockMvc.perform(multipart("/carousel/upload")
                .file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.fileName").value("test-image.jpg"))
                .andExpect(jsonPath("$.filePath").value("/images/test-image.jpg"));

        verify(carouselImageService).saveImage(any());
    }

    @Test
    void testUploadImage_Error() throws Exception {
        // 准备测试数据
        MockMultipartFile file = new MockMultipartFile(
            "file",
            "test-image.jpg",
            MediaType.IMAGE_JPEG_VALUE,
            "test image content".getBytes()
        );

        when(carouselImageService.saveImage(any())).thenThrow(new IOException("Failed to save image"));

        // 执行测试并验证结果
        mockMvc.perform(multipart("/carousel/upload")
                .file(file))
                .andExpect(status().isInternalServerError());

        verify(carouselImageService).saveImage(any());
    }

    @Test
    void testGetAllImages_Success() throws Exception {
        // 准备测试数据
        CarouselImage image1 = new CarouselImage();
        image1.setId(1L);
        image1.setName("image1.jpg");
        image1.setUrl("/images/image1.jpg");

        CarouselImage image2 = new CarouselImage();
        image2.setId(2L);
        image2.setName("image2.jpg");
        image2.setUrl("/images/image2.jpg");

        List<CarouselImage> images = Arrays.asList(image1, image2);

        when(carouselImageService.getAllImages()).thenReturn(images);

        // 执行测试并验证结果
        mockMvc.perform(get("/carousel/images"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].fileName").value("image1.jpg"))
                .andExpect(jsonPath("$[0].filePath").value("/images/image1.jpg"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].fileName").value("image2.jpg"))
                .andExpect(jsonPath("$[1].filePath").value("/images/image2.jpg"));

        verify(carouselImageService).getAllImages();
    }

    @Test
    void testGetImageById_Success() throws Exception {
        // 准备测试数据
        CarouselImage image = new CarouselImage();
        image.setId(1L);
        image.setName("test-image.jpg");
        image.setUrl("/images/test-image.jpg");

        when(carouselImageService.getImageById(1L)).thenReturn(image);

        // 执行测试并验证结果
        mockMvc.perform(get("/carousel/image/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.fileName").value("test-image.jpg"))
                .andExpect(jsonPath("$.filePath").value("/images/test-image.jpg"));

        verify(carouselImageService).getImageById(1L);
    }

    @Test
    void testGetImageById_NotFound() throws Exception {
        // 模拟服务层返回空
        when(carouselImageService.getImageById(1L)).thenReturn(null);

        // 执行测试并验证结果
        mockMvc.perform(get("/carousel/image/1"))
                .andExpect(status().isNotFound());

        verify(carouselImageService).getImageById(1L);
    }

    @Test
    void testDeleteImage_Success() throws Exception {
        // 执行测试并验证结果
        mockMvc.perform(delete("/carousel/image/1"))
                .andExpect(status().isOk());

        verify(carouselImageService).deleteImage(1L);
    }

    @Test
    void testDeleteImage_Error() throws Exception {
        // 模拟服务层抛出异常
        doThrow(new RuntimeException("Failed to delete image")).when(carouselImageService).deleteImage(1L);

        // 执行测试并验证结果
        mockMvc.perform(delete("/carousel/image/1"))
                .andExpect(status().isOk());

        verify(carouselImageService).deleteImage(1L);
    }
} 