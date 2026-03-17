package com.example.demo.dtos;

import java.time.LocalDateTime;
import java.util.UUID;

public class MessageDTO {

    private UUID id;
    private UUID senderId;
    private UUID receiverId;
    private String content;
    private LocalDateTime timestamp;
    private Boolean isRead;

    public MessageDTO() {
    }

    public MessageDTO(UUID id, UUID senderId, UUID receiverId, String content, LocalDateTime timestamp, Boolean isRead) {
        this.id = id;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.content = content;
        this.timestamp = timestamp;
        this.isRead = isRead;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getSenderId() { return senderId; }
    public void setSenderId(UUID senderId) { this.senderId = senderId; }

    public UUID getReceiverId() { return receiverId; }
    public void setReceiverId(UUID receiverId) { this.receiverId = receiverId; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public Boolean getIsRead() { return isRead; }
    public void setIsRead(Boolean read) { isRead = read; }
}