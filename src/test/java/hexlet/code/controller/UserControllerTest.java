package hexlet.code.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.dto.user.UserUpdateDTO;
import hexlet.code.mapper.UserMapper;
import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
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

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private UserMapper userMapper;

    private JwtRequestPostProcessor token;
    private User testUser;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac)
                                 .defaultResponseCharacterEncoding(StandardCharsets.UTF_8)
                                 .apply(springSecurity())
                                 .build();

        // Создаем тестового пользователя вручную
        testUser = new User();
        testUser.setEmail("test@example.com");
        testUser.setPassword("password");
        testUser.setFirstName("John");
        testUser.setLastName("Doe");
        userRepository.save(testUser);

        token = jwt().jwt(builder -> builder.subject(testUser.getEmail()));
    }

    @AfterEach
    void clean() {
        userRepository.deleteAll();
    }

    @Test
    void testIndex() throws Exception {
        // Добавляем второго пользователя для проверки списка
        User anotherUser = new User();
        anotherUser.setEmail("another@example.com");
        anotherUser.setPassword("password");
        anotherUser.setFirstName("Jane");
        anotherUser.setLastName("Smith");
        userRepository.save(anotherUser);

        mockMvc.perform(get("/api/users").with(jwt()))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$").isArray())
               .andExpect(jsonPath("$[0].email").value(testUser.getEmail()))
               .andExpect(jsonPath("$[1].email").value(anotherUser.getEmail()));
    }

    @Test
    void testCreate() throws Exception {
        var data = new User();
        data.setEmail("new@example.com");
        data.setPassword("newpassword");
        data.setFirstName("New");
        data.setLastName("User");

        mockMvc.perform(post("/api/users")
                            .with(token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(om.writeValueAsString(data)))
               .andExpect(status().isCreated())
               .andExpect(jsonPath("$.email").value("new@example.com"))
               .andExpect(jsonPath("$.firstName").value("New"));

        var user = userRepository.findByEmail("new@example.com").orElse(null);
        assertNotNull(user);
        assertThat(user.getFirstName()).isEqualTo("New");
        assertThat(user.getLastName()).isEqualTo("User");
    }

    @Test
    void testUpdate() throws Exception {
        var data = new UserUpdateDTO();
        data.setFirstName(JsonNullable.of("Mike"));
        data.setLastName(JsonNullable.of("Johnson"));

        mockMvc.perform(put("/api/users/" + testUser.getId())
                            .with(token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(om.writeValueAsString(data)))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.firstName").value("Mike"))
               .andExpect(jsonPath("$.lastName").value("Johnson"));

        var user = userRepository.findById(testUser.getId()).orElseThrow();
        assertThat(user.getFirstName()).isEqualTo("Mike");
        assertThat(user.getLastName()).isEqualTo("Johnson");
    }

    @Test
    void testShow() throws Exception {
        mockMvc.perform(get("/api/users/" + testUser.getId()).with(jwt()))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.email").value(testUser.getEmail()))
               .andExpect(jsonPath("$.firstName").value(testUser.getFirstName()))
               .andExpect(jsonPath("$.lastName").value(testUser.getLastName()));
    }
}
