package edu.neu.oaas.service;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import okhttp3.*;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AiService {

    private final OkHttpClient client = new OkHttpClient();
    private final Gson gson = new Gson();
    // 使用您提供的API Key
    private final String apiKey = "sk-7e3f5d581b824014b8d95b18743b4d0c";
    private final String apiUrl = "https://api.deepseek.com/chat/completions";

    public Map<String, Object> parseTextForUserForm(String text) {
        String prompt = buildPrompt(text);

        // 构造请求体
        Map<String, String> systemMessage = new HashMap<>();
        systemMessage.put("role", "system");
        systemMessage.put("content",
                "You are a helpful assistant that extracts user information from natural language and returns it as a JSON object.");

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
                System.err.println("Failed to call AI API: " + response.message());
                return Collections.singletonMap("error", "Failed to call AI API: " + response.message());
            }

            String responseBody = response.body().string();

            // 解析AI返回的顶层JSON
            Map<String, Object> responseMap = gson.fromJson(responseBody, new TypeToken<Map<String, Object>>() {
            }.getType());

            if (responseMap.containsKey("choices")) {
                List<Map<String, Object>> choices = (List<Map<String, Object>>) responseMap.get("choices");
                if (!choices.isEmpty()) {
                    Map<String, Object> firstChoice = choices.get(0);
                    if (firstChoice.containsKey("message")) {
                        Map<String, Object> message = (Map<String, Object>) firstChoice.get("message");
                        if (message.containsKey("content")) {
                            String content = (String) message.get("content");

                            // 新增逻辑：清理AI返回的字符串，移除潜在的Markdown代码块
                            int firstBrace = content.indexOf('{');
                            int lastBrace = content.lastIndexOf('}');
                            String jsonContent = content;
                            if (firstBrace != -1 && lastBrace != -1 && lastBrace > firstBrace) {
                                jsonContent = content.substring(firstBrace, lastBrace + 1);
                            }

                            // AI返回的内容本身应该是一个JSON字符串，我们再次解析它
                            try {
                                Map<String, Object> extractedData = gson.fromJson(jsonContent,
                                        new TypeToken<Map<String, Object>>() {
                                        }.getType());
                                return extractedData;
                            } catch (JsonSyntaxException e) {
                                System.err.println("AI returned invalid JSON format: " + jsonContent);
                                return Collections.singletonMap("error", "AI returned invalid JSON format.");
                            }
                        }
                    }
                }
            }
            return Collections.singletonMap("error", "No valid response from AI.");

        } catch (IOException e) {
            e.printStackTrace();
            return Collections.singletonMap("error", "IOException during AI API call.");
        }
    }

    // 添加租户表单解析方法
    public Map<String, Object> parseTextForTenantForm(String text) {
        String prompt = buildPromptForTenant(text);

        // 构造请求体
        Map<String, String> systemMessage = new HashMap<>();
        systemMessage.put("role", "system");
        systemMessage.put("content",
                "You are a helpful assistant that extracts tenant information from natural language and returns it as a JSON object.");

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
                System.err.println("Failed to call AI API: " + response.message());
                return Collections.singletonMap("error", "Failed to call AI API: " + response.message());
            }

            String responseBody = response.body().string();

            // 解析AI返回的顶层JSON
            Map<String, Object> responseMap = gson.fromJson(responseBody, new TypeToken<Map<String, Object>>() {
            }.getType());

            if (responseMap.containsKey("choices")) {
                List<Map<String, Object>> choices = (List<Map<String, Object>>) responseMap.get("choices");
                if (!choices.isEmpty()) {
                    Map<String, Object> firstChoice = choices.get(0);
                    if (firstChoice.containsKey("message")) {
                        Map<String, Object> message = (Map<String, Object>) firstChoice.get("message");
                        if (message.containsKey("content")) {
                            String content = (String) message.get("content");

                            // 清理AI返回的字符串，移除潜在的Markdown代码块
                            int firstBrace = content.indexOf('{');
                            int lastBrace = content.lastIndexOf('}');
                            String jsonContent = content;
                            if (firstBrace != -1 && lastBrace != -1 && lastBrace > firstBrace) {
                                jsonContent = content.substring(firstBrace, lastBrace + 1);
                            }

                            // AI返回的内容本身应该是一个JSON字符串，我们再次解析它
                            try {
                                Map<String, Object> extractedData = gson.fromJson(jsonContent,
                                        new TypeToken<Map<String, Object>>() {
                                        }.getType());
                                return extractedData;
                            } catch (JsonSyntaxException e) {
                                System.err.println("AI returned invalid JSON format: " + jsonContent);
                                return Collections.singletonMap("error", "AI returned invalid JSON format.");
                            }
                        }
                    }
                }
            }
            return Collections.singletonMap("error", "No valid response from AI.");

        } catch (IOException e) {
            e.printStackTrace();
            return Collections.singletonMap("error", "IOException during AI API call.");
        }
    }

    // 新增：部门表单解析方法
    public Map<String, Object> parseTextForDepartmentForm(String text) {
        String prompt = buildPromptForDepartment(text);

        // 构造请求体
        Map<String, String> systemMessage = new HashMap<>();
        systemMessage.put("role", "system");
        systemMessage.put("content",
                "You are a helpful assistant that extracts department information from natural language and returns it as a JSON object.");

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
                System.err.println("Failed to call AI API: " + response.message());
                return Collections.singletonMap("error", "Failed to call AI API: " + response.message());
            }

            String responseBody = response.body().string();

            // 解析AI返回的顶层JSON
            Map<String, Object> responseMap = gson.fromJson(responseBody, new TypeToken<Map<String, Object>>() {
            }.getType());

            if (responseMap.containsKey("choices")) {
                List<Map<String, Object>> choices = (List<Map<String, Object>>) responseMap.get("choices");
                if (!choices.isEmpty()) {
                    Map<String, Object> firstChoice = choices.get(0);
                    if (firstChoice.containsKey("message")) {
                        Map<String, Object> message = (Map<String, Object>) firstChoice.get("message");
                        if (message.containsKey("content")) {
                            String content = (String) message.get("content");

                            // 清理AI返回的字符串，移除潜在的Markdown代码块
                            int firstBrace = content.indexOf('{');
                            int lastBrace = content.lastIndexOf('}');
                            String jsonContent = content;
                            if (firstBrace != -1 && lastBrace != -1 && lastBrace > firstBrace) {
                                jsonContent = content.substring(firstBrace, lastBrace + 1);
                            }

                            // AI返回的内容本身应该是一个JSON字符串，我们再次解析它
                            try {
                                Map<String, Object> extractedData = gson.fromJson(jsonContent,
                                        new TypeToken<Map<String, Object>>() {
                                        }.getType());
                                return extractedData;
                            } catch (JsonSyntaxException e) {
                                System.err.println("AI returned invalid JSON format: " + jsonContent);
                                return Collections.singletonMap("error", "AI returned invalid JSON format.");
                            }
                        }
                    }
                }
            }
            return Collections.singletonMap("error", "No valid response from AI.");

        } catch (IOException e) {
            e.printStackTrace();
            return Collections.singletonMap("error", "IOException during AI API call.");
        }
    }

    // 生成租户备注富文本内容
    public Map<String, Object> generateTenantRemark(String tenantName, String description) {
        String prompt = buildPromptForTenantRemark(tenantName, description);

        // 构造请求体
        Map<String, String> systemMessage = new HashMap<>();
        systemMessage.put("role", "system");
        systemMessage.put("content",
                "You are a helpful assistant that can generate professional company descriptions in HTML format.");

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
                System.err.println("Failed to call AI API: " + response.message());
                return Collections.singletonMap("error", "Failed to call AI API: " + response.message());
            }

            String responseBody = response.body().string();

            // 解析AI返回的顶层JSON
            Map<String, Object> responseMap = gson.fromJson(responseBody, new TypeToken<Map<String, Object>>() {
            }.getType());

            if (responseMap.containsKey("choices")) {
                List<Map<String, Object>> choices = (List<Map<String, Object>>) responseMap.get("choices");
                if (!choices.isEmpty()) {
                    Map<String, Object> firstChoice = choices.get(0);
                    if (firstChoice.containsKey("message")) {
                        Map<String, Object> message = (Map<String, Object>) firstChoice.get("message");
                        if (message.containsKey("content")) {
                            String content = (String) message.get("content");

                            // 清理内容，获取HTML部分
                            String htmlContent = extractHtmlContent(content);

                            Map<String, Object> result = new HashMap<>();
                            result.put("remark", htmlContent);
                            return result;
                        }
                    }
                }
            }
            return Collections.singletonMap("error", "No valid response from AI.");

        } catch (IOException e) {
            e.printStackTrace();
            return Collections.singletonMap("error", "IOException during AI API call.");
        }
    }

    // 从AI回复中提取HTML内容
    private String extractHtmlContent(String content) {
        // 如果AI返回了Markdown代码块，去除```html和```标记
        if (content.contains("```html")) {
            int startIndex = content.indexOf("```html") + 7;
            int endIndex = content.lastIndexOf("```");
            if (startIndex > 7 && endIndex > startIndex) {
                return content.substring(startIndex, endIndex).trim();
            }
        }

        // 如果没有代码块，但有<html>标签，尝试提取HTML部分
        if (content.contains("<")) {
            return content.trim();
        }

        // 如果内容不包含HTML标记，将其作为纯文本包装在段落标签中
        return "<p>" + content.replace("\n", "</p><p>") + "</p>";
    }

    private String buildPrompt(String userInput) {
        return "You are an efficient OA system assistant. Your task is to extract information from the user's natural language description and return it in a strict JSON format for filling out the 'Create User' form.\n\n"
                +
                "The user's description is:\n" +
                "\"" + userInput + "\"\n\n" +
                "Please extract information based on the following fields and requirements, and return ONLY the JSON object, without any additional explanations or text. The JSON keys must be exactly as specified here.\n\n"
                +
                "{\n" +
                "  \"username\": \"String, the username. If not provided, you can generate it from the pinyin of the nickname, e.g., '张三' becomes 'zhangsan'.\",\n"
                +
                "  \"nickname\": \"String, the user's nickname or name.\",\n" +
                "  \"phoneNumber\": \"String, the phone number.\",\n" +
                "  \"email\": \"String, the email address.\",\n" +
                "  \"position\": \"String, the user's job position.\",\n" +
                "  \"gender\": \"String, the user's gender. Must be 'Male' for male and 'Female' for female.\",\n" +
                "  \"departmentName\": \"String, the name of the department the user belongs to.\"\n" +
                "}\n\n" +
                "If a field is not mentioned in the user's description, omit that field from the JSON. For example, if the phone number is not mentioned, the 'phoneNumber' key should not appear in the final JSON.";
    }

    private String buildPromptForTenant(String userInput) {
        return "You are an efficient OA system assistant. Your task is to extract information from the user's natural language description and return it in a strict JSON format for filling out the 'Create Tenant' form.\n\n"
                +
                "The user's description is:\n" +
                "\"" + userInput + "\"\n\n" +
                "Please extract information based on the following fields and requirements, and return ONLY the JSON object, without any additional explanations or text. The JSON keys must be exactly as specified here.\n\n"
                +
                "{\n" +
                "  \"tenantName\": \"String, the name of the tenant company or organization.\",\n" +
                "  \"contactPerson\": \"String, the name of the contact person for this tenant.\",\n" +
                "  \"phone\": \"String, the contact phone number for this tenant.\",\n" +
                "  \"remark\": \"String, any remarks or description about this tenant.\"\n" +
                "}\n\n" +
                "If a field is not mentioned in the user's description, omit that field from the JSON. For example, if the phone number is not mentioned, the 'phone' key should not appear in the final JSON.";
    }

    private String buildPromptForTenantRemark(String tenantName, String description) {
        return "As a professional business copywriter, please create an attractive, detailed HTML description for a company/organization tenant in our OA system. "
                +
                "Use proper HTML formatting with headings, paragraphs, lists, and styling.\n\n" +
                "Tenant Name: " + tenantName + "\n\n" +
                "Additional information: " + description + "\n\n" +
                "Requirements:\n" +
                "1. Create a structured, visually appealing HTML description with at least 3 sections (e.g., Company Overview, Core Business, Achievements)\n"
                +
                "2. Use appropriate HTML tags (<h3>, <p>, <ul>, <li>, <strong>, <em>, etc.)\n" +
                "3. Include some basic inline CSS for better presentation\n" +
                "4. If no specific information is provided, create plausible professional content based on the company name\n"
                +
                "5. Keep the content professional, informative and positive\n" +
                "6. The final output should be ONLY the HTML content, nothing else\n\n" +
                "Please generate a comprehensive, professional HTML description suitable for an enterprise OA system.";
    }

    private String buildPromptForDepartment(String userInput) {
        return "You are an efficient OA system assistant. Your task is to extract department information from the user's natural language description and return it in a strict JSON format for filling out the 'Create Department' or 'Edit Department' form.\n\n"
                +
                "The user's description is:\n" +
                "\"" + userInput + "\"\n\n" +
                "Please extract information based on the following fields and requirements, and return ONLY the JSON object, without any additional explanations or text. The JSON keys must be exactly as specified here.\n\n"
                +
                "{\n" +
                "  \"departmentName\": \"String, the name of the department. This field is required.\",\n"
                +
                "  \"manager\": \"String, the name of the department manager.\",\n"
                +
                "  \"managerPhone\": \"String, the phone number of the department manager.\",\n"
                +
                "  \"managerEmail\": \"String, the email address of the department manager.\",\n"
                +
                "  \"status\": \"String, the status of the department. Must be 'Active' for normal and 'Inactive' for disabled.\"\n"
                +
                "}\n\n" +
                "If a field is not mentioned in the user's description, omit that field from the JSON. For example, if the manager's phone is not mentioned, the 'managerPhone' key should not appear in the final JSON.";
    }
}