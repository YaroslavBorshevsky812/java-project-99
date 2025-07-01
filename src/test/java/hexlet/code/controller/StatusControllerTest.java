package hexlet.code.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.dto.status.StatusCreateDTO;
import hexlet.code.dto.status.StatusDTO;
import hexlet.code.dto.status.StatusUpdateDTO;
import hexlet.code.mapper.StatusMapper;
import hexlet.code.model.Status;
import hexlet.code.repository.StatusRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class StatusControllerTest {

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private StatusRepository taskStatusRepository;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private StatusMapper taskStatusMapper;

    private JwtRequestPostProcessor token;
    private Status testStatus;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac)
                                 .defaultResponseCharacterEncoding(StandardCharsets.UTF_8)
                                 .apply(springSecurity())
                                 .build();

        // Инициализация тестового статуса
        testStatus = new Status();
        testStatus.setName("Test Status");
        testStatus.setSlug("test-status");
        taskStatusRepository.save(testStatus);

        token = jwt().jwt(builder -> builder.subject("admin@ad.min"));
    }

    @AfterEach
    void clean() {
        taskStatusRepository.deleteAll();
    }

    @Test
    void testIndex() throws Exception {
        var response = mockMvc.perform(get("/api/task_statuses").with(jwt()))
                              .andExpect(status().isOk())
                              .andReturn()
                              .getResponse();
        var body = response.getContentAsString();

        List<StatusDTO> actual = om.readValue(body, new TypeReference<>() { });
        var expected = taskStatusRepository.findAll().stream()
                                           .map(taskStatusMapper::toDto)
                                           .toList();

        assertThat(actual).containsExactlyInAnyOrderElementsOf(expected);
    }

    @Test
    void testCreate() throws Exception {
        StatusCreateDTO data = new StatusCreateDTO();
        data.setName("New Status");
        data.setSlug("new-status");

        var request = post("/api/task_statuses")
            .with(token)
            .contentType(MediaType.APPLICATION_JSON)
            .content(om.writeValueAsString(data));
        mockMvc.perform(request)
               .andExpect(status().isCreated());

        var status = taskStatusRepository.findBySlug(data.getSlug()).orElse(null);

        assertThat(status).isNotNull();
        assertThat(status.getName()).isEqualTo(data.getName());
        assertThat(status.getSlug()).isEqualTo(data.getSlug());
    }

    @Test
    void testUpdate() throws Exception {
        var data = new StatusUpdateDTO();
        data.setName("New Status");

        var request = put("/api/task_statuses/" + testStatus.getId())
            .with(token)
            .contentType(MediaType.APPLICATION_JSON)
            .content(om.writeValueAsString(data));

        mockMvc.perform(request)
               .andExpect(status().isOk());

        var status = taskStatusRepository.findById(testStatus.getId()).orElseThrow();
        assertThat(status.getName()).isEqualTo("New Status");
        // Проверяем, что slug не изменился
        assertThat(status.getSlug()).isEqualTo(testStatus.getSlug());
    }

    @Test
    void testShow() throws Exception {
        var request = get("/api/task_statuses/" + testStatus.getId()).with(jwt());
        var result = mockMvc.perform(request)
                            .andExpect(status().isOk())
                            .andReturn();
        var body = result.getResponse().getContentAsString();

        StatusDTO responseDto = om.readValue(body, StatusDTO.class);
        assertThat(responseDto.getName()).isEqualTo(testStatus.getName());
        assertThat(responseDto.getSlug()).isEqualTo(testStatus.getSlug());
    }

    @Test
    void testDelete() throws Exception {
        var request = delete("/api/task_statuses/" + testStatus.getId())
            .with(token);
        mockMvc.perform(request)
               .andExpect(status().isNoContent());

        assertThat(taskStatusRepository.existsById(testStatus.getId())).isFalse();
    }
}
