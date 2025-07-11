package edu.neu.oaas.mapper;

import edu.neu.oaas.pojo.UserBehavior;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserBehaviorMapper {

    @Insert("INSERT INTO user_behavior (action, page, timestamp, user_id) VALUES (#{action}, #{page}, #{timestamp}, #{userId})")
    void insertUserBehavior(UserBehavior userBehavior);

    @Select("SELECT * FROM user_behavior")
    List<UserBehavior> selectAllUserBehaviors();

    @Select({
            "<script>",
            "SELECT * FROM user_behavior",
            "WHERE 1=1",
            "<if test='action != null and action != \"\"'>",
            "AND action = #{action}",
            "</if>",
            "<if test='timestamp != null and timestamp != \"\"'>",
            "AND DATE(timestamp) = #{timestamp}",
            "</if>",
            "</script>"
    })
    List<UserBehavior> searchUserBehaviors(@Param("action") String action, @Param("timestamp") String timestamp);

    @Select("SELECT COUNT(DISTINCT user_id) FROM user_behavior WHERE timestamp >= NOW() - INTERVAL 5 MINUTE")
    int countOnlineUsers();

    @Select("SELECT * FROM user_behavior WHERE user_id = #{userId}")
    List<UserBehavior> getUserBehaviorsByUserId(@Param("userId") Integer userId);


}
