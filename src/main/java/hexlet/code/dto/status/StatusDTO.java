package hexlet.code.dto.status;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@Getter
@Setter
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class StatusDTO {
    private Long id;
    private String name;
    private String slug;
    private LocalDate createdAt;
}
