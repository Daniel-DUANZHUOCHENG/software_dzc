package edu.neu.oaas.mapper;

import edu.neu.oaas.pojo.Department;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface DepartmentMapper {
    @Insert("INSERT INTO departments (departmentName, status, createdat, parentdepartment, manager, managerphone, manageremail,tenantId) " +
            "VALUES (#{departmentName}, #{status}, #{createdAt}, #{parentDepartment}, #{manager}, #{managerPhone}, #{managerEmail},#{tenantId})")
    @Options(useGeneratedKeys = true, keyProperty = "Id")
    void insertDepartment2(Department department);


    @Select("SELECT * FROM departments")
    List<Department> findAll();

    @Select("SELECT * FROM departments WHERE Id = #{departmentId}")
    Department getDepartmentById(@Param("departmentId") int departmentId);

    @Select("SELECT * FROM departments WHERE TenantId = #{tenantId}")
    List<Department> getDepartmentsByTenantId(int tenantId);


    @Select("select *from departments where path LIKE CONCAT(#{path}, '%') ")
    List<Department> getAllByPrefix(@Param("path") String pathPrefix);//限定范围内所有部门


//    @Select("select *from departments where path LIKE CONCAT(#{path}, '%') and  departmentName LIKE CONCAT('%', #{departmentName}, '%')")
//    List<Department> getAllByPrefixAndName(String path,String departmentName);//限定范围内名称模糊匹配符合的所有部门

    @Select("SELECT * FROM departments WHERE path LIKE CONCAT(#{path}, '%') AND departmentName LIKE CONCAT('%', #{departmentName}, '%') AND Status = #{status}")
    List<Department> getAllByPrefixAndName(@Param("path") String path, @Param("departmentName") String departmentName, @Param("status") String status);

//    @Select("select *from departments where id=#{id}")
//    Department getDepartmentsById(Integer id);

//    @Select("select *from departments where parentDepartment=#{parentDepartment} and departmentName=#{departmentName}")
//    Department reget(Integer parentDepartment,String departmentName);

    @Select("select *from departments where parentDepartment=#{parentDepartment} and departmentName=#{departmentName}")
    Department reget(@Param("parentDepartment") Integer parentDepartment,@Param("departmentName") String departmentName);

//        @Select("SELECT * FROM departments")
//        List<Department> getAllDepartments();


    @Delete("DELETE FROM departments WHERE Id = #{departmentId}")
    int deleteDepartment(@Param("departmentId") int departmentId);



    //部门操作涉及的只有新建,删除部门时删除子部门及员工,查找只通过id和名称(由root域决定)的模糊匹配
    //修改

    @Insert("INSERT INTO departments (id,departmentName, status, createdAt, parentDepartment, manager, managerPhone, managerEmail,path,tenantId) " +
            "VALUES (null,#{departmentName}, #{status}, #{createdAt}, #{parentDepartment}, #{manager}, #{managerPhone}, #{managerEmail},#{path},#{tenantId})")
    void insertDepartment(@Param("departmentName") String departmentName,
                          @Param("status") String status,
                          @Param("createdAt") LocalDateTime createdAt,
                          @Param("parentDepartment") Integer parentDepartment,
                          @Param("manager") String manager,
                          @Param("managerPhone") String managerPhone,
                          @Param("managerEmail") String managerEmail,
                          @Param("path") String path,
                          @Param("tenantId")Integer tenantId);//插入新部门

    @Update("UPDATE departments SET departmentName = #{departmentName}, status = #{status}, createdAt = #{createdAt}, " +
            "parentDepartment = #{parentDepartment}, manager = #{manager}, managerPhone = #{managerPhone}, managerEmail = #{managerEmail}, path = #{path} ,tenantId=#{tenantId}" +
            " WHERE id = #{id}")
    void updateDepartment(@Param("id") Integer id,
                          @Param("departmentName") String departmentName,
                          @Param("status") String status,
                          @Param("createdAt") LocalDateTime createdAt,
                          @Param("parentDepartment") Integer parentDepartment,
                          @Param("manager") String manager,
                          @Param("managerPhone") String managerPhone,
                          @Param("managerEmail") String managerEmail,
                          @Param("path") String path,
                          @Param("tenantId")Integer tenantId);
    @Delete("delete from departments where  path LIKE CONCAT(#{path}, '%')")
    void delete(String path);

    @Delete("DELETE FROM departments WHERE tenantId = #{tenantId}")
    void deleteByTenantId(Integer tenantId);

}







