package edu.neu.oaas.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.neu.oaas.pojo.UserBehavior;
import edu.neu.oaas.service.UserBehaviorService;
import edu.neu.oaas.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class UserBehaviorControllerTest {

    @Mock
    private UserBehaviorService userBehaviorService;

    @Mock
    private UserService userService;

    @InjectMocks
    private UserBehaviorController userBehaviorController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(userBehaviorController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void testTrackUserBehavior_Success() throws Exception {
        // 准备测试数据
        UserBehavior behavior = new UserBehavior();
        behavior.setUserId(1);
        behavior.setAction("login");
        behavior.setTimestamp(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
        behavior.setPage("login-page");

        doNothing().when(userBehaviorService).createUserBehavior(any(UserBehavior.class));

        // 执行测试并验证结果
        mockMvc.perform(post("/userBehavior/track")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(behavior)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("行为记录成功"));

        verify(userBehaviorService).createUserBehavior(any(UserBehavior.class));
    }

    @Test
    void testTrackUserBehavior_Error() throws Exception {
        // 准备测试数据
        UserBehavior behavior = new UserBehavior();
        behavior.setUserId(1);
        behavior.setAction("login");
        behavior.setTimestamp(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
        behavior.setPage("login-page");

        doThrow(new RuntimeException("Failed to track behavior")).when(userBehaviorService).createUserBehavior(any(UserBehavior.class));

        // 执行测试并验证结果
        mockMvc.perform(post("/userBehavior/track")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(behavior)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("行为记录失败"));

        verify(userBehaviorService).createUserBehavior(any(UserBehavior.class));
    }

    @Test
    void testGetAllUserBehaviors_Success() throws Exception {
        UserBehavior behavior1 = new UserBehavior();
        behavior1.setUserId(1);
        behavior1.setAction("login");
        behavior1.setTimestamp(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
        behavior1.setPage("login-page");

        UserBehavior behavior2 = new UserBehavior();
        behavior2.setUserId(1);
        behavior2.setAction("logout");
        behavior2.setTimestamp(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
        behavior2.setPage("logout-page");

        List<UserBehavior> behaviors = Arrays.asList(behavior1, behavior2);

        when(userBehaviorService.getAllUserBehaviors()).thenReturn(behaviors);

        mockMvc.perform(get("/userBehavior"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.behaviors").isArray())
                .andExpect(jsonPath("$.behaviors.length()").value(2))
                .andExpect(jsonPath("$.total").value(2));

        verify(userBehaviorService).getAllUserBehaviors();
    }

    @Test
    void testGetAllUserBehaviors_Error() throws Exception {
        when(userBehaviorService.getAllUserBehaviors()).thenThrow(new RuntimeException("Failed to get behaviors"));

        // 执行测试并验证结果
        mockMvc.perform(get("/userBehavior"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("获取用户行为数据失败"));

        verify(userBehaviorService).getAllUserBehaviors();
    }

    @Test
    void testSearchUserBehaviors_Success() throws Exception {
        // 准备测试数据
        UserBehavior behavior = new UserBehavior();
        behavior.setUserId(1);
        behavior.setAction("login");
        behavior.setTimestamp(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
        behavior.setPage("login-page");

        List<UserBehavior> behaviors = Collections.singletonList(behavior);

        when(userBehaviorService.searchBehaviors(anyString(), anyString())).thenReturn(behaviors);

        // 执行测试并验证结果
        mockMvc.perform(get("/userBehavior/search")
                .param("action", "login")
                .param("timestamp", "2024-03-20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.behaviors[0].id").value(1))
                .andExpect(jsonPath("$.total").value(1));

        verify(userBehaviorService).searchBehaviors("login", "2024-03-20");
    }

    @Test
    void testSearchUserBehaviors_Error() throws Exception {
        when(userBehaviorService.searchBehaviors(anyString(), anyString())).thenThrow(new RuntimeException("Failed to search behaviors"));

        // 执行测试并验证结果
        mockMvc.perform(get("/userBehavior/search")
                .param("action", "login")
                .param("timestamp", "2024-03-20"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("搜索用户行为数据失败"));

        verify(userBehaviorService).searchBehaviors("login", "2024-03-20");
    }

    @Test
    void testIncrementOnlineUsers_Success() throws Exception {
        doNothing().when(userBehaviorService).incrementOnlineUsers();

        // 执行测试并验证结果
        mockMvc.perform(post("/userBehavior/increment"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("用户进入首页"));

        verify(userBehaviorService).incrementOnlineUsers();
    }

    @Test
    void testDecrementOnlineUsers_Success() throws Exception {
        doNothing().when(userBehaviorService).decrementOnlineUsers();

        // 执行测试并验证结果
        mockMvc.perform(post("/userBehavior/decrement"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("用户离开首页"));

        verify(userBehaviorService).decrementOnlineUsers();
    }

    @Test
    void testGetOnlineUsers_Success() throws Exception {
        when(userBehaviorService.getOnlineUsers()).thenReturn(5);

        // 执行测试并验证结果
        mockMvc.perform(get("/userBehavior/onlineUsers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.onlineUsers").value(5));

        verify(userBehaviorService).getOnlineUsers();
    }

    @Test
    void testGetRealTimeVisitors_Success() throws Exception {
        when(userBehaviorService.getRealTimeVisitors()).thenReturn(10);

        // 执行测试并验证结果
        mockMvc.perform(get("/userBehavior/realTimeVisitors"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.realTimeVisitors").value(10));

        verify(userBehaviorService).getRealTimeVisitors();
    }

    @Test
    void testUpdateRealTimeVisitors_Success() throws Exception {
        doNothing().when(userBehaviorService).updateRealTimeVisitors();

        // 执行测试并验证结果
        mockMvc.perform(post("/userBehavior/updateRealTimeVisitors"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("实时访客数已更新"));

        verify(userBehaviorService).updateRealTimeVisitors();
    }

    @Test
    void testGetUserPortrait_Success() throws Exception {
        // 准备测试数据
        Map<String, Object> portrait = new HashMap<>();
        portrait.put("userId", 1);
        portrait.put("loginCount", 10);
        portrait.put("lastLoginTime", "2024-03-20 10:00:00");
        portrait.put("activeLevel", "高");

        when(userService.generateUserPortrait(1)).thenReturn(portrait);

        // 执行测试并验证结果
        mockMvc.perform(get("/userBehavior/portrait/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.loginCount").value(10))
                .andExpect(jsonPath("$.lastLoginTime").value("2024-03-20 10:00:00"))
                .andExpect(jsonPath("$.activeLevel").value("高"));

        verify(userService).generateUserPortrait(1);
    }

    @Test
    void testGetUserPortrait_Error() throws Exception {
        when(userService.generateUserPortrait(1)).thenThrow(new RuntimeException("Failed to get user portrait"));

        // 执行测试并验证结果
        mockMvc.perform(get("/userBehavior/portrait/1"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("获取用户画像失败"));

        verify(userService).generateUserPortrait(1);
    }

    @Test
    void testGetUserPortraits_Success() throws Exception {
        // 准备测试数据
        Map<String, Object> portrait1 = new HashMap<>();
        portrait1.put("userId", 1);
        portrait1.put("loginCount", 10);
        portrait1.put("activeLevel", "高");

        Map<String, Object> portrait2 = new HashMap<>();
        portrait2.put("userId", 2);
        portrait2.put("loginCount", 5);
        portrait2.put("activeLevel", "中");

        List<Map<String, Object>> portraits = Arrays.asList(portrait1, portrait2);

        when(userService.generateAllUserPortraits()).thenReturn(portraits);

        // 执行测试并验证结果
        mockMvc.perform(get("/userBehavior/portraits"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userId").value(1))
                .andExpect(jsonPath("$[0].loginCount").value(10))
                .andExpect(jsonPath("$[0].activeLevel").value("高"))
                .andExpect(jsonPath("$[1].userId").value(2))
                .andExpect(jsonPath("$[1].loginCount").value(5))
                .andExpect(jsonPath("$[1].activeLevel").value("中"));

        verify(userService).generateAllUserPortraits();
    }

    @Test
    void testGetUserPortraits_Error() throws Exception {
        when(userService.generateAllUserPortraits()).thenThrow(new RuntimeException("Failed to get user portraits"));

        // 执行测试并验证结果
        mockMvc.perform(get("/userBehavior/portraits"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$[0].message").value("获取用户画像失败"));

        verify(userService).generateAllUserPortraits();
    }
} 