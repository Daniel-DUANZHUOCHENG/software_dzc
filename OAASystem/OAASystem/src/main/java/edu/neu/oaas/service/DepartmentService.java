package edu.neu.oaas.service;

import edu.neu.oaas.mapper.DepartmentMapper;
import edu.neu.oaas.mapper.UserMapper;
import edu.neu.oaas.pojo.Department;
import edu.neu.oaas.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class DepartmentService {
    @Autowired
    private DepartmentMapper departmentMapper;


    @Autowired
    private UserMapper userMapper;




    @Cacheable(value = "departments", key = "'prefix_' + #pathPrefix")
    public List<Department> getAllByPrefix(String pathPrefix){
        return departmentMapper.getAllByPrefix(pathPrefix);
    }

//    public List<Department> getAllByPrefixAndName(String pathPrefix,String departmentName){
//        return departmentMapper.getAllByPrefixAndName(pathPrefix, departmentName);
//    }

    @Cacheable(value = "departments", key = "'search_' + #path + '_' + #departmentName + '_' + #status")
    public List<Department> getAllByPrefixAndName(String path, String departmentName, String status) {
        return departmentMapper.getAllByPrefixAndName(path, departmentName, status);
    }

    @Cacheable(value = "departments", key = "'parent_' + #parentDepartment + '_name_' + #departmentName")
    public Department reget(Integer parentDepartment, String departmentName) {
        System.out.println("reget:" + parentDepartment);
        System.out.println("reget:" + departmentName);
        return departmentMapper.reget(parentDepartment, departmentName);
    }

    @Cacheable(value = "departments", key = "#id")
    public Department getDepartmentsById(Integer id) {
        return departmentMapper.getDepartmentById(id);
    }

    @CacheEvict(value = "departments", allEntries = true)
    public boolean insertDepartment(Department department) {
        // 设置创建时间为当前时间
        department.setCreatedAt(LocalDateTime.now());
        // 调用Mapper层的方法插入新部门
        departmentMapper.insertDepartment(
                department.getDepartmentName(),
                department.getStatus(),
                department.getCreatedAt(),
                department.getParentDepartment(),
                department.getManager(),
                department.getManagerPhone(),
                department.getManagerEmail(),
                null,
                department.getTenantId()
        );

        Department department1 = reget(department.getParentDepartment(), department.getDepartmentName());
        System.out.println("**********");
        department1.setPath(getDepartmentsById(department1.getParentDepartment()).getPath() + "_" + department1.getId());
        updateDepartment(department1);
        return true;
    }

    @CacheEvict(value = {"departments", "users", "userList"}, allEntries = true)
    public boolean updateDepartment(Department department) {
        System.out.println(department.toString());
        Department d = departmentMapper.getDepartmentById(department.getId());
        Integer parent = d.getParentDepartment();
        if (parent != department.getParentDepartment()) {
            String prefix = department.getPath();
            String replacePath = departmentMapper.getDepartmentById(department.getParentDepartment()).getPath()+"_"+department.getId();
            System.out.println(prefix);
            System.out.println(replacePath);
            List<Department> departments = departmentMapper.getAllByPrefix(d.getPath());
            for (Department department1 : departments) {
                department1.setPath(department1.getPath().replaceFirst(prefix, replacePath));
                departmentMapper.updateDepartment(
                        department1.getId(),
                        department1.getDepartmentName(),
                        department1.getStatus(),
                        department1.getCreatedAt(),
                        department1.getParentDepartment(),
                        department1.getManager(),
                        department1.getManagerPhone(),
                        department1.getManagerEmail(),
                        department1.getPath(),
                        department1.getTenantId()
                );
            }
            List<User> users = userMapper.findByPathPrefix(prefix);
            for (User user : users) {
                user.setPath(user.getPath().replaceFirst(prefix,replacePath));
                userMapper.updateUser(
                        user.getId(),
                        user.getUsername(),
                        user.getPassword(),
                        user.getNickname(),
                        user.getPhoneNumber(),
                        user.getEmail(),
                        user.getGender(),
                        user.getDepartmentId(),
                        user.getStatus(),
                        user.getRole(),
                        user.getCreatedAt(),
                        user.getPosition(),
                        user.getRemark(),
                        user.getAvatar(),
                        user.getTenantId(),
                        user.getPath()
                );
            }
        }

        departmentMapper.updateDepartment(
                department.getId(),
                department.getDepartmentName(),
                department.getStatus(),
                department.getCreatedAt(),
                department.getParentDepartment(),
                department.getManager(),
                department.getManagerPhone(),
                department.getManagerEmail(),
                departmentMapper.getDepartmentById(department.getParentDepartment()).getPath()+"_"+department.getId(),
                department.getTenantId()
        );


        System.out.println("#############################");
        return true;
    }

    @CacheEvict(value = {"departments", "users", "userList"}, allEntries = true)
    public boolean delete(Integer id) {
        String pathPrefix = departmentMapper.getDepartmentById(id).getPath();
        departmentMapper.delete(pathPrefix);
        userMapper.deleteByPath(pathPrefix);
        return true;
    }

    //我的代码
    @Transactional
    @CacheEvict(value = "departments", allEntries = true)
    public void addDepartment(Department department, int tenantId) {
        departmentMapper.insertDepartment2(department);

    }

    @Cacheable(value = "departments", key = "'all'")
    public List<Department> getAllDepartments() {
        return departmentMapper.findAll();
    }

    @Cacheable(value = "departments", key = "#departmentId")
    public Department getDepartmentById(int departmentId) {
        return departmentMapper.getDepartmentById(departmentId);
    }

    @CacheEvict(value = "departments", allEntries = true)
    public void deleteDepartment(int departmentId) {
        departmentMapper.deleteDepartment(departmentId);
    }

    @Cacheable(value = "departments", key = "'tenant_' + #tenantId")
    public List<Department> getDepartmentsByTenantId(int tenantId) {
        return departmentMapper.getDepartmentsByTenantId(tenantId);
    }

    @Transactional
    @CacheEvict(value = "departments", allEntries = true)
    public void deleteDepartmentByTenantId(Integer tenantId) {
        departmentMapper.deleteByTenantId(tenantId);
    }




}



