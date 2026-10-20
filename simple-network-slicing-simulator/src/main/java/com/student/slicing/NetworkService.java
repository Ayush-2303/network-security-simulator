package com.student.slicing;

import org.springframework.stereotype.Service;
import java.util.Comparator;
import java.util.List;

@Service
public class NetworkService {
    private static final int TOTAL_BANDWIDTH = 100;
    private final DeviceStore deviceStore;
    private final WiresharkCsvService wiresharkCsvService;

    public NetworkService(DeviceStore deviceStore, WiresharkCsvService wiresharkCsvService) { this.deviceStore = deviceStore; this.wiresharkCsvService = wiresharkCsvService; }

    public List<TrafficResult> simulateTraffic() {
        List<MutableTraffic> traffic = deviceStore.all().stream().map(this::createTraffic).sorted(Comparator.comparingInt(item -> priority(item.device.slice()))).toList();
        int remaining = TOTAL_BANDWIDTH;
        for (MutableTraffic item : traffic) {
            item.allocated = Math.min(item.requested, remaining);
            remaining -= item.allocated;
        }
        return traffic.stream().map(item -> new TrafficResult(item.device.id(), item.device.name(), item.device.slice(), item.requested,
                item.allocated, item.latency, item.packets, item.allocated < item.requested ? "LIMITED" : "NORMAL")).toList();
    }

    public List<Device> devices() { return deviceStore.all(); }
    public Device addDevice(DeviceStore.DeviceRequest request) { return wiresharkCsvService.addFromCapture(request); }
    public Device moveToSlice(String id, String slice) { return deviceStore.moveToSlice(id, slice); }

    private MutableTraffic createTraffic(Device device) {
        WiresharkCsvService.ObservedTraffic observed = wiresharkCsvService.observedTraffic(device.ipAddress());
        int requested = estimateDemand(device, observed);
        double latency = switch (device.slice()) {
            case "URLLC" -> 5;
            case "eMBB" -> 25;
            default -> 50;
        };
        return new MutableTraffic(device, Math.min(requested, device.maximumBandwidth()), latency, observed.packets());
    }

    private int priority(String slice) { return switch (slice) { case "URLLC" -> 1; case "eMBB" -> 2; default -> 3; }; }

    /**
     * A transparent estimate rather than a claim that Wireshark knows 5G requirements.
     * The packet-capture intensity adjusts a baseline appropriate to the assigned slice.
     */
    private int estimateDemand(Device device, WiresharkCsvService.ObservedTraffic observed) {
        int packetScore = Math.min(10, (int) Math.ceil(observed.packets() / 50.0));
        int rateScore = Math.min(10, (int) Math.ceil(observed.mbps() * 5));
        int trafficScore = Math.max(packetScore, rateScore);
        int baseline = switch (device.slice()) { case "URLLC" -> 10; case "eMBB" -> 25; default -> 2; };
        int estimate = baseline + trafficScore;
        return Math.min(estimate, device.maximumBandwidth());
    }

    private static class MutableTraffic {
        private final Device device; private final int requested; private final double latency; private final int packets; private int allocated;
        private MutableTraffic(Device device, int requested, double latency, int packets) { this.device = device; this.requested = requested; this.latency = latency; this.packets = packets; }
    }
}
