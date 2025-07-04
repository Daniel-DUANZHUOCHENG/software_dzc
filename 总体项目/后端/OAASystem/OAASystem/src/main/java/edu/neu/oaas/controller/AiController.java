package edu.neu.oaas.controller;

import edu.neu.oaas.service.AiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/ai")
public class AiController {

    @Autowired
    private AiService aiService;

    @PostMapping("/parse-form/user")
    public Map<String, Object> parseUserForm(@RequestBody Map<String, String> payload) {
        String text = payload.get("text");
        return aiService.parseTextForUserForm(text);
    }

    @PostMapping("/parse-form/tenant")
    public Map<String, Object> parseTenantForm(@RequestBody Map<String, String> payload) {
        String text = payload.get("text");
        return aiService.parseTextForTenantForm(text);
    }

    @PostMapping("/parse-form/department")
    public Map<String, Object> parseDepartmentForm(@RequestBody Map<String, String> payload) {
        String text = payload.get("text");
        return aiService.parseTextForDepartmentForm(text);
    }

    @PostMapping("/generate/tenant-remark")
    public Map<String, Object> generateTenantRemark(@RequestBody Map<String, String> payload) {
        String tenantName = payload.get("tenantName");
        String description = payload.get("description");
        return aiService.generateTenantRemark(tenantName, description);
    }
}