package com.example.demo.controllers;

import com.example.demo.entities.Device;
import com.example.demo.entities.Measurement;
import com.example.demo.services.MonitoringService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/monitoring")
@CrossOrigin
public class MonitoringController {

    private final MonitoringService monitoringService;

    @Autowired
    public MonitoringController(MonitoringService monitoringService) {
        this.monitoringService = monitoringService;
    }

    @GetMapping("/test")
    public ResponseEntity<String> testEndpoint() {
        return new ResponseEntity<>("MONITORING SERVICE IS ALIVE AND REACHABLE!", HttpStatus.OK);
    }

    @GetMapping("/devices")
    public ResponseEntity<List<Device>> getSyncedDevices() {
        List<Device> devices = monitoringService.getAllMonitoredDevices();
        return new ResponseEntity<>(devices, HttpStatus.OK);
    }

    @GetMapping("/measurements/{deviceId}")
    public ResponseEntity<List<Measurement>> getMeasurementsByDevice(@PathVariable UUID deviceId) {
        List<Measurement> measurements = monitoringService.getMeasurements(deviceId);
        return new ResponseEntity<>(measurements, HttpStatus.OK);
    }
}