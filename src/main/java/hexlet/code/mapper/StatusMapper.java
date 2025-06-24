package hexlet.code.mapper;

import hexlet.code.dto.status.StatusCreateDTO;
import hexlet.code.dto.status.StatusDTO;
import hexlet.code.dto.status.StatusUpdateDTO;
import hexlet.code.model.Status;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    uses = {JsonNullableMapper.class}
)
public abstract class StatusMapper {
    public abstract StatusDTO toDto(Status entity);

    public abstract Status toEntity(StatusCreateDTO dto);

    public abstract void updateEntity(StatusUpdateDTO dto, @MappingTarget Status entity);
}
