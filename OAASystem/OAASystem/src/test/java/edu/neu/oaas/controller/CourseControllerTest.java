package edu.neu.oaas.controller;

import edu.neu.oaas.pojo.Course;
import edu.neu.oaas.service.CourseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class CourseControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CourseService courseService;

    @InjectMocks
    private CourseController courseController;

    private Course course;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(courseController).build();

        course = new Course();
        course.setCourseID(1);
        course.setCoursename("Test Course");
        course.setVideopath("/videos/test.mp4");
        course.setCoverpath("/covers/test.jpg");
        course.setOwner("admin");
        course.setTenantID(1);
        course.setApprovalStatus("pending");
    }

    /**
     * 正常情况：获取课程列表
     */
    @Test
    void testGetCourses() throws Exception {
        when(courseService.getAllCourses()).thenReturn(Arrays.asList(course));

        mockMvc.perform(get("/api/courses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.courses").isArray())
                .andExpect(jsonPath("$.courses[0].coursename").value("Test Course"))
                .andExpect(jsonPath("$.total").value(1));
    }

    /**
     * 边界情况：空课程列表
     */
    @Test
    void testGetCoursesEmpty() throws Exception {
        when(courseService.getAllCourses()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/courses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.courses").isEmpty())
                .andExpect(jsonPath("$.total").value(0));
    }

    @Test
    void testGetCoursesError() throws Exception {
        when(courseService.getAllCourses()).thenThrow(new RuntimeException("Database error"));

        mockMvc.perform(get("/api/courses"))
                .andExpect(status().isInternalServerError());
    }

    /**
     * 正常情况：根据ID获取课程
     */
    @Test
    void testGetCourseById() throws Exception {
        when(courseService.getCourseById(1)).thenReturn(course);

        mockMvc.perform(get("/api/courses/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.coursename").value("Test Course"));
    }

    /**
     * 异常情况：课程不存在
     */
    @Test
    void testGetCourseByIdNotFound() throws Exception {
        when(courseService.getCourseById(999)).thenReturn(null);

        mockMvc.perform(get("/api/courses/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetCourseByIdError() throws Exception {
        when(courseService.getCourseById(1)).thenThrow(new RuntimeException("Database error"));

        mockMvc.perform(get("/api/courses/1"))
                .andExpect(status().isInternalServerError());
    }

    /**
     * 正常情况：创建课程
     */
    @Test
    void testCreateCourse() throws Exception {
        doNothing().when(courseService).addCourse(any(Course.class));

        mockMvc.perform(post("/api/courses")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"coursename\":\"Test Course\",\"videopath\":\"/videos/test.mp4\",\"coverpath\":\"/covers/test.jpg\",\"owner\":\"admin\",\"tenantID\":1}")
                .characterEncoding("UTF-8"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("课程创建成功"));
    }

    /**
     * 正常情况：上传课程封面
     */
    @Test
    void testUploadCover() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
            "file",
            "test.jpg",
            "image/jpeg",
            "test image content".getBytes()
        );

        when(courseService.saveCover(any())).thenReturn("/covers/test.jpg");

        mockMvc.perform(multipart("/api/courses/upload-cover").file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.url").value("/covers/test.jpg"));
    }

    @Test
    void testUploadCoverError() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
            "file",
            "test.jpg",
            "image/jpeg",
            "test image content".getBytes()
        );

        when(courseService.saveCover(any())).thenThrow(new RuntimeException("Failed to save cover"));

        mockMvc.perform(multipart("/api/courses/upload-cover").file(file))
                .andExpect(status().isInternalServerError());
    }

    /**
     * 正常情况：上传课程视频
     */
    @Test
    void testUploadVideo() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
            "file",
            "test.mp4",
            "video/mp4",
            "test video content".getBytes()
        );

        when(courseService.saveVideo(any())).thenReturn("/videos/test.mp4");

        mockMvc.perform(multipart("/api/courses/upload-video").file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.url").value("/videos/test.mp4"))
                .andExpect(jsonPath("$.message").value("视频上传成功"))
                .andExpect(jsonPath("$.fileName").value("test.mp4"));
    }

    @Test
    void testUploadVideoError() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
            "file",
            "test.mp4",
            "video/mp4",
            "test video content".getBytes()
        );

        when(courseService.saveVideo(any())).thenThrow(new RuntimeException("Failed to save video"));

        mockMvc.perform(multipart("/api/courses/upload-video").file(file))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("视频上传失败"))
                .andExpect(jsonPath("$.message").value("Failed to save video"));
    }

    /**
     * 正常情况：删除课程
     */
    @Test
    void testDeleteCourse() throws Exception {
        doNothing().when(courseService).deleteCourse(1);

        mockMvc.perform(delete("/api/courses/1"))
                .andExpect(status().isOk());

        verify(courseService).deleteCourse(1);
    }

    @Test
    void testDeleteCourseError() throws Exception {
        doThrow(new RuntimeException("Failed to delete course")).when(courseService).deleteCourse(1);

        mockMvc.perform(delete("/api/courses/1"))
                .andExpect(status().isInternalServerError());

        verify(courseService).deleteCourse(1);
    }

    /**
     * 正常情况：更新课程
     */
    @Test
    void testUpdateCourse() throws Exception {
        when(courseService.updateCourse(any(Course.class))).thenReturn(1);

        mockMvc.perform(put("/api/courses/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"coursename\":\"Updated Course\",\"videopath\":\"/videos/updated.mp4\"}")
                .characterEncoding("UTF-8"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("课程修改成功"));
    }

    /**
     * 异常情况：更新不存在的课程
     */
    @Test
    void testUpdateCourseNotFound() throws Exception {
        when(courseService.updateCourse(any(Course.class))).thenReturn(0);

        mockMvc.perform(put("/api/courses/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"coursename\":\"Updated Course\"}")
                .characterEncoding("UTF-8"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("课程修改失败"));
    }

    @Test
    void testUpdateCourseError() throws Exception {
        when(courseService.updateCourse(any(Course.class))).thenThrow(new RuntimeException("Failed to update course"));

        mockMvc.perform(put("/api/courses/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"coursename\":\"Updated Course\"}")
                .characterEncoding("UTF-8"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("课程修改失败"));
    }

    /**
     * 正常情况：搜索课程
     */
    @Test
    void testSearchCourses() throws Exception {
        when(courseService.searchCourses("Test", "CS101")).thenReturn(Arrays.asList(course));

        mockMvc.perform(get("/api/courses/search")
                .param("coursename", "Test")
                .param("number", "CS101"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.courses").isArray())
                .andExpect(jsonPath("$.total").value(1));
    }

    @Test
    void testSearchCoursesNoParams() throws Exception {
        when(courseService.searchCourses(null, null)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/courses/search"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.courses").isEmpty())
                .andExpect(jsonPath("$.total").value(0));
    }

    @Test
    void testSearchCoursesError() throws Exception {
        when(courseService.searchCourses(any(), any())).thenThrow(new RuntimeException("Search failed"));

        mockMvc.perform(get("/api/courses/search")
                .param("coursename", "Test"))
                .andExpect(status().isInternalServerError());
    }

    /**
     * 正常情况：获取待审核课程
     */
    @Test
    void testGetPendingCourses() throws Exception {
        when(courseService.getPendingCourses()).thenReturn(Arrays.asList(course));

        mockMvc.perform(get("/api/courses/pending"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.courses").isArray())
                .andExpect(jsonPath("$.total").value(1));
    }

    @Test
    void testGetPendingCoursesEmpty() throws Exception {
        when(courseService.getPendingCourses()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/courses/pending"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.courses").isEmpty())
                .andExpect(jsonPath("$.total").value(0));
    }

    @Test
    void testGetPendingCoursesError() throws Exception {
        when(courseService.getPendingCourses()).thenThrow(new RuntimeException("Failed to get pending courses"));

        mockMvc.perform(get("/api/courses/pending"))
                .andExpect(status().isInternalServerError());
    }

    /**
     * 正常情况：根据审核状态获取课程
     */
    @Test
    void testGetCoursesByApprovalStatus() throws Exception {
        when(courseService.getCoursesByApprovalStatus("approved")).thenReturn(Arrays.asList(course));

        mockMvc.perform(get("/api/courses/status/approved"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.courses").isArray())
                .andExpect(jsonPath("$.total").value(1));
    }

    @Test
    void testGetCoursesByApprovalStatusEmpty() throws Exception {
        when(courseService.getCoursesByApprovalStatus("rejected")).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/courses/status/rejected"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.courses").isEmpty())
                .andExpect(jsonPath("$.total").value(0));
    }

    @Test
    void testGetCoursesByApprovalStatusError() throws Exception {
        when(courseService.getCoursesByApprovalStatus(any())).thenThrow(new RuntimeException("Failed to get courses by status"));

        mockMvc.perform(get("/api/courses/status/approved"))
                .andExpect(status().isInternalServerError());
    }

    /**
     * 正常情况：审核通过课程
     */
    @Test
    void testApproveCourse() throws Exception {
        Map<String, String> approvalData = new HashMap<>();
        approvalData.put("approvalStatus", "approved");
        approvalData.put("rejectionReason", "");

        when(courseService.approveCourse(1, "approved", "")).thenReturn(1);

        mockMvc.perform(post("/api/courses/1/approve")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"approvalStatus\":\"approved\",\"rejectionReason\":\"\"}")
                .characterEncoding("UTF-8"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("课程审核通过"))
                .andExpect(jsonPath("$.status").value("success"));
    }

    @Test
    void testApproveCourseFailure() throws Exception {
        when(courseService.approveCourse(1, "approved", "")).thenReturn(0);

        mockMvc.perform(post("/api/courses/1/approve")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"approvalStatus\":\"approved\",\"rejectionReason\":\"\"}")
                .characterEncoding("UTF-8"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("审核失败"))
                .andExpect(jsonPath("$.status").value("error"));
    }

    @Test
    void testApproveCourseError() throws Exception {
        when(courseService.approveCourse(anyInt(), anyString(), anyString()))
                .thenThrow(new RuntimeException("Failed to approve course"));

        mockMvc.perform(post("/api/courses/1/approve")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"approvalStatus\":\"approved\",\"rejectionReason\":\"\"}")
                .characterEncoding("UTF-8"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("审核失败: Failed to approve course"))
                .andExpect(jsonPath("$.status").value("error"));
    }

    /**
     * 正常情况：拒绝课程
     */
    @Test
    void testRejectCourse() throws Exception {
        Map<String, String> approvalData = new HashMap<>();
        approvalData.put("approvalStatus", "rejected");
        approvalData.put("rejectionReason", "内容不合适");

        when(courseService.approveCourse(1, "rejected", "内容不合适")).thenReturn(1);

        mockMvc.perform(post("/api/courses/1/approve")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"approvalStatus\":\"rejected\",\"rejectionReason\":\"内容不合适\"}")
                .characterEncoding("UTF-8"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("课程审核拒绝"))
                .andExpect(jsonPath("$.status").value("success"));
    }

    /**
     * 正常情况：获取已审核通过的课程
     */
    @Test
    void testGetApprovedCourses() throws Exception {
        course.setApprovalStatus("approved");
        when(courseService.getApprovedCourses()).thenReturn(Arrays.asList(course));

        mockMvc.perform(get("/api/courses/approved"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.courses").isArray())
                .andExpect(jsonPath("$.courses[0].approvalStatus").value("approved"))
                .andExpect(jsonPath("$.total").value(1));
    }

    @Test
    void testGetApprovedCoursesEmpty() throws Exception {
        when(courseService.getApprovedCourses()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/courses/approved"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.courses").isEmpty())
                .andExpect(jsonPath("$.total").value(0));
    }

    @Test
    void testGetApprovedCoursesError() throws Exception {
        when(courseService.getApprovedCourses()).thenThrow(new RuntimeException("Failed to get approved courses"));

        mockMvc.perform(get("/api/courses/approved"))
                .andExpect(status().isInternalServerError());
    }
} 