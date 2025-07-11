package edu.neu.oaas.pojo;

import java.io.Serializable;

public class Course implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer courseID;
    private String coursename;
    private String coverpath;
    private String courseintro;
    private String number;
    private String videopath;
    private String owner;
    private Integer tenantID;
    private String approvalStatus;
    private String rejectionReason;

    public Course(Integer courseID, String coursename, String coverpath, String courseintro, String number, String videopath, String owner, Integer tenantID) {
        this.courseID = courseID;
        this.coursename = coursename;
        this.coverpath = coverpath;
        this.courseintro = courseintro;
        this.number = number;
        this.videopath = videopath;
        this.owner = owner;
        this.tenantID = tenantID;
        this.approvalStatus = "pending";
    }

    public String getCoursename() {
        return coursename;
    }

    public void setCoursename(String coursename) {
        this.coursename = coursename;
    }

    public Course() {
        this.approvalStatus = "pending";
    }

    public Integer getCourseID() {
        return courseID;
    }

    public void setCourseID(Integer courseID) {
        this.courseID = courseID;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getCoverpath() {
        return coverpath;
    }

    public void setCoverpath(String coverpath) {
        this.coverpath = coverpath;
    }

    public String getCourseintro() {
        return courseintro;
    }

    public void setCourseintro(String courseintro) {
        this.courseintro = courseintro;
    }

    public String getVideopath() {
        return videopath;
    }

    public void setVideopath(String videopath) {
        this.videopath = videopath;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public Integer getTenantID() {
        return tenantID;
    }

    public void setTenantID(Integer tenantID) {
        this.tenantID = tenantID;
    }

    public String getApprovalStatus() {
        return approvalStatus;
    }

    public void setApprovalStatus(String approvalStatus) {
        this.approvalStatus = approvalStatus;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }
}
