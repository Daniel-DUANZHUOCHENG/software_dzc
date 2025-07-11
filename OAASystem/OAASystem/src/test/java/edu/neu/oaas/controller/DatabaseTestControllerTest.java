package edu.neu.oaas.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class DatabaseTestControllerTest {

    @Mock
    private DataSource dataSource;

    @Mock
    private Connection connection;

    @Mock
    private DatabaseMetaData databaseMetaData;

    @Mock
    private PreparedStatement preparedStatement;

    @Mock
    private ResultSet resultSet;

    @InjectMocks
    private DatabaseTestController databaseTestController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(databaseTestController).build();

        // 设置基本的 mock 行为
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.getMetaData()).thenReturn(databaseMetaData);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
    }

    @Test
    void testDatabase_Success() throws Exception {
        // 模拟数据库元数据
        when(databaseMetaData.getURL()).thenReturn("jdbc:mysql://localhost:3306/testdb");
        when(databaseMetaData.getUserName()).thenReturn("testuser");
        when(connection.getCatalog()).thenReturn("testdb");

        // 模拟查询结果
        when(resultSet.next()).thenReturn(true, true, true);
        when(resultSet.getInt("userCount")).thenReturn(10);
        when(resultSet.getInt("courseCount")).thenReturn(20);
        when(resultSet.getInt("tenantCount")).thenReturn(5);

        // 执行测试并验证结果
        mockMvc.perform(get("/api/test/database"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.connectionStatus").value("成功"))
                .andExpect(jsonPath("$.databaseUrl").value("jdbc:mysql://localhost:3306/testdb"))
                .andExpect(jsonPath("$.databaseName").value("testdb"))
                .andExpect(jsonPath("$.userName").value("testuser"))
                .andExpect(jsonPath("$.userCount").value(10))
                .andExpect(jsonPath("$.courseCount").value(20))
                .andExpect(jsonPath("$.tenantCount").value(5));
    }

    @Test
    void testDatabase_ConnectionFailure() throws Exception {
        // 模拟连接失败
        when(dataSource.getConnection()).thenThrow(new RuntimeException("Connection failed"));

        // 执行测试并验证结果
        mockMvc.perform(get("/api/test/database"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.connectionStatus").value("失败"))
                .andExpect(jsonPath("$.message").value("数据库连接失败: Connection failed"));
    }

    @Test
    void testDatabase_QueryFailure() throws Exception {
        // 模拟数据库元数据
        when(databaseMetaData.getURL()).thenReturn("jdbc:mysql://localhost:3306/testdb");
        when(databaseMetaData.getUserName()).thenReturn("testuser");
        when(connection.getCatalog()).thenReturn("testdb");

        // 模拟查询失败
        when(preparedStatement.executeQuery()).thenThrow(new RuntimeException("Query failed"));

        // 执行测试并验证结果
        mockMvc.perform(get("/api/test/database"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.connectionStatus").value("失败"))
                .andExpect(jsonPath("$.message").value("数据库连接失败: Query failed"));
    }

    @Test
    void testEnvironment_Success() throws Exception {
        // 执行测试并验证结果
        mockMvc.perform(get("/api/test/environment"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.javaVersion").isString())
                .andExpect(jsonPath("$.osName").isString())
                .andExpect(jsonPath("$.osVersion").isString())
                .andExpect(jsonPath("$.userDir").isString())
                .andExpect(jsonPath("$.userName").isString())
                .andExpect(jsonPath("$.totalMemory").isString())
                .andExpect(jsonPath("$.freeMemory").isString())
                .andExpect(jsonPath("$.maxMemory").isString())
                .andExpect(jsonPath("$.message").value("环境信息获取成功"));
    }
} 