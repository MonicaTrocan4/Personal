package com.example.demo.services;

import com.example.demo.config.RabbitMQConfig;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class DispatcherService {

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    @Value("${app.monitoring.replicas}")
    private int replicaCount;

    public DispatcherService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = new ObjectMapper();
    }

    @RabbitListener(queues = RabbitMQConfig.SENSOR_INPUT_QUEUE)
    public void dispatchMessage(String messageJson) {
        try {
            JsonNode root = objectMapper.readTree(messageJson);

            String deviceId = "";

            if (root.has("deviceId")) {
                deviceId = root.path("deviceId").asText();
            } else if (root.has("device_id")) {
                deviceId = root.path("device_id").asText();
            } else if (root.has("id")) {
                deviceId = root.path("id").asText();
            }

            if (deviceId == null || deviceId.isEmpty()) {
                System.out.println("Ignored message (missing deviceId): " + messageJson);
                return;
            }

            int hash = Math.abs(deviceId.hashCode());
            int replicaIndex = (hash % replicaCount) + 1;

            String targetQueue = RabbitMQConfig.QUEUE_PREFIX + replicaIndex;

            rabbitTemplate.convertAndSend(targetQueue, messageJson);

        } catch (Exception e) {
            System.err.println("Error processing message: " + e.getMessage());
        }
    }
}