package com.example.demo.controllers;

import com.example.demo.dtos.DeviceDTO;
import com.example.demo.dtos.DeviceDetailsDTO;
import com.example.demo.services.DeviceService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/devices")
@Validated
public class DeviceController {

    private final DeviceService deviceService;

    public DeviceController(DeviceService deviceService) {
        this.deviceService = deviceService;
    }

    @GetMapping
    public ResponseEntity<List<DeviceDTO>> getDevices() {
        return ResponseEntity.ok(deviceService.findDevices());
    }

    @PostMapping
    public ResponseEntity<Void> create(@Valid @RequestBody DeviceDetailsDTO device) {
        UUID id = deviceService.insert(device);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(id)
                .toUri();
        return ResponseEntity.created(location).build(); // 201 + Location header
    }

    @GetMapping("/{id}")
    public ResponseEntity<DeviceDetailsDTO> getDeviceById(@PathVariable UUID id) {
        return ResponseEntity.ok(deviceService.findDeviceById(id));
    }

    @GetMapping("/search/by-name")
    public ResponseEntity<List<DeviceDetailsDTO>> getDeviceByName(@RequestParam String name) {
        return ResponseEntity.ok(deviceService.findDeviceByName(name));
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<List<DeviceDetailsDTO>> getDeviceByUserId(@PathVariable UUID userId) {
        return ResponseEntity.ok(deviceService.findByUserId(userId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDevice(@PathVariable UUID id) {
        deviceService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<DeviceDetailsDTO> updateDevice(@PathVariable UUID id, @Valid @RequestBody DeviceDetailsDTO deviceDetailsDTO) {
        DeviceDetailsDTO updatedDevice = deviceService.update(id, deviceDetailsDTO);
        return ResponseEntity.ok(updatedDevice);
    }

    @PatchMapping("/{deviceId}/assign/{userId}")
    public ResponseEntity<Void> assignUserToDevice(@PathVariable UUID deviceId, @PathVariable UUID userId) {
        deviceService.assignUser(deviceId, userId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{deviceId}/unassign")
    public ResponseEntity<Void> unassignUserFromDevice(@PathVariable UUID deviceId) {
        deviceService.unassignUser(deviceId);
        return ResponseEntity.noContent().build();
    }
}
