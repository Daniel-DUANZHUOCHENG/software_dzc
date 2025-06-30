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
        List<Course> courses = courseService.getAllCourses();
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
            courseService.addCourse(course);
            Map<String, String> response = new HashMap<>();
            response.put("message", "课程创建成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "课程创建失败");
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
            String videoUrl = courseService.saveVideo(file);
            Map<String, String> response = new HashMap<>();
            response.put("url", videoUrl);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @DeleteMapping("/{courseID}")
    public void deleteCourse(@PathVariable Integer courseID) {
        courseService.deleteCourse(courseID);
    }

    @PutMapping("/{courseID}")
    public Map<String, String> updateCourse(@PathVariable Integer courseID, @RequestBody Course course) {
        course.setCourseID(courseID);
        int result = courseService.updateCourse(course);
        Map<String, String> response = new HashMap<>();
        if (result > 0) {
            response.put("message", "课程修改成功");
        } else {
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
}

