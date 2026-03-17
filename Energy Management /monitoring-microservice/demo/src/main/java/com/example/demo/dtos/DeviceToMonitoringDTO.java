package com.example.demo.dtos;

import java.io.Serializable;
import java.util.UUID;

public class DeviceToMonitoringDTO implements Serializable {

    private UUID id;
    private String name;
    private int maxConsumption;

    public DeviceToMonitoringDTO() {
    }

    public DeviceToMonitoringDTO(UUID id, String name, int maxConsumption) {
        this.id = id;
        this.name = name;
        this.maxConsumption = maxConsumption;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getMaxConsumption() {
        return maxConsumption;
    }

    public void setMaxConsumption(int maxConsumption) {
        this.maxConsumption = maxConsumption;
    }

}