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
    private static final String COVER_BASE_PATH = "E:/OAASystem/OAASystem/src/main/resources/static/CourseCover/";
    private static final String VIDEO_BASE_PATH = "E:/OAASystem/OAASystem/src/main/resources/static/Video/";

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
        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        String filePath = VIDEO_BASE_PATH + fileName;

        File dest = new File(filePath);
        if (!dest.getParentFile().exists()) {
            dest.getParentFile().mkdirs();
        }

        file.transferTo(dest);
        return "/Video/" + fileName;
    }
}
