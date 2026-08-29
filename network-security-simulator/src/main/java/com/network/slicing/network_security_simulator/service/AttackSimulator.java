package com.network.slicing.network_security_simulator.service;

import com.network.slicing.network_security_simulator.model.Device;
import com.network.slicing.network_security_simulator.model.TrafficLog;
import com.network.slicing.network_security_simulator.repository.DeviceRepository;
import com.network.slicing.network_security_simulator.repository.TrafficLogRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AttackSimulator {

    private final DeviceRepository deviceRepository;
    private final TrafficLogRepository trafficLogRepository;

    public AttackSimulator(
            DeviceRepository deviceRepository,
            TrafficLogRepository trafficLogRepository) {

        this.deviceRepository = deviceRepository;
        this.trafficLogRepository = trafficLogRepository;
    }

    public TrafficLog simulateAttack(String deviceId) {

        Device device = deviceRepository
                .findById(deviceId)
                .orElseThrow(() -> new IllegalArgumentException("Device not found: " + deviceId));

        int attackBandwidth = device.getBandwidth();

        int attackPacketCount = 1500;

        double attackLatency = 80.0;

        TrafficLog attackLog = new TrafficLog(
                device.getId(),
                attackBandwidth,
                attackLatency,
                attackPacketCount,
                "ATTACK",
                LocalDateTime.now()
        );

        return trafficLogRepository.save(attackLog);
    }
}
