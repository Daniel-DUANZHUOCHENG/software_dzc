package edu.neu.oaas.mapper;

import edu.neu.oaas.pojo.User;
import org.apache.ibatis.annotations.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface UserMapper {
    //用户操作涉及的有id查找,按tenantId查找,按username查找,全体展示,(按部门查找,)
    //增加
    //删除
    //修改
    @Insert("INSERT INTO users (username, password, nickname, phoneNumber, email, gender, departmentId, status, role, Createdat, position, remark, avatar,TenantId) " +
            "VALUES (#{username}, #{password}, #{nickname}, #{phoneNumber}, #{email}, #{gender}, #{departmentId}, #{status}, #{role}, #{createdAt}, #{position}, #{remark}, #{avatar},#{tenantId})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insertUser2(User user);

        @Insert("INSERT INTO users (username, password, nickname, phoneNumber, email, gender, departmentId, status, role, createdAt, position, remark, avatar, tenantId, path) " +
                "VALUES (#{username}, #{password}, #{nickname}, #{phoneNumber}, #{email}, #{gender}, #{departmentId}, #{status}, #{role}, #{createdAt}, #{position}, #{remark}, #{avatar}, #{tenantId}, #{path})")
        @Options(useGeneratedKeys = true, keyProperty = "id")
        void insertUser(User user);

    @Select("SELECT * FROM users WHERE Id = #{Id}")
    User getUserById(int Id);

//    @Select("SELECT * FROM users WHERE TenantId = #{tenantId}")
//    List<User> getUsersByTenantId(int tenantId);

    @Select("SELECT * FROM users WHERE TenantId = #{tenantId}")
    List<User> getUsersByTenantId(@Param("tenantId") int tenantId);

    @Select("SELECT * FROM users WHERE Username = #{username}")
    User getUserByUsername(String username);

    @Select("SELECT * FROM users WHERE Email = #{email}")
    User getUserByEmail(String email);


    @Select("select *from users where username=#{username}")
    User getUserByName(String username);//通过姓名查找

    @Select("select *from users")
    List<User> getAllUser();//管理员使用,得到全部用户信息

    @Select("select *from users where departmentId =#{departmentId}")
    List<User> getUserByDepartmentId(Integer departmentId);//管理员使用,得到全部用户信息

    @Delete("DELETE FROM users WHERE tenantId = #{tenantId}")
    void deleteByTenantId(Integer tenantId);




    @Select("SELECT * FROM users WHERE path LIKE CONCAT(#{path}, '%')")
    List<User> findByPathPrefix(@Param("path") String pathPrefix);//限定范围内的用户

    @Update("update users set username=#{username}, password=#{password}, nickname=#{nickname}, phoneNumber=#{phoneNumber}, email=#{email}, gender=#{gender}, departmentId=#{departmentId}, status=#{status}, role=#{role}, createdAt=#{createdAt}, position=#{position}, remark=#{remark}, avatar=#{avatar}, tenantId=#{tenantId}, path=#{path} where id=#{id}")
    int updateUser2(User user);

    @Delete("delete from users where id=#{id}")
    void deleteById(Integer id);

    @Delete("delete from users where path LIKE CONCAT(#{path}, '%') ")
    void deleteByPath(String pathPrefix);

    @Update("update users set username=#{username}, password=#{password}, nickname=#{nickname}, phoneNumber=#{phoneNumber}, email=#{email}, gender=#{gender}, departmentId=#{departmentId}, status=#{status}, role=#{role}, createdAt=#{createdAt}, position=#{position}, remark=#{remark}, avatar=#{avatar}, tenantId=#{tenantId}, path=#{path} where id=#{id}")
    void updateUser(@Param("id") Integer id,
                    @Param("username") String username,
                    @Param("password") String password,
                    @Param("nickname") String nickname,
                    @Param("phoneNumber") String phoneNumber,
                    @Param("email") String email,
                    @Param("gender") String gender,
                    @Param("departmentId") Integer departmentId,
                    @Param("status") String status,
                    @Param("role") String role,
                    @Param("createdAt") LocalDateTime createdAt,
                    @Param("position") String position,
                    @Param("remark") String remark,
                    @Param("avatar") String avatar,
                    @Param("tenantId") Integer tenantId,
                    @Param("path") String path);

//分页
    @Select("SELECT * FROM users LIMIT #{offset}, #{pageSize}")
    List<User> getUsersByPage(@Param("offset") int offset, @Param("pageSize") int pageSize);

    @Select("SELECT COUNT(*) FROM users")
    int getTotalUserCount();


    @Select({
            "<script>",
            "SELECT * FROM users",
            "WHERE 1=1",
            "<if test='username != null and !username.isEmpty()'>",
            "AND username LIKE CONCAT('%', #{username}, '%')",
            "</if>",
            "<if test='phoneNumber != null and !phoneNumber.isEmpty()'>",
            "AND phoneNumber LIKE CONCAT('%', #{phoneNumber}, '%')",
            "</if>",
            "<if test='status != null and !status.isEmpty()'>",
            "AND status = #{status}",
            "</if>",
            "<if test='startDate != null'>",
            "AND createdAt &gt;= #{startDate}",
            "</if>",
            "<if test='endDate != null'>",
            "AND createdAt &lt;= #{endDate}",
            "</if>",
            "</script>"
    })
    List<User> searchUsers(@Param("username") String username,
                           @Param("phoneNumber") String phoneNumber,
                           @Param("status") String status,
                           @Param("startDate") LocalDate startDate,
                           @Param("endDate") LocalDate endDate);



}


