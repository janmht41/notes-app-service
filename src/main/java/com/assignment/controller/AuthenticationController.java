package com.assignment.controller;

import com.assignment.model.AuthenticationResponse;
import com.assignment.model.LoginRequest;
import com.assignment.model.SignUpRequest;
import com.assignment.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService authenticationService;
    @PostMapping("/signup")
    public ResponseEntity<Map<String,Object>> signup(
            @RequestBody @Valid SignUpRequest signUpRequest
    ){
        authenticationService.register(signUpRequest);
        return ResponseEntity.ok(Map.of("Success","registration successful"));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(
            @RequestBody LoginRequest loginRequest
    ){
       return ResponseEntity.ok(authenticationService.authenticate(loginRequest));
    }

}
