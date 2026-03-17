package com.example.demo.entities;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "chat_rules")
public class ChatRule {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false)
    private String keyword;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String responseMessage;

    public ChatRule() {
    }

    public ChatRule(String keyword, String responseMessage) {
        this.keyword = keyword;
        this.responseMessage = responseMessage;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getKeyword() { return keyword; }
    public void setKeyword(String keyword) { this.keyword = keyword; }

    public String getResponseMessage() { return responseMessage; }
    public void setResponseMessage(String responseMessage) { this.responseMessage = responseMessage; }
}