package com.network.slicing.network_security_simulator.controller;

import com.network.slicing.network_security_simulator.allocation.BandwidthAllocation;
import com.network.slicing.network_security_simulator.allocation.BandwidthManager;
import com.network.slicing.network_security_simulator.model.Device;
import com.network.slicing.network_security_simulator.model.TrafficLog;
import com.network.slicing.network_security_simulator.repository.DeviceRepository;
import com.network.slicing.network_security_simulator.repository.TrafficLogRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class BandwidthController {

    private final BandwidthManager bandwidthManager;
    private final DeviceRepository deviceRepository;
    private final TrafficLogRepository trafficLogRepository;

    public BandwidthController(
            BandwidthManager bandwidthManager,
            DeviceRepository deviceRepository,
            TrafficLogRepository trafficLogRepository) {

        this.bandwidthManager = bandwidthManager;
        this.deviceRepository = deviceRepository;
        this.trafficLogRepository = trafficLogRepository;
    }

    @GetMapping("/bandwidth")
    public List<BandwidthAllocation> getBandwidthAllocations() {

        List<Device> devices = deviceRepository.findAll();

        List<TrafficLog> trafficLogs =
                trafficLogRepository.findAll();

        return bandwidthManager.allocateBandwidth(
                devices,
                trafficLogs
        );
    }
}