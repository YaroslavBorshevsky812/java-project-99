package hexlet.code;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class AppApplication {

    public static String showString(String str) {
        return str;
    }

    public static void main(String[] args) {
        SpringApplication.run(AppApplication.class, args);
    }
}

