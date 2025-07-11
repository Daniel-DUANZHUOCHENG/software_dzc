package edu.neu.oaas.fwx.AiChat.Controller;


import edu.neu.oaas.fwx.AiChat.Service.StudyDifyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
@RequestMapping("/dify")
public class StudyDifyController {

    @Autowired
    private StudyDifyService studyDifyService;

    // 我们使用一个专用的线程池来异步处理流式任务，避免阻塞Web服务器的主线程
    private final ExecutorService executor = Executors.newCachedThreadPool();

    @GetMapping("/chat")
    public SseEmitter streamChat(@RequestParam String query,
                                 @RequestParam(required = false) String conversationId,
                                 @RequestParam(required = false) String user) {

        // 创建一个 SseEmitter，设置一个很长的超时时间
        final SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        final String userId = (user != null) ? user : "web-user-" + UUID.randomUUID();

        // 在后台线程中运行流式任务
        executor.execute(() -> {
            try {
                // 调用服务层的方法，并传入一个回调
                studyDifyService.streamChat(query, userId, conversationId, event -> {
                    try {
                        // 回调函数的核心：将从Dify收到的每个事件发送给前端
                        emitter.send(SseEmitter.event().name(event.event).data(event));
                    } catch (IOException e) {
                        // 如果客户端断开连接，会抛出异常
                        emitter.completeWithError(e);
                    }
                });
                // 当服务方法执行完毕后，正常结束这个SSE流
                emitter.complete();
            } catch (Exception e) {
                // 如果在调用difyService时发生任何错误，也通知前端
                emitter.completeWithError(e);
            }
        });

        return emitter;
    }

}