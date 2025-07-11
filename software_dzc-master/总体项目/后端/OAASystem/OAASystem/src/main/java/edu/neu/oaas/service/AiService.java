package edu.neu.oaas.service;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AiService {

    private final OkHttpClient client;
    private final Gson gson;
    
    @Value("${ai.api.key}")
    private String apiKey;
    
    @Value("${ai.api.url:https://api.deepseek.com/chat/completions}")
    private String apiUrl;

    public AiService() {
        this(new OkHttpClient());
    }

    public AiService(OkHttpClient client) {
        this.client = client;
        this.gson = new Gson();
    }

    private Map<String, Object> callAiApi(String prompt, String systemPrompt) {
        Map<String, String> systemMessage = new HashMap<>();
        systemMessage.put("role", "system");
        systemMessage.put("content", systemPrompt);

        Map<String, String> userMessage = new HashMap<>();
        userMessage.put("role", "user");
        userMessage.put("content", prompt);

        Map<String, Object> requestBodyMap = new HashMap<>();
        requestBodyMap.put("model", "deepseek-chat");
        requestBodyMap.put("messages", Arrays.asList(systemMessage, userMessage));
        requestBodyMap.put("stream", false);

        String jsonPayload = gson.toJson(requestBodyMap);
        RequestBody body = RequestBody.create(jsonPayload, MediaType.get("application/json; charset=utf-8"));

        Request request = new Request.Builder()
                .url(apiUrl)
                .header("Authorization", "Bearer " + apiKey)
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful() || response.body() == null) {
                return Collections.singletonMap("error", "Failed to call AI API: " + response.message());
            }

            String responseBody = response.body().string();
            Map<String, Object> responseMap = gson.fromJson(responseBody, new TypeToken<Map<String, Object>>() {}.getType());

            if (responseMap.containsKey("choices")) {
                List<Map<String, Object>> choices = (List<Map<String, Object>>) responseMap.get("choices");
                if (!choices.isEmpty()) {
                    Map<String, Object> firstChoice = choices.get(0);
                    if (firstChoice.containsKey("message")) {
                        Map<String, Object> message = (Map<String, Object>) firstChoice.get("message");
                        if (message.containsKey("content")) {
                            String content = (String) message.get("content");
                            return extractJsonFromContent(content);
                        }
                    }
                }
            }
            return Collections.singletonMap("error", "No valid response from AI.");

        } catch (IOException e) {
            return Collections.singletonMap("error", "IOException during AI API call: " + e.getMessage());
        }
    }

    private Map<String, Object> extractJsonFromContent(String content) {
        try {
                            int firstBrace = content.indexOf('{');
                            int lastBrace = content.lastIndexOf('}');
                            String jsonContent = content;
                            if (firstBrace != -1 && lastBrace != -1 && lastBrace > firstBrace) {
                                jsonContent = content.substring(firstBrace, lastBrace + 1);
                            }
            return gson.fromJson(jsonContent, new TypeToken<Map<String, Object>>() {}.getType());
                            } catch (JsonSyntaxException e) {
                                return Collections.singletonMap("error", "AI returned invalid JSON format.");
        }
    }

    public Map<String, Object> parseTextForUserForm(String text) {
        String systemPrompt = "You are a helpful assistant that extracts user information from natural language and returns it as a JSON object with the following fields: username, nickname, position, departmentName.";
        String prompt = String.format("Extract user information from the following text and return a JSON object: %s", text);
        return callAiApi(prompt, systemPrompt);
    }

    public Map<String, Object> parseTextForTenantForm(String text) {
        String systemPrompt = "You are a helpful assistant that extracts tenant information from natural language and returns it as a JSON object with the following fields: tenantName, contactPerson.";
        String prompt = String.format("Extract tenant information from the following text and return a JSON object: %s", text);
        return callAiApi(prompt, systemPrompt);
    }

    public Map<String, Object> parseTextForDepartmentForm(String text) {
        String systemPrompt = "You are a helpful assistant that extracts department information from natural language and returns it as a JSON object with the following fields: departmentName, manager, managerEmail, status.";
        String prompt = String.format("Extract department information from the following text and return a JSON object: %s", text);
        return callAiApi(prompt, systemPrompt);
    }

    public Map<String, Object> generateTenantRemark(String tenantName, String description) {
        String systemPrompt = "You are a helpful assistant that generates tenant remarks based on tenant information.";
        String prompt = String.format("Generate a professional remark for tenant '%s' based on this description: %s", tenantName, description);
        return callAiApi(prompt, systemPrompt);
    }
}