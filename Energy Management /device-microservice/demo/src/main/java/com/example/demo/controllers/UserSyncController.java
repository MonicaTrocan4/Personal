package com.example.demo.controllers;

import com.example.demo.dtos.*;
import com.example.demo.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.util.stream.Collectors;
import java.util.UUID;
import java.util.List;


@RestController
@RequestMapping("/sync/users")
public class UserSyncController {

    private final UserRepository userRepository;

    @Autowired
    public UserSyncController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping
    public ResponseEntity<List<UserDTO>> getSyncedUsers() {
        List<com.example.demo.entities.User> userList = userRepository.findAll();

        List<UserDTO> dtos = userList.stream()
                .map(user -> new UserDTO(user.getId(), user.getName())) // Mapam Entitatea la DTO
                .collect(Collectors.toList());

        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }

    @PostMapping
    public void syncUser(@RequestBody UserDTO userDto) {
        com.example.demo.entities.User userEntity = new com.example.demo.entities.User(
                userDto.getId(),
                userDto.getName()
        );
        userRepository.save(userEntity);
        System.out.println("User synced from User Service: " + userDto.getId());
    }

    @DeleteMapping("/{id}")
    @Transactional
    public void deleteSyncedUser(@PathVariable UUID id) {
        userRepository.deleteById(id);
    }

    @PutMapping("/{id}")
    @Transactional
    public void updateUser(@PathVariable UUID id, @RequestBody UserDTO userDto) {
        userRepository.findById(id).ifPresent(user -> {
            user.setName(userDto.getName());

            userRepository.save(user);
            System.out.println("User synced (UPDATE) from User Service: " + id);
        });
    }
}