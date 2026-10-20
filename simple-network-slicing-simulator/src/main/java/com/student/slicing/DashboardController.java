package com.student.slicing;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@RestController
public class DashboardController {
    private final NetworkService networkService;
    private final WiresharkCsvService wiresharkCsvService;

    public DashboardController(NetworkService networkService, WiresharkCsvService wiresharkCsvService) {
        this.networkService = networkService; this.wiresharkCsvService = wiresharkCsvService;
    }

    @GetMapping("/api/devices") public List<Device> devices() { return networkService.devices(); }
    @PostMapping("/api/devices") public Device addDevice(@RequestBody DeviceStore.DeviceRequest request) { return networkService.addDevice(request); }
    @PostMapping("/api/devices/{id}/slice") public Device moveSlice(@PathVariable String id, @RequestParam String slice) { return networkService.moveToSlice(id, slice); }
    @PostMapping("/api/simulate") public List<TrafficResult> simulate() { return networkService.simulateTraffic(); }
    @PostMapping(value = "/api/wireshark/analyze", consumes = "multipart/form-data")
    public CaptureAnalysis analyze(@RequestParam("file") MultipartFile file) { return wiresharkCsvService.analyze(file); }
    @GetMapping("/api/wireshark/latest")
    public CaptureAnalysis latestCapture() { return wiresharkCsvService.latest(); }
}
