package edu.neu.oaas.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.neu.oaas.pojo.Position;
import edu.neu.oaas.service.PositionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class PositionControllerTest {

    @Mock
    private PositionService positionService;

    @InjectMocks
    private PositionController positionController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(positionController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void testInsertPosition_Success() throws Exception {
        Position position = new Position();
        position.setDepartmentId(1);
        position.setPosition("Manager");

        doNothing().when(positionService).insert(any(Position.class));

        mockMvc.perform(post("/pos/insert")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(position)))
                .andExpect(status().isOk());

        verify(positionService).insert(any(Position.class));
    }

    @Test
    void testInsertPosition_Error() throws Exception {
        Position position = new Position();
        position.setDepartmentId(1);
        position.setPosition("Manager");

        doThrow(new RuntimeException("Failed to insert position")).when(positionService).insert(any(Position.class));

        mockMvc.perform(post("/pos/insert")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(position)))
                .andExpect(status().isInternalServerError());

        verify(positionService).insert(any(Position.class));
    }

    @Test
    void testDeletePosition_Success() throws Exception {
        Position position = new Position();
        position.setDepartmentId(1);
        position.setPosition("Manager");

        doNothing().when(positionService).delete(any(Position.class));

        mockMvc.perform(post("/pos/delete")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(position)))
                .andExpect(status().isOk());

        verify(positionService).delete(any(Position.class));
    }

    @Test
    void testDeletePosition_Error() throws Exception {
        Position position = new Position();
        position.setDepartmentId(1);
        position.setPosition("Manager");

        doThrow(new RuntimeException("Failed to delete position")).when(positionService).delete(any(Position.class));

        mockMvc.perform(post("/pos/delete")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(position)))
                .andExpect(status().isInternalServerError());

        verify(positionService).delete(any(Position.class));
    }

    @Test
    void testGetPositions_Success() throws Exception {
        Position position1 = new Position();
        position1.setDepartmentId(1);
        position1.setPosition("Manager");

        Position position2 = new Position();
        position2.setDepartmentId(1);
        position2.setPosition("Developer");

        List<Position> positions = Arrays.asList(position1, position2);

        when(positionService.get(1)).thenReturn(positions);

        mockMvc.perform(get("/pos/get")
                .param("departmentId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].position").value("Manager"))
                .andExpect(jsonPath("$[1].position").value("Developer"));

        verify(positionService).get(1);
    }

    @Test
    void testGetPositions_EmptyList() throws Exception {
        when(positionService.get(1)).thenReturn(Collections.emptyList());

        // 执行测试并验证结果
        mockMvc.perform(get("/pos/get")
                .param("departmentId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());

        verify(positionService).get(1);
    }

    @Test
    void testGetPositions_NullDepartmentId() throws Exception {
        when(positionService.get(null)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/pos/get"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());

        verify(positionService).get(null);
    }

    @Test
    void testGetPositions_Error() throws Exception {
        when(positionService.get(1)).thenThrow(new RuntimeException("Failed to get positions"));

        mockMvc.perform(get("/pos/get")
                .param("departmentId", "1"))
                .andExpect(status().isInternalServerError());

        verify(positionService).get(1);
    }
} 