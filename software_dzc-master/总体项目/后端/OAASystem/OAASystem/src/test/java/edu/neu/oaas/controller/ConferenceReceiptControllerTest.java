package edu.neu.oaas.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.neu.oaas.pojo.ConferenceReceipt;
import edu.neu.oaas.service.ConferenceReceiptService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ConferenceReceiptControllerTest {

    @Mock
    private ConferenceReceiptService conferenceReceiptService;

    @InjectMocks
    private ConferenceReceiptController conferenceReceiptController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(conferenceReceiptController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void testSubmitConferenceReceipt_Success() throws Exception {
        // 准备测试数据
        ConferenceReceipt receipt = new ConferenceReceipt();
        receipt.setId(1);
        receipt.setConferenceID(1);
        receipt.setName("张三");
        receipt.setUnit("技术部");
        receipt.setGender("男");
        receipt.setPhone("13800138000");
        receipt.setEmail("zhangsan@example.com");
        receipt.setRoomType("标准间");
        receipt.setArrivalMode("飞机");
        receipt.setArrivalFlight("CA1234");
        receipt.setArrivalTime("2024-03-20 10:00");
        receipt.setDepartureMode("飞机");
        receipt.setDepartureFlight("CA5678");
        receipt.setDepartureTime("2024-03-22 15:00");
        receipt.setRemarks("无特殊要求");

        // 模拟服务层行为
        doNothing().when(conferenceReceiptService).createConferenceReceipt(any(ConferenceReceipt.class));

        // 执行测试并验证结果
        mockMvc.perform(post("/conferenceReceipt/submit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(receipt)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("提交成功"));

        verify(conferenceReceiptService).createConferenceReceipt(any(ConferenceReceipt.class));
    }

    @Test
    void testSubmitConferenceReceipt_Error() throws Exception {
        // 准备测试数据
        ConferenceReceipt receipt = new ConferenceReceipt();
        receipt.setId(1);
        receipt.setConferenceID(1);
        receipt.setName("张三");
        receipt.setUnit("技术部");
        receipt.setGender("男");
        receipt.setPhone("13800138000");
        receipt.setEmail("zhangsan@example.com");

        // 模拟服务层抛出异常
        doThrow(new RuntimeException("Failed to submit receipt")).when(conferenceReceiptService).createConferenceReceipt(any(ConferenceReceipt.class));

        // 执行测试并验证结果
        mockMvc.perform(post("/conferenceReceipt/submit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(receipt)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("提交失败"));

        verify(conferenceReceiptService).createConferenceReceipt(any(ConferenceReceipt.class));
    }

    @Test
    void testSubmitConferenceReceipt_InvalidInput() throws Exception {
        // 执行测试并验证结果
        mockMvc.perform(post("/conferenceReceipt/submit")
                .contentType(MediaType.APPLICATION_JSON)
                .content("invalid json"))
                .andExpect(status().isBadRequest());

        verify(conferenceReceiptService, never()).createConferenceReceipt(any(ConferenceReceipt.class));
    }
} 