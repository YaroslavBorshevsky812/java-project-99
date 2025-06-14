package hexlet.code.config;

import hexlet.code.dto.UserCreateDTO;
import hexlet.code.mapper.UserMapper;
import hexlet.code.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements ApplicationRunner {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        try {
            initAdminUser();
        } catch (Exception ex) {
            log.error("init data error", ex);
        }
    }

    private void initAdminUser() {
        var userData = new UserCreateDTO();
        userData.setFirstName("Mark");
        userData.setLastName("Wayne");
        userData.setEmail("hexlet@example.com");
        userData.setPassword("qwerty");
        var user = userMapper.toEntity(userData);
        userRepository.save(user);
        log.info("admin user was created");
    }
}
