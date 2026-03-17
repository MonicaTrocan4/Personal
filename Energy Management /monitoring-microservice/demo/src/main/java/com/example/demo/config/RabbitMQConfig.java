package com.example.demo.config;

import com.example.demo.dtos.DeviceToMonitoringDTO;
import com.example.demo.dtos.SensorDataDTO;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.DefaultClassMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class RabbitMQConfig {

    public static final String DEVICE_SYNC_QUEUE = "device-sync-queue";
    public static final String ALERTS_QUEUE = "alerts-queue";

    public static final String QUEUE_PREFIX = "monitoring-q-";

    @Bean
    public Queue deviceSyncQueue() {
        return new Queue(DEVICE_SYNC_QUEUE, true);
    }

    @Bean
    public Queue alertsQueue() {
        return new Queue(ALERTS_QUEUE, true);
    }

    @Bean
    public String myAssignedQueue() {
        try {
            String hostname = InetAddress.getLocalHost().getHostName();
            System.out.println("DEBUG: My Hostname is: " + hostname);

            if (hostname.contains("-")) {
                String[] parts = hostname.split("-");
                String index = parts[parts.length - 1];

                if (index.matches("\\d+")) {
                    String assignedQueue = QUEUE_PREFIX + index;
                    System.out.println("DEBUG: Listening on specific queue: " + assignedQueue);
                    return assignedQueue;
                }
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        System.out.println("DEBUG: Fallback to default queue: " + QUEUE_PREFIX + "1");
        return QUEUE_PREFIX + "1";
    }

    @Bean
    public Queue dynamicQueue() {
        return new Queue(myAssignedQueue(), true);
    }


    @Bean
    public Jackson2JsonMessageConverter converter() {
        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter();
        DefaultClassMapper classMapper = new DefaultClassMapper();
        classMapper.setTrustedPackages("*");
        Map<String, Class<?>> idClassMapping = new HashMap<>();
        idClassMapping.put("com.example.demo.dtos.DeviceToMonitoringDTO", DeviceToMonitoringDTO.class);
        idClassMapping.put("com.example.demo.dtos.SensorDataDTO", SensorDataDTO.class);
        classMapper.setIdClassMapping(idClassMapping);
        converter.setClassMapper(classMapper);
        return converter;
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(converter());
        return template;
    }
}