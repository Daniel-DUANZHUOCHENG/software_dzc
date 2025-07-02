package edu.neu.oaas.controller;

import edu.neu.oaas.pojo.Information;
import edu.neu.oaas.service.InformationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/information")
public class InformationController {

    @Autowired
    private InformationService informationService;

    @GetMapping("/path/{pathPrefix}")
    public ResponseEntity<Map<String, Object>> getInformationByPathPrefix(@PathVariable String pathPrefix) {
        List<Information> informationList = informationService.findByPathPrefix(pathPrefix);
        Map<String, Object> response = new HashMap<>();
        response.put("informationList", informationList);
        response.put("total", informationList.size());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchInformation(
            @RequestParam String pathPrefix,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String author) {
        List<Information> informationList = informationService.searchByTitleAndAuthor(pathPrefix, title, author);
        Map<String, Object> response = new HashMap<>();
        response.put("informationList", informationList);
        response.put("total", informationList.size());
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> createInformation(@RequestBody Information information) {
        try {
            informationService.insertInformation(information);
            Map<String, String> response = new HashMap<>();
            response.put("message", "资讯创建成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "资讯创建失败");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, String>> updateInformation(@PathVariable int id, @RequestBody Information information) {
        try {
            information.setId(id);
            int result = informationService.updateInformation(information);
            Map<String, String> response = new HashMap<>();
            if (result > 0) {
                response.put("message", "资讯修改成功");
            } else {
                response.put("message", "资讯修改失败");
            }
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "资讯修改失败");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteInformation(@PathVariable int id) {
        try {
            informationService.deleteInformation(id);
            Map<String, String> response = new HashMap<>();
            response.put("message", "资讯删除成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "资讯删除失败");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Information> getInformationById(@PathVariable int id) {
        Information information = informationService.getInformationById(id);
        if (information != null) {
            return ResponseEntity.ok(information);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @GetMapping("/tenant/{tenantId}")
    public ResponseEntity<Map<String, Object>> getInformationByTenantId(@PathVariable int tenantId) {
        List<Information> informationList = informationService.getInformationByTenantId(tenantId);
        Map<String, Object> response = new HashMap<>();
        response.put("informationList", informationList);
        response.put("total", informationList.size());
        return ResponseEntity.ok(response);
    }

    // 审核相关API
    @GetMapping("/pending")
    public ResponseEntity<Map<String, Object>> getPendingInformation() {
        List<Information> informationList = informationService.getPendingInformation();
        Map<String, Object> response = new HashMap<>();
        response.put("informationList", informationList);
        response.put("total", informationList.size());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/status/{approvalStatus}")
    public ResponseEntity<Map<String, Object>> getInformationByApprovalStatus(@PathVariable String approvalStatus) {
        List<Information> informationList = informationService.getInformationByApprovalStatus(approvalStatus);
        Map<String, Object> response = new HashMap<>();
        response.put("informationList", informationList);
        response.put("total", informationList.size());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<Map<String, String>> approveInformation(
            @PathVariable int id,
            @RequestBody Map<String, String> approvalData) {
        try {
            String approvalStatus = approvalData.get("approvalStatus");
            String rejectionReason = approvalData.get("rejectionReason");
            
            int result = informationService.approveInformation(id, approvalStatus, rejectionReason);
            Map<String, String> response = new HashMap<>();
            
            if (result > 0) {
                if ("approved".equals(approvalStatus)) {
                    response.put("message", "资讯审核通过");
                } else {
                    response.put("message", "资讯审核拒绝");
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
    public ResponseEntity<Map<String, Object>> getApprovedInformation() {
        List<Information> informationList = informationService.getApprovedInformation();
        Map<String, Object> response = new HashMap<>();
        response.put("informationList", informationList);
        response.put("total", informationList.size());
        return ResponseEntity.ok(response);
    }

    // 新增：获取所有资讯的接口
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllInformation() {
        try {
            List<Information> informationList = informationService.getAllInformation();
            Map<String, Object> response = new HashMap<>();
            response.put("informationList", informationList);
            response.put("total", informationList.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("informationList", new java.util.ArrayList<>());
            response.put("total", 0);
            response.put("error", "获取资讯数据失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
