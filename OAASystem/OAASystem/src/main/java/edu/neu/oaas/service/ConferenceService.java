package edu.neu.oaas.service;

import edu.neu.oaas.mapper.ConferenceMapper;
import edu.neu.oaas.pojo.Conference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
public class ConferenceService {
    @Autowired
    private ConferenceMapper conferenceMapper;


    public List<Conference> getAllConferences() {
        return conferenceMapper.getAllConferences();
    }
    public void createConference(Conference conference) {
        conferenceMapper.insertConference(conference);
    }
    private static final String COVER_BASE_PATH = "E:/OAASystem/OAASystem/src/main/resources/static/ConferenceCover/";
    public Conference getConferenceById(Integer conferenceID) {
        return conferenceMapper.getConferenceById(conferenceID);
    }
    public String saveCover(MultipartFile file) throws IOException {
        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        String filePath = COVER_BASE_PATH + fileName;

        File dest = new File(filePath);
        if (!dest.getParentFile().exists()) {
            dest.getParentFile().mkdirs();
        }

        file.transferTo(dest);
        return "/ConferenceCover/" + fileName;
    }
    public void deleteConference(Integer conferenceID) {
        conferenceMapper.deleteConference(conferenceID);
    }
    public int updateConference(Conference conference) {
        return conferenceMapper.updateConference(conference);
    }
    public List<Conference> searchConferences(String conferencename, String creator, String starttime) {
        return conferenceMapper.searchConferences(conferencename, creator, starttime);
    }

    // 新增：获取待审核的会议
    public List<Conference> getPendingConferences() {
        return conferenceMapper.selectPendingConferences();
    }

    // 新增：根据审核状态获取会议
    public List<Conference> getConferencesByApprovalStatus(String approvalStatus) {
        return conferenceMapper.selectConferencesByApprovalStatus(approvalStatus);
    }

    // 新增：审核会议
    public int approveConference(Integer conferenceID, String approvalStatus, String rejectionReason) {
        return conferenceMapper.approveConference(conferenceID, approvalStatus, rejectionReason);
    }

    // 新增：获取已审核通过的会议（用于前端展示）
    public List<Conference> getApprovedConferences() {
        return conferenceMapper.selectApprovedConferences();
    }
}
