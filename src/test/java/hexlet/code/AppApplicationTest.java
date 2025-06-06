package hexlet.code;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AppApplicationTest {

    @Test
    public void testShowString() {
        assertEquals("Hello", AppApplication.showString("Hello"));
    }
}