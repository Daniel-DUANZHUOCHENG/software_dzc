package edu.neu.oaas.service;

import edu.neu.oaas.mapper.TenantMapper;
import edu.neu.oaas.mapper.UserMapper;
import edu.neu.oaas.pojo.Department;
import edu.neu.oaas.pojo.Tenant;
import edu.neu.oaas.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class TenantService {
    @Autowired
    private TenantMapper tenantMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private UserService userService;

    @Cacheable(value = "tenants", key = "'all'")
    public List<Tenant> getAll() {
        return tenantMapper.getAll();
    }

    @Cacheable(value = "tenants", key = "'name_' + #tenantName")
    public List<Tenant> getByName(String tenantName) {
        return tenantMapper.getByName(tenantName);
    }

    @Cacheable(value = "tenants", key = "#id")
    public Tenant getById(Integer id) {
        return tenantMapper.getById(id);
    }

    @Transactional
    @CacheEvict(value = "tenants", allEntries = true)
    public boolean deleteTenant(Integer id) {
        try {
            // 获取租户信息
            Tenant tenant = tenantMapper.getById(id);
            if (tenant == null) {
                throw new IllegalArgumentException("租户不存在");
            }

            // 删除相关的部门
            departmentService.deleteDepartmentByTenantId(tenant.getId());

            // 删除相关的用户
            userService.deleteUserByTenantId(tenant.getId());

            // 删除租户
            tenantMapper.deleteById(id);

            return true;
        } catch (Exception e) {
            throw new RuntimeException("Error deleting tenant: " + e.getMessage(), e);
        }
    }

    @CacheEvict(value = "tenants", allEntries = true)
    @CachePut(value = "tenants", key = "#tenant.id")
    public boolean updateTenant3(Tenant tenant) {
        try {
            if (tenant == null) {
                throw new IllegalArgumentException("Tenant cannot be null");
            }
            tenantMapper.updateTenant(tenant);
            return true;
        } catch (Exception e) {
            throw new RuntimeException("Error updating tenant: " + e.getMessage(), e);
        }
    }

    @Transactional
    @CacheEvict(value = "tenants", allEntries = true)
    public boolean insertTenant2(Tenant tenant) {
        try {
            // 插入租户
            tenant.setCreatedAt(LocalDateTime.now());
            tenantMapper.insertTenant(tenant);

            // // 插入部门
            // department.setTenantId(tenant.getId());
            // department.setCreatedAt(LocalDateTime.now());
            // departmentService.insertDepartment(department);

            // // 更新租户的根部门ID
            // Department insertedDepartment = departmentService.reget(0,
            // department.getDepartmentName());
            // tenant.setRootDepartmentId(insertedDepartment.getId());
            // tenantMapper.updateTenant(tenant);
            //
            // System.out.println(tenant.getId());
            // // 插入用户
            // user.setTenantId(tenant.getId());
            // user.setDepartmentId(insertedDepartment.getId());
            // userService.insertUser(user);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error inserting tenant: " + e.getMessage(), e);
        }
    }

    public boolean insertTenant(Tenant tenant, Department department, User user) {
        tenant.setCreatedAt(LocalDateTime.now());
        System.out.println(tenant.toString());
        tenantMapper.insertTenant2(tenant);
        System.out.println(tenant.getTenantName());
        System.out.println(tenant.getContactPerson());
        Tenant tenant1 = tenantMapper.reget(tenant);
        System.out.println(tenant1.toString());
        department.setCreatedAt(LocalDateTime.now());
        department.setParentDepartment(1);// 测盟会总部门
        department.setTenantId(tenant1.getId());
        departmentService.insertDepartment(department);
        Department department1 = departmentService.reget(1, department.getDepartmentName());
        System.out.println(department1.toString());
        tenant1.setRootDepartmentId(department1.getId());
        updateTenant(tenant1);
        user.setTenantId(tenant1.getId());
        user.setDepartmentId(department1.getId());
        userService.insertUser(user);
        return true;
    }

    // 更新租户信息
    public boolean updateTenant(Tenant tenant) {
        try {
            if (tenant == null) {
                throw new IllegalArgumentException("Tenant cannot be null");
            }
            tenantMapper.updateTenant(tenant);
            System.out.println("Updated tenant with root department ID: " + tenant);
            return true;
        } catch (Exception e) {
            System.err.println("Error updating tenant: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateTenant2(Tenant tenant) {
        tenantMapper.updateTenant2(tenant.getAdminUsername(),
                tenant.getPassword(),
                tenant.getContactPerson(),
                tenant.getPhone(),
                tenant.getTenantName(),
                tenant.getCreatedAt(),
                tenant.getIcon(),
                tenant.getRemark(),
                tenant.getRootDepartmentId(),
                tenant.getId());
        return true;
    }

    public boolean delete(Integer id) {
        Tenant tenant = tenantMapper.getById(id);
        departmentService.delete(tenant.getRootDepartmentId());
        tenantMapper.deleteById(id);
        return true;
    }

    public List<Tenant> searchTenants(String tenantName, String contactPerson, String phone, LocalDate startDate,
            LocalDate endDate) {
        return tenantMapper.searchTenants(tenantName, contactPerson, phone, startDate, endDate);
    }

    public void registerTenantAndUser(String tenantName, String contactperson, String Phone, String contactEmail,
            String username, String password) {
        if (tenantMapper.getTenantByName(tenantName) != null) {
            throw new IllegalArgumentException("Tenant already exists");
        }

        if (userMapper.getUserByUsername(username) != null || userMapper.getUserByEmail(contactEmail) != null) {
            throw new IllegalArgumentException("Username or Email already exists");
        }

        Tenant tenant = new Tenant();
        tenant.setAdminUsername(username);
        tenant.setTenantName(tenantName);
        tenant.setPassword(password);
        tenant.setContactPerson(contactperson);
        tenant.setPhone(Phone);
        tenant.setCreatedAt(new Timestamp(System.currentTimeMillis()).toLocalDateTime());
        tenantMapper.insertTenant2(tenant);

        int tenantId = tenant.getId();

        User user = new User();
        user.setUsername(username);
        user.setPassword(password); // Ensure the password is hashed
        user.setEmail(contactEmail);
        user.setPhoneNumber(Phone);
        user.setNickname(contactperson);
        user.setRole("Admin");
        user.setStatus("Active");
        user.setCreatedAt(new Timestamp(System.currentTimeMillis()).toLocalDateTime());
        user.setTenantId(tenantId);
        userMapper.insertUser2(user);
    }

    private static final String ICON_BASE_PATH = "src/main/resources/static/icons/";

    public String saveIcon(MultipartFile file) throws IOException {
        // 确保目录存在
        File directory = new File(ICON_BASE_PATH);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        String filePath = ICON_BASE_PATH + fileName;

        File dest = new File(filePath);
        if (!dest.getParentFile().exists()) {
            dest.getParentFile().mkdirs();
        }

        file.transferTo(dest);
        return "/icons/" + fileName;
    }

    public List<Tenant> getAllTenantNames() {
        return tenantMapper.getAllTenantNames(); // 使用新的方法名
    }
}
