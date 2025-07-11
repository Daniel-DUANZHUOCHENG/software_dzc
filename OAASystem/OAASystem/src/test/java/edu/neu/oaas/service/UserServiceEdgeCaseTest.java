package edu.neu.oaas.service;

import edu.neu.oaas.mapper.DepartmentMapper;
import edu.neu.oaas.mapper.UserMapper;
import edu.neu.oaas.pojo.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 额外分支测试，补充 UserService 的失败路径等未覆盖场景
 */
public class UserServiceEdgeCaseTest {

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
        user.setId(1);
        user.setUsername("edge");
        user.setPassword("pwd");
        user.setCreatedAt(LocalDateTime.now());
        user.setAvatar(null); // 无头像
        user.setDepartmentId(2);
        user.setTenantId(1);
    }

    /**
     * 登录失败：用户名存在但密码错误应返回 null
     */
    @Test
    public void testLoginWrongPassword() {
        when(userMapper.getUserByUsername("edge")).thenReturn(user);
        User result = userService.login("edge", "wrong");
        assertNull(result);
    }

    /**
     * 更新密码失败：旧密码不匹配应返回 false
     */
    @Test
    public void testUpdatePasswordWrongOld() {
        when(userMapper.getUserById(1)).thenReturn(user);
        boolean ok = userService.updatePassword(1, "incorrect", "new");
        assertFalse(ok);
        // 不应调用 updateUser2
        verify(userMapper, never()).updateUser2(any(User.class));
    }

    /**
     * getUserProfile：无头像应返回默认头像
     */
    @Test
    public void testGetUserProfileDefaultAvatar() {
        when(userMapper.getUserById(1)).thenReturn(user);
        User profile = userService.getUserProfile(1);
        assertEquals("/avatar/default.jpg", profile.getAvatar());
    }

    /**
     * 根据租户 / 部门查询
     */
    @Test
    public void testGetUsersByTenantAndDepartment() {
        when(userMapper.getUsersByTenantId(1)).thenReturn(Collections.singletonList(user));
        List<User> byTenant = userService.getUsersByTenantId(1);
        assertEquals(1, byTenant.size());

        when(userMapper.getUserByDepartmentId(2)).thenReturn(Collections.singletonList(user));
        List<User> byDept = userService.getUserByDepartmentId(2);
        assertEquals(1, byDept.size());
    }
} 