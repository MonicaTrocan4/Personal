package com.example.demo.controllers;

import com.example.demo.dtos.JwtResponse;
import com.example.demo.dtos.LoginDTO;
import com.example.demo.dtos.RegisterDTO;
import com.example.demo.entities.Credentials; // <--- Entitatea ta Corecta
import com.example.demo.repositories.CredentialRepository; // <--- Repository-ul Corect
import com.example.demo.services.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@CrossOrigin
public class AuthController {

    private final AuthService authService;
    private final CredentialRepository credentialRepository;

    @Autowired
    public AuthController(AuthService authService, CredentialRepository credentialRepository) {
        this.authService = authService;
        this.credentialRepository = credentialRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO loginDTO) {
        try {
            String token = authService.login(loginDTO);

            Credentials credentials = credentialRepository.findByUsername(loginDTO.getUsername())
                    .orElseThrow(() -> new RuntimeException("User/Credentials not found"));

            return ResponseEntity.ok(new JwtResponse(
                    token,
                    credentials.getId(),
                    credentials.getUsername(),
                    credentials.getRole()
            ));

        } catch (RuntimeException e) {
            return ResponseEntity.status(401).body(e.getMessage());
        }
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterDTO registerDTO) {
        try {
            authService.register(registerDTO);
            return ResponseEntity.ok("User registered successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}