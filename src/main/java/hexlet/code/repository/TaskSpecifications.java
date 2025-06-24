package hexlet.code.repository;

import hexlet.code.model.Task;
import org.springframework.data.jpa.domain.Specification;

public class TaskSpecifications {
    public static Specification<Task> withTitleContaining(String titleCont) {
        return (root, query, cb) ->
            titleCont == null ? null : cb.like(cb.lower(root.get("title")), "%" + titleCont.toLowerCase() + "%");
    }

    public static Specification<Task> withAssigneeId(Long assigneeId) {
        return (root, query, cb) ->
            assigneeId == null ? null : cb.equal(root.get("assignee").get("id"), assigneeId);
    }

    public static Specification<Task> withStatus(String status) {
        return (root, query, cb) ->
            status == null ? null : cb.equal(root.get("status").get("slug"), status);
    }

    public static Specification<Task> withLabelId(Long labelId) {
        return (root, query, cb) ->
            labelId == null ? null : cb.isMember(labelId, root.join("labels").get("id"));
    }
}
