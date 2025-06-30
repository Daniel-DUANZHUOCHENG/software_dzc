package edu.neu.oaas.mapper;


import edu.neu.oaas.pojo.Tenant;
import org.apache.ibatis.annotations.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface TenantMapper {
    @Insert("INSERT INTO tenants (adminusername, password, contactperson, phone, tenantname, createdat, icon, remark,rootDepartmentId) " +
            "VALUES (#{adminUsername}, #{password}, #{contactPerson}, #{phone}, #{tenantName}, #{createdAt}, #{icon}, #{remark},#{rootDepartmentId})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insertTenant2(Tenant tenant);

    @Insert("INSERT INTO tenants (adminUsername, password, contactPerson, phone, tenantName, createdAt, icon, remark,rootDepartmentId) " +
            "VALUES (#{adminUsername}, #{password}, #{contactPerson}, #{phone}, #{tenantName}, #{createdAt}, #{icon}, #{remark},#{rootDepartmentId})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insertTenant(Tenant tenant);

    @Select("SELECT * FROM tenants")
    List<Tenant> findAll();


    @Select("SELECT * FROM tenants WHERE TenantName = #{tenantName}")
    Tenant getTenantByName(String tenantName);

    @Select("SELECT * FROM tenants")
    List<Tenant> getAllTenants();


    @Select("select *from tenants")
    List<Tenant> getAll();

    @Select("select *from tenants where id=#{id}")
    Tenant getById(Integer id);

    @Select("select *from tenants where tenantName LIKE CONCAT('%', #{tenantName}, '%')")
    List<Tenant> getByName(String tenantName);

//    @Insert("insert into tenants(id,adminUsername,password,contactPerson,phone,tenantName,createdAt,icon,remark,rootDepartmentId) " +
//            "values (null,#{adminUsername},#{password},#{contactPerson},#{phone},#{tenantName},#{createdAt},#{icon},#{remark},#{rootDepartmentId})")
//    void insertTenant(String adminUsername,
//                      String password,
//                      String contactPerson,
//                      String phone,
//                      String tenantName,
//                      LocalDateTime createdAt,
//                      String icon,
//                      String remark,
//                      Integer rootDepartmentId);

    @Update("update tenants set adminUsername = #{adminUsername},password = #{password},contactPerson = #{contactPerson},phone = #{phone},tenantName = #{tenantName},createdAt = #{createdAt},icon = #{icon},remark = #{remark},rootDepartmentId = #{rootDepartmentId}" +
            "where id = #{id}")
    void updateTenant2(String adminUsername,
                      String password,
                      String contactPerson,
                      String phone,
                      String tenantName,
                      LocalDateTime createdAt,
                      String icon,
                      String remark,
                      Integer rootDepartmentId,
                      Integer id);

    @Update("UPDATE tenants SET adminUsername = #{adminUsername}, password = #{password}, contactPerson = #{contactPerson}, phone = #{phone}, tenantName = #{tenantName}, createdAt = #{createdAt}, icon = #{icon}, remark = #{remark}, rootDepartmentId = #{rootDepartmentId} WHERE id = #{id}")
    void updateTenant(Tenant tenant);

    @Delete("delete from tenants where id=#{id}")
    void deleteById(Integer id);

    @Select({
            "<script>",
            "SELECT * FROM tenants",
            "WHERE 1=1",
            "<if test='tenantName != null and !tenantName.isEmpty()'>",
            "AND tenantName LIKE CONCAT('%', #{tenantName}, '%')",
            "</if>",
            "<if test='contactPerson != null and !contactPerson.isEmpty()'>",
            "AND contactPerson LIKE CONCAT('%', #{contactPerson}, '%')",
            "</if>",
            "<if test='phone != null and !phone.isEmpty()'>",
            "AND phone LIKE CONCAT('%', #{phone}, '%')",
            "</if>",
            "<if test='startDate != null'>",
            "AND createdAt &gt;= #{startDate}",
            "</if>",
            "<if test='endDate != null'>",
            "AND createdAt &lt;= #{endDate}",
            "</if>",
            "</script>"
    })
    List<Tenant> searchTenants(@Param("tenantName") String tenantName,
                               @Param("contactPerson") String contactPerson,
                               @Param("phone") String phone,
                               @Param("startDate") LocalDate startDate,
                               @Param("endDate") LocalDate endDate);

    @Select("SELECT MAX(id) FROM tenant")
    int getMaxTenantId();

    @Select("SELECT * FROM tenants")
    List<Tenant> getAllTenantNames(); // 使用新的方法名

    @Update("UPDATE tenants SET adminUsername = #{adminUsername}, password = #{password}, contactPerson = #{contactPerson}, phone = #{phone}, tenantName = #{tenantName}, createdAt = #{createdAt}, icon = #{icon}, remark = #{remark}, rootDepartmentId = #{rootDepartmentId} WHERE id = #{id}")
    void updateTenant3(Tenant tenant);

//    @Select("select * from tenants where tenantName=#{tenantName} and contactPerson=#{contactPerson}")
//    Tenant (String tenantName,String contactPerson);

    @Select("select * from tenants where tenantName=#{tenantName} and contactPerson=#{contactPerson}")
    Tenant reget(Tenant tenant);
}

