package edu.neu.oaas.mapper;

import edu.neu.oaas.pojo.Course;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CourseMapper {

    @Select("SELECT * FROM course")
    List<Course> selectAllCourses();

    @Select("<script>" +
            "SELECT * FROM course WHERE 1=1" +
            "<if test='coursename != null and coursename != \"\"'> AND coursename LIKE CONCAT('%', #{coursename}, '%')</if>" +
            "<if test='number != null and number != \"\"'> AND number LIKE CONCAT('%', #{number}, '%')</if>" +
            "</script>")
    List<Course> searchCourses(@Param("coursename") String coursename, @Param("number") String number);

    @Insert("INSERT INTO course (coursename, coverpath, courseintro, number, videopath, owner, tenantID, approvalStatus) " +
            "VALUES (#{coursename}, #{coverpath}, #{courseintro}, #{number}, #{videopath}, #{owner}, #{tenantID}, #{approvalStatus})")
    void insertCourse(Course course);

    @Update("UPDATE course SET coursename=#{coursename}, coverpath=#{coverpath}, courseintro=#{courseintro}, number=#{number}, " +
            "videopath=#{videopath}, owner=#{owner}, tenantID=#{tenantID}, approvalStatus=#{approvalStatus}, rejectionReason=#{rejectionReason} WHERE courseID=#{courseID}")
    int updateCourse(Course course);

    @Delete("DELETE FROM course WHERE courseID=#{courseID}")
    void deleteCourse(@Param("courseID") Integer courseID);
    
    @Select("SELECT * FROM course WHERE courseID = #{courseID}")
    Course selectCourseById(@Param("courseID") Integer courseID);

    // 审核相关方法
    @Select("SELECT * FROM course WHERE approvalStatus = 'pending'")
    List<Course> selectPendingCourses();

    @Select("SELECT * FROM course WHERE approvalStatus = #{approvalStatus}")
    List<Course> selectCoursesByApprovalStatus(@Param("approvalStatus") String approvalStatus);

    @Update("UPDATE course SET approvalStatus = #{approvalStatus}, rejectionReason = #{rejectionReason} WHERE courseID = #{courseID}")
    int approveCourse(@Param("courseID") Integer courseID, @Param("approvalStatus") String approvalStatus, @Param("rejectionReason") String rejectionReason);

    @Select("SELECT * FROM course WHERE approvalStatus = 'approved'")
    List<Course> selectApprovedCourses();
}
