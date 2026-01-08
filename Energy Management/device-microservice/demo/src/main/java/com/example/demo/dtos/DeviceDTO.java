package com.example.demo.dtos;

import java.util.Objects;
import java.util.UUID;

public class DeviceDTO {
    private UUID id;
    private String name;
    private int max_consumption;
    private UUID userId;

    public DeviceDTO() {}
    public DeviceDTO(UUID id, String name, int max_consumption, UUID userId) {
        this.id = id; this.name = name; this.max_consumption = max_consumption; this.userId = userId;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getMax_consumption() { return max_consumption; }
    public void setMax_consumption(int max_consumption) { this.max_consumption = max_consumption; }

    public UUID getUserId() { return userId; }

    public void setUserId(UUID userId) { this.userId = userId; }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DeviceDTO that = (DeviceDTO) o;
        return userId == that.userId && Objects.equals(name, that.name);
    }
    @Override public int hashCode() { return Objects.hash(name, max_consumption); }
}
