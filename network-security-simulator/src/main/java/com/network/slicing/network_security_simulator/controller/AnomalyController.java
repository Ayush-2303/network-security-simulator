package com.network.slicing.network_security_simulator.controller;

import com.network.slicing.network_security_simulator.model.AnomalyResult;
import com.network.slicing.network_security_simulator.service.AnomalyDetector;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class AnomalyController {

    private final AnomalyDetector anomalyDetector;

    public AnomalyController(AnomalyDetector anomalyDetector) {
        this.anomalyDetector = anomalyDetector;
    }

    @GetMapping("/anomalies")
    public List<AnomalyResult> getAnomalies() {

        return anomalyDetector.detectAnomalies();
    }
}