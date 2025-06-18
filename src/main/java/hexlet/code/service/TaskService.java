package hexlet.code.service;

import hexlet.code.dto.task.TaskCreateDTO;
import hexlet.code.dto.task.TaskDTO;
import hexlet.code.dto.task.TaskUpdateDTO;
import hexlet.code.mapper.TaskMapper;
import hexlet.code.model.Status;
import hexlet.code.model.Task;
import hexlet.code.model.User;
import hexlet.code.repository.StatusRepository;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService {

    @Autowired
    private final TaskRepository taskRepository;
    @Autowired
    private final StatusRepository statusRepository;
    @Autowired
    private final UserRepository userRepository;
    @Autowired
    private final TaskMapper taskMapper;

    public List<TaskDTO> getAll() {
        return taskRepository.findAll().stream()
                             .map(taskMapper::toDto)
                             .toList();
    }

    public TaskDTO getById(Long id) {
        return taskRepository.findById(id)
                             .map(taskMapper::toDto)
                             .orElseThrow(() -> new EntityNotFoundException("Task not found"));
    }

    @Transactional
    public TaskDTO create(TaskCreateDTO dto) {
        Task task = taskMapper.toEntity(dto);

        Status status = statusRepository.findBySlug(dto.getStatus())
                                        .orElseThrow(() -> new IllegalArgumentException("Status not found"));
        task.setStatus(status);

        if (dto.getAssigneeId() != null) {
            User assignee = userRepository.findById(dto.getAssigneeId())
                                          .orElseThrow(() -> new IllegalArgumentException("User not found"));
            task.setAssignee(assignee);
        }

        task = taskRepository.save(task);
        return taskMapper.toDto(task);
    }

    @Transactional
    public TaskDTO update(Long id, TaskUpdateDTO dto) {
        Task task = taskRepository.findById(id)
                                  .orElseThrow(() -> new EntityNotFoundException("Task not found"));

        taskMapper.updateEntity(dto, task);

        if (dto.getStatus() != null) {
            Status status = statusRepository.findBySlug(dto.getStatus())
                                            .orElseThrow(() -> new IllegalArgumentException("Status not found"));
            task.setStatus(status);
        }

        if (dto.getAssigneeId() != null) {
            User assignee = userRepository.findById(dto.getAssigneeId())
                                          .orElseThrow(() -> new IllegalArgumentException("User not found"));
            task.setAssignee(assignee);
        }

        task = taskRepository.save(task);
        return taskMapper.toDto(task);
    }

    @Transactional
    public void delete(Long id) {
        taskRepository.deleteById(id);
    }
}
