package edu.neu.oaas.fwx.AiChat.Service.Impl;

import edu.neu.oaas.fwx.AiChat.Service.StudyAiChatService;
import edu.neu.oaas.fwx.AiChat.Service.StudyDifyService;
import edu.neu.oaas.fwx.AiChat.pojo.AjaxResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

@Service
public class StudyAiChatServiceImpl implements StudyAiChatService {

    @Autowired
    private StudyDifyService studyDifyService;

    @Override
    public AjaxResult uploadFileSER(String filePath, String user) throws IOException {
        File file = new File("vue-ZFAiLesson/" + filePath);
        if (file.exists()) {
            return AjaxResult.success(studyDifyService.uploadFile(file.getAbsolutePath(), user));
        }
        else
            return AjaxResult.success(studyDifyService.uploadFile(filePath, user));
    }
}
