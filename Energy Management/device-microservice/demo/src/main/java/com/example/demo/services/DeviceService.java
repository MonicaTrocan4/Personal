package com.example.demo.services;


import com.example.demo.dtos.DeviceDTO;
import com.example.demo.dtos.DeviceDetailsDTO;
import com.example.demo.dtos.builders.DeviceBuilder;
import com.example.demo.entities.Device;
import com.example.demo.handlers.exceptions.model.ResourceNotFoundException;
import com.example.demo.repositories.DeviceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.demo.repositories.UserRepository;
import com.example.demo.entities.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class DeviceService {
    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceService.class);
    private final DeviceRepository deviceRepository;
    private final UserRepository userRepository;

    @Autowired
    public DeviceService(DeviceRepository deviceRepository, UserRepository userRepository) {
        this.deviceRepository = deviceRepository;
        this.userRepository = userRepository;
    }

    public List<DeviceDTO> findDevices() {
        List<Device> deviceList = deviceRepository.findAll();
        return deviceList.stream()
                .map(DeviceBuilder::toDeviceDTO)
                .collect(Collectors.toList());
    }

    public DeviceDetailsDTO findDeviceById(UUID id) {
        Optional<Device> prosumerOptional = deviceRepository.findById(id);
        if (!prosumerOptional.isPresent()) {
            LOGGER.error("Device with id {} was not found in db", id);
            throw new ResourceNotFoundException(Device.class.getSimpleName() + " with id: " + id);
        }
        return DeviceBuilder.toDeviceDetailsDTO(prosumerOptional.get());
    }

    public UUID insert(DeviceDetailsDTO deviceDTO) {
        Device device = DeviceBuilder.toEntity(deviceDTO);
        device = deviceRepository.save(device);
        LOGGER.debug("Device with id {} was inserted in db", device.getId());
        return device.getId();
    }

    public DeviceDetailsDTO update(UUID id, DeviceDetailsDTO deviceDetailsDTO) {
        Optional<Device> deviceOptional = deviceRepository.findById(id);
        if (!deviceOptional.isPresent()) {
            LOGGER.error("Device with id {} was not found in db", id);
            throw new ResourceNotFoundException(Device.class.getSimpleName() + " with id: " + id);
        }

        Device deviceToUpdate = deviceOptional.get();

        deviceToUpdate.setName(deviceDetailsDTO.getName());
        deviceToUpdate.setMax_consumption(deviceDetailsDTO.getMax_consumption());
        deviceToUpdate.setUserId(deviceDetailsDTO.getUserId());

        Device updatedDevice = deviceRepository.save(deviceToUpdate);

        LOGGER.debug("Device with id {} was updated in db", updatedDevice.getId());
        return DeviceBuilder.toDeviceDetailsDTO(updatedDevice);
    }

    public UUID delete(UUID id) {
        Optional<Device> deviceOptional = deviceRepository.findById(id);
        if (!deviceOptional.isPresent()) {
            LOGGER.error("Device with id {} was not found in db", id);
            throw new ResourceNotFoundException(Device.class.getSimpleName() + " with id: " + id);
        }

        deviceRepository.deleteById(id);

        LOGGER.debug("Device with id {} was deleted from db", id);

        return id;
    }

    public List<DeviceDetailsDTO> findByUserId(UUID userId){
        return deviceRepository.findByUserId(userId).stream().map(DeviceBuilder::toDeviceDetailsDTO).collect(Collectors.toList());
    }

    public List<DeviceDetailsDTO> findDeviceByName(String name){

        List<Device> devices = deviceRepository.findByName(name);

        if(devices.isEmpty()) {
            LOGGER.error("Devices with name {} were not found in db", name);
            throw new ResourceNotFoundException(Device.class.getSimpleName() + " with name: " + name);
        }

        return devices.stream().map(DeviceBuilder::toDeviceDetailsDTO).collect(Collectors.toList());
    }

    public void assignUser(UUID deviceId, UUID userId) {
        Optional<Device> deviceOptional = deviceRepository.findById(deviceId);
        if (!deviceOptional.isPresent()) {
            throw new ResourceNotFoundException(Device.class.getSimpleName() + " with id: " + deviceId);
        }

        Optional<User> userOptional = userRepository.findById(userId);
        if (!userOptional.isPresent()) {
            throw new ResourceNotFoundException(User.class.getSimpleName() + " with id: " + userId);
        }

        Device device = deviceOptional.get();
        device.setUserId(userOptional.get().getId());

        deviceRepository.save(device);
        LOGGER.debug("Device with id {} was assigned to user {}", deviceId, userId);
    }

    public void unassignUser(UUID deviceId) {
        Optional<Device> deviceOptional = deviceRepository.findById(deviceId);
        if (!deviceOptional.isPresent()) {
            throw new ResourceNotFoundException(Device.class.getSimpleName() + " with id: " + deviceId);
        }

        Device device = deviceOptional.get();

        device.setUserId(null);

        deviceRepository.save(device);
        LOGGER.debug("Device with id {} was unassigned", deviceId);
    }

}
