package hexlet.code.controller;

import hexlet.code.dto.label.LabelCreateDto;
import hexlet.code.dto.label.LabelDto;
import hexlet.code.dto.label.LabelUpdateDto;
import hexlet.code.service.LabelService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openapitools.jackson.nullable.JsonNullable;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
class LabelControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private LabelService labelService;

    private SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor token;

    @BeforeEach
    void setUp() {
        token = SecurityMockMvcRequestPostProcessors.jwt().jwt(builder ->
                                                                   builder.subject("test@example.com")
        );
    }

    private LabelDto createTestLabelDto(Long id) {
        LabelDto dto = new LabelDto();
        dto.setId(id);
        dto.setName("Test Label");
        dto.setCreatedAt(LocalDate.now());
        return dto;
    }

    private LabelCreateDto createLabelCreateDto() {
        LabelCreateDto dto = new LabelCreateDto();
        dto.setName("Test Label");
        return dto;
    }

    private LabelUpdateDto createLabelUpdateDto() {
        LabelUpdateDto dto = new LabelUpdateDto();
        dto.setName(JsonNullable.of("Updated Label"));
        return dto;
    }

    @Test
    void getAllLabelsShouldReturnLabelListWithCountHeader() throws Exception {
        LabelDto labelDto = createTestLabelDto(1L);
        List<LabelDto> labels = List.of(labelDto);

        when(labelService.getAllLabels()).thenReturn(labels);

        mockMvc.perform(get("/api/labels")
                            .with(token))
               .andExpect(status().isOk())
               .andExpect(header().string("X-Total-Count", "1"))
               .andExpect(jsonPath("$[0].id").value(1))
               .andExpect(jsonPath("$[0].name").value("Test Label"));
    }

    @Test
    void getLabelByIdShouldReturnLabel() throws Exception {
        LabelDto labelDto = createTestLabelDto(1L);

        when(labelService.getLabelById(1L)).thenReturn(labelDto);

        mockMvc.perform(get("/api/labels/1")
                            .with(token))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.id").value(1))
               .andExpect(jsonPath("$.name").value("Test Label"));
    }

    @Test
    void createLabelShouldReturnCreatedLabel() throws Exception {
        LabelCreateDto createDto = createLabelCreateDto();
        LabelDto createdDto = createTestLabelDto(1L);

        when(labelService.createLabel(any(LabelCreateDto.class))).thenReturn(createdDto);

        mockMvc.perform(post("/api/labels")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createDto))
                            .with(token))
               .andExpect(status().isCreated())
               .andExpect(jsonPath("$.id").value(1))
               .andExpect(jsonPath("$.name").value("Test Label"));
    }

    @Test
    void updateLabelShouldReturnUpdatedLabel() throws Exception {
        LabelUpdateDto updateDto = createLabelUpdateDto();
        LabelDto updatedDto = createTestLabelDto(1L);
        updatedDto.setName("Updated Label");

        when(labelService.updateLabel(eq(1L), any(LabelUpdateDto.class))).thenReturn(updatedDto);

        mockMvc.perform(put("/api/labels/1")
                            .with(token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateDto)))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.id").value(1))
               .andExpect(jsonPath("$.name").value("Updated Label"));
    }

    @Test
    void deleteLabelShouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/api/labels/1")
                            .with(token))
               .andExpect(status().isNoContent());

        verify(labelService).deleteLabel(1L);
    }

    @Test
    void createLabelShouldReturnBadRequestForShortName() throws Exception {
        LabelCreateDto invalidDto = new LabelCreateDto();
        invalidDto.setName("a"); // меньше минимальной длины

        mockMvc.perform(post("/api/labels")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidDto))
                            .with(token))
               .andExpect(status().isBadRequest());
    }

    @Test
    void createLabelShouldReturnBadRequestForEmptyName() throws Exception {
        LabelCreateDto invalidDto = new LabelCreateDto();
        invalidDto.setName(""); // пустое имя

        mockMvc.perform(post("/api/labels")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidDto))
                            .with(token))
               .andExpect(status().isBadRequest());
    }

    @Test
    void updateLabelShouldReturnUnauthorizedWithoutToken() throws Exception {
        LabelUpdateDto updateDto = createLabelUpdateDto();

        mockMvc.perform(put("/api/labels/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateDto)))
               .andExpect(status().isUnauthorized());
    }

    @Test
    void getNonExistentLabelShouldReturnNotFound() throws Exception {
        when(labelService.getLabelById(999L)).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));

        mockMvc.perform(get("/api/labels/999")
                            .with(token))
               .andExpect(status().isNotFound());
    }
}
