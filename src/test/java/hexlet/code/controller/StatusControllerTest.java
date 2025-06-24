package hexlet.code.controller;

import hexlet.code.dto.status.StatusCreateDTO;
import hexlet.code.dto.status.StatusDTO;
import hexlet.code.dto.status.StatusUpdateDTO;
import hexlet.code.service.StatusService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

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
class StatusControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private StatusService statusService;

    private SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor token;

    @BeforeEach
    void setUp() {
        token = SecurityMockMvcRequestPostProcessors.jwt().jwt(builder ->
                                                                   builder.subject("test@example.com")
        );
    }

    private StatusDTO createTestStatusDTO(Long id) {
        StatusDTO dto = new StatusDTO();
        dto.setId(id);
        dto.setName("Test Status");
        dto.setSlug("test-status");
        dto.setCreatedAt(LocalDate.now());
        return dto;
    }

    private StatusCreateDTO createStatusCreateDTO() {
        StatusCreateDTO dto = new StatusCreateDTO();
        dto.setName("Test Status");
        dto.setSlug("test-status");
        return dto;
    }

    private StatusUpdateDTO createStatusUpdateDTO() {
        StatusUpdateDTO dto = new StatusUpdateDTO();
        dto.setName("Updated Status");
        dto.setSlug("updated-status");
        return dto;
    }

    @Test
    void getAllStatusesShouldReturnStatusList() throws Exception {
        StatusDTO statusDTO = createTestStatusDTO(1L);

        when(statusService.getAll()).thenReturn(List.of(statusDTO));

        mockMvc.perform(get("/api/task_statuses")
                            .with(token))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$[0].id").value(1))
               .andExpect(jsonPath("$[0].name").value("Test Status"))
               .andExpect(jsonPath("$[0].slug").value("test-status"));
    }

    @Test
    void getStatusByIdShouldReturnStatus() throws Exception {
        StatusDTO statusDTO = createTestStatusDTO(1L);

        when(statusService.getById(1L)).thenReturn(statusDTO);

        mockMvc.perform(get("/api/task_statuses/1")
                            .with(token))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.id").value(1))
               .andExpect(jsonPath("$.name").value("Test Status"))
               .andExpect(jsonPath("$.slug").value("test-status"));
    }

    @Test
    void createStatusShouldReturnCreatedStatus() throws Exception {
        StatusCreateDTO createDTO = createStatusCreateDTO();
        StatusDTO createdDTO = createTestStatusDTO(1L);

        when(statusService.create(any(StatusCreateDTO.class))).thenReturn(createdDTO);

        mockMvc.perform(post("/api/task_statuses")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createDTO))
                            .with(token))
               .andExpect(status().isCreated())
               .andExpect(jsonPath("$.id").value(1))
               .andExpect(jsonPath("$.name").value("Test Status"))
               .andExpect(jsonPath("$.slug").value("test-status"));
    }

    @Test
    void updateStatusShouldReturnUpdatedStatus() throws Exception {
        StatusUpdateDTO updateDTO = createStatusUpdateDTO();
        StatusDTO updatedDTO = createTestStatusDTO(1L);
        updatedDTO.setName("Updated Status");
        updatedDTO.setSlug("updated-status");

        when(statusService.update(eq(1L), any(StatusUpdateDTO.class))).thenReturn(updatedDTO);

        mockMvc.perform(put("/api/task_statuses/1")
                            .with(token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateDTO)))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.id").value(1))
               .andExpect(jsonPath("$.name").value("Updated Status"))
               .andExpect(jsonPath("$.slug").value("updated-status"));
    }

    @Test
    void deleteStatusShouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/api/task_statuses/1")
                            .with(token))
               .andExpect(status().isNoContent());

        verify(statusService).delete(1L);
    }

    @Test
    void createStatusShouldReturnBadRequestForInvalidData() throws Exception {
        StatusCreateDTO invalidDTO = new StatusCreateDTO(); // Пустой DTO

        mockMvc.perform(post("/api/task_statuses")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidDTO))
                            .with(token))
               .andExpect(status().isBadRequest());
    }

    @Test
    void createStatusShouldReturnBadRequestForLongName() throws Exception {
        StatusCreateDTO invalidDTO = createStatusCreateDTO();
        invalidDTO.setName("a".repeat(101)); // Превышение максимальной длины

        mockMvc.perform(post("/api/task_statuses")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidDTO))
                            .with(token))
               .andExpect(status().isBadRequest());
    }

    @Test
    void updateStatusShouldReturnUnauthorizedWithoutToken() throws Exception {
        StatusUpdateDTO updateDTO = createStatusUpdateDTO();

        mockMvc.perform(put("/api/task_statuses/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateDTO)))
               .andExpect(status().isUnauthorized());
    }

    @Test
    void getNonExistentStatusShouldReturnNotFound() throws Exception {
        when(statusService.getById(999L)).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));

        mockMvc.perform(get("/api/task_statuses/999")
                            .with(token))
               .andExpect(status().isNotFound());
    }
}
