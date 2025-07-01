package hexlet.code.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.dto.task.TaskCreateDTO;
import hexlet.code.dto.task.TaskDTO;
import hexlet.code.dto.task.TaskUpdateDTO;
import hexlet.code.mapper.TaskMapper;
import hexlet.code.model.Status;
import hexlet.code.model.Task;
import hexlet.code.model.User;
import hexlet.code.model.Label;
import hexlet.code.repository.StatusRepository;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.UserRepository;
import hexlet.code.repository.LabelRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class TaskControllerTest {

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private StatusRepository taskStatusRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LabelRepository labelRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TaskMapper taskMapper;

    private JwtRequestPostProcessor token;
    private Task testTask;
    private Task testTask2;
    private Status testStatus;
    private Status testStatus2;
    private User testUser;
    private User testUser2;
    private Label testLabel1;
    private Label testLabel2;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac)
                                 .defaultResponseCharacterEncoding(StandardCharsets.UTF_8)
                                 .apply(springSecurity())
                                 .build();

        testUser = new User();
        testUser.setEmail("user1@example.com");
        testUser.setPassword("password1");
        testUser.setFirstName("John");
        testUser.setLastName("Doe");
        userRepository.save(testUser);

        testUser2 = new User();
        testUser2.setEmail("user2@example.com");
        testUser2.setPassword("password2");
        testUser2.setFirstName("Jane");
        testUser2.setLastName("Smith");
        userRepository.save(testUser2);

        testStatus = new Status();
        testStatus.setName("To Do");
        testStatus.setSlug("to-do");
        taskStatusRepository.save(testStatus);

        testStatus2 = new Status();
        testStatus2.setName("In Progress");
        testStatus2.setSlug("in-progress");
        taskStatusRepository.save(testStatus2);

        testLabel1 = new Label();
        testLabel1.setName("Bug");
        labelRepository.save(testLabel1);

        testLabel2 = new Label();
        testLabel2.setName("Feature");
        labelRepository.save(testLabel2);

        testTask = new Task();
        testTask.setName("First test task");
        testTask.setDescription("Description for first task");
        testTask.setStatus(testStatus);
        testTask.setAssignee(testUser);
        testTask.setLabels(Set.of(testLabel1));
        taskRepository.save(testTask);

        testTask2 = new Task();
        testTask2.setName("Second test task");
        testTask2.setDescription("Description for second task");
        testTask2.setStatus(testStatus2);
        testTask2.setAssignee(testUser2);
        testTask2.setLabels(Set.of(testLabel2));
        taskRepository.save(testTask2);

        token = jwt().jwt(builder -> builder.subject(testUser.getEmail()));
    }

    @AfterEach
    void clean() {
        taskRepository.deleteAll();
        taskStatusRepository.deleteAll();
        userRepository.deleteAll();
        labelRepository.deleteAll();
    }

    @Test
    @Transactional
    void testIndex() throws Exception {
        var response = mockMvc.perform(get("/api/tasks").with(jwt()))
                              .andExpect(status().isOk())
                              .andReturn()
                              .getResponse();
        var body = response.getContentAsString();

        List<TaskDTO> actual = objectMapper.readValue(body, new TypeReference<>() { });
        var expected = taskRepository.findAll().stream()
                                     .map(taskMapper::toDto)
                                     .toList();

        assertThat(actual).hasSize(expected.size());

        response = mockMvc.perform(get("/api/tasks")
                                       .param("nameCont", "First")
                                       .with(jwt()))
                          .andExpect(status().isOk())
                          .andReturn()
                          .getResponse();
        body = response.getContentAsString();
        actual = objectMapper.readValue(body, new TypeReference<>() { });

        assertThat(actual).hasSize(2);
        assertThat(actual.getFirst().getId()).isEqualTo(testTask.getId());
        assertThat(actual.getFirst().getTitle()).contains("First");

        response = mockMvc.perform(get("/api/tasks")
                                       .param("assigneeId", testUser.getId().toString())
                                       .with(jwt()))
                          .andExpect(status().isOk())
                          .andReturn()
                          .getResponse();
        body = response.getContentAsString();
        actual = objectMapper.readValue(body, new TypeReference<>() { });

        assertThat(actual).hasSize(1);
        assertThat(actual.getFirst().getId()).isEqualTo(testTask.getId());
        assertThat(actual.getFirst().getAssigneeId()).isEqualTo(testUser.getId());

        response = mockMvc.perform(get("/api/tasks")
                                       .param("status", testStatus.getSlug())
                                       .with(jwt()))
                          .andExpect(status().isOk())
                          .andReturn()
                          .getResponse();
        body = response.getContentAsString();
        actual = objectMapper.readValue(body, new TypeReference<>() { });

        assertThat(actual).hasSize(1);
        assertThat(actual.getFirst().getId()).isEqualTo(testTask.getId());
        assertThat(actual.getFirst().getStatus()).isEqualTo(testStatus.getSlug());

        response = mockMvc.perform(get("/api/tasks")
                                       .param("labelId", testLabel1.getId().toString())
                                       .with(jwt()))
                          .andExpect(status().isOk())
                          .andReturn()
                          .getResponse();
        body = response.getContentAsString();
        actual = objectMapper.readValue(body, new TypeReference<>() { });

        assertThat(actual).hasSize(1);
        assertThat(actual.getFirst().getId()).isEqualTo(testTask.getId());
        assertThat(actual.getFirst().getTaskLabelIds()).contains(testLabel1.getId());

        response = mockMvc.perform(get("/api/tasks")
                                       .param("nameCont", "test")
                                       .param("assigneeId", testUser2.getId().toString())
                                       .with(jwt()))
                          .andExpect(status().isOk())
                          .andReturn()
                          .getResponse();
        body = response.getContentAsString();
        actual = objectMapper.readValue(body, new TypeReference<>() { });

        assertThat(actual).hasSize(1);
        assertThat(actual.getFirst().getId()).isEqualTo(testTask2.getId());
        assertThat(actual.getFirst().getAssigneeId()).isEqualTo(testUser2.getId());
    }

    @Test
    @Transactional
    void testCreate() throws Exception {
        var request = new TaskCreateDTO();
        request.setTitle("New Task");
        request.setContent("Task description");
        request.setStatus(testStatus.getSlug());
        request.setAssigneeId(testUser.getId());
        request.setTaskLabelIds(Set.of(testLabel1.getId(), testLabel2.getId()));

        mockMvc.perform(post("/api/tasks")
                            .with(token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
               .andExpect(status().isCreated())
               .andReturn()
               .getResponse();

        var createdTask = taskRepository.findAll().stream()
                                        .filter(t -> t.getName().equals("New Task"))
                                        .findFirst()
                                        .orElse(null);

        assertNotNull(createdTask);
        assertThat(createdTask.getName()).isEqualTo("New Task");
        assertThat(createdTask.getDescription()).isEqualTo("Task description");
        assertThat(createdTask.getStatus().getName()).isEqualTo(testStatus.getName());
        assertThat(createdTask.getAssignee().getId()).isEqualTo(testUser.getId());
        assertThat(createdTask.getLabels()).hasSize(2);
        assertThat(createdTask.getLabels()).contains(testLabel1, testLabel2);
    }

    @Test
    @Transactional
    void testUpdate() throws Exception {
        var data = new TaskUpdateDTO();
        data.setTitle(JsonNullable.of("Updated Title"));
        data.setContent(JsonNullable.of("Updated Content"));
        data.setTaskLabelIds(JsonNullable.of(Set.of(testLabel2.getId())));

        var request = put("/api/tasks/" + testTask.getId())
            .with(token)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(data));

        mockMvc.perform(request)
               .andExpect(status().isOk());

        var task = taskRepository.findById(testTask.getId()).orElseThrow();
        assertThat(task.getName()).isEqualTo("Updated Title");
        assertThat(task.getDescription()).isEqualTo("Updated Content");
        assertThat(task.getLabels()).hasSize(1);
        assertThat(task.getLabels()).contains(testLabel2);
    }

    @Test
    void testShow() throws Exception {
        var request = get("/api/tasks/" + testTask.getId()).with(jwt());
        var result = mockMvc.perform(request)
                            .andExpect(status().isOk())
                            .andReturn();
        var body = result.getResponse().getContentAsString();

        TaskDTO responseDto = objectMapper.readValue(body, TaskDTO.class);
        assertThat(responseDto.getTitle()).isEqualTo(testTask.getName());
        assertThat(responseDto.getContent()).isEqualTo(testTask.getDescription());
        assertThat(responseDto.getTaskLabelIds()).contains(testLabel1.getId());
    }

    @Test
    void testDelete() throws Exception {
        var request = delete("/api/tasks/" + testTask.getId())
            .with(token);
        mockMvc.perform(request)
               .andExpect(status().isNoContent());

        assertThat(taskRepository.existsById(testTask.getId())).isFalse();
    }
}
