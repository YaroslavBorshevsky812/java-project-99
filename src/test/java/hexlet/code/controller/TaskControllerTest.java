package hexlet.code.controller;

import hexlet.code.dto.task.TaskCreateDTO;
import hexlet.code.dto.task.TaskDTO;
import hexlet.code.dto.task.TaskFilter;
import hexlet.code.dto.task.TaskUpdateDTO;
import hexlet.code.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TaskService taskService;

    private SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor token;

    @BeforeEach
    void setUp() {
        token = SecurityMockMvcRequestPostProcessors.jwt().jwt(builder ->
                                                                   builder.subject("test@example.com")
        );
    }

    private TaskDTO createTestTaskDTO(Long id) {
        TaskDTO dto = new TaskDTO();
        dto.setId(id);
        dto.setIndex(1);
        dto.setCreatedAt(LocalDate.now());
        dto.setAssigneeId(1L);
        dto.setTitle("Test Task");
        dto.setContent("Test Content");
        dto.setStatus("OPEN");
        dto.setTaskLabelIds(Set.of(1L, 2L));
        return dto;
    }

    private TaskCreateDTO createTaskCreateDTO() {
        TaskCreateDTO dto = new TaskCreateDTO();
        dto.setIndex(1);
        dto.setAssigneeId(1L);
        dto.setTitle("Test Task");
        dto.setContent("Test Content");
        dto.setStatus("OPEN");
        dto.setTaskLabelIds(Set.of(1L, 2L));
        return dto;
    }

    private TaskUpdateDTO createTaskUpdateDTO() {
        TaskUpdateDTO dto = new TaskUpdateDTO();
        dto.setIndex(JsonNullable.of(2));
        dto.setAssigneeId(JsonNullable.of(2L));
        dto.setTitle(JsonNullable.of("Updated Task"));
        dto.setContent(JsonNullable.of("Updated Content"));
        dto.setStatus(JsonNullable.of("IN_PROGRESS"));
        dto.setTaskLabelIds(JsonNullable.of(Set.of(3L)));
        return dto;
    }

    @Test
    void getAllTasksShouldReturnTaskList() throws Exception {
        TaskDTO taskDTO = createTestTaskDTO(1L);

        when(taskService.getAll()).thenReturn(List.of(taskDTO));

        mockMvc.perform(get("/api/tasks")
                            .with(token))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$[0].id").value(1))
               .andExpect(jsonPath("$[0].title").value("Test Task"))
               .andExpect(jsonPath("$[0].status").value("OPEN"));
    }

    @Test
    void getFilteredTasksShouldReturnFilteredList() throws Exception {
        TaskDTO taskDTO = createTestTaskDTO(1L);
        TaskFilter filter = new TaskFilter();
        filter.setStatus("OPEN");

        when(taskService.getFilteredTasks(any(TaskFilter.class))).thenReturn(List.of(taskDTO));

        mockMvc.perform(get("/api/tasks?status=OPEN")
                            .with(token))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$[0].id").value(1))
               .andExpect(jsonPath("$[0].status").value("OPEN"));
    }

    @Test
    void getTaskByIdShouldReturnTask() throws Exception {
        TaskDTO taskDTO = createTestTaskDTO(1L);

        when(taskService.getById(1L)).thenReturn(taskDTO);

        mockMvc.perform(get("/api/tasks/1")
                            .with(token))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.id").value(1))
               .andExpect(jsonPath("$.title").value("Test Task"))
               .andExpect(jsonPath("$.status").value("OPEN"));
    }

    @Test
    void createTaskShouldReturnCreatedTask() throws Exception {
        TaskCreateDTO createDTO = createTaskCreateDTO();
        TaskDTO createdDTO = createTestTaskDTO(1L);

        when(taskService.create(any(TaskCreateDTO.class))).thenReturn(createdDTO);

        mockMvc.perform(post("/api/tasks")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createDTO))
                            .with(token))
               .andExpect(status().isCreated())
               .andExpect(jsonPath("$.id").value(1))
               .andExpect(jsonPath("$.title").value("Test Task"))
               .andExpect(jsonPath("$.status").value("OPEN"));
    }

    @Test
    void updateTaskShouldReturnUpdatedTask() throws Exception {
        TaskUpdateDTO updateDTO = createTaskUpdateDTO();
        TaskDTO updatedDTO = createTestTaskDTO(1L);
        updatedDTO.setTitle("Updated Task");
        updatedDTO.setStatus("IN_PROGRESS");

        when(taskService.update(eq(1L), any(TaskUpdateDTO.class))).thenReturn(updatedDTO);

        mockMvc.perform(put("/api/tasks/1")
                            .with(token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateDTO)))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.id").value(1))
               .andExpect(jsonPath("$.title").value("Updated Task"))
               .andExpect(jsonPath("$.status").value("IN_PROGRESS"));
    }

    @Test
    void deleteTaskShouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/api/tasks/1")
                            .with(token))
               .andExpect(status().isNoContent());

        verify(taskService).delete(1L);
    }

    @Test
    void createTaskShouldReturnBadRequestForInvalidData() throws Exception {
        TaskCreateDTO invalidDTO = new TaskCreateDTO(); // Пустой DTO

        mockMvc.perform(post("/api/tasks")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidDTO))
                            .with(token))
               .andExpect(status().isBadRequest());
    }

    @Test
    void updateTaskShouldReturnUnauthorizedWithoutToken() throws Exception {
        TaskUpdateDTO updateDTO = createTaskUpdateDTO();

        mockMvc.perform(put("/api/tasks/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateDTO)))
               .andExpect(status().isUnauthorized());
    }
}
