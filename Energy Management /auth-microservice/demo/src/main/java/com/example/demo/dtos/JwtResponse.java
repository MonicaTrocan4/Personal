package com.example.demo.dtos;

import java.util.UUID;
import com.example.demo.entities.Role;

public class JwtResponse {
    private String token;
    private UUID id;
    private String username;
    private Role role;

    public JwtResponse(String token, UUID id, String username, Role role) {
        this.token = token;
        this.id = id;
        this.username = username;
        this.role = role;
    }

    public String getToken() { return token; }
    public UUID getId() { return id; }
    public String getUsername() { return username; }
    public Role getRole() { return role; }
}