package com.network.slicing.network_security_simulator.service;

import com.network.slicing.network_security_simulator.model.TrafficLog;
import com.network.slicing.network_security_simulator.repository.TrafficLogRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TrafficLogService {

    private final TrafficLogRepository trafficLogRepository;

    public TrafficLogService(TrafficLogRepository trafficLogRepository) {
        this.trafficLogRepository = trafficLogRepository;
    }

    public TrafficLog createTrafficLog(TrafficLog trafficLog) {
        return trafficLogRepository.save(trafficLog);
    }

    public List<TrafficLog> getAllTrafficLogs() {
        return trafficLogRepository.findAll();
    }

    public TrafficLog getLatestTrafficLog(String deviceId) {

        return trafficLogRepository
                .findTopByDeviceIdOrderByTimestampDesc(deviceId)
                .orElse(null);
    }
}