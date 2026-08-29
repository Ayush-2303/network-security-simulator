package com.network.slicing.network_security_simulator.controller;

import com.network.slicing.network_security_simulator.model.TrafficLog;
import com.network.slicing.network_security_simulator.service.AttackSimulator;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/attack")
public class AttackController {

    private final AttackSimulator attackSimulator;

    public AttackController(AttackSimulator attackSimulator) {
        this.attackSimulator = attackSimulator;
    }

    @PostMapping("/{deviceId}")
    public TrafficLog simulateAttack(
            @PathVariable String deviceId) {

        return attackSimulator.simulateAttack(deviceId);
    }
}