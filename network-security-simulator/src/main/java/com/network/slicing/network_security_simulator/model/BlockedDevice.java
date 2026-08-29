package com.network.slicing.network_security_simulator.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "blocked_devices")
public class BlockedDevice {
    @Id
    private String deviceId;
    private String reason;
    private String severity;
    private LocalDateTime blockedAt;

    public BlockedDevice() { }
    public BlockedDevice(String deviceId, String reason, String severity, LocalDateTime blockedAt) {
        this.deviceId = deviceId; this.reason = reason; this.severity = severity; this.blockedAt = blockedAt;
    }
    public String getDeviceId() { return deviceId; }
    public String getReason() { return reason; }
    public String getSeverity() { return severity; }
    public LocalDateTime getBlockedAt() { return blockedAt; }
}
