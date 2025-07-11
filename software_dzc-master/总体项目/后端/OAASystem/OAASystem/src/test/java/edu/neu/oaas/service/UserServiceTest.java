package edu.neu.oaas.service;

import edu.neu.oaas.mapper.DepartmentMapper;
import edu.neu.oaas.mapper.UserBehaviorMapper;
import edu.neu.oaas.mapper.UserMapper;
import edu.neu.oaas.pojo.Department;
import edu.neu.oaas.pojo.User;
import edu.neu.oaas.pojo.UserBehavior;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("User Service Tests")
class UserServiceTest {

    @Mock
    private UserMapper userMapper;

    @Mock
    private DepartmentMapper departmentMapper;

    @Mock
    private UserBehaviorMapper userBehaviorMapper;

    @InjectMocks
    private UserService userService;

    @TempDir
    Path tempDir;

    private User createSampleUser() {
        User user = new User();
        user.setId(1);
        user.setUsername("johndoe");
        user.setPassword("password123");
        user.setNickname("John Doe");
        user.setEmail("john@example.com");
        user.setDepartmentId(1);
        user.setRole("user");
        user.setStatus("active");
        return user;
    }

    @Nested
    @DisplayName("User Query Tests")
    class UserQueryTests {

        @Test
        @DisplayName("Should get all users")
        void testGetAllUsers() {
            // Arrange
            List<User> expectedUsers = Arrays.asList(createSampleUser());
            when(userMapper.getAllUser()).thenReturn(expectedUsers);

            // Act
            List<User> result = userService.getAllUser();

            // Assert
            assertEquals(expectedUsers, result);
            verify(userMapper).getAllUser();
        }

        @Test
        @DisplayName("Should get users by tenant ID")
        void testGetUsersByTenantId() {
            // Arrange
            List<User> expectedUsers = Arrays.asList(createSampleUser());
            when(userMapper.getUsersByTenantId(anyInt())).thenReturn(expectedUsers);

            // Act
            List<User> result = userService.getUsersByTenantId(1);

            // Assert
            assertEquals(expectedUsers, result);
            verify(userMapper).getUsersByTenantId(1);
        }

        @Test
        @DisplayName("Should get users by department ID")
        void testGetUserByDepartmentId() {
            // Arrange
            List<User> expectedUsers = Arrays.asList(createSampleUser());
            when(userMapper.getUserByDepartmentId(anyInt())).thenReturn(expectedUsers);

            // Act
            List<User> result = userService.getUserByDepartmentId(1);

            // Assert
            assertEquals(expectedUsers, result);
            verify(userMapper).getUserByDepartmentId(1);
        }

        @Test
        @DisplayName("Should search users with criteria")
        void testSearchUsers() {
            // Arrange
            List<User> expectedUsers = Arrays.asList(createSampleUser());
            when(userMapper.searchUsers(anyString(), anyString(), anyString(), any(), any()))
                .thenReturn(expectedUsers);

            // Act
            List<User> result = userService.searchUsers(
                "john", "1234567890", "active", 
                LocalDate.now(), LocalDate.now().plusDays(1)
            );

            // Assert
            assertEquals(expectedUsers, result);
            verify(userMapper).searchUsers(eq("john"), eq("1234567890"), eq("active"), 
                any(LocalDate.class), any(LocalDate.class));
        }

        @Test
        @DisplayName("Should get user by ID")
        void testGetUserById() {
            // Arrange
            User expectedUser = createSampleUser();
            when(userMapper.getUserById(anyInt())).thenReturn(expectedUser);

            // Act
            User result = userService.getUserById(1);

            // Assert
            assertEquals(expectedUser, result);
            verify(userMapper).getUserById(1);
        }

        @Test
        @DisplayName("Should get user by name")
        void testGetUserByName() {
            // Arrange
            User expectedUser = createSampleUser();
            when(userMapper.getUserByName(anyString())).thenReturn(expectedUser);

            // Act
            User result = userService.getUserByName("johndoe");

            // Assert
            assertEquals(expectedUser, result);
            verify(userMapper).getUserByName("johndoe");
        }
    }

    @Nested
    @DisplayName("User Management Tests")
    class UserManagementTests {

