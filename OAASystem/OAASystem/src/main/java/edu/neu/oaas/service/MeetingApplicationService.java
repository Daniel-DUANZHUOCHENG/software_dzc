package edu.neu.oaas.service;

import edu.neu.oaas.mapper.MeetingApplicationMapper;
import edu.neu.oaas.pojo.MeetingApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MeetingApplicationService {

    @Autowired
    private MeetingApplicationMapper meetingApplicationMapper;

    /**
     * 提交会议申请
     */
    public int submitMeetingApplication(MeetingApplication application) {
        // 检查是否已经申请过
        int existingCount = meetingApplicationMapper.checkExistingApplication(
                application.getMeetingId(), application.getApplicantId());
        
        if (existingCount > 0) {
            throw new RuntimeException("您已经申请过该会议，无需重复申请");
        }
        
        // 设置申请时间和状态
        application.setApplicationTime(LocalDateTime.now());
        application.setStatus("pending");
        
        return meetingApplicationMapper.insertMeetingApplication(application);
    }

    /**
     * 检查是否已经申请过某个会议
     */
    public int checkExistingApplication(Integer meetingId, Integer applicantId) {
        return meetingApplicationMapper.checkExistingApplication(meetingId, applicantId);
    }

    /**
     * 获取某个会议的所有申请
     */
    public List<MeetingApplication> getMeetingApplications(Integer meetingId) {
        return meetingApplicationMapper.getMeetingApplications(meetingId);
    }

    /**
     * 获取某个租户会议的申请（用于租户管理员查看）
     */
    public List<MeetingApplication> getMeetingApplicationsByTenant(Integer meetingId, Integer tenantId) {
        return meetingApplicationMapper.getMeetingApplicationsByTenant(meetingId, tenantId);
    }

    /**
     * 根据ID获取申请信息
     */
    public MeetingApplication getMeetingApplicationById(Integer applicationId) {
        return meetingApplicationMapper.getMeetingApplicationById(applicationId);
    }

    /**
     * 更新申请状态（审批）
     */
    public int updateApplicationStatus(Integer applicationId, String status, String rejectionReason, 
                                     Integer approverId, String approverName) {
        MeetingApplication application = new MeetingApplication();
        application.setId(applicationId);
        application.setStatus(status);
        application.setRejectionReason(rejectionReason);
        application.setApproverId(approverId);
        application.setApproverName(approverName);
        application.setApprovalTime(LocalDateTime.now());
        
        return meetingApplicationMapper.updateMeetingApplication(application);
    }

    /**
     * 获取用户的所有申请
     */
    public List<MeetingApplication> getUserApplications(Integer applicantId) {
        return meetingApplicationMapper.getUserApplications(applicantId);
    }

    /**
     * 获取租户的待审批申请
     */
    public List<MeetingApplication> getTenantPendingApplications(Integer tenantId) {
        return meetingApplicationMapper.getTenantPendingApplications(tenantId);
    }

    /**
     * 获取租户的所有申请（包括已审批的）
     */
    public List<MeetingApplication> getTenantAllApplications(Integer tenantId) {
        return meetingApplicationMapper.getTenantAllApplications(tenantId);
    }

    /**
     * 删除申请（如果需要）
     */
    public int deleteMeetingApplication(Integer applicationId) {
        return meetingApplicationMapper.deleteMeetingApplication(applicationId);
    }

    /**
     * 批量更新申请状态
     */
    public int batchUpdateApplicationStatus(List<Integer> applicationIds, String status, 
                                          Integer approverId, String approverName) {
        int count = 0;
        for (Integer id : applicationIds) {
            count += updateApplicationStatus(id, status, null, approverId, approverName);
        }
        return count;
    }

    /**
     * 获取申请统计信息
     */
    public java.util.Map<String, Object> getApplicationStatistics(Integer tenantId) {
        java.util.Map<String, Object> stats = new java.util.HashMap<>();
        
        // 获取待审批数量
        int pendingCount = meetingApplicationMapper.getTenantApplicationCountByStatus(tenantId, "pending");
        // 获取已通过数量
        int approvedCount = meetingApplicationMapper.getTenantApplicationCountByStatus(tenantId, "approved");
        // 获取已拒绝数量
        int rejectedCount = meetingApplicationMapper.getTenantApplicationCountByStatus(tenantId, "rejected");
        
        stats.put("pendingCount", pendingCount);
        stats.put("approvedCount", approvedCount);
        stats.put("rejectedCount", rejectedCount);
        stats.put("totalCount", pendingCount + approvedCount + rejectedCount);
        
        return stats;
    }

    /**
     * 根据状态获取申请列表
     */
    public List<MeetingApplication> getMeetingApplicationsByStatus(String status) {
        return meetingApplicationMapper.getMeetingApplicationsByStatus(status);
    }

    /**
     * 根据申请人ID获取申请列表
     */
    public List<MeetingApplication> getMeetingApplicationsByApplicantId(Integer applicantId) {
        return meetingApplicationMapper.getUserApplications(applicantId);
    }

    /**
     * 审批会议申请
     */
    public int approveMeetingApplication(Integer applicationId, String status, String rejectionReason, 
                                       Integer approverId, String approverName) {
        return updateApplicationStatus(applicationId, status, rejectionReason, approverId, approverName);
    }

    /**
     * 检查是否已经申请过该会议
     */
    public boolean hasAppliedForMeeting(Integer meetingId, Integer applicantId) {
        return checkExistingApplication(meetingId, applicantId) > 0;
    }

    /**
     * 获取待审批的申请数量
     */
    public int getPendingApplicationCount(Integer tenantId) {
        return meetingApplicationMapper.getTenantApplicationCountByStatus(tenantId, "pending");
    }

    /**
     * 获取所有申请（系统管理员用）
     */
    public List<MeetingApplication> getAllMeetingApplications() {
        return meetingApplicationMapper.getAllMeetingApplications();
    }
} 