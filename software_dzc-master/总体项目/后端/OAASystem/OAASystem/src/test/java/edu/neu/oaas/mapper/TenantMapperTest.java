package edu.neu.oaas.mapper;

import edu.neu.oaas.mapper.TenantMapper;
import edu.neu.oaas.pojo.Tenant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@Transactional
public class TenantMapperTest {

    @Autowired
    private TenantMapper tenantMapper;

    private Tenant tenant;

    @BeforeEach
    public void setUp() {
        tenant = new Tenant();
        tenant.setAdminUsername("admin");
        tenant.setPassword("password");
        tenant.setContactPerson("John Doe");
        tenant.setPhone("1234567890");
        tenant.setTenantName("TestTenant");
        tenant.setCreatedAt(LocalDateTime.now());
        tenant.setIcon("icon.png");
        tenant.setRemark("Remark");
        tenant.setRootDepartmentId(1);
    }

    @Test
    public void testInsertTenant() {
        tenantMapper.insertTenant2(tenant);
        assertNotNull(tenant.getId());
        Tenant found = tenantMapper.getById(tenant.getId());
        assertNotNull(found);
        assertEquals("admin", found.getAdminUsername());
    }

    @Test
    public void testGetTenantByName() {
        tenantMapper.insertTenant2(tenant);
        Tenant found = tenantMapper.getTenantByName("TestTenant");
        assertNotNull(found);
        assertEquals("admin", found.getAdminUsername());
    }

    @Test
    public void testGetAllTenants() {
        tenantMapper.insertTenant2(tenant);
        List<Tenant> tenants = tenantMapper.getAllTenants();
        assertFalse(tenants.isEmpty());
    }

    @Test
    public void testUpdateTenant() {
        tenantMapper.insertTenant2(tenant);
        tenant.setAdminUsername("newAdmin");
        tenantMapper.updateTenant(tenant);
        Tenant updated = tenantMapper.getById(tenant.getId());
        assertEquals("newAdmin", updated.getAdminUsername());
    }

    @Test
    public void testDeleteTenant() {
        tenantMapper.insertTenant2(tenant);
        tenantMapper.deleteById(tenant.getId());
        Tenant deleted = tenantMapper.getById(tenant.getId());
        assertNull(deleted);
    }

    @Test
    public void testFindAll() {
        tenantMapper.insertTenant2(tenant);
        List<Tenant> tenants = tenantMapper.findAll();
        assertFalse(tenants.isEmpty());
    }

    @Test
    public void testGetAll() {
        tenantMapper.insertTenant2(tenant);
        List<Tenant> tenants = tenantMapper.getAll();
        assertFalse(tenants.isEmpty());
    }

    @Test
    public void testGetById() {
        tenantMapper.insertTenant2(tenant);
        Tenant found = tenantMapper.getById(tenant.getId());
        assertNotNull(found);
    }

    @Test
    public void testGetByName() {
        tenantMapper.insertTenant2(tenant);
        List<Tenant> tenants = tenantMapper.getByName("TestTenant");
        assertFalse(tenants.isEmpty());
        assertEquals("admin", tenants.get(0).getAdminUsername());
    }

    @Test
    public void testInsertTenantWithParams() {
        Tenant newTenant = new Tenant();
        newTenant.setAdminUsername("admin");
        newTenant.setPassword("password");
        newTenant.setContactPerson("John Doe");
        newTenant.setPhone("1234567890");
        newTenant.setTenantName("TestTenant");
        newTenant.setCreatedAt(LocalDateTime.now());
        newTenant.setIcon("icon.png");
        newTenant.setRemark("Remark");
        newTenant.setRootDepartmentId(1);
        
        tenantMapper.insertTenant(newTenant);
        List<Tenant> tenants = tenantMapper.getByName("TestTenant");
        assertFalse(tenants.isEmpty());
        assertEquals("admin", tenants.get(0).getAdminUsername());
    }

    @Test
    public void testReget() {
        tenantMapper.insertTenant2(tenant);
        Tenant regetTenant = new Tenant();
        regetTenant.setTenantName("TestTenant");
        regetTenant.setContactPerson("John Doe");
        Tenant found = tenantMapper.reget(regetTenant);
        assertNotNull(found);
        assertEquals("admin", found.getAdminUsername());
    }
}
