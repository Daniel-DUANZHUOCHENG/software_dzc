package edu.neu.oaas.mapper;

import edu.neu.oaas.pojo.MeetingApplication;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface MeetingApplicationMapper {

    // 提交会议申请
    @Insert("INSERT INTO meeting_application (meetingId, applicantId, applicantName, applicantCompany, applicationTime, status, tenantId, meetingTenantId) " +
            "VALUES (#{meetingId}, #{applicantId}, #{applicantName}, #{applicantCompany}, #{applicationTime}, #{status}, #{tenantId}, #{meetingTenantId})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertMeetingApplication(MeetingApplication application);

    // 检查是否已经申请过
    @Select("SELECT COUNT(*) FROM meeting_application WHERE meetingId = #{meetingId} AND applicantId = #{applicantId} AND status != 'rejected'")
    int checkExistingApplication(@Param("meetingId") Integer meetingId, @Param("applicantId") Integer applicantId);

    // 根据会议ID获取申请列表
    @Select("SELECT * FROM meeting_application WHERE meetingId = #{meetingId} ORDER BY applicationTime DESC")
    List<MeetingApplication> getMeetingApplications(Integer meetingId);

    // 根据会议ID和租户ID获取申请列表（用于租户管理员查看本租户会议的申请）
    @Select("SELECT ma.* FROM meeting_application ma " +
            "INNER JOIN conference c ON ma.meetingId = c.conferenceID " +
            "WHERE ma.meetingId = #{meetingId} AND c.tenantID = #{tenantId} " +
            "ORDER BY ma.applicationTime DESC")
    List<MeetingApplication> getMeetingApplicationsByTenant(@Param("meetingId") Integer meetingId, @Param("tenantId") Integer tenantId);

    // 根据ID获取申请信息
    @Select("SELECT * FROM meeting_application WHERE id = #{id}")
    MeetingApplication getMeetingApplicationById(Integer id);

    // 更新申请状态
    @Update("UPDATE meeting_application SET status = #{status}, rejectionReason = #{rejectionReason}, " +
            "approverId = #{approverId}, approverName = #{approverName}, approvalTime = #{approvalTime} " +
            "WHERE id = #{id}")
    int updateMeetingApplication(MeetingApplication application);

    // 获取用户的所有申请
    @Select("SELECT ma.*, c.conferencename as meetingName FROM meeting_application ma " +
            "LEFT JOIN conference c ON ma.meetingId = c.conferenceID " +
            "WHERE ma.applicantId = #{applicantId} ORDER BY ma.applicationTime DESC")
    List<MeetingApplication> getUserApplications(Integer applicantId);

    // 获取租户的待审批申请
    @Select("SELECT ma.*, c.conferencename as meetingName FROM meeting_application ma " +
            "INNER JOIN conference c ON ma.meetingId = c.conferenceID " +
            "WHERE c.tenantID = #{tenantId} AND ma.status = 'pending' " +
            "ORDER BY ma.applicationTime DESC")
    List<MeetingApplication> getTenantPendingApplications(Integer tenantId);

    // 获取租户的所有申请
    @Select("SELECT ma.*, c.conferencename as meetingName FROM meeting_application ma " +
            "INNER JOIN conference c ON ma.meetingId = c.conferenceID " +
            "WHERE c.tenantID = #{tenantId} ORDER BY ma.applicationTime DESC")
    List<MeetingApplication> getTenantAllApplications(Integer tenantId);

    // 删除申请
    @Delete("DELETE FROM meeting_application WHERE id = #{id}")
    int deleteMeetingApplication(Integer id);

    // 根据租户和状态获取申请数量统计
    @Select("SELECT COUNT(*) FROM meeting_application ma " +
            "INNER JOIN conference c ON ma.meetingId = c.conferenceID " +
            "WHERE c.tenantID = #{tenantId} AND ma.status = #{status}")
    int getTenantApplicationCountByStatus(@Param("tenantId") Integer tenantId, @Param("status") String status);

    // 获取某个时间段内的申请数量
    @Select("SELECT COUNT(*) FROM meeting_application " +
            "WHERE applicationTime >= #{startTime} AND applicationTime <= #{endTime}")
    int getApplicationCountByTimeRange(@Param("startTime") String startTime, @Param("endTime") String endTime);

    // 获取最近的申请列表（用于dashboard展示）
    @Select("SELECT ma.*, c.conferencename as meetingName FROM meeting_application ma " +
            "INNER JOIN conference c ON ma.meetingId = c.conferenceID " +
            "WHERE c.tenantID = #{tenantId} " +
            "ORDER BY ma.applicationTime DESC LIMIT #{limit}")
    List<MeetingApplication> getRecentApplications(@Param("tenantId") Integer tenantId, @Param("limit") int limit);

    // 根据申请人租户获取申请列表
    @Select("SELECT ma.*, c.conferencename as meetingName FROM meeting_application ma " +
            "LEFT JOIN conference c ON ma.meetingId = c.conferenceID " +
            "WHERE ma.tenantId = #{tenantId} ORDER BY ma.applicationTime DESC")
    List<MeetingApplication> getApplicationsByApplicantTenant(Integer tenantId);

    // 批量更新申请状态
    @Update("<script>" +
            "UPDATE meeting_application SET status = #{status}, approverId = #{approverId}, " +
            "approverName = #{approverName}, approvalTime = NOW() WHERE id IN " +
            "<foreach collection='ids' item='id' open='(' separator=',' close=')'>" +
            "#{id}" +
            "</foreach>" +
            "</script>")
    int batchUpdateApplicationStatus(@Param("ids") List<Integer> ids, @Param("status") String status, 
                                   @Param("approverId") Integer approverId, @Param("approverName") String approverName);

    // 根据状态获取申请列表
    @Select("SELECT * FROM meeting_application WHERE status = #{status} ORDER BY applicationTime DESC")
    List<MeetingApplication> getMeetingApplicationsByStatus(@Param("status") String status);

    // 获取所有申请（系统管理员用）
    @Select("SELECT * FROM meeting_application ORDER BY applicationTime DESC")
    List<MeetingApplication> getAllMeetingApplications();
} 