        @Test
        @DisplayName("Should insert new user")
        void testInsertUser() {
            // Arrange
            User user = createSampleUser();
            Department dept = new Department();
            dept.setPath("dept_path");
            when(departmentMapper.getDepartmentById(anyInt())).thenReturn(dept);
            doNothing().when(userMapper).insertUser(any());

            // Act
            boolean result = userService.insertUser(user);

            // Assert
            assertTrue(result);
            verify(userMapper).insertUser(user);
        }

        @Test
        @DisplayName("Should delete user by ID")
        void testDeleteById() {
            // Arrange
            doNothing().when(userMapper).deleteById(anyInt());

            // Act
            boolean result = userService.deleteById(1);

            // Assert
            assertTrue(result);
            verify(userMapper).deleteById(1);
        }

        @Test
        @DisplayName("Should update user")
        void testUpdateUser() {
            // Arrange
            User user = createSampleUser();
            doNothing().when(userMapper).updateUser(anyInt(), anyString(), anyString(), anyString(), 
                anyString(), anyString(), anyString(), anyInt(), anyString(), 
                anyString(), any(), anyString(), anyString(), anyString(), 
                anyInt(), anyString());

            // Act
            boolean result = userService.updateUser(user);

            // Assert
            assertTrue(result);
            verify(userMapper).updateUser(
                eq(user.getId()), eq(user.getUsername()), eq(user.getPassword()),
                eq(user.getNickname()), eq(user.getPhoneNumber()), eq(user.getEmail()),
                eq(user.getGender()), eq(user.getDepartmentId()), eq(user.getStatus()),
                eq(user.getRole()), eq(user.getCreatedAt()), eq(user.getPosition()),
                eq(user.getRemark()), eq(user.getAvatar()), eq(user.getTenantId()),
                eq(user.getPath())
            );
        }
    }

    @Nested
    @DisplayName("Authentication Tests")
    class AuthenticationTests {

        @Test
        @DisplayName("Should login successfully")
        void testLogin_Success() {
            // Arrange
            User user = createSampleUser();
            when(userMapper.getUserByUsername(anyString())).thenReturn(user);

            // Act
            User result = userService.login("johndoe", "password123");

            // Assert
            assertNotNull(result);
            assertEquals(user, result);
            verify(userMapper).getUserByUsername("johndoe");
        }

        @Test
        @DisplayName("Should fail login with wrong password")
        void testLogin_WrongPassword() {
            // Arrange
            User user = createSampleUser();
            when(userMapper.getUserByUsername(anyString())).thenReturn(user);

            // Act
            User result = userService.login("johndoe", "wrongpassword");

            // Assert
            assertNull(result);
            verify(userMapper).getUserByUsername("johndoe");
        }

        @Test
        @DisplayName("Should fail login with non-existent user")
        void testLogin_NonExistentUser() {
            // Arrange
            when(userMapper.getUserByUsername(anyString())).thenReturn(null);

            // Act
            User result = userService.login("nonexistent", "password");

            // Assert
            assertNull(result);
            verify(userMapper).getUserByUsername("nonexistent");
        }

        @Test
        @DisplayName("Should update password successfully")
        void testUpdatePassword_Success() {
            // Arrange
            User user = createSampleUser();
            when(userMapper.getUserById(anyInt())).thenReturn(user);
            when(userMapper.updateUser2(any())).thenReturn(1);

            // Act
            boolean result = userService.updatePassword(1, "password123", "newpassword");

            // Assert
            assertTrue(result);
            verify(userMapper).getUserById(1);
            verify(userMapper).updateUser2(any());
        }

        @Test
        @DisplayName("Should fail password update with wrong old password")
        void testUpdatePassword_WrongOldPassword() {
            // Arrange
            User user = createSampleUser();
            when(userMapper.getUserById(anyInt())).thenReturn(user);

            // Act
            boolean result = userService.updatePassword(1, "wrongpassword", "newpassword");

            // Assert
            assertFalse(result);
            verify(userMapper).getUserById(1);
            verify(userMapper, never()).updateUser2(any());
        }
    }

    @Nested
    @DisplayName("Avatar Management Tests")
    class AvatarManagementTests {

