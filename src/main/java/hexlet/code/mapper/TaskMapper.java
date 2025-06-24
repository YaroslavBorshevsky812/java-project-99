package hexlet.code.mapper;

import hexlet.code.dto.task.TaskCreateDTO;
import hexlet.code.dto.task.TaskDTO;
import hexlet.code.dto.task.TaskUpdateDTO;
import hexlet.code.model.Status;
import hexlet.code.model.Task;
import hexlet.code.repository.StatusRepository;
import hexlet.code.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;

import static org.mapstruct.CollectionMappingStrategy.TARGET_IMMUTABLE;

@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    collectionMappingStrategy = TARGET_IMMUTABLE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    uses = {JsonNullableMapper.class, ReferenceMapper.class}
)
@Slf4j
public abstract class TaskMapper {
    @Autowired
    protected StatusRepository statusRepository;
    @Autowired
    protected UserRepository userRepository;

    @Mapping(target = "assigneeId", source = "assignee.id")
    @Mapping(target = "status", source = "status.slug")
    @Mapping(target = "title", source = "name")
    @Mapping(target = "content", source = "description")
    @Mapping(target = "taskLabelIds", source = "labels")
    public abstract TaskDTO toDto(Task task);

    @Mapping(target = "name", source = "title")
    @Mapping(target = "description", source = "content")
    @Mapping(target = "labels", source = "taskLabelIds")
    public abstract Task toEntity(TaskCreateDTO dto);

    @Mapping(target = "name", source = "title")
    @Mapping(target = "description", source = "content")
    @Mapping(target = "labels", source = "taskLabelIds")
    public abstract void updateEntity(TaskUpdateDTO dto, @MappingTarget Task entity);

    public Status toStatus(String statusSlag) {
        return statusRepository.findBySlug(statusSlag)
                                   .orElseThrow(() -> new EntityNotFoundException(statusSlag));
    }


}
