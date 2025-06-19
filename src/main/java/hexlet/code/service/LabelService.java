package hexlet.code.service;

import hexlet.code.dto.label.LabelCreateDto;
import hexlet.code.dto.label.LabelDto;
import hexlet.code.dto.label.LabelUpdateDto;
import hexlet.code.mapper.LabelMapper;
import hexlet.code.repository.LabelRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LabelService {
    @Autowired
    private final LabelRepository labelRepository;
    @Autowired
    private final LabelMapper labelMapper;

    public LabelDto getLabelById(Long id) {
        var label = labelRepository.findById(id)
                                   .orElseThrow(() -> new EntityNotFoundException());
        return labelMapper.toResponse(label);
    }

    public List<LabelDto> getAllLabels() {
        return labelRepository.findAll().stream()
                              .map(labelMapper::toResponse)
                              .toList();
    }

    public LabelDto createLabel(LabelCreateDto request) {
        var label = labelMapper.toEntity(request);
        var savedLabel = labelRepository.save(label);
        return labelMapper.toResponse(savedLabel);
    }

    public LabelDto updateLabel(Long id, LabelUpdateDto request) {
        var label = labelRepository.findById(id)
                                   .orElseThrow(() -> new EntityNotFoundException());
        labelMapper.updateEntity(label, request);
        var updatedLabel = labelRepository.save(label);
        return labelMapper.toResponse(updatedLabel);
    }

    @Transactional
    public void deleteLabel(Long id) {
        var label = labelRepository.findById(id)
                                   .orElseThrow(() -> new EntityNotFoundException());

        labelRepository.delete(label);
    }
}
