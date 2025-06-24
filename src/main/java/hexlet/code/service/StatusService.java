package hexlet.code.service;

import hexlet.code.dto.status.StatusCreateDTO;
import hexlet.code.dto.status.StatusDTO;
import hexlet.code.dto.status.StatusUpdateDTO;
import hexlet.code.mapper.StatusMapper;
import hexlet.code.repository.StatusRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StatusService {

    @Autowired
    private StatusRepository statusRepository;
    @Autowired
    private StatusMapper statusMapper;

    public List<StatusDTO> getAll() {
        return statusRepository.findAll().stream()
                               .map(statusMapper::toDto)
                               .collect(Collectors.toList());
    }

    public StatusDTO getById(Long id) {
        return statusRepository
            .findById(id)
            .map(statusMapper::toDto)
            .orElseThrow(() -> new EntityNotFoundException("TaskStatus not found with id: " + id));
    }

    @Transactional
    public StatusDTO create(StatusCreateDTO dto) {
        var taskStatus = statusMapper.toEntity(dto);
        taskStatus = statusRepository.save(taskStatus);
        return statusMapper.toDto(taskStatus);
    }

    @Transactional
    public StatusDTO update(Long id, StatusUpdateDTO dto) {
        var taskStatus = statusRepository
                .findById(id)
                .orElseThrow(() -> new EntityNotFoundException("TaskStatus not found with id: " + id));

        statusMapper.updateEntity(dto, taskStatus);
        taskStatus = statusRepository.save(taskStatus);
        return statusMapper.toDto(taskStatus);
    }

    @Transactional
    public void delete(Long id) {
        statusRepository.deleteById(id);
    }
}
