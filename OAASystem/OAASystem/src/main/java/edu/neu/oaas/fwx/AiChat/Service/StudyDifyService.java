package edu.neu.oaas.fwx.AiChat.Service;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.annotations.SerializedName;
import okhttp3.*;
import okio.BufferedSource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

@Service
public class StudyDifyService {

    // --- 配置信息 ---
    private static final String DIFY_API_BASE_URL = "https://api.dify.ai/v1";
    private static final String DIFY_API_KEY = "app-M9LsH2cuALCheHcGmuTn49wL";

    private final OkHttpClient client;
    private final Gson gson = new Gson();

    public StudyDifyService() {
        this.client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(300, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();
    }

    // uploadFile 方法保持不变...
    public String uploadFile(String filePath, String user) throws IOException {
//        System.out.println("--- 步骤 1: 开始上传文件 ---");
        File file = new File(filePath);
        if (!file.exists()) throw new IOException("文件未找到: " + filePath);

        String contentType = Files.probeContentType(Paths.get(filePath));
        MediaType mediaType = MediaType.parse(Objects.requireNonNullElse(contentType, "application/octet-stream"));

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("user", user)
                .addFormDataPart("file", file.getName(), RequestBody.create(file, mediaType))
                .build();

        Request request = new Request.Builder()
                .url(DIFY_API_BASE_URL + "/files/upload")
                .header("Authorization", "Bearer " + DIFY_API_KEY)
                .post(requestBody)
                .build();

        try (Response response = client.newCall(request).execute()) {
            String responseBodyString = response.body().string();
            if (!response.isSuccessful()) {
                throw new IOException("上传文件失败: " + response.code() + " " + responseBodyString);
            }
            FileUploadResponse uploadResponse = gson.fromJson(responseBodyString, FileUploadResponse.class);
            if (uploadResponse == null || uploadResponse.id == null) {
                throw new IOException("从响应中解析文件ID失败。");
            }
            return uploadResponse.id;
        }
    }


    /**
     * 流式对话（不带文件）
     * @param eventConsumer 一个用于处理从Dify收到的每个事件的回调函数。
     */
    public void streamChat(String query, String user, String conversationId, Consumer<UnifiedEventResponse> eventConsumer) throws IOException {
        Map<String, Object> payload = new HashMap<>();
        payload.put("inputs", new HashMap<>());
        payload.put("query", query);
        payload.put("user", user);
        payload.put("response_mode", "streaming");
        if (conversationId != null && !conversationId.isEmpty()) {
            payload.put("conversation_id", conversationId);
        }
        executeStreamingRequest(payload, eventConsumer);
    }

    /**
     * 流式对话（带文件）
     * @param eventConsumer 一个用于处理从Dify收到的每个事件的回调函数。
     */
    public void streamChatWithFile(String query, String user, String fileId, String conversationId, Consumer<UnifiedEventResponse> eventConsumer) throws IOException {
        Map<String, Object> fileInfo = new HashMap<>();
        fileInfo.put("type", "document");
        fileInfo.put("transfer_method", "local_file");
        fileInfo.put("upload_file_id", fileId);

        Map<String, Object> payload = new HashMap<>();
        payload.put("inputs", new HashMap<>());
        payload.put("query", query);
        payload.put("user", user);
        payload.put("response_mode", "streaming");
        payload.put("files", Collections.singletonList(fileInfo));
        if (conversationId != null && !conversationId.isEmpty()) {
            payload.put("conversation_id", conversationId);
        }
        executeStreamingRequest(payload, eventConsumer);
    }

    /**
     * 核心的请求执行和流处理逻辑
     */
    private void executeStreamingRequest(Map<String, Object> payload, Consumer<UnifiedEventResponse> eventConsumer) throws IOException {
        String jsonPayload = gson.toJson(payload);
        RequestBody body = RequestBody.create(jsonPayload, MediaType.get("application/json; charset=utf-8"));
        Request request = new Request.Builder()
                .url(DIFY_API_BASE_URL + "/chat-messages")
                .header("Authorization", "Bearer " + DIFY_API_KEY)
                .header("Accept", "text/event-stream")
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("请求对话流失败: " + response.code() + " " + response.body().string());
            }

            try (BufferedSource source = response.body().source()) {
                while (!source.exhausted()) {
                    String line = source.readUtf8Line();
                    if (line == null || !line.startsWith("data:")) {
                        continue;
                    }
                    String jsonData = line.substring(5).trim();
                    if (jsonData.isEmpty() || "[DONE]".equals(jsonData)) {
                        continue;
                    }

                    try {
                        UnifiedEventResponse event = gson.fromJson(jsonData, UnifiedEventResponse.class);
                        // 通过回调将事件发送出去
                        eventConsumer.accept(event);
                    } catch (JsonSyntaxException e) {
                        System.err.println("JSON 解析失败，忽略: " + jsonData);
                    }
                }
            }
        }
    }

    // --- 辅助类 ---
    // 这些辅助类最好定义在它们自己的文件里，或者作为一个公共的 DTOs 包。
    // 为了方便，暂时放在这里。
    static class FileUploadResponse {
        String id;
    }

    public class UnifiedEventResponse {
        @SerializedName("event")
        public String event;
        @SerializedName("conversation_id")
        public String conversationId;
        @SerializedName("message")
        public String message;
        @SerializedName("answer")
        public String answer;
        @SerializedName("data")
        public Data data;

        static class Data {
            @SerializedName("status")
            public String status;
            @SerializedName("title")
            public String title;
            @SerializedName("error")
            public String error;
        }
    }
}