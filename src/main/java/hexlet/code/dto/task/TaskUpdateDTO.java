package hexlet.code.dto.task;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.openapitools.jackson.nullable.JsonNullable;

import java.util.Set;


@Getter
@Setter
public class TaskUpdateDTO {
    private JsonNullable<Integer> index;

    @JsonProperty("assignee_id")
    private JsonNullable<Long> assigneeId;

    @Size(min = 1)
    @NotBlank
    private JsonNullable<String> title;

    private JsonNullable<String> content;

    private JsonNullable<String> status;

    @JsonProperty("taskLabelIds")
    private JsonNullable<Set<Long>> taskLabelIds;
}
