package edu.neu.oaas.pojo;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CourseTest {

    /**
     * 测试默认构造函数
     */
    @Test
    void testDefaultConstructor() {
        Course course = new Course();
        assertEquals("pending", course.getApprovalStatus());
    }

    /**
     * 测试所有属性的Getter和Setter
     */
    @Test
    void testGettersAndSetters() {
        Course course = new Course();

        course.setCourseID(1);
        assertEquals(Integer.valueOf(1), course.getCourseID());

        course.setCoursename("Test Course");
        assertEquals("Test Course", course.getCoursename());

        course.setVideopath("/videos/test.mp4");
        assertEquals("/videos/test.mp4", course.getVideopath());

        course.setCoverpath("/covers/test.jpg");
        assertEquals("/covers/test.jpg", course.getCoverpath());

        course.setOwner("Test Owner");
        assertEquals("Test Owner", course.getOwner());

        course.setTenantID(1);
        assertEquals(Integer.valueOf(1), course.getTenantID());

        course.setApprovalStatus("approved");
        assertEquals("approved", course.getApprovalStatus());

        course.setRejectionReason("Content inappropriate");
        assertEquals("Content inappropriate", course.getRejectionReason());
    }

    /**
     * 测试边界值
     */
    @Test
    void testBoundaryValues() {
        Course course = new Course();

        // 测试空字符串
        course.setCoursename("");
        assertEquals("", course.getCoursename());

        course.setOwner("");
        assertEquals("", course.getOwner());

        // 测试null值
        course.setCourseID(null);
        assertNull(course.getCourseID());

        course.setTenantID(null);
        assertNull(course.getTenantID());

        course.setVideopath(null);
        assertNull(course.getVideopath());

        course.setCoverpath(null);
        assertNull(course.getCoverpath());

        course.setRejectionReason(null);
        assertNull(course.getRejectionReason());

        // 测试极值
        course.setCourseID(Integer.MAX_VALUE);
        assertEquals(Integer.valueOf(Integer.MAX_VALUE), course.getCourseID());

        course.setTenantID(Integer.MIN_VALUE);
        assertEquals(Integer.valueOf(Integer.MIN_VALUE), course.getTenantID());
    }

    /**
     * 测试审核状态
     */
    @Test
    void testApprovalStatus() {
        Course course = new Course();

        // 默认状态
        assertEquals("pending", course.getApprovalStatus());

        // 更改状态
        course.setApprovalStatus("approved");
        assertEquals("approved", course.getApprovalStatus());

        course.setApprovalStatus("rejected");
        assertEquals("rejected", course.getApprovalStatus());
        
        // 设置拒绝原因
        course.setRejectionReason("Content inappropriate");
        assertEquals("Content inappropriate", course.getRejectionReason());
    }

    /**
     * 测试文件路径格式
     */
    @Test
    void testFilePaths() {
        Course course = new Course();

        // 视频路径
        course.setVideopath("/videos/lecture1.mp4");
        assertTrue(course.getVideopath().startsWith("/videos/"));
        assertTrue(course.getVideopath().endsWith(".mp4"));

        // 封面路径
        course.setCoverpath("/covers/thumbnail1.jpg");
        assertTrue(course.getCoverpath().startsWith("/covers/"));
        assertTrue(course.getCoverpath().endsWith(".jpg"));
    }
} 