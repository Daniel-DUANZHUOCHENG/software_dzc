package edu.neu.oaas.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/ai") // 为所有AI相关接口提供一个统一的入口
public class AiController {

    // 从配置文件安全地读取API Key
    @Value("${deepseek.api.key}")
    private String deepSeekApiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * AI生成摘要和标签的代理接口
     * 
     * @param payload 前端传来的包含 "content" 的JSON对象
     * @return DeepSeek API返回的原始JSON字符串
     */
    @PostMapping("/summarize")
    public ResponseEntity<String> getSummaryAndTags(@RequestBody Map<String, String> payload) {
        String content = payload.get("content");
        if (content == null || content.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("{\"error\":\"内容不能为空\"}");
        }

        String deepSeekApiUrl = "https://api.deepseek.com/chat/completions";

        // 1. 设置请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(deepSeekApiKey);

        // 2. 精心设计我们的指令 (Prompt)
        String prompt = "你是一个专业的Markdown内容分析师。请对以下文本进行分析，并严格按照JSON格式返回一个包含'summary'和'tags'两个键的对象。" +
                "其中'summary'的值是文章摘要（100字以内），'tags'的值是一个包含3-5个关键词的字符串数组。\n\n" +
                "Markdown文本内容如下：\n" + content;

        // 3. 构建请求体
        Map<String, Object> message = new HashMap<>();
        message.put("role", "user");
        message.put("content", prompt);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "deepseek-chat"); // 或者 deepseek-coder, 根据你的需要
        requestBody.put("messages", Collections.singletonList(message));

        // 4. 发送请求
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(deepSeekApiUrl, entity, String.class);
            return response;
        } catch (Exception e) {
            // 如果API调用失败，返回一个统一的错误信息
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\":\"AI服务调用失败: " + e.getMessage() + "\"}");
        }
    }
}
