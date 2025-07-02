package edu.neu.oaas.utils;

import org.springframework.stereotype.Component;

/**
 * 权限验证工具类
 * 提供各种权限检查的具体实现
 */
@Component
public class PermissionValidator {

    /**
     * 检查用户是否为系统管理员
     */
    public boolean isSystemAdmin(String userRole) {
        return "Admin".equals(userRole);
    }

    /**
     * 检查用户是否为租户管理员
     */
    public boolean isTenantAdmin(String userRole) {
        return "TAdmin".equals(userRole);
    }

    /**
     * 检查用户是否为普通用户
     */
    public boolean isRegularUser(String userRole) {
        return "User".equals(userRole);
    }

    /**
     * 检查用户是否有管理员权限（系统管理员或租户管理员）
     */
    public boolean isAdminUser(String userRole) {
        return isSystemAdmin(userRole) || isTenantAdmin(userRole);
    }

    /**
     * 检查用户是否可以访问资讯管理
     */
    public boolean canAccessInformation(String userRole) {
        return isAdminUser(userRole);
    }

    /**
     * 检查用户是否可以修改特定资讯
     */
    public boolean canModifyInformation(String userRole, Integer userTenantId, Integer informationTenantId) {
        // 系统管理员可以修改所有资讯
        if (isSystemAdmin(userRole)) {
            return true;
        }
        
        // 租户管理员只能修改自己租户的资讯
        if (isTenantAdmin(userRole)) {
            return userTenantId != null && userTenantId.equals(informationTenantId);
        }
        
        // 普通用户不能修改资讯
        return false;
    }

    /**
     * 检查用户是否可以删除特定资讯
     */
    public boolean canDeleteInformation(String userRole, Integer userTenantId, Integer informationTenantId) {
        return canModifyInformation(userRole, userTenantId, informationTenantId);
    }

    /**
     * 检查用户是否可以访问会议管理
     */
    public boolean canAccessConference(String userRole) {
        return isAdminUser(userRole);
    }

    /**
     * 检查用户是否可以修改特定会议
     */
    public boolean canModifyConference(String userRole, Integer userTenantId, Integer conferenceTenantId) {
        // 系统管理员可以修改所有会议
        if (isSystemAdmin(userRole)) {
            return true;
        }
        
        // 租户管理员只能修改自己租户的会议
        if (isTenantAdmin(userRole)) {
            return userTenantId != null && userTenantId.equals(conferenceTenantId);
        }
        
        // 普通用户不能修改会议
        return false;
    }

    /**
     * 检查用户是否可以删除特定会议
     */
    public boolean canDeleteConference(String userRole, Integer userTenantId, Integer conferenceTenantId) {
        return canModifyConference(userRole, userTenantId, conferenceTenantId);
    }

    /**
     * 检查用户是否可以申请参加会议
     */
    public boolean canApplyForMeeting(String userRole) {
        // 所有用户都可以申请参加会议
        return true;
    }

    /**
     * 检查用户是否可以审批会议申请
     */
    public boolean canApproveMeetingApplication(String userRole, Integer userTenantId, Integer meetingTenantId) {
        // 系统管理员可以审批所有申请
        if (isSystemAdmin(userRole)) {
            return true;
        }
        
        // 租户管理员只能审批自己租户会议的申请
        if (isTenantAdmin(userRole)) {
            return userTenantId != null && userTenantId.equals(meetingTenantId);
        }
        
        // 普通用户不能审批申请
        return false;
    }

    /**
     * 检查用户是否可以访问用户管理
     */
    public boolean canAccessUserManagement(String userRole) {
        return isAdminUser(userRole);
    }

    /**
     * 检查用户是否可以修改特定用户信息
     */
    public boolean canModifyUser(String userRole, Integer currentUserId, Integer targetUserId, 
                                Integer currentUserTenantId, Integer targetUserTenantId) {
        // 系统管理员可以修改所有用户
        if (isSystemAdmin(userRole)) {
            return true;
        }
        
        // 租户管理员只能修改自己租户的用户
        if (isTenantAdmin(userRole)) {
            return currentUserTenantId != null && currentUserTenantId.equals(targetUserTenantId);
        }
        
        // 普通用户只能修改自己的信息
        if (isRegularUser(userRole)) {
            return currentUserId != null && currentUserId.equals(targetUserId);
        }
        
        return false;
    }

    /**
     * 检查用户是否可以删除特定用户
     */
    public boolean canDeleteUser(String userRole, Integer currentUserTenantId, Integer targetUserTenantId) {
        // 系统管理员可以删除所有用户
        if (isSystemAdmin(userRole)) {
            return true;
        }
        
        // 租户管理员只能删除自己租户的用户
        if (isTenantAdmin(userRole)) {
            return currentUserTenantId != null && currentUserTenantId.equals(targetUserTenantId);
        }
        
        // 普通用户不能删除用户
        return false;
    }

    /**
     * 检查用户是否可以访问部门管理
     */
    public boolean canAccessDepartmentManagement(String userRole) {
        return isAdminUser(userRole);
    }

    /**
     * 检查用户是否可以修改部门
     */
    public boolean canModifyDepartment(String userRole, Integer userTenantId, Integer departmentTenantId) {
        // 系统管理员可以修改所有部门
        if (isSystemAdmin(userRole)) {
            return true;
        }
        
        // 租户管理员只能修改自己租户的部门
        if (isTenantAdmin(userRole)) {
            return userTenantId != null && userTenantId.equals(departmentTenantId);
        }
        
        // 普通用户不能修改部门
        return false;
    }

    /**
     * 检查用户是否可以访问租户管理
     */
    public boolean canAccessTenantManagement(String userRole) {
        // 只有系统管理员可以访问租户管理
        return isSystemAdmin(userRole);
    }

    /**
     * 检查用户是否可以访问审核管理
     */
    public boolean canAccessApprovalManagement(String userRole) {
        return isAdminUser(userRole);
    }

    /**
     * 验证租户ID是否匹配
     */
    public boolean validateTenantAccess(Integer userTenantId, Integer resourceTenantId) {
        return userTenantId != null && userTenantId.equals(resourceTenantId);
    }

    /**
     * 验证用户ID是否匹配
     */
    public boolean validateUserAccess(Integer currentUserId, Integer targetUserId) {
        return currentUserId != null && currentUserId.equals(targetUserId);
    }
} 