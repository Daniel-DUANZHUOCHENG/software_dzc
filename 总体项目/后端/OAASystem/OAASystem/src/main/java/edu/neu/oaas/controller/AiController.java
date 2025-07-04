package edu.neu.oaas.controller;

import edu.neu.oaas.service.AiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/ai")
public class AiController {

    @Autowired
    private AiService aiService;

    @PostMapping("/parse-form/user")
    public Map<String, Object> parseUserForm(@RequestBody Map<String, String> payload) {
        String text = payload.get("text");
        return aiService.parseTextForUserForm(text);
    }
}