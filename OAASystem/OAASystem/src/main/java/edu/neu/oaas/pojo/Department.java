package edu.neu.oaas.pojo;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Department implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer id;
    private String departmentName;
    private String status;
    private LocalDateTime createdAt;
    private Integer parentDepartment;
    private String manager;
    private String managerPhone;
    private String managerEmail;

    private Integer tenantId;

    private String path;

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Integer getParentDepartment() {
        return parentDepartment;
    }

    public void setParentDepartment(Integer parentDepartment) {
        this.parentDepartment = parentDepartment;
    }

    public String getManager() {
        return manager;
    }

    public void setManager(String manager) {
        this.manager = manager;
    }

    public String getManagerPhone() {
        return managerPhone;
    }

    public void setManagerPhone(String managerPhone) {
        this.managerPhone = managerPhone;
    }

    public String getManagerEmail() {
        return managerEmail;
    }

    public void setManagerEmail(String managerEmail) {
        this.managerEmail = managerEmail;
    }

    public Integer getTenantId(){return tenantId;}

    public void setTenantId(Integer tenantId){this.tenantId = tenantId;}

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
