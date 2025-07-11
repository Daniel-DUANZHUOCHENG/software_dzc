package edu.neu.oaas.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Base64;
import java.net.URLDecoder;

@Component
public class AuthorizationFilter extends OncePerRequestFilter {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String requestURI = request.getRequestURI();
        String method = request.getMethod();

        // 跳过OPTIONS请求
        if ("OPTIONS".equals(method)) {
            filterChain.doFilter(request, response);
            return;
        }

        // 跳过不需要权限验证的接口
        if (shouldSkipAuthorization(requestURI)) {
            filterChain.doFilter(request, response);
            return;
        }

        // 临时：跳过所有权限验证（用于调试问题）
        System.out.println("🔐 权限验证 - URI: " + requestURI + ", Method: " + method);
        filterChain.doFilter(request, response);
        return;

        /*
         * 临时注释掉权限验证逻辑
         * // 从请求头或参数中获取用户信息
         * String userRole = getUserRole(request);
         * String userTenantId = getUserTenantId(request);
         * String userId = getUserId(request);
         * 
         * // 权限验证逻辑
         * if (!hasPermission(requestURI, method, userRole, userTenantId, userId,
         * request)) {
         * // 权限不足，返回403错误
         * response.setStatus(HttpServletResponse.SC_FORBIDDEN);
         * response.setContentType("application/json;charset=UTF-8");
         * 
         * Map<String, Object> errorResponse = new HashMap<>();
         * errorResponse.put("success", false);
         * errorResponse.put("message", "权限不足，无法访问该资源");
         * errorResponse.put("code", 403);
         * 
         * response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
         * return;
         * }
         * 
         * // 权限验证通过，继续处理请求
         * filterChain.doFilter(request, response);
         */
    }

    /**
     * 判断是否跳过权限验证
     */
    private boolean shouldSkipAuthorization(String requestURI) {
        // 登录相关接口不需要权限验证
        return requestURI.startsWith("/login") ||
                requestURI.startsWith("/register") ||
                requestURI.equals("/users/login") ||
                requestURI.equals("/users/register") ||
                requestURI.equals("/users/test") || // 测试接口
                requestURI.equals("/users/growth-stats") || // 用户增长统计接口
                requestURI.startsWith("/userBehavior") || // 用户行为相关接口
                requestURI.startsWith("/public") ||
                requestURI.contains("/static/") ||
                requestURI.startsWith("/images/") || // 轮播图及其他图片资源
                requestURI.startsWith("/avatar/") || // 头像资源
                requestURI.startsWith("/CourseCover/") || // 课程封面资源
                requestURI.startsWith("/Video/") || // 视频资源
                requestURI.startsWith("/tenant-icons/") || // 租户图标资源
                requestURI.startsWith("/icons/") || // 通用图标资源
                requestURI.startsWith("/ConferenceCover/") || // 会议封面资源
                requestURI.startsWith("/carousel/") || // 轮播图相关接口
                requestURI.endsWith(".js") ||
                requestURI.endsWith(".css") ||
                requestURI.endsWith(".html") ||
                requestURI.endsWith(".ico") ||
                requestURI.endsWith(".jpg") ||
                requestURI.endsWith(".jpeg") ||
                requestURI.endsWith(".png") ||
                requestURI.endsWith(".gif") ||
                requestURI.endsWith(".webp") ||
                requestURI.endsWith(".svg") ||
                requestURI.endsWith(".mp4") ||
                requestURI.endsWith(".avi") ||
                requestURI.endsWith(".mov") ||
                // 临时添加：跳过所有API权限验证（用于调试）
                requestURI.startsWith("/api/courses") || // 课程相关接口
                requestURI.startsWith("/users/all") || // 用户列表接口
                requestURI.startsWith("/users/tenant/") || // 租户用户接口
                requestURI.startsWith("/users/id/") || // 用户详情接口
                requestURI.startsWith("/users/profile/") || // 用户资料接口
                requestURI.startsWith("/users/search") || // 用户搜索接口
                requestURI.startsWith("/departments") || // 部门相关接口
                requestURI.startsWith("/api/tenants") || // 租户相关接口
                requestURI.equals("/api/tenants/upload-icon") || // 租户图标上传接口
                requestURI.startsWith("/conferences") || // 会议相关接口
                requestURI.startsWith("/api/information") || // 资讯相关接口
                requestURI.startsWith("/api/meeting-applications") || // 会议申请相关接口
                requestURI.startsWith("/api/test") ||
                requestURI.startsWith("/api/ai") || // AI相关接口
                requestURI.startsWith("/users/upload-avatar") || // 头像上传接口
                requestURI.startsWith("/api/cache"); // 缓存管理接口
    }

    /**
     * 权限验证核心逻辑
     */
    private boolean hasPermission(String requestURI, String method, String userRole,
            String userTenantId, String userId, HttpServletRequest request) {

        // 系统管理员拥有所有权限
        if ("Admin".equals(userRole)) {
            return true;
        }

        // 资讯管理权限控制
        if (requestURI.startsWith("/api/information")) {
            return checkInformationPermission(requestURI, method, userRole, userTenantId, userId, request);
        }

        // 会议申请权限控制
        if (requestURI.startsWith("/api/meeting-applications")) {
            return checkMeetingApplicationPermission(requestURI, method, userRole, userTenantId, userId, request);
        }

        // 会议管理权限控制
        if (requestURI.startsWith("/conferences")) {
            return checkConferencePermission(requestURI, method, userRole, userTenantId, userId, request);
        }

        // 用户管理权限控制
        if (requestURI.startsWith("/users")) {
            return checkUserPermission(requestURI, method, userRole, userTenantId, userId, request);
        }

        // 部门管理权限控制
        if (requestURI.startsWith("/departments")) {
            return checkDepartmentPermission(requestURI, method, userRole, userTenantId, userId, request);
        }

        // 课程管理权限控制
        if (requestURI.startsWith("/api/courses")) {
            return checkCoursePermission(requestURI, method, userRole, userTenantId, userId, request);
        }

        // 租户管理权限控制
        if (requestURI.startsWith("/api/tenants")) {
            return checkTenantPermission(requestURI, method, userRole, userTenantId, userId, request);
        }

        // 默认允许访问
        return true;
    }

    /**
     * 资讯管理权限检查
     */
    private boolean checkInformationPermission(String requestURI, String method, String userRole,
            String userTenantId, String userId, HttpServletRequest request) {
        // 普通用户可以查看资讯，但不能修改或删除
        if ("User".equals(userRole)) {
            return method.equals("GET"); // 只允许GET请求（查看）
        }

        // 租户管理员只能访问自己租户的资讯
        if ("TAdmin".equals(userRole)) {
            // 如果是获取特定租户的资讯，检查租户ID
            if (requestURI.contains("/tenant/")) {
                String[] parts = requestURI.split("/tenant/");
                if (parts.length > 1) {
                    String requestTenantId = parts[1].split("/")[0];
                    return userTenantId.equals(requestTenantId);
                }
            }
            // 其他操作需要在业务层进一步验证
            return true;
        }

        return false;
    }

    /**
     * 会议申请权限检查
     */
    private boolean checkMeetingApplicationPermission(String requestURI, String method, String userRole,
            String userTenantId, String userId, HttpServletRequest request) {
        // 普通用户可以提交申请和查看自己的申请
        if ("User".equals(userRole)) {
            if (method.equals("POST") && requestURI.endsWith("/submit")) {
                return true; // 可以提交申请
            }
            if (method.equals("GET") && requestURI.contains("/applicant/")) {
                String[] parts = requestURI.split("/applicant/");
                if (parts.length > 1) {
                    String requestUserId = parts[1].split("/")[0];
                    return userId.equals(requestUserId); // 只能查看自己的申请
                }
            }
            if (method.equals("GET") && requestURI.endsWith("/check")) {
                return true; // 可以检查申请状态
            }
            return false;
        }

        // 租户管理员可以查看和审批本租户会议的申请
        if ("TAdmin".equals(userRole)) {
            if (requestURI.contains("/tenant/")) {
                String[] parts = requestURI.split("/tenant/");
                if (parts.length > 1) {
                    String requestTenantId = parts[1].split("/")[0];
                    return userTenantId.equals(requestTenantId);
                }
            }
            // 审批权限在业务层进一步验证
            return true;
        }

        return false;
    }

    /**
     * 会议管理权限检查
     */
    private boolean checkConferencePermission(String requestURI, String method, String userRole,
            String userTenantId, String userId, HttpServletRequest request) {
        // 普通用户只能查看会议，不能修改
        if ("User".equals(userRole)) {
            return method.equals("GET");
        }

        // 租户管理员可以管理本租户的会议
        if ("TAdmin".equals(userRole)) {
            return true; // 在业务层进一步验证租户权限
        }

        return false;
    }

    /**
     * 用户管理权限检查
     */
    private boolean checkUserPermission(String requestURI, String method, String userRole,
            String userTenantId, String userId, HttpServletRequest request) {
        // 普通用户权限
        if ("User".equals(userRole)) {
            // 允许查看用户列表（用于显示目的）
            if (method.equals("GET") && (requestURI.equals("/users/all") || requestURI.startsWith("/users/tenant/"))) {
                return true;
            }
            // 可以查看和修改自己的信息
            if (method.equals("GET") && requestURI.contains("/" + userId)) {
                return true; // 可以查看自己的信息
            }
            if (method.equals("PUT") && requestURI.contains("/" + userId)) {
                return true; // 可以修改自己的信息
            }
            // 允许查看个人资料
            if (method.equals("GET") && requestURI.startsWith("/users/profile/")) {
                return true;
            }
            // 允许访问统计相关接口（用于工作台显示）
            if (method.equals("GET") && (requestURI.equals("/users/growth-stats") ||
                    requestURI.startsWith("/users/stats") ||
                    requestURI.startsWith("/users/search"))) {
                return true;
            }
            return false;
        }

        // 租户管理员可以管理本租户的用户
        if ("TAdmin".equals(userRole)) {
            return true; // 在业务层进一步验证租户权限
        }

        return false;
    }

    /**
     * 部门管理权限检查
     */
    private boolean checkDepartmentPermission(String requestURI, String method, String userRole,
            String userTenantId, String userId, HttpServletRequest request) {
        // 普通用户只能查看部门信息，不能修改
        if ("User".equals(userRole)) {
            return method.equals("GET");
        }

        // 租户管理员可以管理本租户的部门
        if ("TAdmin".equals(userRole)) {
            return true; // 在业务层进一步验证租户权限
        }

        return false;
    }

    /**
     * 课程管理权限检查
     */
    private boolean checkCoursePermission(String requestURI, String method, String userRole,
            String userTenantId, String userId, HttpServletRequest request) {
        // 普通用户可以查看已审核通过的课程，不能修改
        if ("User".equals(userRole)) {
            return method.equals("GET");
        }

        // 租户管理员可以管理本租户的课程
        if ("TAdmin".equals(userRole)) {
            return true; // 在业务层进一步验证租户权限
        }

        return false;
    }

    /**
     * 从请求中获取用户角色
     */
    private String getUserRole(HttpServletRequest request) {
        // 可以从JWT Token、Session、请求头等方式获取
        String role = request.getHeader("User-Role");
        if (role == null) {
            role = request.getParameter("userRole");
        }
        return role != null ? role : "User"; // 默认为普通用户
    }

    /**
     * 从请求中获取用户租户ID
     */
    private String getUserTenantId(HttpServletRequest request) {
        String tenantId = request.getHeader("User-Tenant-Id");
        if (tenantId == null) {
            tenantId = request.getParameter("userTenantId");
        }
        return tenantId;
    }

    /**
     * 租户管理权限检查
     */
    private boolean checkTenantPermission(String requestURI, String method, String userRole,
            String userTenantId, String userId, HttpServletRequest request) {
        // 系统管理员拥有所有权限
        if ("Admin".equals(userRole)) {
            return true;
        }

        // 对于查看租户列表，所有登录用户都可以访问（用于显示合作成员）
        if (method.equals("GET") && (requestURI.equals("/api/tenants/all") || requestURI.equals("/api/tenants/list"))) {
            return true;
        }

        // 租户管理员只能查看自己的租户信息
        if ("TAdmin".equals(userRole) && method.equals("GET")) {
            if (requestURI.matches("/api/tenants/\\d+")) {
                // 提取URL中的租户ID
                String[] parts = requestURI.split("/");
                if (parts.length >= 4) {
                    String requestTenantId = parts[3];
                    return userTenantId != null && userTenantId.equals(requestTenantId);
                }
            }
            return false;
        }

        // 普通用户只能查看租户列表，不能进行其他操作
        if ("User".equals(userRole)) {
            return method.equals("GET")
                    && (requestURI.equals("/api/tenants/all") || requestURI.equals("/api/tenants/list"));
        }

        // 其他操作只有系统管理员可以进行
        return false;
    }

    /**
     * 从请求中获取用户ID
     */
    private String getUserId(HttpServletRequest request) {
        String userId = request.getHeader("User-Id");
        if (userId == null) {
            userId = request.getParameter("userId");
        }
        return userId;
    }

    /**
     * 获取用户名（解码Base64编码的中文用户名）
     */
    private String getUserName(HttpServletRequest request) {
        String encodedUserName = request.getHeader("User-Name");
        if (encodedUserName != null) {
            try {
                // 解码Base64编码的用户名
                byte[] decodedBytes = Base64.getDecoder().decode(encodedUserName);
                String decodedUserName = new String(decodedBytes, "UTF-8");
                return URLDecoder.decode(decodedUserName, "UTF-8");
            } catch (Exception e) {
                // 如果解码失败，返回原始值（向后兼容）
                return encodedUserName;
            }
        }
        return null;
    }
}