package hexlet.code.dto.task;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskCreateDTO {
    private Integer index;

    private Long assigneeId;

    @NotBlank
    private String title;

    private String content;

    @NotBlank
    private String status;
}
