package com.example.demo;

import java.util.UUID;

public class SensorData {
    private UUID deviceId;
    private long timestamp;
    private double measurementValue;

    public SensorData(UUID deviceId, long timestamp, double measurementValue) {
        this.deviceId = deviceId;
        this.timestamp = timestamp;
        this.measurementValue = measurementValue;
    }

    public UUID getDeviceId() {
        return deviceId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public double getMeasurementValue() {
        return measurementValue;
    }
}