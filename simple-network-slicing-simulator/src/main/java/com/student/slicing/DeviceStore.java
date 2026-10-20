package com.student.slicing;

import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class DeviceStore {
    private final AtomicInteger counter = new AtomicInteger(4);
    private final List<Device> devices = new ArrayList<>();

    public synchronized List<Device> all() { return List.copyOf(devices); }
    public synchronized Device add(String name, String ipAddress, String type, String slice, int maximumBandwidth) {
        validate(ipAddress, slice, maximumBandwidth);
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Enter a device name");
        Device device = new Device("D" + counter.getAndIncrement(), name.trim(), type, ipAddress, slice, maximumBandwidth);
        devices.add(device);
        return device;
    }
    public synchronized Device moveToSlice(String id, String slice) {
        validate("127.0.0.1", slice, 1);
        for (int i = 0; i < devices.size(); i++) {
            Device old = devices.get(i);
            if (old.id().equals(id)) {
                Device updated = new Device(old.id(), old.name(), old.type(), old.ipAddress(), slice, old.maximumBandwidth());
                devices.set(i, updated);
                return updated;
            }
        }
        throw new IllegalArgumentException("Device not found: " + id);
    }
    public synchronized Device get(String id) { return devices.stream().filter(device -> device.id().equals(id)).findFirst().orElseThrow(() -> new IllegalArgumentException("Device not found: " + id)); }

    private void validate(String ip, String slice, int bandwidth) {
        boolean private172 = ip != null && ip.matches("^172\\.(1[6-9]|2[0-9]|3[01])\\.\\d{1,3}\\.\\d{1,3}$");
        if (ip == null || !(ip.equals("::1") || ip.startsWith("127.") || ip.startsWith("192.168.") || ip.startsWith("10.") || private172)) throw new IllegalArgumentException("Use a private or loopback IP address only");
        if (!("URLLC".equals(slice) || "eMBB".equals(slice) || "mMTC".equals(slice))) throw new IllegalArgumentException("Slice must be URLLC, eMBB, or mMTC");
        if (bandwidth < 1 || bandwidth > 100) throw new IllegalArgumentException("Bandwidth must be between 1 and 100 Mbps");
    }

    public record DeviceRequest(String name, String ipAddress) { }
}
