package edu.neu.oaas.controller;

import edu.neu.oaas.pojo.UserBehavior;
import edu.neu.oaas.service.UserBehaviorService;
import edu.neu.oaas.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/userBehavior")
public class UserBehaviorController {

    @Autowired
    private UserBehaviorService userBehaviorService;

    @Autowired
    private UserService userService;

    @PostMapping("/track")
    public ResponseEntity<Map<String, Object>> trackUserBehavior(@RequestBody UserBehavior userBehavior) {
        try {
            userBehaviorService.createUserBehavior(userBehavior);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "行为记录成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "行为记录失败");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllUserBehaviors() {
        try {
            List<UserBehavior> behaviors = userBehaviorService.getAllUserBehaviors();
            Map<String, Object> response = new HashMap<>();
            response.put("behaviors", behaviors);
            response.put("total", behaviors.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "获取用户行为数据失败");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchUserBehaviors(
            @RequestParam(required = false) String action,
            @RequestParam(required = false) String timestamp) {
        try {
            List<UserBehavior> behaviors = userBehaviorService.searchUserBehaviors(action, timestamp);
            Map<String, Object> response = new HashMap<>();
            response.put("behaviors", behaviors);
            response.put("total", behaviors.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "搜索用户行为数据失败");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }



    @PostMapping("/increment")
    public ResponseEntity<Map<String, Object>> incrementOnlineUsers() {
        userBehaviorService.incrementOnlineUsers();
        Map<String, Object> response = new HashMap<>();
        response.put("message", "用户进入首页");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/decrement")
    public ResponseEntity<Map<String, Object>> decrementOnlineUsers() {
        userBehaviorService.decrementOnlineUsers();
        Map<String, Object> response = new HashMap<>();
        response.put("message", "用户离开首页");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/onlineUsers")
    public ResponseEntity<Map<String, Object>> getOnlineUsers() {
        int onlineUsers = userBehaviorService.getOnlineUsers();
        Map<String, Object> response = new HashMap<>();
        response.put("onlineUsers", onlineUsers);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/realTimeVisitors")
    public ResponseEntity<Map<String, Object>> getRealTimeVisitors() {
        int realTimeVisitors = userBehaviorService.getRealTimeVisitors();
        Map<String, Object> response = new HashMap<>();
        response.put("realTimeVisitors", realTimeVisitors);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/updateRealTimeVisitors")
    public ResponseEntity<Map<String, Object>> updateRealTimeVisitors() {
        userBehaviorService.updateRealTimeVisitors();
        Map<String, Object> response = new HashMap<>();
        response.put("message", "实时访客数已更新");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/portrait/{userId}")
    public ResponseEntity<Map<String, Object>> getUserPortrait(@PathVariable Integer userId) {
        try {
            Map<String, Object> portrait = userService.generateUserPortrait(userId);
            return ResponseEntity.ok(portrait);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "获取用户画像失败");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/portraits")
    public ResponseEntity<List<Map<String, Object>>> getUserPortraits() {
        try {
            List<Map<String, Object>> portraits = userService.generateAllUserPortraits();
            return ResponseEntity.ok(portraits);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "获取用户画像失败");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonList(response)); // Returning as a list
        }
    }




}
