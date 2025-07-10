package edu.neu.oaas.mapper;

import edu.neu.oaas.mapper.UserMapper;
import edu.neu.oaas.pojo.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@Transactional
public class UserMapperTest {

    @Autowired
    private UserMapper userMapper;

    private User user;

    @BeforeEach
    public void setUp() {
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
        user.setPath("1/2/3");
    }

    @Test
    public void testInsertUser() {
        userMapper.insertUser2(user);
        assertNotNull(user.getId());
        User found = userMapper.getUserById(user.getId());
        assertNotNull(found);
        assertEquals("testuser", found.getUsername());
    }

    @Test
    public void testFindAll() {
        userMapper.insertUser2(user);
        List<User> users = userMapper.getAllUser();
        assertFalse(users.isEmpty());
    }

    @Test
    public void testGetUserById() {
        userMapper.insertUser2(user);
        User found = userMapper.getUserById(user.getId());
        assertNotNull(found);
        assertEquals("testuser", found.getUsername());
    }

    @Test
    public void testGetUsersByTenantId() {
        userMapper.insertUser2(user);
        List<User> users = userMapper.getUsersByTenantId(1);
        assertFalse(users.isEmpty());
    }

    @Test
    public void testGetUserByUsername() {
        userMapper.insertUser2(user);
        User found = userMapper.getUserByUsername("testuser");
        assertNotNull(found);
    }

    @Test
    public void testUpdateUser() {
        userMapper.insertUser2(user);
        user.setUsername("updateduser");
        userMapper.updateUser2(user);
        User updated = userMapper.getUserById(user.getId());
        assertEquals("updateduser", updated.getUsername());
    }

    @Test
    public void testDeleteUser() {
        userMapper.insertUser2(user);
        userMapper.deleteById(user.getId());
        User deleted = userMapper.getUserById(user.getId());
        assertNull(deleted);
    }

    @Test
    public void testFindByPathPrefix() {
        userMapper.insertUser2(user);
        List<User> users = userMapper.findByPathPrefix("1");
        assertFalse(users.isEmpty());
    }

    @Test
    public void testGetUsersByPage() {
        userMapper.insertUser2(user);
        List<User> users = userMapper.getUsersByPage(0, 10);
        assertFalse(users.isEmpty());
    }

    @Test
    public void testGetTotalUserCount() {
        userMapper.insertUser2(user);
        int count = userMapper.getTotalUserCount();
        assertTrue(count > 0);
    }
}
