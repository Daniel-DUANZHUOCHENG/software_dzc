package edu.neu.oaas.mapper;

import edu.neu.oaas.pojo.Information;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface InformationMapper {

    @Select("SELECT * FROM information WHERE path LIKE CONCAT(#{pathPrefix}, '%')")
    List<Information> findByPathPrefix(@Param("pathPrefix") String pathPrefix);

    @Select("<script>" +
            "SELECT * FROM information WHERE path LIKE CONCAT(#{pathPrefix}, '%')" +
            "<if test='title != null and title != \"\"'> AND title LIKE CONCAT('%', #{title}, '%')</if>" +
            "<if test='author != null and author != \"\"'> AND author LIKE CONCAT('%', #{author}, '%')</if>" +
            "</script>")
    List<Information> searchByTitleAndAuthor(@Param("pathPrefix") String pathPrefix, @Param("title") String title, @Param("author") String author);

    @Insert("INSERT INTO information (title, picture, content, introduction, author, company, tenantId, path, approvalStatus) " +
            "VALUES (#{title}, #{picture}, #{content}, #{introduction}, #{author}, #{company}, #{tenantId}, #{path}, #{approvalStatus})")
    void insertInformation(Information information);

    @Update("UPDATE information SET title=#{title}, picture=#{picture}, content=#{content}, introduction=#{introduction}, " +
            "author=#{author}, company=#{company}, tenantId=#{tenantId}, path=#{path}, approvalStatus=#{approvalStatus}, rejectionReason=#{rejectionReason} WHERE id=#{id}")
    int updateInformation(Information information);

    @Delete("DELETE FROM information WHERE id=#{id}")
    void deleteInformation(@Param("id") int id);

    @Select("SELECT * FROM information WHERE id = #{id}")
    Information getInformationById(@Param("id") int id);

    @Select("SELECT * FROM information WHERE tenantId = #{tenantId}")
    List<Information> getInformationByTenantId(@Param("tenantId") int tenantId);

    @Delete("DELETE FROM information WHERE path LIKE CONCAT(#{pathPrefix}, '%')")
    void deleteByPathPrefix(@Param("pathPrefix") String pathPrefix);

    // 审核相关方法
    @Select("SELECT * FROM information WHERE approvalStatus = 'pending'")
    List<Information> selectPendingInformation();

    @Select("SELECT * FROM information WHERE approvalStatus = #{approvalStatus}")
    List<Information> selectInformationByApprovalStatus(@Param("approvalStatus") String approvalStatus);

    @Update("UPDATE information SET approvalStatus = #{approvalStatus}, rejectionReason = #{rejectionReason} WHERE id = #{id}")
    int approveInformation(@Param("id") int id, @Param("approvalStatus") String approvalStatus, @Param("rejectionReason") String rejectionReason);

    @Select("SELECT * FROM information WHERE approvalStatus = 'approved'")
    List<Information> selectApprovedInformation();

    // 新增：获取所有资讯的方法
    @Select("SELECT * FROM information")
    List<Information> getAllInformation();
}

