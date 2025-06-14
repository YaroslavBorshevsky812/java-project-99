package hexlet.code.service;

import hexlet.code.dto.UserCreateDTO;
import hexlet.code.dto.UserDTO;
import hexlet.code.dto.UserUpdateDTO;
import hexlet.code.mapper.UserMapper;
import hexlet.code.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    public List<UserDTO> getAll() {
        return userRepository.findAll().stream()
                             .map(userMapper::toDTO)
                             .toList();
    }

    public UserDTO getById(Long id) {
        return userRepository.findById(id)
                             .map(userMapper::toDTO)
                             .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));
    }

    public UserDTO create(UserCreateDTO userCreateDTO) {
        var user = userMapper.toEntity(userCreateDTO);
        var savedUser = userRepository.save(user);

        return userMapper.toDTO(savedUser);
    }

    public UserDTO updateUser(Long id, UserUpdateDTO userUpdateDTO) {
        var user = userRepository.findById(id)
                                 .orElseThrow(() -> new EntityNotFoundException());

        userMapper.update(userUpdateDTO, user);
        var updatedUser = userRepository.save(user);
        return userMapper.toDTO(updatedUser);
    }

    public void delete(Long id) {
        if (!userRepository.existsById(id)) {
            throw new EntityNotFoundException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }
}
