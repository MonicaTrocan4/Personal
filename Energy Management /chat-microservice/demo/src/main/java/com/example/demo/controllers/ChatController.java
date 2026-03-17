package com.example.demo.controllers;

import com.example.demo.dtos.ChatRuleDTO;
import com.example.demo.dtos.MessageDTO;
import com.example.demo.services.ChatService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/chat")
@CrossOrigin(origins = "*")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @GetMapping("/test")
    public ResponseEntity<String> testEndpoint() {
        return ResponseEntity.ok("Chat Microservice is up and running!");
    }

    @PostMapping("/send")
    public ResponseEntity<String> sendMessage(@RequestBody MessageDTO messageDTO) {
        chatService.sendMessage(messageDTO);
        return ResponseEntity.ok("Message sent successfully");
    }

    @GetMapping("/history/{userId1}/{userId2}")
    public ResponseEntity<List<MessageDTO>> getChatHistory(@PathVariable UUID userId1, @PathVariable UUID userId2) {
        List<MessageDTO> history = chatService.getChatHistory(userId1, userId2);
        return ResponseEntity.ok(history);
    }

    @PostMapping("/rules")
    public ResponseEntity<ChatRuleDTO> addRule(@RequestBody ChatRuleDTO ruleDTO) {
        ChatRuleDTO createdRule = chatService.addRule(ruleDTO);
        return ResponseEntity.ok(createdRule);
    }

    @GetMapping("/rules")
    public ResponseEntity<List<ChatRuleDTO>> getAllRules() {
        return ResponseEntity.ok(chatService.getAllRules());
    }

    @DeleteMapping("/rules/{id}")
    public ResponseEntity<String> deleteRule(@PathVariable UUID id) {
        chatService.deleteRule(id);
        return ResponseEntity.ok("Rule deleted successfully");
    }
}