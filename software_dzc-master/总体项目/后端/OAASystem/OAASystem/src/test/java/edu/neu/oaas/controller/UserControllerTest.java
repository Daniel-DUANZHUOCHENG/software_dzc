package edu.neu.oaas.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import edu.neu.oaas.pojo.LoginResponse;
import edu.neu.oaas.pojo.User;
import edu.neu.oaas.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

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

    // 1. 核心功能测试 - 登录和认证
    @Test
    public void testLoginSuccess() throws Exception {
        Map<String, String> credentials = new HashMap<>();
        credentials.put("username", "testuser");
        credentials.put("password", "password");

        when(userService.login("testuser", "password")).thenReturn(user);

        mockMvc.perform(post("/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(credentials)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Login successful"))
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.user.username").value("testuser"));
    }

    @Test
    public void testLoginFailure() throws Exception {
        Map<String, String> credentials = new HashMap<>();
        credentials.put("username", "testuser");
        credentials.put("password", "wrongpassword");

        when(userService.login("testuser", "wrongpassword")).thenReturn(null);

        mockMvc.perform(post("/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(credentials)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid username or password"))
                .andExpect(jsonPath("$.userId").value(-1))
                .andExpect(jsonPath("$.user").isEmpty());
    }

    // 2. 核心功能测试 - 用户注册
    @Test
    public void testInsertUserSuccess() throws Exception {
        when(userService.getUserByName("testuser")).thenReturn(null);
        when(userService.insertUser(any(User.class))).thenReturn(true);

        mockMvc.perform(post("/users/insert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isOK").value(true))
                .andExpect(jsonPath("$.msg").value("注册成功"));
    }

    @Test
    public void testInsertUserExists() throws Exception {
        when(userService.getUserByName("testuser")).thenReturn(user);

        mockMvc.perform(post("/users/insert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isOK").value(false))
                .andExpect(jsonPath("$.msg").value("用户已存在"));
    }

    // 3. 核心功能测试 - 密码更新
    @Test
    public void testUpdatePasswordSuccess() throws Exception {
        when(userService.updatePassword(1, "oldPassword", "newPassword")).thenReturn(true);

        mockMvc.perform(put("/users/updatePassword")
                        .param("userId", "1")
                        .param("oldPassword", "oldPassword")
                        .param("newPassword", "newPassword"))
                .andExpect(status().isOk())
                .andExpect(content().string("Password updated successfully"));
    }

    @Test
    public void testUpdatePasswordWrongOldPassword() throws Exception {
        when(userService.updatePassword(1, "wrongPassword", "newPassword")).thenReturn(false);

        mockMvc.perform(put("/users/updatePassword")
                        .param("userId", "1")
                        .param("oldPassword", "wrongPassword")
                        .param("newPassword", "newPassword"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Old password is incorrect"));
    }

    // 4. 核心功能测试 - 用户信息查询
    @Test
    public void testGetUserProfile() throws Exception {
        when(userService.getUserProfile(1)).thenReturn(user);

        mockMvc.perform(get("/users/profile/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"));
    }

    @Test
    public void testGetUserProfileNotFound() throws Exception {
        when(userService.getUserProfile(999)).thenReturn(null);

        mockMvc.perform(get("/users/profile/999"))
                .andExpect(status().isNotFound());
    }

    // 5. 核心功能测试 - 用户信息更新
    @Test
    public void testUpdateUserInfo() throws Exception {
        doNothing().when(userService).updateUserInfo(any(User.class));

        mockMvc.perform(put("/users/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(content().string("User information updated successfully"));
    }

    // 6. 核心功能测试 - 头像上传
    @Test
    public void testUploadAvatar() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "avatar.jpg",
                "image/jpeg",
                "test image content".getBytes()
        );

        when(userService.saveAvatar(any(), eq(1))).thenReturn("/avatars/1/avatar.jpg");

        mockMvc.perform(multipart("/users/upload-avatar")
                        .file(file)
                        .param("userId", "1"))
                .andExpect(status().isOk())
                .andExpect(content().string("/avatars/1/avatar.jpg"));
    }

    @Test
    public void testUploadAvatarEmptyFile() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "avatar.jpg",
                "image/jpeg",
                new byte[0]
        );

        mockMvc.perform(multipart("/users/upload-avatar")
                        .file(file)
                        .param("userId", "1"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("File is empty"));
    }

    // 7. 核心功能测试 - 基本查询
    @Test
    public void testGetAllUsers() throws Exception {
        List<User> users = Arrays.asList(user);
        when(userService.getAllUser()).thenReturn(users);

        mockMvc.perform(get("/users/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isOK").value(true))
                .andExpect(jsonPath("$.userList[0].username").value("testuser"));
    }

    @Test
    public void testGetUsersByTenantId() throws Exception {
        List<User> users = Arrays.asList(user);
        when(userService.getUsersByTenantId(1)).thenReturn(users);

        mockMvc.perform(get("/users/tenant/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isOK").value(true))
                .andExpect(jsonPath("$.userList[0].username").value("testuser"));
    }

    @Test
    public void testGetUserById() throws Exception {
        when(userService.getUserById(1)).thenReturn(user);

        mockMvc.perform(get("/users/id/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"));
    }

    @Test
    public void testGetUserByDepartmentId() throws Exception {
        List<User> users = Arrays.asList(user);
        when(userService.getUserByDepartmentId(1)).thenReturn(users);

        mockMvc.perform(get("/users/getBD")
                        .param("departmentId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isOK").value(true))
                .andExpect(jsonPath("$.userList[0].username").value("testuser"));
    }
}