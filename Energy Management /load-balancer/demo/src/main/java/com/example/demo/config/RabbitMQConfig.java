package com.example.demo.config;

import org.springframework.amqp.core.Declarables;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class RabbitMQConfig {

    public static final String SENSOR_INPUT_QUEUE = "sensor-data-queue";
    public static final String QUEUE_PREFIX = "monitoring-q-";

    @Value("${app.monitoring.replicas}")
    private int replicaCount;

    @Bean
    public Queue sensorInputQueue() {
        return new Queue(SENSOR_INPUT_QUEUE, true);
    }

    @Bean
    public Declarables replicaQueues() {
        List<Queue> queues = new ArrayList<>();

        System.out.println("Configuring Load Balancer for " + replicaCount + " replicas.");

        for (int i = 1; i <= replicaCount; i++) {
            String queueName = QUEUE_PREFIX + i;
            queues.add(new Queue(queueName, true));
            System.out.println(" - Created Queue definition: " + queueName);
        }

        return new Declarables(queues);
    }
}