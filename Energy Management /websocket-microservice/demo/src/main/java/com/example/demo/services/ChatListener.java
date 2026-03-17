package com.example.demo.services;

import com.example.demo.config.RabbitMQConfig;
import com.example.demo.dtos.MessageDTO;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class ChatListener {

    private final SimpMessagingTemplate messagingTemplate;

    public ChatListener(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @RabbitListener(queues = RabbitMQConfig.CHAT_QUEUE)
    public void receiveMessage(MessageDTO messageDTO) {
        System.out.println("Chat Message received: " + messageDTO.getContent() + " from " + messageDTO.getSenderId());

        messagingTemplate.convertAndSend("/topic/messages", messageDTO);
    }
}