package hexlet.code.mapper;

import hexlet.code.dto.UserCreateDTO;
import hexlet.code.dto.UserDTO;
import hexlet.code.dto.UserUpdateDTO;
import hexlet.code.model.User;
import org.mapstruct.BeforeMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    uses = {JsonNullableMapper.class}
)
@Component
public abstract class UserMapper {

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JsonNullableMapper jsonNullableMapper;

    public abstract User toEntity(UserCreateDTO dto);

    public abstract void update(UserUpdateDTO dto, @MappingTarget User entity);

    public abstract UserDTO toDTO(User entity);

    @BeforeMapping
    public void encryptPassword(UserCreateDTO userCreateDTO) {
        var password = userCreateDTO.getPassword();
        userCreateDTO.setPassword(passwordEncoder.encode(password));
    }

    @BeforeMapping
    public void encryptPassword(UserUpdateDTO userUpdateDTO) {
        var password = userUpdateDTO.getPassword();
        if (jsonNullableMapper.isPresent(password)) {
            var encodedPassword = JsonNullable.of(passwordEncoder.encode(password.get()));
            userUpdateDTO.setPassword(encodedPassword);
        }
    }
}