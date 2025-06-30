package edu.neu.oaas.pojo;

import edu.neu.oaas.pojo.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class UserTest {

    private User user;

    @BeforeEach
    public void setUp() {
        user = new User();
    }

    @Test
    public void testGettersAndSetters() {
        LocalDateTime now = LocalDateTime.now();

        user.setId(1);
        user.setUsername("testuser");
        user.setPassword("password");
        user.setNickname("Test User");
        user.setPhoneNumber("1234567890");
        user.setEmail("testuser@example.com");
        user.setGender("Male");
        user.setDepartmentId(2);
        user.setStatus("Active");
        user.setRole("Admin");
        user.setCreatedAt(now);
        user.setPosition("Manager");
        user.setRemark("No remarks");
        user.setAvatar("avatar.png");
        user.setTenantId(3);
        user.setPath("/1/2/3");

        assertEquals(1, user.getId());
        assertEquals("testuser", user.getUsername());
        assertEquals("password", user.getPassword());
        assertEquals("Test User", user.getNickname());
        assertEquals("1234567890", user.getPhoneNumber());
        assertEquals("testuser@example.com", user.getEmail());
        assertEquals("Male", user.getGender());
        assertEquals(2, user.getDepartmentId());
        assertEquals("Active", user.getStatus());
        assertEquals("Admin", user.getRole());
        assertEquals(now, user.getCreatedAt());
        assertEquals("Manager", user.getPosition());
        assertEquals("No remarks", user.getRemark());
        assertEquals("avatar.png", user.getAvatar());
        assertEquals(3, user.getTenantId());
        assertEquals("/1/2/3", user.getPath());
    }

    @Test
    public void testToString() {
        LocalDateTime now = LocalDateTime.now();

        user.setId(1);
        user.setUsername("testuser");
        user.setPassword("password");
        user.setNickname("Test User");
        user.setPhoneNumber("1234567890");
        user.setEmail("testuser@example.com");
        user.setGender("Male");
        user.setDepartmentId(2);
        user.setStatus("Active");
        user.setRole("Admin");
        user.setCreatedAt(now);
        user.setPosition("Manager");
        user.setRemark("No remarks");
        user.setAvatar("avatar.png");
        user.setTenantId(3);
        user.setPath("/1/2/3");

        String expected = "User{id=1, username='testuser', password='password', nickname='Test User', phoneNumber='1234567890', email='testuser@example.com', gender='Male', departmentId=2, status='Active', role='Admin', createdAt=" + now + ", position='Manager', remark='No remarks', avatar='avatar.png', tenantId=3, path='/1/2/3'}";
        assertEquals(expected, user.toString());
    }
}
