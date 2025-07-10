package edu.neu.oaas.service;
import edu.neu.oaas.mapper.CourseMapper;
import edu.neu.oaas.pojo.Course;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;




@Service
public class CourseService {

    @Autowired
    private CourseMapper courseMapper;

    public List<Course> getAllCourses() {
        return courseMapper.selectAllCourses();
    }

    public List<Course> searchCourses(String coursename, String number) {
        return courseMapper.searchCourses(coursename, number);
    }
    public Course getCourseById(Integer courseID) {
        return courseMapper.selectCourseById(courseID);
    }

    public void addCourse(Course course) {
        courseMapper.insertCourse(course);
    }

    public int updateCourse(Course course) {
        return courseMapper.updateCourse(course);
    }

    public void deleteCourse(Integer courseID) {
        courseMapper.deleteCourse(courseID);
    }
    private static final String PROJECT_PATH = System.getProperty("user.dir");
    private static final String COVER_BASE_PATH = PROJECT_PATH + "/src/main/resources/static/CourseCover/";
    private static final String VIDEO_BASE_PATH = PROJECT_PATH + "/src/main/resources/static/Video/";

    public String saveCover(MultipartFile file) throws IOException {
        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        String filePath = COVER_BASE_PATH + fileName;

        File dest = new File(filePath);
        if (!dest.getParentFile().exists()) {
            dest.getParentFile().mkdirs();
        }

        file.transferTo(dest);
        return "/CourseCover/" + fileName;
    }

    public String saveVideo(MultipartFile file) throws IOException {
        System.out.println("🎬 开始保存视频文件: " + file.getOriginalFilename());
        System.out.println("📁 文件大小: " + file.getSize() + " bytes");
        
        if (file.isEmpty()) {
            throw new IOException("上传的视频文件为空");
        }
        
        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        String filePath = VIDEO_BASE_PATH + fileName;
        
        System.out.println("💾 目标保存路径: " + filePath);
        
        File dest = new File(filePath);
        if (!dest.getParentFile().exists()) {
            System.out.println("📂 创建目录: " + dest.getParentFile().getAbsolutePath());
            boolean created = dest.getParentFile().mkdirs();
            System.out.println("📂 目录创建结果: " + created);
        }

        try {
            file.transferTo(dest);
            System.out.println("✅ 视频文件保存成功: " + dest.getAbsolutePath());
            
            // 验证文件是否真的被保存
            if (dest.exists() && dest.length() > 0) {
                System.out.println("✅ 文件验证成功，大小: " + dest.length() + " bytes");
            } else {
                throw new IOException("文件保存后验证失败");
            }
            
        } catch (IOException e) {
            System.err.println("❌ 视频文件保存失败: " + e.getMessage());
            throw e;
        }
        
        String resultPath = "/Video/" + fileName;
        System.out.println("🔗 返回路径: " + resultPath);
        return resultPath;
    }

    // 新增：获取待审核的课程
    public List<Course> getPendingCourses() {
        return courseMapper.selectPendingCourses();
    }

    // 新增：根据审核状态获取课程
    public List<Course> getCoursesByApprovalStatus(String approvalStatus) {
        return courseMapper.selectCoursesByApprovalStatus(approvalStatus);
    }

    // 新增：审核课程
    public int approveCourse(Integer courseID, String approvalStatus, String rejectionReason) {
        return courseMapper.approveCourse(courseID, approvalStatus, rejectionReason);
    }

    // 新增：获取已审核通过的课程（用于前端展示）
    public List<Course> getApprovedCourses() {
        return courseMapper.selectApprovedCourses();
    }
}
