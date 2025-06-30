package edu.neu.oaas.mapper;


import edu.neu.oaas.pojo.Position;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;


@Mapper
public interface PositionMapper {
    @Insert("insert into position (departmentId,position) values (#{departmentId},#{position})")
    void insert(Position position);

    @Delete("delete from position where departmentId=#{departmentId} and position=#{position}")
    void delete(Position position);

    @Select("select * from position where departmentId =#{departmentId}")
    List<Position> getAll(Integer departmentId);
}

