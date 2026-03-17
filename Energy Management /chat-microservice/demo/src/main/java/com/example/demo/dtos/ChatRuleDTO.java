package com.example.demo.dtos;

import java.util.UUID;

public class ChatRuleDTO {

    private UUID id;
    private String keyword;
    private String responseMessage;

    public ChatRuleDTO() {
    }

    public ChatRuleDTO(UUID id, String keyword, String responseMessage) {
        this.id = id;
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