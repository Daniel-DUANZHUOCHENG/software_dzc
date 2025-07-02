package edu.neu.oaas.controller;


import edu.neu.oaas.pojo.Department;
import edu.neu.oaas.service.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;




@RestController
@RequestMapping("/departments")
public class DepartmentController {

    @Autowired
    private DepartmentService departmentService;

//    @GetMapping
//    public List<Department> getDepartmentsByTenantId(@RequestParam int tenantId) {
//        return departmentService.getDepartmentsByTenantId(tenantId);
//    }

    @GetMapping
    public List<Department> getDepartmentsByTenantId(@RequestParam Integer departementId) {
        System.out.println(departementId);
        String pathPrefix = departmentService.getDepartmentById(departementId).getPath();
        return departmentService.getAllByPrefix(pathPrefix);
    }


    @GetMapping("/getall")
    public Map getAllByPrefix(@RequestParam String path){
        Map map = new HashMap<>();
        System.out.println("1 " +path);
        List<Department> departments = departmentService.getAllByPrefix(path);
        System.out.println("2 " +path);
        map.put("departmentList",departments);
        map.put("isOK",true);
        return map;
    }

    //这里修改,根据管理员所属部门id来返回
    @GetMapping("/get")
    public Map getAllByDepartmentId(@RequestParam Integer id){
        String pathPrefix = departmentService.getDepartmentsById(id).getPath();
        Map map = new HashMap<>();
        List<Department> departments = departmentService.getAllByPrefix(pathPrefix);
        map.put("departmentList",departments);
        map.put("isOK",true);
        return map;
    }



    @GetMapping("/gets/{tmp}")
    public Map getAllByPre(@PathVariable String tmp){
        Map map = new HashMap<>();
        List<Department> departments = departmentService.getAllByPrefix(tmp);
        System.out.println(departments);
        map.put("departmentList",departments);
        map.put("isOK",true);
        return map;
    }

    @GetMapping("/search")
    public Map getAllByPrefixAndName(@RequestParam String departmentName,@RequestParam String status,@RequestParam Integer departementId){
        System.out.println(departementId);
        System.out.println(departmentName);
        System.out.println(status);
        String pathPrefix = departmentService.getDepartmentsById(departementId).getPath();
        System.out.println(pathPrefix);
        Map map = new HashMap<>();
        List<Department> departments = departmentService.getAllByPrefixAndName(pathPrefix,departmentName,status);
        map.put("departmentList",departments);
        map.put("isOK",true);
        return map;
    }

    //新建
    @RequestMapping("/search/{departmentName}/{status}/{departementId}")
    public Map agetAllByPrefixAndName(@RequestParam String departmentName,@RequestParam String status,@RequestParam Integer departementId){
        System.out.println(departementId);
        System.out.println(departmentName);
        System.out.println(status);
        String pathPrefix = departmentService.getDepartmentsById(departementId).getPath();
        System.out.println(pathPrefix);
        Map map = new HashMap<>();
        List<Department> departments = departmentService.getAllByPrefixAndName(pathPrefix,departmentName,status);
        map.put("departmentList",departments);
        map.put("isOK",true);
        return map;
    }

//    @RequestMapping("/search")
//    public Map getAllByPrefixAndName(@RequestParam String pathPrefix,@RequestParam String departmentName){
//        Map map = new HashMap<>();
//        List<Department> departments = departmentService.getAllByPrefixAndName(pathPrefix,departmentName);
//        map.put("departmentList",departments);
//        map.put("isOK",true);
//        return map;
//    }


    @GetMapping("/get/{id}")
    public Map getDepartmentsById(@PathVariable Integer id) {
        Map map = new HashMap<>();
        Department department = departmentService.getDepartmentsById(id);
        if (department == null) {
            map.put("isOK", false);
            map.put("msg", "好像没找到");
        } else {
            map.put("isOK", true);
            map.put("department", department);
        }
        return map;

    }

    @PostMapping("/insert")
    public Map insertDepartment(@RequestBody Department department){
        Map map = new HashMap<>();
        if (departmentService.insertDepartment(department)){
            map.put("isOK",true);
        }else {
            map.put("isOK",false);
            map.put("msg","系统故障");
        }
//        Department department1 = departmentService.reget(department.getParentDepartment(),department.getDepartmentName());
//        Integer id = department1.getParentDepartment();
//        department1.setPath(departmentService.getDepartmentsById(id).getPath()+"_"+id);
//        departmentService.updateDepartment(department1);
        return map;

    }


    @PutMapping("/update")
    public Map updateDepartment(@RequestBody Department department){
        Map map = new HashMap<>();
        if(departmentService.updateDepartment(department)){
            map.put("isOK",true);
        }else {
            map.put("isOK",false);
            map.put("msg","系统故障");
        }
        return map;
    }

    @DeleteMapping("/delete")
    public Map delete(@RequestParam Integer id){
        Map map = new HashMap<>();
        if(departmentService.delete(id)){
            map.put("isOK",true);
        }else {
            map.put("isOK",false);
            map.put("msg","系统故障");
        }
        return map;
    }

    //我的代码
    //新增部门
    @PostMapping("/add")
    public String addDepartment(@RequestBody Department department, @RequestParam int tenantId) {
        departmentService.addDepartment(department, tenantId);
        return "Department added successfully";
    }

//    @GetMapping
//    public List<Department> getAllDepartments() {
//        return departmentService.getAllDepartments();
//    }

    //更新部门
    @PutMapping("/{id}")
    public void updateDepartment(@PathVariable int id, @RequestBody Department department) {
        department.setId(id);
        departmentService.updateDepartment(department);
    }

    //删除部门
    @DeleteMapping("/{id}")
    public void deleteDepartment(@PathVariable int id) {
        departmentService.deleteDepartment(id);
    }

    // 根据租户ID获取部门列表
    @GetMapping("/getByTenantId")
    public Map getDepartmentsByTenant(@RequestParam Integer tenantId) {
        Map map = new HashMap<>();
        try {
            List<Department> departments = departmentService.getDepartmentsByTenantId(tenantId);
            map.put("departmentList", departments);
            map.put("isOK", true);
        } catch (Exception e) {
            map.put("departmentList", new ArrayList<>());
            map.put("isOK", false);
            map.put("msg", "获取部门失败: " + e.getMessage());
        }
        return map;
    }

    // 根据部门ID获取部门信息
    @GetMapping("/getByDepartmentId")
    public Map getDepartmentByDepartmentId(@RequestParam Integer departmentId) {
        Map map = new HashMap<>();
        try {
            Department department = departmentService.getDepartmentById(departmentId);
            if (department != null) {
                List<Department> departments = new ArrayList<>();
                departments.add(department);
                map.put("departmentList", departments);
                map.put("isOK", true);
            } else {
                map.put("departmentList", new ArrayList<>());
                map.put("isOK", false);
                map.put("msg", "部门不存在");
            }
        } catch (Exception e) {
            map.put("departmentList", new ArrayList<>());
            map.put("isOK", false);
            map.put("msg", "获取部门失败: " + e.getMessage());
        }
        return map;
    }

    ////////////




















}

