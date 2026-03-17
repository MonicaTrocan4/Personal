package com.example.demo.services;

import com.example.demo.config.RabbitMQConfig;
import com.example.demo.dtos.DeviceToMonitoringDTO;
import com.example.demo.dtos.SensorDataDTO;
import com.example.demo.entities.Device;
import com.example.demo.entities.Measurement;
import com.example.demo.repositories.DeviceRepository;
import com.example.demo.repositories.MeasurementRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.List;
import java.util.UUID;

@Service
public class MonitoringService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MonitoringService.class);

    private final DeviceRepository deviceRepository;
    private final MeasurementRepository measurementRepository;
    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public MonitoringService(DeviceRepository deviceRepository,
                             MeasurementRepository measurementRepository,
                             RabbitTemplate rabbitTemplate) {
        this.deviceRepository = deviceRepository;
        this.measurementRepository = measurementRepository;
        this.rabbitTemplate = rabbitTemplate;
    }

    public List<Device> getAllMonitoredDevices() {
        return deviceRepository.findAll();
    }

    public List<Measurement> getMeasurements(UUID deviceId) {
        return measurementRepository.findByDeviceId(deviceId);
    }

    public void syncDevice(DeviceToMonitoringDTO dto) {
        if (dto.getName() == null) {
            if (deviceRepository.existsById(dto.getId())) {
                deviceRepository.deleteById(dto.getId());
                LOGGER.info("Device deleted: {}", dto.getId());
            }
        } else {
            Device device = new Device(dto.getId(), dto.getName(), dto.getMaxConsumption());
            deviceRepository.save(device);
            LOGGER.info("Device synced: {} - Max: {}", dto.getName(), dto.getMaxConsumption());
        }
    }

    public void processSensorData(SensorDataDTO sensorData) {
        Optional<Device> deviceOptional = deviceRepository.findById(sensorData.getDeviceId());

        if (!deviceOptional.isPresent()) {
            LOGGER.warn("Device-ul cu ID {} nu exista in baza de date!", sensorData.getDeviceId());
            return;
        }

        Device device = deviceOptional.get();
        LocalDateTime dateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(sensorData.getTimestamp()), ZoneId.systemDefault());

        Measurement measurement = new Measurement();
        measurement.setDeviceId(device.getId());
        measurement.setTimestamp(dateTime);
        measurement.setHourlyConsumption(sensorData.getMeasurementValue());

        measurementRepository.save(measurement);
        LOGGER.info("1. Masuratoare salvata: {} la ora {}", sensorData.getMeasurementValue(), dateTime);

        LocalDateTime startOfHour = dateTime.truncatedTo(ChronoUnit.HOURS);

        LOGGER.info("2. Cautam in DB masuratori dupa ora: {}", startOfHour);

        Double totalHourlyConsumption = measurementRepository.getTotalConsumptionForDeviceSince(device.getId(), startOfHour);

        if (totalHourlyConsumption == null) {
            LOGGER.warn("!!! Query-ul a returnat NULL. Folosim valoarea curenta.");
            totalHourlyConsumption = sensorData.getMeasurementValue();
        }

        LOGGER.info("3. STATISTICI: Device={} | ConsumDB={} | Limita={}",
                device.getName(), totalHourlyConsumption, device.getMaxConsumption());

        if (totalHourlyConsumption > device.getMaxConsumption()) {
            String alertMessage = "Limit exceeded for device " + device.getName() +
                    "! Total: " + totalHourlyConsumption +
                    " (Max: " + device.getMaxConsumption() + ")";

            rabbitTemplate.convertAndSend(RabbitMQConfig.ALERTS_QUEUE, alertMessage);
        } else {
            LOGGER.info("4. Nu trimit alerta ({} <= {})", totalHourlyConsumption, device.getMaxConsumption());
        }
    }
}