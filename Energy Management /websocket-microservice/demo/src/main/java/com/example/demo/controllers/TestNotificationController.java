package com.example.demo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestNotificationController {

    private final SimpMessagingTemplate template;

    @Autowired
    public TestNotificationController(SimpMessagingTemplate template) {
        this.template = template;
    }

    @PostMapping("/send")
    public ResponseEntity<String> sendTestMessage(@RequestBody String message) {
        template.convertAndSend("/topic/alerts", message);

        return ResponseEntity.ok("Message sent to WebSocket clients: " + message);
    }
}