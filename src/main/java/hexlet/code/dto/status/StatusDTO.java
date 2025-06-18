package hexlet.code.dto.status;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@Getter
@Setter
@ToString
public class StatusDTO {
    private Long id;
    private String name;
    private String slug;
    private LocalDate createdAt;
}
