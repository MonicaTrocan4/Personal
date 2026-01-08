package com.example.demo.services;

import com.example.demo.dtos.LoginDTO;
import com.example.demo.dtos.RegisterDTO;
import com.example.demo.entities.Credentials;
import com.example.demo.entities.Role;
import com.example.demo.repositories.CredentialRepository;
import com.example.demo.security.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.UUID;

import java.util.Optional;

@Service
public class AuthService {

    private final CredentialRepository credentialRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final RestTemplate restTemplate;

    @Value("${user.service.url:http://localhost:8080/people}")
    private String userServiceUrl;
    @Autowired
    public AuthService(CredentialRepository credentialRepository,
                       PasswordEncoder passwordEncoder,
                       JwtUtils jwtUtils,
                       RestTemplate restTemplate) {
        this.credentialRepository = credentialRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
        this.restTemplate = restTemplate;
    }

    public String login(LoginDTO loginDTO) {
        Optional<Credentials> credentialsOpt = credentialRepository.findByUsername(loginDTO.getUsername());

        if (credentialsOpt.isPresent()) {
            Credentials creds = credentialsOpt.get();
            if (passwordEncoder.matches(loginDTO.getPassword(), creds.getPassword())) {
                return jwtUtils.generateToken(creds.getUsername(), creds.getRole().name(), creds.getId().toString());
            }
        }
        throw new RuntimeException("Invalid username or password");
    }

    public void register(RegisterDTO registerDTO) {
        if (credentialRepository.findByUsername(registerDTO.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists!");
        }

        Credentials credentials = new Credentials();
        credentials.setUsername(registerDTO.getUsername());
        credentials.setPassword(passwordEncoder.encode(registerDTO.getPassword())); // Hash parola!
        credentials.setRole(registerDTO.getRole());

        Credentials savedCreds = credentialRepository.save(credentials);

        try {
            UserDataForSync userPayload = new UserDataForSync(
                    savedCreds.getId(),
                    registerDTO.getName(),
                    registerDTO.getAddress(),
                    registerDTO.getAge(),
                    registerDTO.getRole()
            );

            restTemplate.postForObject(userServiceUrl, userPayload, Void.class);

        } catch (Exception e) {
            System.err.println("Error syncing to User Service: " + e.getMessage());
            throw new RuntimeException("Failed to sync user profile. Registration aborted.");
        }
    }

    private static class UserDataForSync {
        public UUID id;
        public String name;
        public String address;
        public int age;
        public com.example.demo.entities.Role role;

        public UserDataForSync(UUID id, String name, String address, int age, com.example.demo.entities.Role role) {
            this.id = id;
            this.name = name;
            this.address = address;
            this.age = age;
            this.role = role;
        }
    }
}