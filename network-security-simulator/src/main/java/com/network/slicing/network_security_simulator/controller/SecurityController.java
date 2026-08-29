package com.network.slicing.network_security_simulator.controller;

import com.network.slicing.network_security_simulator.model.SecurityResponse;
import com.network.slicing.network_security_simulator.service.SecurityResponseService;
import org.springframework.web.bind.annotation.*;

import com.network.slicing.network_security_simulator.model.BlockedDevice;
import com.network.slicing.network_security_simulator.model.SecurityEvent;
import java.util.List;

@RestController
@RequestMapping("/security")
public class SecurityController {

    private final SecurityResponseService securityResponseService;

    public SecurityController(
            SecurityResponseService securityResponseService) {

        this.securityResponseService = securityResponseService;
    }

    @PostMapping("/respond/{deviceId}")
    public SecurityResponse respondToThreat(
            @PathVariable String deviceId) {

        return securityResponseService.respondToThreat(deviceId);
    }

    @GetMapping("/blocked")
    public List<BlockedDevice> getBlockedDevices() {

        return securityResponseService.getBlockedDevices();
    }

    @DeleteMapping("/blocked/{deviceId}")
    public void unblockDevice(@PathVariable String deviceId) {
        securityResponseService.unblockDevice(deviceId);
    }

    @GetMapping("/events")
    public List<SecurityEvent> getEvents() { return securityResponseService.getEvents(); }
}
