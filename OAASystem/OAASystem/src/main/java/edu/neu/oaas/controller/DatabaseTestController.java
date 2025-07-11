package edu.neu.oaas.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/test")
public class DatabaseTestController {

    @Autowired
    private DataSource dataSource;

    @GetMapping("/database")
    public Map<String, Object> testDatabase() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            System.out.println("🔍 开始测试数据库连接...");
            
            // 测试数据库连接
            try (Connection connection = dataSource.getConnection()) {
                result.put("connectionStatus", "成功");
                result.put("databaseUrl", connection.getMetaData().getURL());
                result.put("databaseName", connection.getCatalog());
                result.put("userName", connection.getMetaData().getUserName());
                
                System.out.println("✅ 数据库连接成功");
                System.out.println("📊 数据库URL: " + connection.getMetaData().getURL());
                System.out.println("📊 数据库名: " + connection.getCatalog());
                System.out.println("📊 用户名: " + connection.getMetaData().getUserName());
                
                // 测试查询用户表
                String userCountSql = "SELECT COUNT(*) as userCount FROM users";
                try (PreparedStatement ps = connection.prepareStatement(userCountSql);
                     ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        int userCount = rs.getInt("userCount");
                        result.put("userCount", userCount);
                        System.out.println("👥 用户表记录数: " + userCount);
                    }
                }
                
                // 测试查询课程表
                String courseCountSql = "SELECT COUNT(*) as courseCount FROM course";
                try (PreparedStatement ps = connection.prepareStatement(courseCountSql);
                     ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        int courseCount = rs.getInt("courseCount");
                        result.put("courseCount", courseCount);
                        System.out.println("📚 课程表记录数: " + courseCount);
                    }
                }
                
                // 测试查询租户表
                String tenantCountSql = "SELECT COUNT(*) as tenantCount FROM tenants";
                try (PreparedStatement ps = connection.prepareStatement(tenantCountSql);
                     ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        int tenantCount = rs.getInt("tenantCount");
                        result.put("tenantCount", tenantCount);
                        System.out.println("🏢 租户表记录数: " + tenantCount);
                    }
                }
                
                result.put("success", true);
                result.put("message", "数据库连接和查询测试成功");
                
            }
        } catch (Exception e) {
            System.err.println("❌ 数据库测试失败: " + e.getMessage());
            e.printStackTrace();
            
            result.put("connectionStatus", "失败");
            result.put("success", false);
            result.put("error", e.getMessage());
            result.put("message", "数据库连接失败: " + e.getMessage());
        }
        
        return result;
    }
    
    @GetMapping("/environment")
    public Map<String, Object> testEnvironment() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 获取系统信息
            result.put("javaVersion", System.getProperty("java.version"));
            result.put("osName", System.getProperty("os.name"));
            result.put("osVersion", System.getProperty("os.version"));
            result.put("userDir", System.getProperty("user.dir"));
            result.put("userName", System.getProperty("user.name"));
            
            // 获取内存信息
            Runtime runtime = Runtime.getRuntime();
            result.put("totalMemory", runtime.totalMemory() / 1024 / 1024 + " MB");
            result.put("freeMemory", runtime.freeMemory() / 1024 / 1024 + " MB");
            result.put("maxMemory", runtime.maxMemory() / 1024 / 1024 + " MB");
            
            result.put("success", true);
            result.put("message", "环境信息获取成功");
            
            System.out.println("🖥️ 系统环境信息:");
            System.out.println("   Java版本: " + System.getProperty("java.version"));
            System.out.println("   操作系统: " + System.getProperty("os.name") + " " + System.getProperty("os.version"));
            System.out.println("   工作目录: " + System.getProperty("user.dir"));
            System.out.println("   用户名: " + System.getProperty("user.name"));
            
        } catch (Exception e) {
            System.err.println("❌ 环境信息获取失败: " + e.getMessage());
            result.put("success", false);
            result.put("error", e.getMessage());
            result.put("message", "环境信息获取失败: " + e.getMessage());
        }
        
        return result;
    }
} 