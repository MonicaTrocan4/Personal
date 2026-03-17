package com.example.demo.services;

import com.example.demo.config.RabbitMQConfig;
import com.example.demo.dtos.DeviceToMonitoringDTO;
import com.example.demo.dtos.SensorDataDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class QueueListener {

    private final MonitoringService monitoringService;
    private final ObjectMapper objectMapper;

    @Autowired
    public QueueListener(MonitoringService monitoringService) {
        this.monitoringService = monitoringService;
        this.objectMapper = new ObjectMapper();
    }

    @RabbitListener(queues = RabbitMQConfig.DEVICE_SYNC_QUEUE)
    public void consumeSyncMessage(DeviceToMonitoringDTO dto) {
        monitoringService.syncDevice(dto);
    }

    @RabbitListener(queues = "#{myAssignedQueue}")
    public void consumeSensorData(String messageJson) {
        try {
            System.out.println("DEBUG Raw JSON received: " + messageJson);

            SensorDataDTO dto = objectMapper.readValue(messageJson, SensorDataDTO.class);

            System.out.println("Replica processed value: " + dto.getMeasurementValue());
            monitoringService.processSensorData(dto);

        } catch (Exception e) {
            System.err.println("Eroare la procesarea JSON-ului in Monitoring: " + e.getMessage());
            e.printStackTrace();
        }
    }
}