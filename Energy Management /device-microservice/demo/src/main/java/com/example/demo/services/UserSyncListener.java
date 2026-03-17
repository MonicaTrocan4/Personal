package com.example.demo.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.example.demo.entities.User;
import com.example.demo.repositories.UserRepository;
import com.example.demo.dtos.UserDTO;

import java.util.Optional;

@Component
public class UserSyncListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserSyncListener.class);
    private final UserRepository userRepository;

    @Autowired
    public UserSyncListener(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @RabbitListener(queues = "user-sync-queue")
    public void syncUser(UserDTO userDto) {
        LOGGER.info("RabbitMQ Listener received: {}", userDto);

        try {
            if (userDto.getName() == null) {
                if (userRepository.existsById(userDto.getId())) {
                    userRepository.deleteById(userDto.getId());
                    LOGGER.info("Successfully DELETED User ID: {} from local DB.", userDto.getId());
                } else {
                    LOGGER.warn("Received DELETE for User ID: {}, but user not found locally.", userDto.getId());
                }
            }
            else {
                Optional<User> existingUser = userRepository.findById(userDto.getId());

                if (!existingUser.isPresent()) {

                    User newUser = new User();
                    newUser.setId(userDto.getId());
                    newUser.setName(userDto.getName());
                    userRepository.save(newUser);
                    LOGGER.info("Successfully CREATED User: {}", userDto.getName());
                } else {

                    User existing = existingUser.get();
                    if (!existing.getName().equals(userDto.getName())) {
                        existing.setName(userDto.getName());
                        userRepository.save(existing);
                        LOGGER.info("Successfully UPDATED name for User ID: {}", userDto.getId());
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error("Failed to process sync event for user: {}", userDto, e);
        }
    }
}