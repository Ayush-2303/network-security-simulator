package com.network.slicing.network_security_simulator.controller;

import com.network.slicing.network_security_simulator.model.NetworkScan;
import com.network.slicing.network_security_simulator.service.NmapScanService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/network-scans")
public class NetworkScanController {
    private final NmapScanService nmapScanService;
    public NetworkScanController(NmapScanService nmapScanService) { this.nmapScanService = nmapScanService; }
    @PostMapping("/{deviceId}")
    public NetworkScan scan(@PathVariable String deviceId) { return nmapScanService.scanDevice(deviceId); }
    @GetMapping
    public List<NetworkScan> history() { return nmapScanService.recentScans(); }
}
