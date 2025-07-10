package edu.neu.oaas.pojo;

import java.time.LocalDateTime;

public class MeetingApplication {
    private Integer id;
    private Integer meetingId;
    private Integer applicantId;
    private String applicantName;
    private String applicantCompany;
    private LocalDateTime applicationTime;
    private String status; // pending, approved, rejected
    private String rejectionReason;
    private Integer approverId;
    private String approverName;
    private LocalDateTime approvalTime;
    private Integer tenantId; // 申请人所属租户
    private Integer meetingTenantId; // 会议所属租户

    public MeetingApplication() {
        this.status = "pending";
        this.applicationTime = LocalDateTime.now();
    }

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getMeetingId() {
        return meetingId;
    }

    public void setMeetingId(Integer meetingId) {
        this.meetingId = meetingId;
    }

    public Integer getApplicantId() {
        return applicantId;
    }

    public void setApplicantId(Integer applicantId) {
        this.applicantId = applicantId;
    }

    public String getApplicantName() {
        return applicantName;
    }

    public void setApplicantName(String applicantName) {
        this.applicantName = applicantName;
    }

    public String getApplicantCompany() {
        return applicantCompany;
    }

    public void setApplicantCompany(String applicantCompany) {
        this.applicantCompany = applicantCompany;
    }

    public LocalDateTime getApplicationTime() {
        return applicationTime;
    }

    public void setApplicationTime(LocalDateTime applicationTime) {
        this.applicationTime = applicationTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    public Integer getApproverId() {
        return approverId;
    }

    public void setApproverId(Integer approverId) {
        this.approverId = approverId;
    }

    public String getApproverName() {
        return approverName;
    }

    public void setApproverName(String approverName) {
        this.approverName = approverName;
    }

    public LocalDateTime getApprovalTime() {
        return approvalTime;
    }

    public void setApprovalTime(LocalDateTime approvalTime) {
        this.approvalTime = approvalTime;
    }

    public Integer getTenantId() {
        return tenantId;
    }

    public void setTenantId(Integer tenantId) {
        this.tenantId = tenantId;
    }

    public Integer getMeetingTenantId() {
        return meetingTenantId;
    }

    public void setMeetingTenantId(Integer meetingTenantId) {
        this.meetingTenantId = meetingTenantId;
    }

    @Override
    public String toString() {
        return "MeetingApplication{" +
                "id=" + id +
                ", meetingId=" + meetingId +
                ", applicantId=" + applicantId +
                ", applicantName='" + applicantName + '\'' +
                ", applicantCompany='" + applicantCompany + '\'' +
                ", applicationTime=" + applicationTime +
                ", status='" + status + '\'' +
                ", rejectionReason='" + rejectionReason + '\'' +
                ", approverId=" + approverId +
                ", approverName='" + approverName + '\'' +
                ", approvalTime=" + approvalTime +
                ", tenantId=" + tenantId +
                ", meetingTenantId=" + meetingTenantId +
                '}';
    }
} 