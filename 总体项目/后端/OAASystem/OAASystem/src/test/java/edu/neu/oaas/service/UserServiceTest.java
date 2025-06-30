package edu.neu.oaas.service;

import edu.neu.oaas.mapper.UserMapper;
import edu.neu.oaas.mapper.DepartmentMapper;
import edu.neu.oaas.pojo.Department;
import edu.neu.oaas.pojo.User;
import edu.neu.oaas.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    @Mock
    private UserMapper userMapper;

    @Mock
    private DepartmentMapper departmentMapper;

    @InjectMocks
    private UserService userService;

    private User user;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User();
        user.setUsername("testuser");
        user.setPassword("password");
        user.setNickname("Test User");
        user.setPhoneNumber("1234567890");
        user.setEmail("testuser@example.com");
        user.setGender("Male");
        user.setDepartmentId(1);
        user.setStatus("Active");
        user.setRole("Admin");
        user.setCreatedAt(LocalDateTime.now());
        user.setPosition("Manager");
        user.setRemark("No remarks");
        user.setAvatar("avatar.png");
        user.setTenantId(1);
        user.setPath("/1/2/3");
        user.setId(10);
    }

    @Test
    public void testGetAllUser() {
        when(userMapper.getAllUser()).thenReturn(List.of(user));
        List<User> users = userService.getAllUser();
        assertFalse(users.isEmpty());
        verify(userMapper, times(1)).getAllUser();
    }

    @Test
    public void testGetUserById() {
        when(userMapper.getUserById(1)).thenReturn(user);
        User found = userService.getUserById(1);
        assertNotNull(found);
        verify(userMapper, times(1)).getUserById(1);
    }

    @Test
    public void testFindByPath() {
        when(userMapper.findByPathPrefix("/1")).thenReturn(List.of(user));
        List<User> users = userService.findByPath("/1");
        assertFalse(users.isEmpty());
        verify(userMapper, times(1)).findByPathPrefix("/1");
    }

    @Test
    public void testGetUserByName() {
        when(userMapper.getUserByName("testuser")).thenReturn(user);
        User found = userService.getUserByName("testuser");
        assertNotNull(found);
        verify(userMapper, times(1)).getUserByName("testuser");
    }

    @Test
    public void testInsertUser() {
        when(departmentMapper.getDepartmentById(1)).thenReturn(new Department() {{
            setPath("/1/2");
        }});
        doNothing().when(userMapper).insertUser(any(User.class));

        boolean result = userService.insertUser(user);
        assertTrue(result);
        verify(departmentMapper, times(1)).getDepartmentById(1);
        verify(userMapper, times(1)).insertUser(any(User.class));
    }

    @Test
    public void testDeleteById() {
        doNothing().when(userMapper).deleteById(1);
        boolean result = userService.deleteById(1);
        assertTrue(result);
        verify(userMapper, times(1)).deleteById(1);
    }

    @Test
    public void testUpdateUser() {
        boolean result = userService.updateUser(user);
        assertTrue(result);
        verify(userMapper, times(1)).updateUser(anyInt(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyInt(), anyString(), anyString(), any(LocalDateTime.class), anyString(), anyString(), anyString(), anyInt(), anyString());
    }

    @Test
    public void testRegisterUser() {
        doNothing().when(userMapper).insertUser2(user);
        userService.registerUser(user);
        verify(userMapper, times(1)).insertUser2(user);
    }

    @Test
    public void testLogin() {
        when(userMapper.getUserByUsername("testuser")).thenReturn(user);
        User loggedInUser = userService.login("testuser", "password");
        assertNotNull(loggedInUser);
        verify(userMapper, times(1)).getUserByUsername("testuser");
    }

    @Test
    public void testUpdatePassword() {
        when(userMapper.getUserById(1)).thenReturn(user);

        boolean result = userService.updatePassword(1, "password", "newpassword");
        assertTrue(result);
        verify(userMapper, times(1)).getUserById(1);
        verify(userMapper, times(1)).updateUser2(user);
    }

    @Test
    public void testSaveAvatar() throws IOException {
        // 模拟 MultipartFile 对象
        MultipartFile file = mock(MultipartFile.class);
        when(file.getOriginalFilename()).thenReturn("avatar.png");
        when(file.getInputStream()).thenReturn(getClass().getResourceAsStream("/avatar.png"));

        // 模拟 User 对象
        User user = new User();
        user.setId(1);
        user.setAvatar("old_avatar.png");

        // 模拟 UserMapper 的行为
        when(userMapper.getUserById(1)).thenReturn(user);

        // 调用 UserService 的方法
        String avatarUrl = userService.saveAvatar(file, 1);

        // 断言返回的 avatarUrl 不是 null
        assertNotNull(avatarUrl);

        // 验证 userMapper 的方法被调用
        verify(userMapper, times(1)).getUserById(1);
        verify(userMapper, times(1)).updateUser2(any(User.class));
    }

    @Test
    public void testGetUsersByPage() {
        when(userMapper.getUsersByPage(anyInt(), anyInt())).thenReturn(List.of(user));
        when(userMapper.getTotalUserCount()).thenReturn(1);

        Map<String, Object> result = userService.getUsersByPage(1, 10);
        assertNotNull(result.get("users"));
        assertEquals(1, result.get("total"));
        verify(userMapper, times(1)).getUsersByPage(anyInt(), anyInt());
        verify(userMapper, times(1)).getTotalUserCount();
    }
}
