package com.example.demo.entities;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.type.SqlTypes;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "measurements")
public class Measurement implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    @UuidGenerator
    @JdbcTypeCode(SqlTypes.UUID)
    private UUID id;

    @Column(name = "device_id", nullable = false)
    @JdbcTypeCode(SqlTypes.UUID)
    private UUID deviceId;

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;

    @Column(name = "hourly_consumption", nullable = false)
    private Double hourlyConsumption;

    public Measurement() {
    }

    public Measurement(UUID deviceId, LocalDateTime timestamp, Double hourlyConsumption) {
        this.deviceId = deviceId;
        this.timestamp = timestamp;
        this.hourlyConsumption = hourlyConsumption;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(UUID deviceId) {
        this.deviceId = deviceId;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public Double getHourlyConsumption() {
        return hourlyConsumption;
    }

    public void setHourlyConsumption(Double hourlyConsumption) {
        this.hourlyConsumption = hourlyConsumption;
    }
}