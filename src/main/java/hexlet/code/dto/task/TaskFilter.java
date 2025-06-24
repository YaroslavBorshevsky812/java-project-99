package hexlet.code.dto.task;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskFilter {
    private String titleCont;
    private Long assigneeId;
    private String status;
    private Long labelId;

    public boolean isEmpty() {
        return titleCont == null
            && assigneeId == null
            && status == null
            && labelId == null;
    }
}
