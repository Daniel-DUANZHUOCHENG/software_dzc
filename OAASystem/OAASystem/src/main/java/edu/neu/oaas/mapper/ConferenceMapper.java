package edu.neu.oaas.mapper;

import edu.neu.oaas.pojo.Conference;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ConferenceMapper {
    @Select("SELECT * FROM conference")
    List<Conference> getAllConferences();

    @Insert("INSERT INTO conference (conferencename, creator, coverpath, contentspath, state, starttime, endtime, tenantID, approvalStatus) " +
            "VALUES (#{conferencename}, #{creator}, #{coverpath}, #{contentspath}, #{state}, #{starttime}, #{endtime}, #{tenantID}, #{approvalStatus})")
    void insertConference(Conference conference);

    @Delete("DELETE FROM conference WHERE conferenceID = #{conferenceID}")
    void deleteConference(Integer conferenceID);

    @Update("UPDATE conference SET conferencename=#{conferencename}, creator=#{creator}, situation=#{situation}, coverpath=#{coverpath}, " +
            "contentspath=#{contentspath}, starttime=#{starttime}, endtime=#{endtime}, state=#{state}, tenantID=#{tenantID}, " +
            "approvalStatus=#{approvalStatus}, rejectionReason=#{rejectionReason} " +
            "WHERE conferenceID = #{conferenceID}")
    int updateConference(Conference conference);

    @Select("<script>" +
            "SELECT * FROM conference WHERE 1=1" +
            "<if test='conferencename != null and conferencename != \"\"'>" +
            " AND conferencename LIKE CONCAT('%', #{conferencename}, '%')" +
            "</if>" +
            "<if test='creator != null and creator != \"\"'>" +
            " AND creator LIKE CONCAT('%', #{creator}, '%')" +
            "</if>" +
            "<if test='starttime != null and starttime != \"\"'>" +
            " AND DATE(starttime) = #{starttime}" +
            "</if>" +
            "</script>")
    List<Conference> searchConferences(@Param("conferencename") String conferencename,
                                       @Param("creator") String creator,
                                       @Param("starttime") String starttime);

    @Select("SELECT * FROM conference WHERE conferenceID = #{conferenceID}")
    Conference getConferenceById(@Param("conferenceID") Integer conferenceID);

    // 新增：获取待审核的会议
    @Select("SELECT * FROM conference WHERE approvalStatus = 'pending'")
    List<Conference> selectPendingConferences();

    // 新增：根据审核状态获取会议
    @Select("SELECT * FROM conference WHERE approvalStatus = #{approvalStatus}")
    List<Conference> selectConferencesByApprovalStatus(@Param("approvalStatus") String approvalStatus);

    // 新增：审核会议
    @Update("UPDATE conference SET approvalStatus = #{approvalStatus}, rejectionReason = #{rejectionReason} WHERE conferenceID = #{conferenceID}")
    int approveConference(@Param("conferenceID") Integer conferenceID, @Param("approvalStatus") String approvalStatus, @Param("rejectionReason") String rejectionReason);

    // 新增：获取已审核通过的会议（用于前端展示）
    @Select("SELECT * FROM conference WHERE approvalStatus = 'approved'")
    List<Conference> selectApprovedConferences();
}
