package edu.neu.oaas.mapper;

import edu.neu.oaas.pojo.Information;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface InformationMapper {

    @Select("SELECT id, title, picture, content, introduction, author, company, tenantId FROM Information")
    List<Information> getAllInformation();

    @Insert("INSERT INTO Information (title, picture, content, introduction, author, company, tenantId) VALUES (#{title}, #{picture}, #{content}, #{introduction}, #{author}, #{company}, #{tenantId})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void addInformation(Information information);

    @Update("UPDATE Information SET title=#{title}, picture=#{picture}, content=#{content}, introduction=#{introduction}, author=#{author}, company=#{company}, tenantId=#{tenantId} WHERE id=#{id}")
    void updateInformation(Information information);

    @Delete("DELETE FROM Information WHERE id=#{id}")
    void deleteInformation(int id);

    @Select("SELECT id, title, picture, content, introduction, author, company, tenantId FROM Information WHERE id = #{id}")
    Information getInformationById(@Param("id") int id);
}

