package hexlet.code.dto.label;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class LabelDto {
    private Long id;
    private String name;
    private LocalDate createdAt;
}
