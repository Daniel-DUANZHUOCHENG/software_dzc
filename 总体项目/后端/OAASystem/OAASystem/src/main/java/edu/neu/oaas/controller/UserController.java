package edu.neu.oaas.controller;

import edu.neu.oaas.pojo.LoginResponse;
import edu.neu.oaas.pojo.User;
import edu.neu.oaas.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserService userService;


    //超级管理员
    @RequestMapping("/all")//得到所有用户信息
    public Map getAllUser() {
        Map map = new HashMap<>();
        List<User> users = userService.getAllUser();
        map.put("userList", users);
        map.put("isOK", true);
        return map;
    }

    // 租户管理员获取租户下的用户
    @GetMapping("/tenant/{tenantId}")
    public ResponseEntity<Map<String, Object>> getUsersForTenantAdmin(@PathVariable int tenantId) {
        Map<String, Object> response = new HashMap<>();
        List<User> users = userService.getUsersByTenantId(tenantId);
        response.put("userList", users);
        response.put("isOK", true);
        return ResponseEntity.ok(response);
    }

    @RequestMapping("/id/{id}")
    public User getUserById(@PathVariable Integer id) {
        return userService.getUserById(id);
    }

    @RequestMapping("/getBD")
    public Map getUserByDepartmentId(@RequestParam Integer departmentId){
        Map map = new HashMap<>();
        List<User> users = userService.getUserByDepartmentId(departmentId);
        map.put("userList",users);
        map.put("isOK",true);
        return map;
    }

    @RequestMapping("/path/{path}")
    public Map getUserByPath(@PathVariable String path) {
        Map map = new HashMap<>();
        List<User> users = userService.findByPath(path);
        map.put("userList", users);
        map.put("isOK", true);
        return map;
    }

    @RequestMapping("/name/{name}")
    public Map getUserByName(@PathVariable String name) {
        Map map = new HashMap<>();
        User user = userService.getUserByName(name);
        map.put("user", user);
        map.put("isOK", true);
        return map;
    }

    @RequestMapping(value = "/insert", consumes = "application/json")
//    @RequestMapping("/insert")//前端的id属性必须设为空!!!
    public Map insertUser(@RequestBody User user) {
        System.out.println(user.toString());
        Map map = new HashMap<>();
        User u = userService.getUserByName(user.getUsername());

        if (u != null) {
            map.put("isOK", false);
            map.put("msg", "用户已存在");
        } else if (userService.insertUser(user)) {
            map.put("isOK", true);
            map.put("msg", "注册成功");
        } else {
            map.put("isOK", false);
            map.put("msg", "系统故障");
        }
        return map;
    }

    @RequestMapping("/delete")
    public Map delete(@RequestParam Integer id) {
        Map map = new HashMap<>();
        if (userService.deleteById(id)) {
            map.put("isOK", true);
        } else {
            map.put("isOK", false);
            map.put("msg", "系统故障");
        }

        return map;
    }

//    @RequestMapping("/login")
//    public Map login(@RequestParam String username, @RequestParam String password) {
//        Map map = new HashMap<>();
//        User user = userService.getUserByName(username);
//        if (user == null) {
//            map.put("isOK", false);
//            map.put("msg", "账号不存在");
//        } else if (user.getPassword().equals(password)) {
//            map.put("isOK", true);
//        } else {
//            map.put("isOK", false);
//            map.put("msg", "密码错误");
//        }
//        return map;
//    }

    @RequestMapping("/reset")
    public Map reset(@RequestBody User user) {
        Map map = new HashMap<>();
        if (userService.updateUser(user)) {
            map.put("isOK", true);
        } else {
            map.put("isOK", false);
            map.put("msg", "系统故障");
        }
        return map;
    }


    //修改个人信息
    @PutMapping("/update")
    public ResponseEntity<String> updateUserInfo(@RequestBody User user) {
        try {
            userService.updateUserInfo(user);
            return ResponseEntity.ok("User information updated successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred during updating user information");
        }
    }

    @PutMapping("/updatePassword")
    public ResponseEntity<String> updatePassword(@RequestParam int userId, @RequestParam String oldPassword, @RequestParam String newPassword) {
        System.out.println(userId+" " + oldPassword +" " + newPassword);
        try {
            boolean result = userService.updatePassword(userId, oldPassword, newPassword);
            if (result) {
                return ResponseEntity.ok("Password updated successfully");
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Old password is incorrect");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred during updating password");
        }
    }

    @GetMapping("/profile/{userId}")
    public ResponseEntity<User> getUserProfile(@PathVariable int userId) {
        try {
            User user = userService.getUserProfile(userId);
            if (user != null) {
                return ResponseEntity.ok(user);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/name/{username}")
    public ResponseEntity<Map<String, Object>> checkUsernameExists(@PathVariable String username) {
        Map<String, Object> response = new HashMap<>();
        try {
            User user = userService.getUserByName(username);
            response.put("user", user != null ? user : null);
            response.put("exists", user != null);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            response.put("error", "An error occurred while checking the username.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }


    @PostMapping("/upload-avatar")
    public ResponseEntity<String> uploadAvatar(@RequestParam("file") MultipartFile file, @RequestParam("userId") int userId) {
        try {
            String avatarUrl = userService.saveAvatar(file, userId);
            return ResponseEntity.ok(avatarUrl);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred during file upload");
        }
    }

    //分页
    @GetMapping("/page")
    public ResponseEntity<Map<String, Object>> getUsersByPage(@RequestParam int page, @RequestParam int pageSize) {
        try {
            Map<String, Object> result = userService.getUsersByPage(page, pageSize);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    //添加
//    @PostMapping("/add")
//    public String addUser(@RequestBody User user, @RequestParam int departmentId) {
//        userService.addUser(user, departmentId);
//        return "User added successfully";
//    }
//
    //登录
    @PostMapping(value = "/login", consumes = "application/json")
//    @PostMapping("/login")
    public ResponseEntity<LoginResponse> loginUser(@RequestBody Map<String, String> credentials) {
        String username = credentials.get("username");
        String password = credentials.get("password");

        try {
            User user = userService.login(username, password);
            if (user != null) {
                LoginResponse response = new LoginResponse("Login successful", user.getId(), user);
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new LoginResponse("Invalid username or password", -1, null));
            }
        } catch (Exception e) {
            e.printStackTrace();

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new LoginResponse("An error occurred during login", -1, null));
        }
    }

    @GetMapping("/search")
    public Map searchUsers(@RequestParam(required = false) String username,
                           @RequestParam(required = false) String phoneNumber,
                           @RequestParam(required = false) String status,
                           @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
                           @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        Map map = new HashMap<>();
        List<User> users = userService.searchUsers(username, phoneNumber, status, startDate, endDate);
        map.put("userList", users);
        map.put("isOK", true);
        return map;
    }

    //文件上传
    @PostMapping("/import")
    public ResponseEntity<String> importUsers(@RequestParam("file") MultipartFile file) {
        System.out.println(file);
        try {
            userService.importUsers(file);
            System.out.println("添加用户成功");
            return ResponseEntity.ok("User import successful");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }


}

