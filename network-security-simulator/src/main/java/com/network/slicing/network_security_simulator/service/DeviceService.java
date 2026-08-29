package com.network.slicing.network_security_simulator.service;

import com.network.slicing.network_security_simulator.model.Device;
import com.network.slicing.network_security_simulator.repository.DeviceRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DeviceService {

    private final DeviceRepository deviceRepository;

    public DeviceService(DeviceRepository deviceRepository) {
        this.deviceRepository = deviceRepository;
    }

    public List<Device> getAllDevices() {
        return deviceRepository.findAll();
    }

    public Device getDeviceById(String id) {
        return deviceRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Device not found: " + id));
    }

    public Device createDevice(Device device) {
        return deviceRepository.save(device);
    }

    public void deleteDevice(String id) {
        if (!deviceRepository.existsById(id)) throw new IllegalArgumentException("Device not found: " + id);
        deviceRepository.deleteById(id);
    }

    public Device updateDevice(String id, Device device) {
        if (!deviceRepository.existsById(id)) throw new IllegalArgumentException("Device not found: " + id);
        device.setId(id);
        return deviceRepository.save(device);
    }
}
