package edu.neu.oaas.fwx.AiChat.Service;


import edu.neu.oaas.fwx.AiChat.pojo.AjaxResult;

import java.io.IOException;

public interface StudyAiChatService {
    AjaxResult uploadFileSER(String filePath, String user) throws IOException;
}
