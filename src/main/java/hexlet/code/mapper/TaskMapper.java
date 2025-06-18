package hexlet.code.mapper;

import hexlet.code.dto.task.TaskCreateDTO;
import hexlet.code.dto.task.TaskDTO;
import hexlet.code.dto.task.TaskUpdateDTO;
import hexlet.code.model.Task;
import hexlet.code.repository.StatusRepository;
import hexlet.code.repository.UserRepository;
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
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
@Slf4j
public abstract class TaskMapper {
    @Autowired
    protected StatusRepository statusRepository;
    @Autowired
    protected UserRepository userRepository;

    @Mapping(target = "assigneeId", source = "assignee.id")
    @Mapping(target = "status", source = "status.slug")
    public abstract TaskDTO toDto(Task task);

    @Mapping(target = "status", ignore = true)
    @Mapping(target = "assignee", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    public abstract Task toEntity(TaskCreateDTO dto);

    @Mapping(target = "status", ignore = true)
    @Mapping(target = "assignee", ignore = true)
    public abstract void updateEntity(TaskUpdateDTO dto, @MappingTarget Task entity);
}