        @Test
        @DisplayName("Should save avatar successfully")
        void testSaveAvatar_Success() throws IOException {
            // Arrange
            User user = createSampleUser();
            user.setAvatar(null);
            when(userMapper.getUserById(anyInt())).thenReturn(user);
            when(userMapper.updateUser2(any())).thenReturn(1);

            MockMultipartFile file = new MockMultipartFile(
                "avatar", "test.jpg", "image/jpeg", "test image".getBytes()
            );

            // Act
            String result = userService.saveAvatar(file, 1);

            // Assert
            assertNotNull(result);
            assertTrue(result.startsWith("/avatar/"));
            verify(userMapper).getUserById(1);
            verify(userMapper).updateUser2(any());
        }

        @Test
        @DisplayName("Should throw exception when user not found")
        void testSaveAvatar_UserNotFound() {
            // Arrange
            when(userMapper.getUserById(anyInt())).thenReturn(null);
            MockMultipartFile file = new MockMultipartFile(
                "avatar", "test.jpg", "image/jpeg", "test image".getBytes()
            );

            // Act & Assert
            assertThrows(RuntimeException.class, () -> userService.saveAvatar(file, 1));
            verify(userMapper).getUserById(1);
            verify(userMapper, never()).updateUser2(any());
        }
    }

    @Nested
    @DisplayName("Pagination Tests")
    class PaginationTests {

        @Test
        @DisplayName("Should get users by page")
        void testGetUsersByPage() {
            // Arrange
            List<User> expectedUsers = Arrays.asList(createSampleUser());
            when(userMapper.getUsersByPage(anyInt(), anyInt())).thenReturn(expectedUsers);
            when(userMapper.getTotalUserCount()).thenReturn(10);

            // Act
            Map<String, Object> result = userService.getUsersByPage(1, 5);

            // Assert
            assertNotNull(result);
            assertEquals(expectedUsers, result.get("users"));
            assertEquals(10, result.get("total"));
            verify(userMapper).getUsersByPage(0, 5);
            verify(userMapper).getTotalUserCount();
        }
    }

    @Nested
    @DisplayName("User Portrait Tests")
    class UserPortraitTests {

        @Test
        @DisplayName("Should generate user portrait")
        void testGenerateUserPortrait() {
            // Arrange
            User user = createSampleUser();
            when(userMapper.getUserById(anyInt())).thenReturn(user);
            when(userBehaviorMapper.getUserBehaviorsByUserId(anyInt())).thenReturn(Arrays.asList(
                createBehavior("page_load"),
                createBehavior("video_play")
            ));

            // Act
            Map<String, Object> result = userService.generateUserPortrait(1);

            // Assert
            assertNotNull(result);
            assertEquals(user.getGender(), result.get("gender"));
            Map<String, Object> stats = (Map<String, Object>) result.get("behaviorStats");
            assertEquals(1L, stats.get("pageViews"));
            assertEquals(1L, stats.get("videoPlay"));
            verify(userMapper).getUserById(1);
            verify(userBehaviorMapper).getUserBehaviorsByUserId(1);
        }

        private UserBehavior createBehavior(String action) {
            UserBehavior behavior = new UserBehavior();
            behavior.setAction(action);
            behavior.setUserId(1);
            behavior.setTimestamp(new java.util.Date());
            return behavior;
        }

        @Test
        @DisplayName("Should generate all user portraits")
        void testGenerateAllUserPortraits() {
            // Arrange
            List<User> users = Arrays.asList(createSampleUser());
            when(userMapper.getAllUser()).thenReturn(users);
            when(userBehaviorMapper.getUserBehaviorsByUserId(anyInt())).thenReturn(Arrays.asList(
                createBehavior("page_load"),
                createBehavior("video_play")
            ));

            // Act
            List<Map<String, Object>> result = userService.generateAllUserPortraits();

            // Assert
            assertNotNull(result);
            assertFalse(result.isEmpty());
            Map<String, Object> stats = (Map<String, Object>) result.get(0).get("behaviorStats");
            assertEquals(1L, stats.get("pageViews"));
            assertEquals(1L, stats.get("videoPlay"));
            verify(userMapper).getAllUser();
            verify(userBehaviorMapper).getUserBehaviorsByUserId(1);
        }
    }
}
