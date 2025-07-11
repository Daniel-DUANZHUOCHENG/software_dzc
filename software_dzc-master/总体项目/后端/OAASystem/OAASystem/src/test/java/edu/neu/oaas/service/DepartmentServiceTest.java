package edu.neu.oaas.service;

import edu.neu.oaas.mapper.DepartmentMapper;
import edu.neu.oaas.mapper.UserMapper;
import edu.neu.oaas.pojo.Department;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.*;

public class DepartmentServiceTest {

    @Mock
    private DepartmentMapper departmentMapper;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private DepartmentService departmentService;

    private Department department;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        department = new Department();
        department.setId(1);
        department.setDepartmentName("Test Department");
        department.setParentDepartment(0);
        department.setTenantId(1);
        department.setPath("/1");
        department.setStatus("Active");
        department.setCreatedAt(LocalDateTime.now());
    }

    @Test
    public void testDeleteDepartment() {
        when(departmentMapper.getDepartmentById(1)).thenReturn(department);
        doNothing().when(userMapper).deleteByPath(department.getPath());
        doNothing().when(departmentMapper).delete(department.getPath());
        
        boolean result = departmentService.delete(1);
        assertTrue(result);
        verify(departmentMapper).getDepartmentById(1);
        verify(userMapper).deleteByPath(department.getPath());
        verify(departmentMapper).delete(department.getPath());
    }

    @Test
    public void testUpdateDepartment() {
        // Mock existing department
        Department existingDept = new Department();
        existingDept.setId(1);
        existingDept.setPath("/1");
        when(departmentMapper.getDepartmentById(1)).thenReturn(existingDept);
        
        // Mock parent department
        Department parentDept = new Department();
        parentDept.setId(0);
        parentDept.setPath("/0");
        when(departmentMapper.getDepartmentById(0)).thenReturn(parentDept);
        
        // Setup department to update
        Department updateDept = new Department();
        updateDept.setId(1);
        updateDept.setDepartmentName("Updated Dept");
        updateDept.setStatus("Active");
        updateDept.setCreatedAt(LocalDateTime.now());
        updateDept.setParentDepartment(0);
        updateDept.setPath("/1");
        updateDept.setTenantId(1);
        
        // Mock the reget method
        when(departmentMapper.reget(0, "Updated Dept")).thenReturn(updateDept);
        
        // Mock getAllByPrefix for path updates
        when(departmentMapper.getAllByPrefix("/1")).thenReturn(Arrays.asList(updateDept));
        
        // Mock findByPathPrefix for user path updates
        when(userMapper.findByPathPrefix("/1")).thenReturn(Arrays.asList());
        
        // Mock both updateDepartment calls
        doNothing().when(departmentMapper).updateDepartment(
            eq(1),
            eq("Updated Dept"),
            eq("Active"),
            any(LocalDateTime.class),
            eq(0),
            isNull(),
            isNull(),
            isNull(),
            eq("/0_1"),
            eq(1)
        );
        
        boolean result = departmentService.updateDepartment(updateDept);
        assertTrue(result);
        
        // Verify both updateDepartment calls
        verify(departmentMapper, times(2)).updateDepartment(
            eq(1),
            eq("Updated Dept"),
            eq("Active"),
            any(LocalDateTime.class),
            eq(0),
            isNull(),
            isNull(),
            isNull(),
            eq("/0_1"),
            eq(1)
        );
    }

    @Test
    public void testGetDepartmentsByTenantId() {
        List<Department> departments = Arrays.asList(department);
        when(departmentMapper.getDepartmentsByTenantId(1)).thenReturn(departments);
        
        List<Department> result = departmentService.getDepartmentsByTenantId(1);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        verify(departmentMapper, times(1)).getDepartmentsByTenantId(1);
    }

    @Test
    public void testInsertDepartment() {
        // Mock parent department
        Department parentDept = new Department();
        parentDept.setId(1);
        parentDept.setPath("/1");
        when(departmentMapper.getDepartmentById(1)).thenReturn(parentDept);
        
        // Setup new department
        Department newDept = new Department();
        newDept.setDepartmentName("New Dept");
        newDept.setParentDepartment(1);
        newDept.setStatus("Active");
        newDept.setCreatedAt(LocalDateTime.now());
        newDept.setTenantId(1);
        
        // Mock the reget method to return the newly created department
        Department regetDept = new Department();
        regetDept.setId(2);
        regetDept.setDepartmentName("New Dept");
        regetDept.setParentDepartment(1);
        regetDept.setStatus("Active");
        regetDept.setCreatedAt(newDept.getCreatedAt());
        regetDept.setTenantId(1);
        when(departmentMapper.reget(eq(1), eq("New Dept"))).thenReturn(regetDept);
        
        // Mock getDepartmentById for the parent lookup during path construction
        when(departmentMapper.getDepartmentById(regetDept.getParentDepartment())).thenReturn(parentDept);
        
        // Mock the void methods
        doNothing().when(departmentMapper).insertDepartment(
            eq("New Dept"),
            eq("Active"),
            any(LocalDateTime.class),
            eq(1),
            isNull(),
            isNull(),
            isNull(),
            isNull(),
            eq(1)
        );
        
        doNothing().when(departmentMapper).updateDepartment(
            eq(2),
            eq("New Dept"),
            eq("Active"),
            any(LocalDateTime.class),
            eq(1),
            isNull(),
            isNull(),
            isNull(),
            eq("/1_2"),
            eq(1)
        );
        
        // Execute test
        boolean result = departmentService.insertDepartment(newDept);
        assertTrue(result);
        
        // Verify interactions
        verify(departmentMapper).insertDepartment(
            eq("New Dept"),
            eq("Active"),
            any(LocalDateTime.class),
            eq(1),
            isNull(),
            isNull(),
            isNull(),
            isNull(),
            eq(1)
        );
        
        verify(departmentMapper).updateDepartment(
            eq(2),
            eq("New Dept"),
            eq("Active"),
            any(LocalDateTime.class),
            eq(1),
            isNull(),
            isNull(),
            isNull(),
            eq("/1_2"),
            eq(1)
        );
    }

    @Test
    public void testDelete() {
        when(departmentMapper.getDepartmentById(1)).thenReturn(department);
        doNothing().when(userMapper).deleteByPath(department.getPath());
        doNothing().when(departmentMapper).delete(department.getPath());
        
        boolean result = departmentService.delete(1);
        assertTrue(result);
        verify(departmentMapper).getDepartmentById(1);
        verify(userMapper).deleteByPath(department.getPath());
        verify(departmentMapper).delete(department.getPath());
    }

    @Test
    public void testGetDepartmentById() {
        when(departmentMapper.getDepartmentById(1)).thenReturn(department);
        
        Department result = departmentService.getDepartmentById(1);
        assertNotNull(result);
        assertEquals(1, result.getId());
        verify(departmentMapper, times(1)).getDepartmentById(1);
    }

    @Test
    public void testGetAllDepartments() {
        List<Department> departments = Arrays.asList(department);
        when(departmentMapper.findAll()).thenReturn(departments);
        
        List<Department> result = departmentService.getAllDepartments();
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        verify(departmentMapper, times(1)).findAll();
    }
} 