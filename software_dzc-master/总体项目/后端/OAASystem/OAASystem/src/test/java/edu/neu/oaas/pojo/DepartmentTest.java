package edu.neu.oaas.pojo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class DepartmentTest {
    private Department department;
    private final LocalDateTime testTime = LocalDateTime.of(2024, 1, 1, 12, 0);

    @BeforeEach
    void setUp() {
        department = new Department();
    }

    @Test
    void testId() {
        Integer id = 1;
        department.setId(id);
        assertEquals(id, department.getId());
    }

    @Test
    void testDepartmentName() {
        String name = "Engineering";
        department.setDepartmentName(name);
        assertEquals(name, department.getDepartmentName());
    }

    @Test
    void testStatus() {
        String status = "Active";
        department.setStatus(status);
        assertEquals(status, department.getStatus());
    }

    @Test
    void testCreatedAt() {
        department.setCreatedAt(testTime);
        assertEquals(testTime, department.getCreatedAt());
    }

    @Test
    void testParentDepartment() {
        Integer parentId = 2;
        department.setParentDepartment(parentId);
        assertEquals(parentId, department.getParentDepartment());
    }

    @Test
    void testManager() {
        String manager = "John Doe";
        department.setManager(manager);
        assertEquals(manager, department.getManager());
    }

    @Test
    void testManagerPhone() {
        String phone = "1234567890";
        department.setManagerPhone(phone);
        assertEquals(phone, department.getManagerPhone());
    }

    @Test
    void testManagerEmail() {
        String email = "john.doe@example.com";
        department.setManagerEmail(email);
        assertEquals(email, department.getManagerEmail());
    }

    @Test
    void testTenantId() {
        Integer tenantId = 3;
        department.setTenantId(tenantId);
        assertEquals(tenantId, department.getTenantId());
    }

    @Test
    void testPath() {
        String path = "1_2_3";
        department.setPath(path);
        assertEquals(path, department.getPath());
    }

    @Test
    void testNullValues() {
        Department emptyDepartment = new Department();
        assertNull(emptyDepartment.getId());
        assertNull(emptyDepartment.getDepartmentName());
        assertNull(emptyDepartment.getStatus());
        assertNull(emptyDepartment.getCreatedAt());
        assertNull(emptyDepartment.getParentDepartment());
        assertNull(emptyDepartment.getManager());
        assertNull(emptyDepartment.getManagerPhone());
        assertNull(emptyDepartment.getManagerEmail());
        assertNull(emptyDepartment.getTenantId());
        assertNull(emptyDepartment.getPath());
    }
} 