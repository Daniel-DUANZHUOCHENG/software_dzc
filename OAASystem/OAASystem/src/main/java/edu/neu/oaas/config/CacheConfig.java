package edu.neu.oaas.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.jcache.JCacheCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.cache.CacheManager;

import javax.cache.Caching;
import javax.cache.spi.CachingProvider;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * 缓存配置类
 * 配置EhCache作为Spring Cache的实现
 */
@Configuration
@EnableCaching
public class CacheConfig {

    /**
     * 配置缓存管理器
     * 使用EhCache作为缓存提供者
     */
    @Bean
    public CacheManager cacheManager() throws URISyntaxException {
        // 获取EhCache提供者
        CachingProvider cachingProvider = Caching.getCachingProvider("org.ehcache.jsr107.EhcacheCachingProvider");
        
        // 获取缓存管理器
        javax.cache.CacheManager cacheManager = cachingProvider.getCacheManager(
            getClass().getResource("/ehcache.xml").toURI(),
            getClass().getClassLoader()
        );
        
        // 返回Spring的缓存管理器包装器
        return new JCacheCacheManager(cacheManager);
    }
} 