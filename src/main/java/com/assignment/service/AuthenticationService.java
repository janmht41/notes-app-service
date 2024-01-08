package com.assignment.service;

import com.assignment.entity.Role;
import com.assignment.entity.User;
import com.assignment.exception.UserAlreadyExistsException;
import com.assignment.model.AuthenticationResponse;
import com.assignment.model.LoginRequest;
import com.assignment.model.SignUpRequest;
import com.assignment.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    public void register(SignUpRequest signUpRequest) throws UserAlreadyExistsException{
        var email = signUpRequest.getEmail();
        userRepository.findByEmail(email)
                .ifPresent(existingUser -> {
                    log.error("user with mail "+email+" already exists!");
                    throw new UserAlreadyExistsException("Duplicate Registration");
                });
        var newUser = User.builder()
                .firstname(signUpRequest.getFirstName())
                .lastname(signUpRequest.getLastName())
                .email(email)
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
           log.error("Authentication failed: "+e);
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
