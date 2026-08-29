package com.network.slicing.network_security_simulator.controller;

import com.network.slicing.network_security_simulator.model.TrafficLog;
import com.network.slicing.network_security_simulator.service.TrafficLogService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/traffic")
public class TrafficLogController {

    private final TrafficLogService trafficLogService;

    public TrafficLogController(TrafficLogService trafficLogService) {
        this.trafficLogService = trafficLogService;
    }

    @PostMapping
    public TrafficLog createTrafficLog(@Valid @RequestBody TrafficLog trafficLog) {
        return trafficLogService.createTrafficLog(trafficLog);
    }

    @GetMapping
    public List<TrafficLog> getAllTrafficLogs() {
        return trafficLogService.getAllTrafficLogs();
    }
}
