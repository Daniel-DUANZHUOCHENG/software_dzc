package edu.neu.oaas.controller;

import edu.neu.oaas.pojo.Conference;
import edu.neu.oaas.service.ConferenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/conferences")
public class ConferenceController {
    @Autowired
    private ConferenceService conferenceService;

    @GetMapping
    public Map<String, Object> getConferences() {
        List<Conference> conferences = conferenceService.getAllConferences();
        Map<String, Object> response = new HashMap<>();
        response.put("meetings", conferences);
        response.put("total", conferences.size());
        return response;
    }


    @GetMapping("/{conferenceID}")
    public ResponseEntity<Conference> getConferenceById(@PathVariable Integer conferenceID) {
        Conference conference = conferenceService.getConferenceById(conferenceID);
        if (conference == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.ok(conference);
    }
    @PostMapping
    public ResponseEntity<Map<String, String>> createConference(@RequestBody Conference conference) {
        try {
            conferenceService.createConference(conference);
            Map<String, String> response = new HashMap<>();
            response.put("message", "会议创建成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "会议创建失败");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/upload-cover")
    public ResponseEntity<Map<String, String>> uploadCover(@RequestParam("file") MultipartFile file) {
        try {
            String coverUrl = conferenceService.saveCover(file);
            Map<String, String> response = new HashMap<>();
            response.put("url", coverUrl);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    @DeleteMapping("/{conferenceID}")
    public void deleteConference(@PathVariable Integer conferenceID) {
        conferenceService.deleteConference(conferenceID);
    }
    @PutMapping("/{conferenceID}")
    public Map<String, String> updateConference(@PathVariable Integer conferenceID, @RequestBody Conference conference) {
        conference.setConferenceID(conferenceID);
        int result = conferenceService.updateConference(conference);
        Map<String, String> response = new HashMap<>();
        if (result > 0) {
            response.put("message", "会议修改成功");
        } else {
            response.put("message", "会议修改失败");
        }
        return response;
    }
    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchConferences(
            @RequestParam(value = "conferencename", required = false) String conferencename,
            @RequestParam(value = "creator", required = false) String creator,
            @RequestParam(value = "starttime", required = false) String starttime) {

        List<Conference> conferences = conferenceService.searchConferences(conferencename, creator, starttime);
        Map<String, Object> response = new HashMap<>();
        response.put("meetings", conferences);
        response.put("total", conferences.size());
        return ResponseEntity.ok(response);
    }

    // 审核相关API
    @GetMapping("/pending")
    public ResponseEntity<Map<String, Object>> getPendingConferences() {
        List<Conference> conferences = conferenceService.getPendingConferences();
        Map<String, Object> response = new HashMap<>();
        response.put("meetings", conferences);
        response.put("total", conferences.size());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/status/{approvalStatus}")
    public ResponseEntity<Map<String, Object>> getConferencesByApprovalStatus(@PathVariable String approvalStatus) {
        List<Conference> conferences = conferenceService.getConferencesByApprovalStatus(approvalStatus);
        Map<String, Object> response = new HashMap<>();
        response.put("meetings", conferences);
        response.put("total", conferences.size());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{conferenceID}/approve")
    public ResponseEntity<Map<String, String>> approveConference(
            @PathVariable Integer conferenceID,
            @RequestBody Map<String, String> approvalData) {
        try {
            String approvalStatus = approvalData.get("approvalStatus");
            String rejectionReason = approvalData.get("rejectionReason");
            
            int result = conferenceService.approveConference(conferenceID, approvalStatus, rejectionReason);
            Map<String, String> response = new HashMap<>();
            
            if (result > 0) {
                if ("approved".equals(approvalStatus)) {
                    response.put("message", "会议审核通过");
                } else {
                    response.put("message", "会议审核拒绝");
                }
                response.put("status", "success");
            } else {
                response.put("message", "审核失败");
                response.put("status", "error");
            }
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "审核失败: " + e.getMessage());
            response.put("status", "error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/approved")
    public ResponseEntity<Map<String, Object>> getApprovedConferences() {
        List<Conference> conferences = conferenceService.getApprovedConferences();
        Map<String, Object> response = new HashMap<>();
        response.put("meetings", conferences);
        response.put("total", conferences.size());
        return ResponseEntity.ok(response);
    }
}
