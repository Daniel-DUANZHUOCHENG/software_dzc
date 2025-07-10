package edu.neu.oaas.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import edu.neu.oaas.controller.UserController;
import edu.neu.oaas.pojo.User;
import edu.neu.oaas.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private User user;

    private ObjectMapper objectMapper;


    @BeforeEach
    public void setUp() {
        user = new User();
        user.setId(1);
        user.setUsername("testuser");
        user.setPassword("password");
        user.setNickname("Test User");
        user.setPhoneNumber("1234567890");
        user.setEmail("testuser@example.com");
        user.setGender("Male");
        user.setDepartmentId(1);
        user.setStatus("Active");
        user.setRole("User");
        user.setCreatedAt(LocalDateTime.now());
        user.setPosition("Developer");
        user.setRemark("Remark");
        user.setAvatar("/avatar/default.jpg");
        user.setTenantId(1);
        user.setPath("/1_u");

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }


    @Test
    public void testGetAllUser() throws Exception {
        when(userService.getAllUser()).thenReturn(Collections.singletonList(user));

        mockMvc.perform(get("/users/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isOK", is(true)))
                .andExpect(jsonPath("$.userList[0].username", is("testuser")));

        verify(userService, times(1)).getAllUser();
    }

    @Test
    public void testGetUserById() throws Exception {
        when(userService.getUserById(1)).thenReturn(user);

        mockMvc.perform(get("/users/id/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is("testuser")));

        verify(userService, times(1)).getUserById(1);
    }

    @Test
    public void 
    testInsertUser() throws Exception {
        when(userService.getUserByName("testuser")).thenReturn(null);
        when(userService.insertUser(any(User.class))).thenReturn(true);

        mockMvc.perform(post("/users/insert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isOK", is(true)))
                .andExpect(jsonPath("$.msg", is("注册成功")));

        verify(userService, times(1)).getUserByName("testuser");
        verify(userService, times(1)).insertUser(any(User.class));
    }

    @Test
    public void testLogin() throws Exception {
        when(userService.getUserByName("testuser")).thenReturn(user);

        mockMvc.perform(post("/users/login")
                        .param("username", "testuser")
                        .param("password", "password"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.isOK").value(true))
                .andExpect(jsonPath("$.msg").doesNotExist());

        when(userService.getUserByName("testuser")).thenReturn(null);

        mockMvc.perform(post("/users/login")
                        .param("username", "testuser")
                        .param("password", "password"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.isOK").value(false))
                .andExpect(jsonPath("$.msg").value("账号不存在"));
    }

    @Test
    public void testUploadAvatar() throws Exception {
        // 模拟 MultipartFile
        MockMultipartFile file = new MockMultipartFile("file", "avatar.png", "image/png", "test image content".getBytes());

        when(userService.saveAvatar(any(MultipartFile.class), eq(1))).thenReturn("/avatar/1_avatar.png");

        mockMvc.perform(multipart("/users/upload-avatar")
                        .file(file)
                        .param("userId", "1"))
                .andExpect(status().isOk())
                .andExpect(content().string("/avatar/1_avatar.png"));

        verify(userService, times(1)).saveAvatar(any(MultipartFile.class), eq(1));
    }
}