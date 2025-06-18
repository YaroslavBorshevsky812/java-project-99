package hexlet.code.dto.status;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StatusCreateDTO {
    @NotBlank(message = "Name cannot be blank")
    @Size(min = 1, max = 100, message = "Name must be between 1 and 100 characters")
    private String name;

    @NotBlank(message = "Slug cannot be blank")
    @Size(min = 1, max = 100, message = "Slug must be between 1 and 100 characters")
    private String slug;
}
