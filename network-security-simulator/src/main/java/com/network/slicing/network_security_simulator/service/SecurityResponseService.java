package com.network.slicing.network_security_simulator.service;

import com.network.slicing.network_security_simulator.model.AnomalyResult;
import com.network.slicing.network_security_simulator.model.SecurityResponse;
import org.springframework.stereotype.Service;

import com.network.slicing.network_security_simulator.model.BlockedDevice;
import com.network.slicing.network_security_simulator.model.SecurityEvent;
import com.network.slicing.network_security_simulator.repository.BlockedDeviceRepository;
import com.network.slicing.network_security_simulator.repository.SecurityEventRepository;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class SecurityResponseService {

    private final AnomalyDetector anomalyDetector;

    private final BlockedDeviceRepository blockedDeviceRepository;
    private final SecurityEventRepository securityEventRepository;

    public SecurityResponseService(AnomalyDetector anomalyDetector, BlockedDeviceRepository blockedDeviceRepository,
                                   SecurityEventRepository securityEventRepository) {
        this.anomalyDetector = anomalyDetector;
        this.blockedDeviceRepository = blockedDeviceRepository;
        this.securityEventRepository = securityEventRepository;
    }

    public SecurityResponse respondToThreat(String deviceId) {

        List<AnomalyResult> anomalies =
                anomalyDetector.detectAnomalies();

        for (AnomalyResult anomaly : anomalies) {

            if (!anomaly.getDeviceId().equals(deviceId)) {
                continue;
            }

            if (!anomaly.isAnomalous()) {

                return record(deviceId, "NO_ACTION", "Traffic is normal", anomaly.getSeverity());
            }

            blockedDeviceRepository.save(new BlockedDevice(deviceId, anomaly.getReason(), anomaly.getSeverity(), LocalDateTime.now()));

            return record(deviceId, "BLOCK_DEVICE", anomaly.getReason(), anomaly.getSeverity());
        }

        return record(deviceId, "NO_ACTION", "No traffic data found", "UNKNOWN");
    }

    public boolean isBlocked(String deviceId) {
        return blockedDeviceRepository.existsById(deviceId);
    }

    public List<BlockedDevice> getBlockedDevices() {
        return blockedDeviceRepository.findAll();
    }

    public void unblockDevice(String deviceId) {
        if (!blockedDeviceRepository.existsById(deviceId)) {
            throw new IllegalArgumentException("Device is not blocked: " + deviceId);
        }
        blockedDeviceRepository.deleteById(deviceId);
        record(deviceId, "UNBLOCK_DEVICE", "Device manually unblocked", "INFO");
    }

    public List<SecurityEvent> getEvents() {
        return securityEventRepository.findTop50ByOrderByCreatedAtDesc();
    }

    private SecurityResponse record(String deviceId, String action, String reason, String severity) {
        securityEventRepository.save(new SecurityEvent(deviceId, action, reason, severity, LocalDateTime.now()));
        return new SecurityResponse(deviceId, action, reason, severity);
    }
}
