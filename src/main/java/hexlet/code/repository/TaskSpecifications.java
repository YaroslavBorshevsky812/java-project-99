package hexlet.code.repository;

import hexlet.code.model.Label;
import hexlet.code.model.Task;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

public class TaskSpecifications {
    public static Specification<Task> withNameContaining(String nameCont) {
        return (root, query, cb) ->
            nameCont == null ? null : cb.like(cb.lower(root.get("name")), "%" + nameCont.toLowerCase() + "%");
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
        return (root, query, cb) -> {
            if (labelId == null) {
                return null;
            }

            Join<Task, Label> labelJoin = root.join("labels");
            return cb.equal(labelJoin.get("id"), labelId);
        };
    }
}
