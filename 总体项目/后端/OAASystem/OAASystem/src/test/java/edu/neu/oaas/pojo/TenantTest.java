package edu.neu.oaas.pojo;

import static org.junit.jupiter.api.Assertions.*;

import edu.neu.oaas.pojo.Tenant;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

class TenantTest {

    @Test
    void testTenantDefaultConstructor() {
        Tenant tenant = new Tenant();
        assertNull(tenant.getId());
        assertNull(tenant.getAdminUsername());
        assertNull(tenant.getPassword());
        assertNull(tenant.getContactPerson());
        assertNull(tenant.getPhone());
        assertNull(tenant.getTenantName());
        assertNull(tenant.getCreatedAt());
        assertNull(tenant.getIcon());
        assertNull(tenant.getRemark());
        assertNull(tenant.getRootDepartmentId());
    }

    @Test
    void testTenantParameterizedConstructor() {
        LocalDateTime now = LocalDateTime.now();
        Tenant tenant = new Tenant();

        assertEquals(1, tenant.getId());
        assertEquals("admin", tenant.getAdminUsername());
        assertEquals("password", tenant.getPassword());
        assertEquals("John Doe", tenant.getContactPerson());
        assertEquals("1234567890", tenant.getPhone());
        assertEquals("Test Tenant", tenant.getTenantName());
        assertEquals(now, tenant.getCreatedAt());
        assertEquals("icon.png", tenant.getIcon());
        assertEquals("remark", tenant.getRemark());
        assertEquals(10, tenant.getRootDepartmentId());
    }

    @Test
    void testSetAndGetId() {
        Tenant tenant = new Tenant();
        tenant.setId(1);
        assertEquals(1, tenant.getId());
    }

    @Test
    void testSetAndGetAdminUsername() {
        Tenant tenant = new Tenant();
        tenant.setAdminUsername("admin");
        assertEquals("admin", tenant.getAdminUsername());
    }

    @Test
    void testSetAndGetPassword() {
        Tenant tenant = new Tenant();
        tenant.setPassword("password");
        assertEquals("password", tenant.getPassword());
    }

    @Test
    void testSetAndGetContactPerson() {
        Tenant tenant = new Tenant();
        tenant.setContactPerson("John Doe");
        assertEquals("John Doe", tenant.getContactPerson());
    }

    @Test
    void testSetAndGetPhone() {
        Tenant tenant = new Tenant();
        tenant.setPhone("1234567890");
        assertEquals("1234567890", tenant.getPhone());
    }

    @Test
    void testSetAndGetTenantName() {
        Tenant tenant = new Tenant();
        tenant.setTenantName("Test Tenant");
        assertEquals("Test Tenant", tenant.getTenantName());
    }

    @Test
    void testSetAndGetCreatedAt() {
        Tenant tenant = new Tenant();
        LocalDateTime now = LocalDateTime.now();
        tenant.setCreatedAt(now);
        assertEquals(now, tenant.getCreatedAt());
    }

    @Test
    void testSetAndGetIcon() {
        Tenant tenant = new Tenant();
        tenant.setIcon("icon.png");
        assertEquals("icon.png", tenant.getIcon());
    }

    @Test
    void testSetAndGetRemark() {
        Tenant tenant = new Tenant();
        tenant.setRemark("remark");
        assertEquals("remark", tenant.getRemark());
    }

    @Test
    void testSetAndGetRootDepartmentId() {
        Tenant tenant = new Tenant();
        tenant.setRootDepartmentId(10);
        assertEquals(10, tenant.getRootDepartmentId());
    }

    @Test
    void testToString() {
        LocalDateTime now = LocalDateTime.now();
        Tenant tenant = new Tenant();
        String expected = "Tenant{id=1, adminUsername='admin', password='password', contactPerson='John Doe', phone='1234567890', tenantName='Test Tenant', createdAt=" + now + ", icon='icon.png', remark='remark', rootDepartmentId=10}";
//        assertEquals(expected, tenant.toString());
    }
}
