package com.assignment.service;

import com.assignment.entity.Role;
import com.assignment.entity.User;
import com.assignment.model.AuthenticationResponse;
import com.assignment.model.LoginRequest;
import com.assignment.model.SignUpRequest;
import com.assignment.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    public void register(SignUpRequest signUpRequest) {

        var newUser = User.builder()
                .firstname(signUpRequest.getFirstName())
                .lastname(signUpRequest.getLastName())
                .email(signUpRequest.getEmail())
                .password(passwordEncoder.encode(signUpRequest.getPassword()))
                .role(Role.USER)
                .build();
        userRepository.save(newUser);
    }
    public AuthenticationResponse authenticate(LoginRequest loginRequest) {
        var tempUser = getUserByEmail(loginRequest.getEmail());
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            tempUser.getUserId(),
                            loginRequest.getPassword())
            );
        } catch (AuthenticationException e) {
            throw new RuntimeException(e);
        }
        return getAuthenticationResponse(tempUser);
    }

    private AuthenticationResponse getAuthenticationResponse(User user) {
        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    private User getUserByEmail(String email){
        return userRepository.findByEmail(email).orElseThrow();
    }
}
