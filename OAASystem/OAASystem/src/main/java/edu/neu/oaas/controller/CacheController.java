package edu.neu.oaas.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * 缓存管理Controller
 * 提供缓存统计、清理和监控功能
 */
@RestController
@RequestMapping("/api/cache")
public class CacheController {

    @Autowired
    private CacheManager cacheManager;

    /**
     * 获取所有缓存的统计信息
     */
    @GetMapping("/stats")
    public Map<String, Object> getCacheStats() {
        Map<String, Object> stats = new HashMap<>();
        Collection<String> cacheNames = cacheManager.getCacheNames();
        
        stats.put("totalCaches", cacheNames.size());
        stats.put("cacheNames", cacheNames);
        
        Map<String, Object> cacheDetails = new HashMap<>();
        for (String cacheName : cacheNames) {
            Cache cache = cacheManager.getCache(cacheName);
            if (cache != null) {
                Map<String, Object> cacheInfo = new HashMap<>();
                cacheInfo.put("name", cacheName);
                cacheInfo.put("nativeCache", cache.getNativeCache().getClass().getSimpleName());
                cacheDetails.put(cacheName, cacheInfo);
            }
        }
        stats.put("cacheDetails", cacheDetails);
        
        return stats;
    }

    /**
     * 清理指定缓存
     */
    @DeleteMapping("/{cacheName}")
    public Map<String, Object> clearCache(@PathVariable String cacheName) {
        Map<String, Object> result = new HashMap<>();
        
        Cache cache = cacheManager.getCache(cacheName);
        if (cache != null) {
            cache.clear();
            result.put("success", true);
            result.put("message", "缓存 " + cacheName + " 已清理");
        } else {
            result.put("success", false);
            result.put("message", "缓存 " + cacheName + " 不存在");
        }
        
        return result;
    }

    /**
     * 清理所有缓存
     */
    @DeleteMapping("/all")
    public Map<String, Object> clearAllCaches() {
        Map<String, Object> result = new HashMap<>();
        
        Collection<String> cacheNames = cacheManager.getCacheNames();
        int clearedCount = 0;
        
        for (String cacheName : cacheNames) {
            Cache cache = cacheManager.getCache(cacheName);
            if (cache != null) {
                cache.clear();
                clearedCount++;
            }
        }
        
        result.put("success", true);
        result.put("message", "已清理 " + clearedCount + " 个缓存");
        result.put("clearedCaches", cacheNames);
        
        return result;
    }

    /**
     * 获取指定缓存的详细信息
     */
    @GetMapping("/{cacheName}")
    public Map<String, Object> getCacheInfo(@PathVariable String cacheName) {
        Map<String, Object> result = new HashMap<>();
        
        Cache cache = cacheManager.getCache(cacheName);
        if (cache != null) {
            result.put("success", true);
            result.put("cacheName", cacheName);
            result.put("nativeCache", cache.getNativeCache().getClass().getSimpleName());
            result.put("exists", true);
        } else {
            result.put("success", false);
            result.put("message", "缓存 " + cacheName + " 不存在");
            result.put("exists", false);
        }
        
        return result;
    }

    /**
     * 清理指定缓存的特定键
     */
    @DeleteMapping("/{cacheName}/key/{key}")
    public Map<String, Object> evictCacheKey(@PathVariable String cacheName, @PathVariable String key) {
        Map<String, Object> result = new HashMap<>();
        
        Cache cache = cacheManager.getCache(cacheName);
        if (cache != null) {
            cache.evict(key);
            result.put("success", true);
            result.put("message", "已清理缓存 " + cacheName + " 中的键: " + key);
        } else {
            result.put("success", false);
            result.put("message", "缓存 " + cacheName + " 不存在");
        }
        
        return result;
    }

    /**
     * 预热缓存（可根据需要扩展）
     */
    @PostMapping("/warmup")
    public Map<String, Object> warmupCache() {
        Map<String, Object> result = new HashMap<>();
        
        // 这里可以添加预热逻辑
        // 例如：预加载常用数据到缓存中
        
        result.put("success", true);
        result.put("message", "缓存预热完成");
        
        return result;
    }

    /**
     * 获取缓存健康状态
     */
    @GetMapping("/health")
    public Map<String, Object> getCacheHealth() {
        Map<String, Object> health = new HashMap<>();
        
        Collection<String> cacheNames = cacheManager.getCacheNames();
        boolean allHealthy = true;
        
        for (String cacheName : cacheNames) {
            Cache cache = cacheManager.getCache(cacheName);
            if (cache == null) {
                allHealthy = false;
                break;
            }
        }
        
        health.put("status", allHealthy ? "UP" : "DOWN");
        health.put("totalCaches", cacheNames.size());
        health.put("timestamp", System.currentTimeMillis());
        
        return health;
    }
} 