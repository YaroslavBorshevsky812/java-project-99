package hexlet.code.controller;

import hexlet.code.dto.user.UserCreateDTO;
import hexlet.code.dto.user.UserDTO;
import hexlet.code.dto.user.UserUpdateDTO;
import hexlet.code.mapper.UserMapper;
import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
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
import java.util.Optional;
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
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private UserMapper userMapper;

    private SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor token;

    @BeforeEach
    void setUp() {
        User testUser = createTestUser(1L);
        token = SecurityMockMvcRequestPostProcessors.jwt().jwt(builder ->
                                                                   builder.subject(testUser.getEmail())
        );
    }

    private User createTestUser(Long id) {
        User user = new User();
        user.setId(id);
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john.doe@example.com");
        user.setPassword("password");
        user.setCreatedAt(LocalDate.now());
        user.setUpdatedAt(LocalDate.now());
        return user;
    }

    private UserDTO createTestUserDTO(Long id) {
        UserDTO dto = new UserDTO();
        dto.setId(id);
        dto.setFirstName("John");
        dto.setLastName("Doe");
        dto.setEmail("john.doe@example.com");
        return dto;
    }

    private UserCreateDTO createUserCreateDTO() {
        UserCreateDTO dto = new UserCreateDTO();
        dto.setFirstName("John");
        dto.setLastName("Doe");
        dto.setEmail("john.doe@example.com");
        dto.setPassword("password");
        return dto;
    }

    private UserUpdateDTO createUserUpdateDTO() {
        UserUpdateDTO dto = new UserUpdateDTO();
        dto.setFirstName(JsonNullable.of("John"));
        dto.setLastName(JsonNullable.of("Smith"));
        dto.setEmail(JsonNullable.of("john.smith@example.com"));
        return dto;
    }

    @Test
    void getAllUsersShouldReturnUserList() throws Exception {
        User user = createTestUser(1L);
        UserDTO userDTO = createTestUserDTO(1L);

        when(userRepository.findAll()).thenReturn(List.of(user));
        when(userMapper.toDTO(user)).thenReturn(userDTO);

        mockMvc.perform(get("/api/users")
                            .with(token))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$[0].id").value(1))
               .andExpect(jsonPath("$[0].firstName").value("John"))
               .andExpect(jsonPath("$[0].lastName").value("Doe"))
               .andExpect(jsonPath("$[0].email").value("john.doe@example.com"));
    }

    @Test
    void getUserByIdShouldReturnUser() throws Exception {
        User user = createTestUser(1L);
        UserDTO userDTO = createTestUserDTO(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userMapper.toDTO(user)).thenReturn(userDTO);

        mockMvc.perform(get("/api/users/1")
                            .with(token))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.id").value(1))
               .andExpect(jsonPath("$.firstName").value("John"))
               .andExpect(jsonPath("$.lastName").value("Doe"))
               .andExpect(jsonPath("$.email").value("john.doe@example.com"));
    }

    @Test
    void createUserShouldReturnCreatedUser() throws Exception {
        UserCreateDTO createDTO = createUserCreateDTO();
        User newUser = createTestUser(null);
        User savedUser = createTestUser(1L);
        UserDTO savedUserDTO = createTestUserDTO(1L);

        when(userMapper.toEntity(createDTO)).thenReturn(newUser);
        when(userRepository.save(newUser)).thenReturn(savedUser);
        when(userMapper.toDTO(savedUser)).thenReturn(savedUserDTO);

        mockMvc.perform(post("/api/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createDTO)))
               .andExpect(status().isCreated())
               .andExpect(jsonPath("firstName").value("John"))
               .andExpect(jsonPath("lastName").value("Doe"))
               .andExpect(jsonPath("email").value("john.doe@example.com"));
    }


    @Test
    void updateUserShouldReturnUpdatedUser() throws Exception {
        UserUpdateDTO updateDTO = createUserUpdateDTO();
        User existingUser = createTestUser(1L);
        User updatedUser = createTestUser(1L);
        updatedUser.setLastName("Smith");
        updatedUser.setEmail("john.smith@example.com");

        UserDTO updatedUserDTO = createTestUserDTO(1L);
        updatedUserDTO.setLastName("Smith");
        updatedUserDTO.setEmail("john.smith@example.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(existingUser)).thenReturn(updatedUser);
        when(userMapper.toDTO(updatedUser)).thenReturn(updatedUserDTO);

        mockMvc.perform(put("/api/users/1")
                            .with(token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateDTO)))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.id").value(1))
               .andExpect(jsonPath("$.firstName").value("John"))
               .andExpect(jsonPath("$.lastName").value("Smith"))
               .andExpect(jsonPath("$.email").value("john.smith@example.com"));
    }

    @Test
    void deleteUserShouldReturnNoContent() throws Exception {
        when(userRepository.existsById(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/users/1")
                            .with(token))
               .andExpect(status().isNoContent());

        verify(userRepository).deleteById(1L);
    }

    @Test
    void createUserShouldReturnBadRequestForInvalidData() throws Exception {
        UserCreateDTO invalidDTO = new UserCreateDTO(); // Пустой DTO

        mockMvc.perform(post("/api/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidDTO)))
               .andExpect(status().isBadRequest());
    }

    @Test
    void updateUserShouldReturnUnauthorizedWithoutToken() throws Exception {
        UserUpdateDTO updateDTO = createUserUpdateDTO();

        mockMvc.perform(put("/api/users/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateDTO)))
               .andExpect(status().isUnauthorized());
    }
}
