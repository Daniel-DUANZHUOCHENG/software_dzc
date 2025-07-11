package edu.neu.oaas.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

/**
 * 缓存服务类
 * 提供缓存预热和管理功能
 */
@Service
public class CacheService implements CommandLineRunner {

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private UserService userService;

    @Autowired
    private TenantService tenantService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private DepartmentService departmentService;

    /**
     * 应用启动时执行缓存预热
     */
    @Override
    public void run(String... args) throws Exception {
        System.out.println("🔥 开始缓存预热...");
        warmupCache();
        System.out.println("✅ 缓存预热完成");
    }

    /**
     * 缓存预热方法
     */
    public void warmupCache() {
        try {
            // 预热用户缓存
            warmupUserCache();
            
            // 预热租户缓存
            warmupTenantCache();
            
            // 预热课程缓存
            warmupCourseCache();
            
            // 预热部门缓存
            warmupDepartmentCache();
            
        } catch (Exception e) {
            System.err.println("❌ 缓存预热失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 预热用户缓存
     */
    private void warmupUserCache() {
        try {
            System.out.println("🔥 预热用户缓存...");
            // 预加载所有用户
            userService.getAllUser();
            System.out.println("✅ 用户缓存预热完成");
        } catch (Exception e) {
            System.err.println("❌ 用户缓存预热失败: " + e.getMessage());
        }
    }

    /**
     * 预热租户缓存
     */
    private void warmupTenantCache() {
        try {
            System.out.println("🔥 预热租户缓存...");
            // 预加载所有租户
            tenantService.getAll();
            System.out.println("✅ 租户缓存预热完成");
        } catch (Exception e) {
            System.err.println("❌ 租户缓存预热失败: " + e.getMessage());
        }
    }

    /**
     * 预热课程缓存
     */
    private void warmupCourseCache() {
        try {
            System.out.println("🔥 预热课程缓存...");
            // 预加载所有课程
            courseService.getAllCourses();
            // 预加载已审核通过的课程
            courseService.getApprovedCourses();
            System.out.println("✅ 课程缓存预热完成");
        } catch (Exception e) {
            System.err.println("❌ 课程缓存预热失败: " + e.getMessage());
        }
    }

    /**
     * 预热部门缓存
     */
    private void warmupDepartmentCache() {
        try {
            System.out.println("🔥 预热部门缓存...");
            // 预加载所有部门
            departmentService.getAllDepartments();
            System.out.println("✅ 部门缓存预热完成");
        } catch (Exception e) {
            System.err.println("❌ 部门缓存预热失败: " + e.getMessage());
        }
    }

    /**
     * 清理所有缓存
     */
    public void clearAllCaches() {
        Collection<String> cacheNames = cacheManager.getCacheNames();
        for (String cacheName : cacheNames) {
            Cache cache = cacheManager.getCache(cacheName);
            if (cache != null) {
                cache.clear();
                System.out.println("🗑️ 已清理缓存: " + cacheName);
            }
        }
    }

    /**
     * 清理指定缓存
     */
    public void clearCache(String cacheName) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache != null) {
            cache.clear();
            System.out.println("🗑️ 已清理缓存: " + cacheName);
        }
    }

    /**
     * 获取缓存统计信息
     */
    public void printCacheStats() {
        Collection<String> cacheNames = cacheManager.getCacheNames();
        System.out.println("📊 缓存统计信息:");
        System.out.println("总缓存数: " + cacheNames.size());
        
        for (String cacheName : cacheNames) {
            Cache cache = cacheManager.getCache(cacheName);
            if (cache != null) {
                System.out.println("- " + cacheName + " (" + cache.getNativeCache().getClass().getSimpleName() + ")");
            }
        }
    }

    /**
     * 检查缓存健康状态
     */
    public boolean isCacheHealthy() {
        Collection<String> cacheNames = cacheManager.getCacheNames();
        for (String cacheName : cacheNames) {
            Cache cache = cacheManager.getCache(cacheName);
            if (cache == null) {
                return false;
            }
        }
        return true;
    }
} 