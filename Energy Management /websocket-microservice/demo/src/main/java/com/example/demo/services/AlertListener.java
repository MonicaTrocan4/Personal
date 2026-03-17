package com.example.demo.services;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class AlertListener {

    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public AlertListener(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @RabbitListener(queues = "alerts-queue")
    public void receiveAlert(String message) {
        System.out.println("Alerta primita din RabbitMQ: " + message);

        messagingTemplate.convertAndSend("/topic/alerts", message);
    }
}