package edu.neu.oaas.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.reflect.TypeToken;
import edu.neu.oaas.pojo.Department;
import edu.neu.oaas.pojo.RegistrationRequest;
import edu.neu.oaas.pojo.Tenant;
import edu.neu.oaas.pojo.User;
import edu.neu.oaas.service.TenantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("/api/tenants")
public class TenantController {
    @Autowired
    private TenantService tenantService;

    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, (JsonDeserializer<LocalDateTime>) (json, type, jsonDeserializationContext) ->
                    LocalDateTime.parse(json.getAsJsonPrimitive().getAsString(), DateTimeFormatter.ISO_DATE_TIME))
            .create();

    @GetMapping("/all")
    public Map getAll(){
        Map map = new HashMap<>();
        List<Tenant> tenants = tenantService.getAll();
        map.put("tenantList",tenants);
        map.put("isOK",true);
        return map;
    }

    @GetMapping("/list")
    public Map<String, Object> getAllTenantNames() {
        Map<String, Object> map = new HashMap<>();
        List<Tenant> tenants = tenantService.getAllTenantNames(); // 使用新的方法名
        map.put("tenantList", tenants);
        map.put("isOK", true);
        return map;
    }

    @GetMapping("/search")
    public Map<String, Object> searchTenants(@RequestParam(value = "tenantName", required = false) String tenantName,
                                             @RequestParam(value = "contactPerson", required = false) String contactPerson,
                                             @RequestParam(value = "phone", required = false) String phone,
                                             @RequestParam(value = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                             @RequestParam(value = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        List<Tenant> tenants = tenantService.searchTenants(tenantName, contactPerson, phone, startDate, endDate);
        Map<String, Object> response = new HashMap<>();
        response.put("tenantList", tenants);
        response.put("total", tenants.size());
        return response;
    }

    @GetMapping("/{id}")
    public Map getById(@PathVariable Integer id){
        Map map = new HashMap<>();
        Tenant tenant = tenantService.getById(id);
        map.put("tenant",tenant);
        map.put("isOK",true);
        return map;
    }

    @PostMapping("/insert2")
    public Map<String, Object> insertTenant2(@RequestBody Map<String, Object> payload) {
        Map<String, Object> response = new HashMap<>();
        try {
            Tenant tenant = new Tenant();
            tenant.setTenantName((String) payload.get("tenantName"));
            tenant.setAdminUsername((String) payload.get("adminUsername"));
            tenant.setPassword((String) payload.get("password"));
            tenant.setContactPerson((String) payload.get("contactPerson"));
            tenant.setPhone((String) payload.get("phone"));
            tenant.setIcon((String) payload.get("icon"));
            tenant.setRemark((String) payload.get("remark"));
            tenant.setCreatedAt(LocalDateTime.now());

//            Department department = new Department();
//            department.setDepartmentName((String) payload.get("departmentName"));
//            department.setStatus("Active"); // 假设部门状态为Active
//            department.setParentDepartment(0);
//            department.setTenantId(tenant.getId());

//            User user = new User();
////            user.setUsername((String) payload.get("username"));
////            user.setPassword((String) payload.get("password")); // 确保密码已加密
////            user.setNickname((String) payload.get("contactPerson"));
////            user.setPhoneNumber((String) payload.get("phone"));
//            user.setRole("Admin");
//            user.setStatus("Active");
//            user.setCreatedAt(LocalDateTime.now());

            boolean success = tenantService.insertTenant2(tenant);
            System.out.println(success);
            if (success) {
                response.put("isOK", true);
                response.put("msg", "租户创建成功");
            } else {
                response.put("isOK", false);
                response.put("msg", "租户创建失败");
            }
        } catch (Exception e) {
            response.put("isOK", false);
            response.put("msg", "系统故障：" + e.getMessage());
        }
        return response;
    }

    @PostMapping("/insert")
    public Map<String, Object> insert(@RequestBody RegistrationRequest request){
        Map<String, Object> map = new HashMap<>();
        Tenant tenant = request.getTenant();
        Department department = request.getDepartment();
        User user = request.getUser();

        System.out.println(tenant.toString());
        System.out.println(department.toString());
        System.out.println(user.toString());

        if(tenantService.insertTenant(tenant, department, user)){
            map.put("isOK", true);
        } else {
            map.put("isOK", false);
            map.put("msg", "系统故障");
        }
        return map;
    }
    @PutMapping("/reset")
    public Map reset(@RequestBody Tenant tenant){
        Map map = new HashMap<>();
        if(tenantService.updateTenant(tenant)){
            map.put("isOK", true);
        } else {
            map.put("isOK", false);
            map.put("msg", "系统故障");
        }
        return map;
    }

    @DeleteMapping("/delete")
    public Map delete(@RequestParam Integer id){
        Map map = new HashMap<>();
        if(tenantService.delete(id)){
            map.put("isOK", true);
        } else {
            map.put("isOK", false);
            map.put("msg", "系统故障");
        }
        return map;
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Map<String, Object>> deleteTenant(@PathVariable Integer id) {
        Map<String, Object> response = new HashMap<>();
        try {
            boolean success = tenantService.deleteTenant(id);
            if (success) {
                response.put("isOK", true);
                response.put("msg", "租户删除成功");
                return ResponseEntity.ok(response);
            } else {
                response.put("isOK", false);
                response.put("msg", "租户删除失败");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }
        } catch (Exception e) {
            response.put("isOK", false);
            response.put("msg", "系统故障：" + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }



    @PostMapping("/upload-icon")
    public ResponseEntity<Map<String, String>> uploadIcon(@RequestParam("file") MultipartFile file) {
        try {
            String iconUrl = tenantService.saveIcon(file);
            Map<String, String> response = new HashMap<>();
            response.put("url", iconUrl);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerTenantAndUser(@RequestParam String tenantName,
                                                        @RequestParam String contactperson,
                                                        @RequestParam String Phone,
                                                        @RequestParam String contactEmail,
                                                        @RequestParam String username,
                                                        @RequestParam String password) {
        try {
            tenantService.registerTenantAndUser(tenantName, contactperson, Phone, contactEmail, username, password);
            return ResponseEntity.ok("Tenant and user registration successful");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }


    @PutMapping("/{id}")
    public ResponseEntity<String> updateTenant(@PathVariable Integer id, @RequestBody Tenant tenant) {
        try {
            tenant.setId(id);
            tenantService.updateTenant3(tenant);
            return ResponseEntity.ok("Tenant updated successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating tenant: " + e.getMessage());
        }
    }
}

