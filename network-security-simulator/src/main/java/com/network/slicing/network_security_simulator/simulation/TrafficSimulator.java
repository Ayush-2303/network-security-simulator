package com.network.slicing.network_security_simulator.simulation;

import com.network.slicing.network_security_simulator.model.Device;
import com.network.slicing.network_security_simulator.model.TrafficLog;
import com.network.slicing.network_security_simulator.repository.DeviceRepository;
import com.network.slicing.network_security_simulator.repository.TrafficLogRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Component
public class TrafficSimulator {

    private final DeviceRepository deviceRepository;
    private final TrafficLogRepository trafficLogRepository;

    private final Random random = new Random();

    public TrafficSimulator(DeviceRepository deviceRepository,
                            TrafficLogRepository trafficLogRepository) {
        this.deviceRepository = deviceRepository;
        this.trafficLogRepository = trafficLogRepository;
    }
    @Scheduled(fixedRate = 5000)
    public void generateTraffic() {

        List<Device> devices = deviceRepository.findAll();

        for (Device device : devices) {

            int bandwidthUsed;
            double latencyMs;
            int packetCount;

            switch (device.getSlice()) {

                case "eMBB":
                    bandwidthUsed = random.nextInt(device.getBandwidth() + 1);
                    latencyMs = 10 + random.nextDouble() * 30;
                    packetCount = 500 + random.nextInt(1001);
                    break;

                case "URLLC":
                    bandwidthUsed = random.nextInt(device.getBandwidth() + 1);
                    latencyMs = 1 + random.nextDouble() * 9;
                    packetCount = 200 + random.nextInt(801);
                    break;

                case "mMTC":
                    bandwidthUsed = random.nextInt(device.getBandwidth() + 1);
                    latencyMs = 20 + random.nextDouble() * 30;
                    packetCount = 50 + random.nextInt(201);
                    break;

                default:
                    bandwidthUsed = random.nextInt(device.getBandwidth() + 1);
                    latencyMs = 5 + random.nextDouble() * 45;
                    packetCount = random.nextInt(1001);
            }

            TrafficLog trafficLog = new TrafficLog(
                    device.getId(),
                    bandwidthUsed,
                    latencyMs,
                    packetCount,
                    "NORMAL",
                    LocalDateTime.now()
            );

            trafficLogRepository.save(trafficLog);
        }
    }


}