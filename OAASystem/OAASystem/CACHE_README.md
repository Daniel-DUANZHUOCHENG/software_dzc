# EhCache 缓存系统使用说明

## 概述

本项目已集成EhCache作为缓存解决方案，用于提高应用程序的性能。缓存系统会自动缓存频繁访问的数据，减少数据库查询次数。

## 缓存配置

### 1. 依赖配置 (pom.xml)
```xml
<!-- EhCache 缓存依赖 -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-cache</artifactId>
</dependency>
<dependency>
    <groupId>org.ehcache</groupId>
    <artifactId>ehcache</artifactId>
</dependency>
<dependency>
    <groupId>javax.cache</groupId>
    <artifactId>cache-api</artifactId>
</dependency>
```

### 2. 应用配置 (application.properties)
```properties
# 缓存配置
spring.cache.type=jcache
spring.cache.jcache.config=classpath:ehcache.xml
spring.cache.jcache.provider=org.ehcache.jsr107.EhcacheCachingProvider
```

### 3. 缓存详细配置 (ehcache.xml)
配置文件定义了以下缓存区域：
- **users**: 用户数据缓存 (30分钟过期)
- **userList**: 用户列表缓存 (15分钟过期)
- **departments**: 部门数据缓存 (2小时过期)
- **tenants**: 租户数据缓存 (4小时过期)
- **courses**: 课程数据缓存 (1小时过期)
- **conferences**: 会议数据缓存 (45分钟过期)
- **information**: 资讯数据缓存 (1小时过期)
- **carouselImages**: 轮播图缓存 (6小时过期)
- **statistics**: 统计数据缓存 (10分钟过期)
- **meetingApplications**: 会议申请缓存 (20分钟过期)

## 缓存注解使用

### @Cacheable - 缓存查询结果
```java
@Cacheable(value = "users", key = "#id")
public User getUserById(Integer id) {
    return userMapper.getUserById(id);
}
```

### @CacheEvict - 清除缓存
```java
@CacheEvict(value = {"users", "userList"}, allEntries = true)
public boolean deleteById(Integer id) {
    userMapper.deleteById(id);
    return true;
}
```

### @CachePut - 更新缓存
```java
@CacheEvict(value = {"users", "userList"}, allEntries = true)
@CachePut(value = "users", key = "#user.id")
public boolean updateUser(User user) {
    // 更新逻辑
    return true;
}
```

## 缓存管理API

### 1. 获取缓存统计信息
```
GET /api/cache/stats
```

### 2. 清理指定缓存
```
DELETE /api/cache/{cacheName}
```

### 3. 清理所有缓存
```
DELETE /api/cache/all
```

### 4. 获取缓存健康状态
```
GET /api/cache/health
```

### 5. 缓存预热
```
POST /api/cache/warmup
```

## 缓存预热机制

应用启动时会自动执行缓存预热，预加载以下数据：
- 所有用户数据
- 所有租户数据
- 所有课程数据
- 所有部门数据

## 缓存键策略

### 用户相关
- 单个用户: `用户ID`
- 用户列表: `"all"`, `"tenant_" + 租户ID`, `"department_" + 部门ID`
- 用户搜索: `"search_" + 用户名 + "_" + 电话 + "_" + 状态`

### 租户相关
- 单个租户: `租户ID`
- 租户列表: `"all"`
- 按名称查询: `"name_" + 租户名称`

### 课程相关
- 单个课程: `课程ID`
- 课程列表: `"all"`, `"approved"`, `"pending"`
- 课程搜索: `"search_" + 课程名 + "_" + 编号`

### 部门相关
- 单个部门: `部门ID`
- 部门列表: `"all"`, `"tenant_" + 租户ID`
- 路径查询: `"prefix_" + 路径前缀`

## 性能优化建议

### 1. 缓存命中率优化
- 合理设置缓存过期时间
- 使用合适的缓存键策略
- 避免缓存穿透和雪崩

### 2. 内存管理
- 监控缓存内存使用情况
- 适当调整堆内存和堆外内存大小
- 定期清理不必要的缓存

### 3. 缓存更新策略
- 数据更新时及时清理相关缓存
- 使用@CachePut更新单个缓存项
- 批量操作时清理整个缓存区域

## 监控和调试

### 1. 日志监控
应用启动时会输出缓存相关日志：
```
🔥 开始缓存预热...
🔥 预热用户缓存...
✅ 用户缓存预热完成
✅ 缓存预热完成
```

### 2. 缓存统计
通过管理API获取缓存使用统计信息，包括：
- 缓存总数
- 各缓存区域详情
- 缓存健康状态

### 3. 性能测试
- 对比启用/禁用缓存的性能差异
- 监控数据库查询次数减少情况
- 测试缓存命中率

## 常见问题

### Q: 缓存数据不一致怎么办？
A: 检查缓存更新策略，确保数据修改时正确清理相关缓存。

### Q: 内存使用过高怎么办？
A: 调整ehcache.xml中的内存配置，减少缓存大小或缩短过期时间。

### Q: 缓存命中率低怎么办？
A: 分析访问模式，优化缓存键策略，调整缓存过期时间。

### Q: 如何禁用缓存？
A: 在application.properties中设置 `spring.cache.type=none`

## 注意事项

1. 缓存适用于读多写少的场景
2. 避免缓存大对象或频繁变化的数据
3. 定期监控缓存性能和内存使用
4. 在集群环境中考虑缓存同步问题
5. 重要数据修改后及时清理相关缓存 