package edu.neu.oaas.controller;

import edu.neu.oaas.pojo.Course;
import edu.neu.oaas.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/courses")
public class CourseController {
    @Autowired
    private CourseService courseService;

    @GetMapping
    public Map<String, Object> getCourses() {
        System.out.println("📚 获取课程列表请求");
        List<Course> courses = courseService.getAllCourses();
        
        System.out.println("📊 找到 " + courses.size() + " 门课程:");
        for (int i = 0; i < courses.size(); i++) {
            Course course = courses.get(i);
            System.out.println("课程" + (i + 1) + ": " + course.getCoursename() + 
                             ", 视频路径: " + course.getVideopath() + 
                             ", 封面路径: " + course.getCoverpath());
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("courses", courses);
        response.put("total", courses.size());
        return response;
    }
    @GetMapping("/{courseID}")
    public ResponseEntity<Course> getCourseById(@PathVariable Integer courseID) {
        Course course = courseService.getCourseById(courseID);
        if (course != null) {
            return ResponseEntity.ok(course);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> createCourse(@RequestBody Course course) {
        try {
            System.out.println("📚 接收到课程创建请求:");
            System.out.println("课程名称: " + course.getCoursename());
            System.out.println("视频路径: " + course.getVideopath());
            System.out.println("封面路径: " + course.getCoverpath());
            System.out.println("课程作者: " + course.getOwner());
            System.out.println("租户ID: " + course.getTenantID());
            
            courseService.addCourse(course);
            
            System.out.println("✅ 课程创建成功");
            Map<String, String> response = new HashMap<>();
            response.put("message", "课程创建成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("❌ 课程创建失败: " + e.getMessage());
            e.printStackTrace();
            Map<String, String> response = new HashMap<>();
            response.put("message", "课程创建失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/upload-cover")
    public ResponseEntity<Map<String, String>> uploadCover(@RequestParam("file") MultipartFile file) {
        try {
            String coverUrl = courseService.saveCover(file);
            Map<String, String> response = new HashMap<>();
            response.put("url", coverUrl);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping("/upload-video")
    public ResponseEntity<Map<String, String>> uploadVideo(@RequestParam("file") MultipartFile file) {
        try {
            System.out.println("🎬 接收到视频上传请求，文件名: " + file.getOriginalFilename());
            System.out.println("📁 文件大小: " + (file.getSize() / 1024 / 1024) + " MB");
            
            String videoUrl = courseService.saveVideo(file);
            System.out.println("✅ 视频保存成功，路径: " + videoUrl);
            
            Map<String, String> response = new HashMap<>();
            response.put("url", videoUrl);
            response.put("message", "视频上传成功");
            response.put("fileName", file.getOriginalFilename());
            
            System.out.println("📤 返回响应: " + response);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("❌ 视频上传失败: " + e.getMessage());
            e.printStackTrace();
            
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "视频上传失败");
            errorResponse.put("message", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @DeleteMapping("/{courseID}")
    public void deleteCourse(@PathVariable Integer courseID) {
        courseService.deleteCourse(courseID);
    }

    @PutMapping("/{courseID}")
    public Map<String, String> updateCourse(@PathVariable Integer courseID, @RequestBody Course course) {
        System.out.println("📝 接收到课程修改请求 ID: " + courseID);
        System.out.println("课程名称: " + course.getCoursename());
        System.out.println("视频路径: " + course.getVideopath());
        System.out.println("封面路径: " + course.getCoverpath());
        System.out.println("课程作者: " + course.getOwner());
        
        course.setCourseID(courseID);
        int result = courseService.updateCourse(course);
        
        Map<String, String> response = new HashMap<>();
        if (result > 0) {
            System.out.println("✅ 课程修改成功");
            response.put("message", "课程修改成功");
        } else {
            System.err.println("❌ 课程修改失败");
            response.put("message", "课程修改失败");
        }
        return response;
    }

    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchCourses(
            @RequestParam(value = "coursename", required = false) String coursename,
            @RequestParam(value = "number", required = false) String number) {

        List<Course> courses = courseService.searchCourses(coursename, number);
        Map<String, Object> response = new HashMap<>();
        response.put("courses", courses);
        response.put("total", courses.size());
        return ResponseEntity.ok(response);
    }

    // 审批相关接口
    @GetMapping("/pending")
    public ResponseEntity<Map<String, Object>> getPendingCourses() {
        List<Course> courses = courseService.getPendingCourses();
        Map<String, Object> response = new HashMap<>();
        response.put("courses", courses);
        response.put("total", courses.size());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/status/{approvalStatus}")
    public ResponseEntity<Map<String, Object>> getCoursesByApprovalStatus(@PathVariable String approvalStatus) {
        List<Course> courses = courseService.getCoursesByApprovalStatus(approvalStatus);
        Map<String, Object> response = new HashMap<>();
        response.put("courses", courses);
        response.put("total", courses.size());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{courseID}/approve")
    public ResponseEntity<Map<String, String>> approveCourse(
            @PathVariable Integer courseID,
            @RequestBody Map<String, String> approvalData) {
        try {
            String approvalStatus = approvalData.get("approvalStatus");
            String rejectionReason = approvalData.get("rejectionReason");
            
            System.out.println("📋 接收到课程审核请求:");
            System.out.println("课程ID: " + courseID);
            System.out.println("审核状态: " + approvalStatus);
            System.out.println("拒绝原因: " + rejectionReason);
            
            int result = courseService.approveCourse(courseID, approvalStatus, rejectionReason);
            Map<String, String> response = new HashMap<>();
            
            if (result > 0) {
                if ("approved".equals(approvalStatus)) {
                    response.put("message", "课程审核通过");
                    System.out.println("✅ 课程审核通过成功");
                } else {
                    response.put("message", "课程审核拒绝");
                    System.out.println("❌ 课程审核拒绝成功");
                }
                response.put("status", "success");
            } else {
                response.put("message", "审核失败");
                response.put("status", "error");
                System.err.println("❌ 课程审核操作失败");
            }
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("❌ 课程审核异常: " + e.getMessage());
            e.printStackTrace();
            Map<String, String> response = new HashMap<>();
            response.put("message", "审核失败: " + e.getMessage());
            response.put("status", "error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/approved")
    public ResponseEntity<Map<String, Object>> getApprovedCourses() {
        List<Course> courses = courseService.getApprovedCourses();
        Map<String, Object> response = new HashMap<>();
        response.put("courses", courses);
        response.put("total", courses.size());
        return ResponseEntity.ok(response);
    }
}

