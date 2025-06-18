package hexlet.code.controller;

import hexlet.code.dto.AuthDTO;
import hexlet.code.repository.UserRepository;
import hexlet.code.util.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AuthController {
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;

    @Autowired
    private final UserRepository userRepository;

    @PostMapping("/login")
    public String create(@RequestBody AuthDTO authRequest) {
        var authentication =
            new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword());

        var user = userRepository.findAll().getFirst();

        System.out.println(user.getEmail());
        System.out.println(user.getPassword());

        authenticationManager.authenticate(authentication);

        return jwtUtils.generateToken(authRequest.getUsername());
    }
}
