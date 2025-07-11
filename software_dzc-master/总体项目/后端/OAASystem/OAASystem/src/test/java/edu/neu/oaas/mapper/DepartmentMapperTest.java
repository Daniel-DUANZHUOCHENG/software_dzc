package edu.neu.oaas.mapper;

import edu.neu.oaas.pojo.Department;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@DisplayName("Department Mapper Tests")
class DepartmentMapperTest {

    @Autowired
    private DepartmentMapper departmentMapper;

    private Department createTestDepartment(String name, Integer parentId, Integer tenantId) {
        Department department = new Department();
        department.setDepartmentName(name);
        department.setStatus("active");
        department.setCreatedAt(LocalDateTime.now());
        department.setParentDepartment(parentId);
        department.setManager("John Doe");
        department.setManagerPhone("1234567890");
        department.setManagerEmail("john@example.com");
        department.setTenantId(tenantId);
        return department;
    }

    @Test
    @DisplayName("Should find all departments")
    void testFindAllDepartments() {
        // Create and insert test departments
        Department dept1 = createTestDepartment("HR", 0, 1);
        Department dept2 = createTestDepartment("IT", 0, 1);

        assertDoesNotThrow(() -> {
            departmentMapper.insertDepartment2(dept1);
            departmentMapper.insertDepartment2(dept2);
        });

        // Retrieve all departments
        List<Department> departments = departmentMapper.findAll();
        
        // Verify
        assertFalse(departments.isEmpty());
        assertTrue(departments.stream().anyMatch(d -> d.getDepartmentName().equals("HR")));
        assertTrue(departments.stream().anyMatch(d -> d.getDepartmentName().equals("IT")));
    }

    @Test
    @DisplayName("Should search departments by name and status")
    void testSearchDepartmentsByNameAndStatus() {
        // Create and insert test departments
        Department dept1 = createTestDepartment("HR Department", 0, 1);
        Department dept2 = createTestDepartment("HR Services", 0, 1);
        Department dept3 = createTestDepartment("IT Department", 0, 1);
        dept3.setStatus("inactive");

        assertDoesNotThrow(() -> {
            departmentMapper.insertDepartment2(dept1);
            departmentMapper.insertDepartment2(dept2);
            departmentMapper.insertDepartment2(dept3);
        });

        // Search by name and status
        List<Department> hrActiveDepts = departmentMapper.getAllByPrefixAndName("", "HR", "active");
        
        // Verify
        assertEquals(2, hrActiveDepts.size());
        assertTrue(hrActiveDepts.stream().allMatch(d -> d.getDepartmentName().contains("HR")));
        assertTrue(hrActiveDepts.stream().allMatch(d -> "active".equals(d.getStatus())));
    }

    @Test
    @DisplayName("Should retrieve departments by path prefix")
    void testRetrieveDepartmentsByPathPrefix() {
        // Create and insert test departments with paths
        Department dept1 = createTestDepartment("HR", 0, 1);
        dept1.setPath("/company/hr");
        Department dept2 = createTestDepartment("Payroll", 1, 1);
        dept2.setPath("/company/hr/payroll");
        Department dept3 = createTestDepartment("IT", 0, 1);
        dept3.setPath("/company/it");

        assertDoesNotThrow(() -> {
            departmentMapper.insertDepartment2(dept1);
            departmentMapper.insertDepartment2(dept2);
            departmentMapper.insertDepartment2(dept3);
        });

        // Retrieve by path prefix
        List<Department> hrDepts = departmentMapper.getAllByPrefix("/company/hr");
        
        // Verify
        assertEquals(2, hrDepts.size());
        assertTrue(hrDepts.stream().allMatch(d -> d.getPath().startsWith("/company/hr")));
    }

    @Test
    @DisplayName("Should retrieve departments by tenant ID")
    void testRetrieveDepartmentsByTenantId() {
        // Create and insert test departments
        Department dept1 = createTestDepartment("HR", 0, 1);
        Department dept2 = createTestDepartment("IT", 0, 1);
        Department dept3 = createTestDepartment("Sales", 0, 2);

        assertDoesNotThrow(() -> {
            departmentMapper.insertDepartment2(dept1);
            departmentMapper.insertDepartment2(dept2);
            departmentMapper.insertDepartment2(dept3);
        });

        // Retrieve by tenant ID
        List<Department> tenant1Depts = departmentMapper.getDepartmentsByTenantId(1);
        List<Department> tenant2Depts = departmentMapper.getDepartmentsByTenantId(2);
        
        // Verify
        assertEquals(2, tenant1Depts.size());
        assertEquals(1, tenant2Depts.size());
        assertTrue(tenant1Depts.stream().allMatch(d -> d.getTenantId() == 1));
        assertTrue(tenant2Depts.stream().allMatch(d -> d.getTenantId() == 2));
    }

    @Test
    @DisplayName("Should insert and retrieve department")
    void testInsertAndRetrieveDepartment() {
        // Create test department
        Department dept = createTestDepartment("Test Department", 0, 1);
        
        // Insert department
        assertDoesNotThrow(() -> departmentMapper.insertDepartment2(dept));
        assertNotNull(dept.getId());

        // Retrieve department
        Department retrieved = departmentMapper.getDepartmentById(dept.getId());
        
        // Verify
        assertNotNull(retrieved);
        assertEquals(dept.getDepartmentName(), retrieved.getDepartmentName());
        assertEquals(dept.getManager(), retrieved.getManager());
        assertEquals(dept.getManagerPhone(), retrieved.getManagerPhone());
        assertEquals(dept.getManagerEmail(), retrieved.getManagerEmail());
        assertEquals(dept.getTenantId(), retrieved.getTenantId());
    }
}