package edu.neu.oaas.pojo;

import java.io.Serializable;

public class RegistrationRequest implements Serializable {
    private static final long serialVersionUID = 1L;
    private Tenant tenant;
    private Department department;
    private User user;

    public Tenant getTenant() {
        return tenant;
    }

    public void setTenant(Tenant tenant) {
        this.tenant = tenant;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
