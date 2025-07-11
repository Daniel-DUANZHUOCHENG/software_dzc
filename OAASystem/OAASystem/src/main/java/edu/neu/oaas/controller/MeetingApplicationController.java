package edu.neu.oaas.controller;

import edu.neu.oaas.pojo.MeetingApplication;
import edu.neu.oaas.service.MeetingApplicationService;
import edu.neu.oaas.utils.PermissionValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/meeting-applications")
@CrossOrigin(origins = "*")
public class MeetingApplicationController {

    @Autowired
    private MeetingApplicationService meetingApplicationService;

    @Autowired
    private PermissionValidator permissionValidator;

    /**
     * 提交会议申请
     */
    @PostMapping("/submit")
    public ResponseEntity<Map<String, Object>> submitMeetingApplication(
            @RequestBody MeetingApplication application,
            HttpServletRequest request) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 获取用户信息
            String userRole = request.getHeader("User-Role");
            String userId = request.getHeader("User-Id");
            String userTenantId = request.getHeader("User-Tenant-Id");
            
            // 验证权限
            if (!permissionValidator.canApplyForMeeting(userRole)) {
                response.put("success", false);
                response.put("message", "权限不足，无法申请会议");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }
            
            // 设置申请人信息
            application.setApplicantId(Integer.parseInt(userId));
            application.setTenantId(Integer.parseInt(userTenantId));
            
            // 提交申请
            int result = meetingApplicationService.submitMeetingApplication(application);
            
            if (result > 0) {
                response.put("success", true);
                response.put("message", "申请提交成功");
                response.put("applicationId", application.getId());
            } else {
                response.put("success", false);
                response.put("message", "申请提交失败");
            }
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        
        return ResponseEntity.ok(response);
    }

    /**
     * 检查用户是否已申请过某个会议
     */
    @GetMapping("/check")
    public ResponseEntity<Map<String, Object>> checkApplicationExists(
            @RequestParam Integer meetingId,
            @RequestParam Integer applicantId,
            HttpServletRequest request) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            String userRole = request.getHeader("User-Role");
            String userId = request.getHeader("User-Id");
            
            // 普通用户只能查看自己的申请状态
            if ("User".equals(userRole) && !userId.equals(applicantId.toString())) {
                response.put("success", false);
                response.put("message", "权限不足");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }
            
            int count = meetingApplicationService.checkExistingApplication(meetingId, applicantId);
            
            response.put("success", true);
            response.put("hasApplied", count > 0);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "查询失败: " + e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }

    /**
     * 获取某个会议的所有申请
     */
    @GetMapping("/meeting/{meetingId}")
    public ResponseEntity<Map<String, Object>> getMeetingApplications(
            @PathVariable Integer meetingId,
            HttpServletRequest request) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            String userRole = request.getHeader("User-Role");
            String userTenantId = request.getHeader("User-Tenant-Id");
            
            // 只有管理员可以查看会议申请列表
            if (!permissionValidator.isAdminUser(userRole)) {
                response.put("success", false);
                response.put("message", "权限不足，无法查看申请列表");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }
            
            List<MeetingApplication> applications = meetingApplicationService.getMeetingApplications(meetingId);
            
            // 如果是租户管理员，只能查看本租户会议的申请
            if ("TAdmin".equals(userRole)) {
                applications = meetingApplicationService.getMeetingApplicationsByTenant(meetingId, Integer.parseInt(userTenantId));
            }
            response.put("success", true);
            response.put("applications", applications);
            response.put("total", applications.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "获取申请列表失败：" + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 根据申请人ID获取申请列表（用户查看自己的申请）
     */
    @GetMapping("/applicant/{applicantId}")
    public ResponseEntity<Map<String, Object>> getMeetingApplicationsByApplicantId(@PathVariable Integer applicantId) {
        try {
            List<MeetingApplication> applications = meetingApplicationService.getMeetingApplicationsByApplicantId(applicantId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("applications", applications);
            response.put("total", applications.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "获取申请列表失败：" + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 审批会议申请
     */
    @PostMapping("/{applicationId}/approve")
    public ResponseEntity<Map<String, Object>> approveMeetingApplication(
            @PathVariable Integer applicationId,
            @RequestBody Map<String, Object> approvalData) {
        
        Map<String, Object> response = new HashMap<>();
        try {
            String status = (String) approvalData.get("status");
            String rejectionReason = (String) approvalData.get("rejectionReason");
            Integer approverId = (Integer) approvalData.get("approverId");
            String approverName = (String) approvalData.get("approverName");
            
            int result = meetingApplicationService.approveMeetingApplication(
                    applicationId, status, rejectionReason, approverId, approverName);
            
            if (result > 0) {
                response.put("success", true);
                if ("approved".equals(status)) {
                    response.put("message", "申请已通过");
                } else if ("rejected".equals(status)) {
                    response.put("message", "申请已拒绝");
                } else {
                    response.put("message", "申请状态已更新");
                }
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "审批失败");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "审批失败：" + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 根据ID获取申请详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getMeetingApplicationById(@PathVariable Integer id) {
        try {
            MeetingApplication application = meetingApplicationService.getMeetingApplicationById(id);
            Map<String, Object> response = new HashMap<>();
            if (application != null) {
                response.put("success", true);
                response.put("application", application);
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "申请不存在");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "获取申请详情失败：" + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 获取待审批的申请数量
     */
    @GetMapping("/pending/count")
    public ResponseEntity<Map<String, Object>> getPendingApplicationCount(@RequestParam Integer tenantId) {
        try {
            int count = meetingApplicationService.getPendingApplicationCount(tenantId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("count", count);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "获取待审批数量失败：" + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 租户管理员获取本租户全部待审批申请
     */
    @GetMapping("/tenant/{tenantId}/pending")
    public ResponseEntity<Map<String, Object>> getTenantPendingApplications(@PathVariable Integer tenantId) {
        try {
            List<MeetingApplication> list = meetingApplicationService.getTenantPendingApplications(tenantId);
            Map<String, Object> resp = new HashMap<>();
            resp.put("success", true);
            resp.put("applications", list);
            resp.put("total", list.size());
            return ResponseEntity.ok(resp);
        } catch (Exception e) {
            Map<String, Object> resp = new HashMap<>();
            resp.put("success", false);
            resp.put("message", "获取申请列表失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(resp);
        }
    }

    /**
     * 删除申请
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteMeetingApplication(@PathVariable Integer id) {
        try {
            int result = meetingApplicationService.deleteMeetingApplication(id);
            Map<String, Object> response = new HashMap<>();
            if (result > 0) {
                response.put("success", true);
                response.put("message", "申请删除成功");
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "申请删除失败");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "删除申请失败：" + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 获取所有申请（系统管理员用）
     */
    @GetMapping("/all")
    public ResponseEntity<Map<String, Object>> getAllMeetingApplications() {
        try {
            List<MeetingApplication> applications = meetingApplicationService.getAllMeetingApplications();
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("applications", applications);
            response.put("total", applications.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "获取申请列表失败：" + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 根据状态获取申请列表
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<Map<String, Object>> getMeetingApplicationsByStatus(@PathVariable String status) {
        try {
            List<MeetingApplication> applications = meetingApplicationService. getMeetingApplicationsByStatus(status);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("applications", applications);
            response.put("total", applications.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "获取申请列表失败：" + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
} 