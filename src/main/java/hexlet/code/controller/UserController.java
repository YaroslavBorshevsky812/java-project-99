package hexlet.code.controller;

import hexlet.code.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import hexlet.code.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;

public class UserController {

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/users")
    @ResponseStatus(HttpStatus.CREATED)
    public User create(@RequestBody User user) {
        userRepository.save(user);
        return user;
    }

    @GetMapping("/users/{id}")
    @ResponseStatus(HttpStatus.OK)
    public User show(@PathVariable Long id) {
        var user = userRepository.findById(id).get();
        return user;
    }
}
