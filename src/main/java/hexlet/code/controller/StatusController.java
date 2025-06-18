package hexlet.code.controller;

import hexlet.code.dto.status.StatusCreateDTO;
import hexlet.code.dto.status.StatusDTO;
import hexlet.code.dto.status.StatusUpdateDTO;
import hexlet.code.service.StatusService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/task_statuses")
@RequiredArgsConstructor
public class StatusController {

    private final StatusService taskStatusService;

    @GetMapping
    public List<StatusDTO> getAll() {
        return taskStatusService.getAll();
    }

    @GetMapping("/{id}")
    public StatusDTO getById(@PathVariable Long id) {
        return taskStatusService.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public StatusDTO create(@Valid @RequestBody StatusCreateDTO dto) {
        return taskStatusService.create(dto);
    }

    @PutMapping("/{id}")
    public StatusDTO update(@PathVariable Long id, @Valid @RequestBody StatusUpdateDTO dto) {
        return taskStatusService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        taskStatusService.delete(id);
    }
}
