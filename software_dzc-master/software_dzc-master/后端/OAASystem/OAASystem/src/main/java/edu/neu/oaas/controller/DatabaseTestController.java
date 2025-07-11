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
            try (Connection conn = dataSource.getConnection()) {
                result.put("connection", "成功");
                result.put("database_info", conn.getMetaData().getDatabaseProductName() + " " + conn.getMetaData().getDatabaseProductVersion());
                
                // 测试简单查询
                try (PreparedStatement stmt = conn.prepareStatement("SELECT 1")) {
                    ResultSet rs = stmt.executeQuery();
                    if (rs.next()) {
                        result.put("query", "成功");
                    }
                }
            }
            
            result.put("status", "success");
            System.out.println("✅ 数据库连接测试完成");
            
        } catch (Exception e) {
            System.err.println("❌ 数据库连接测试失败: " + e.getMessage());
            result.put("status", "error");
            result.put("error", e.getMessage());
            result.put("error_type", e.getClass().getName());
            
            // 获取数据源配置信息（注意不要暴露敏感信息）
            try {
                result.put("datasource_type", dataSource.getClass().getName());
            } catch (Exception ex) {
                result.put("datasource_info_error", ex.getMessage());
            }
        }
        
        return result;
    }
} 