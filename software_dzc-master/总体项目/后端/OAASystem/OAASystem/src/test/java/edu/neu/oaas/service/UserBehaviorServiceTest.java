package edu.neu.oaas.service;

import edu.neu.oaas.mapper.UserBehaviorMapper;
import edu.neu.oaas.pojo.UserBehavior;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("User Behavior Service Tests")
class UserBehaviorServiceTest {

    @Mock
    private UserBehaviorMapper userBehaviorMapper;

    @InjectMocks
    private UserBehaviorService userBehaviorService;

    private File tempFile;

    @BeforeEach
    void setUp() throws IOException {
        // Create a temporary file for visitor counter
        tempFile = File.createTempFile("visitor-counter", ".txt");
        tempFile.deleteOnExit();
        
        // Initialize with 0 visitors
        try (FileWriter writer = new FileWriter(tempFile)) {
            writer.write("0");
        }
        
        ReflectionTestUtils.setField(userBehaviorService, "visitorCounterFile", tempFile.getAbsolutePath());
        userBehaviorService.init(); // Initialize the service
    }

    @AfterEach
    void tearDown() throws IOException {
        Files.deleteIfExists(tempFile.toPath());
    }

    private UserBehavior createTestBehavior() {
        UserBehavior behavior = new UserBehavior();
        behavior.setUserId(1);
        behavior.setAction("login");
        behavior.setPage("login");
        behavior.setTimestamp(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
        return behavior;
    }

    @Nested
    @DisplayName("Basic Behavior Recording Tests")
    class BasicBehaviorRecordingTests {

        @Test
        @DisplayName("Should record user behavior")
        void testRecordBehavior() {
            // Prepare test data
            UserBehavior behavior = createTestBehavior();
            doNothing().when(userBehaviorMapper).insertUserBehavior(any(UserBehavior.class));

            // Execute test
            userBehaviorService.recordBehavior(behavior);

            // Verify
            verify(userBehaviorMapper).insertUserBehavior(behavior);
            assertTrue(userBehaviorService.getRealTimeVisitors() > 0);
        }

        @Test
        @DisplayName("Should create user behavior")
        void testCreateUserBehavior() {
            // Prepare test data
            UserBehavior behavior = createTestBehavior();
            doNothing().when(userBehaviorMapper).insertUserBehavior(any(UserBehavior.class));

            // Execute test
            userBehaviorService.createUserBehavior(behavior);

            // Verify
            verify(userBehaviorMapper).insertUserBehavior(behavior);
            assertTrue(userBehaviorService.getRealTimeVisitors() > 0);
        }
    }

    @Nested
    @DisplayName("Behavior Query Tests")
    class BehaviorQueryTests {

        @Test
        @DisplayName("Should get behaviors by user ID")
        void testGetBehaviorsByUserId() {
            // Prepare test data
            UserBehavior behavior = createTestBehavior();
            List<UserBehavior> expected = Arrays.asList(behavior);
            when(userBehaviorMapper.getUserBehaviorsByUserId(1)).thenReturn(expected);

            // Execute test
            List<UserBehavior> result = userBehaviorService.getBehaviorsByUserId(1);

            // Verify
            assertFalse(result.isEmpty());
            assertEquals(expected.size(), result.size());
            assertEquals("login", result.get(0).getAction());
            verify(userBehaviorMapper).getUserBehaviorsByUserId(1);
        }

        @Test
        @DisplayName("Should search behaviors by criteria")
        void testSearchBehaviors() {
            // Prepare test data
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            UserBehavior behavior = createTestBehavior();
            List<UserBehavior> expected = Arrays.asList(behavior);

            when(userBehaviorMapper.searchUserBehaviors("login", timestamp)).thenReturn(expected);

            // Execute test
            List<UserBehavior> result = userBehaviorService.searchBehaviors("login", timestamp);

            // Verify
            assertFalse(result.isEmpty());
            assertEquals(expected.size(), result.size());
            verify(userBehaviorMapper).searchUserBehaviors("login", timestamp);
        }

        @Test
        @DisplayName("Should get all user behaviors")
        void testGetAllUserBehaviors() {
            // Prepare test data
            UserBehavior behavior = createTestBehavior();
            List<UserBehavior> expected = Arrays.asList(behavior);

            when(userBehaviorMapper.selectAllUserBehaviors()).thenReturn(expected);

            // Execute test
            List<UserBehavior> result = userBehaviorService.getAllUserBehaviors();

            // Verify
            assertFalse(result.isEmpty());
            assertEquals(expected.size(), result.size());
            verify(userBehaviorMapper).selectAllUserBehaviors();
        }
    }

    @Nested
    @DisplayName("Real-time Statistics Tests")
    class RealTimeStatisticsTests {

        @Test
        @DisplayName("Should get real-time visitors count")
        void testGetRealTimeVisitors() {
            // Record some behaviors
            UserBehavior behavior1 = createTestBehavior();
            UserBehavior behavior2 = createTestBehavior();
            behavior2.setUserId(2);

            userBehaviorService.recordBehavior(behavior1);
            userBehaviorService.recordBehavior(behavior2);

            // Execute test
            int visitors = userBehaviorService.getRealTimeVisitors();

            // Verify
            assertTrue(visitors > 0);
        }

        @Test
        @DisplayName("Should handle expired visitors")
        void testHandleExpiredVisitors() throws InterruptedException {
            // Record a behavior
            UserBehavior behavior = createTestBehavior();
            userBehaviorService.recordBehavior(behavior);

            // Wait for expiration (simulating 5 minutes passing)
            TimeUnit.MILLISECONDS.sleep(100); // Simulate time passing

            // Force expiration check by getting visitors
            int visitors = userBehaviorService.getRealTimeVisitors();

            // The count should still be maintained in total visitors
            assertTrue(visitors > 0);
        }

        @Test
        @DisplayName("Should manage online users count")
        void testOnlineUsersCount() {
            // Test increment
            userBehaviorService.incrementOnlineUsers();
            assertEquals(1, userBehaviorService.getOnlineUsers());

            // Test increment again
            userBehaviorService.incrementOnlineUsers();
            assertEquals(2, userBehaviorService.getOnlineUsers());

            // Test decrement
            userBehaviorService.decrementOnlineUsers();
            assertEquals(1, userBehaviorService.getOnlineUsers());
        }

        @Test
        @DisplayName("Should update real-time visitors")
        void testUpdateRealTimeVisitors() {
            // Set up some online users
            userBehaviorService.incrementOnlineUsers();
            userBehaviorService.incrementOnlineUsers();

            // Update real-time visitors
            userBehaviorService.updateRealTimeVisitors();

            // Verify the total visitors count was updated
            assertTrue(userBehaviorService.getRealTimeVisitors() >= 2);
        }
    }

    @Nested
    @DisplayName("File Handling Tests")
    class FileHandlingTests {

        @Test
        @DisplayName("Should handle missing counter file")
        void testHandleMissingCounterFile() {
            // Delete the counter file
            tempFile.delete();

            // Try to initialize the service
            userBehaviorService.init();

            // Should not throw exception and should start counting from 0
            assertEquals(0, userBehaviorService.getRealTimeVisitors());
        }

        @Test
        @DisplayName("Should handle invalid counter file content")
        void testHandleInvalidCounterFile() throws IOException {
            // Write invalid content to the file
            try (FileWriter writer = new FileWriter(tempFile)) {
                writer.write("invalid");
            }

            // Try to initialize the service
            userBehaviorService.init();

            // Should not throw exception and should start counting from 0
            assertEquals(0, userBehaviorService.getRealTimeVisitors());
        }

        @Test
        @DisplayName("Should persist visitor count")
        void testPersistVisitorCount() throws IOException {
            // Record some behaviors
            userBehaviorService.recordBehavior(createTestBehavior());
            userBehaviorService.recordBehavior(createTestBehavior());

            // Read the file content
            String content = new String(Files.readAllBytes(tempFile.toPath()));
            int savedCount = Integer.parseInt(content);

            // Verify the count was saved
            assertTrue(savedCount > 0);
        }
    }
} 