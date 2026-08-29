package com.network.slicing.network_security_simulator.service;

import com.network.slicing.network_security_simulator.model.AnomalyResult;
import com.network.slicing.network_security_simulator.model.Device;
import com.network.slicing.network_security_simulator.model.TrafficLog;
import com.network.slicing.network_security_simulator.repository.DeviceRepository;
import com.network.slicing.network_security_simulator.repository.TrafficLogRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AnomalyDetector {

    private final DeviceRepository deviceRepository;
    private final TrafficLogRepository trafficLogRepository;

    public AnomalyDetector(
            DeviceRepository deviceRepository,
            TrafficLogRepository trafficLogRepository) {

        this.deviceRepository = deviceRepository;
        this.trafficLogRepository = trafficLogRepository;
    }

    public List<AnomalyResult> detectAnomalies() {

        List<AnomalyResult> results = new ArrayList<>();

        List<Device> devices = deviceRepository.findAll();

        for (Device device : devices) {

            TrafficLog latestLog =
                    trafficLogRepository
                            .findTopByDeviceIdOrderByTimestampDesc(
                                    device.getId()
                            )
                            .orElse(null);

            if (latestLog == null) {
                continue;
            }

            int bandwidthUsed =
                    latestLog.getBandwidthUsed();

            double latency =
                    latestLog.getLatencyMs();

            int packetCount =
                    latestLog.getPacketCount();

            boolean highBandwidth =
                    bandwidthUsed >
                            device.getBandwidth() * 0.80;

            double latencyLimit = switch (device.getSlice()) {
                case "URLLC" -> 10;
                case "eMBB" -> 40;
                case "mMTC" -> 50;
                default -> 40;
            };
            boolean highLatency = latency > latencyLimit;

            double averagePackets = trafficLogRepository.findTop5ByDeviceIdOrderByTimestampDesc(device.getId()).stream()
                    .mapToInt(TrafficLog::getPacketCount).average().orElse(packetCount);
            boolean highPacketCount = packetCount > 900 || (averagePackets > 0 && packetCount > averagePackets * 1.75);

            boolean anomalous =
                    highBandwidth
                            || highLatency
                            || highPacketCount;

            String severity = "NORMAL";
            String reason = "Normal traffic";

            if (anomalous) {

                if (highBandwidth && highLatency) {
                    severity = "CRITICAL";
                    reason = "High bandwidth usage and high latency";

                } else if (highBandwidth) {
                    severity = "HIGH";
                    reason = "Unusually high bandwidth usage";

                } else if (highLatency) {
                    severity = "MEDIUM";
                    reason = "Latency exceeds " + latencyLimit + " ms threshold for " + device.getSlice();

                } else if (highPacketCount) {
                    severity = "MEDIUM";
                    reason = "Unusually high packet count";
                }
            }

            results.add(
                    new AnomalyResult(
                            device.getId(),
                            device.getName(),
                            device.getSlice(),
                            anomalous,
                            severity,
                            reason,
                            bandwidthUsed,
                            latency,
                            packetCount
                    )
            );
        }

        return results;
    }
}
