package edu.neu.oaas.pojo;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Tenant implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer id;
    private String adminUsername;
    private String password;
    private String contactPerson;
    private String phone;
    private String tenantName;
    private LocalDateTime createdAt;
    private String icon;
    private String remark;
    private Integer rootDepartmentId;

    public Integer getRootDepartmentId() {
        return rootDepartmentId;
    }

    public void setRootDepartmentId(Integer rootDepartmentId) {
        this.rootDepartmentId = rootDepartmentId;
    }

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAdminUsername() {
        return adminUsername;
    }

    public void setAdminUsername(String adminUsername) {
        this.adminUsername = adminUsername;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getTenantName() {
        return tenantName;
    }

    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }


}
