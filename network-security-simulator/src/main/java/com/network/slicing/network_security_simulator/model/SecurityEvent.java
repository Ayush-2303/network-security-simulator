package com.network.slicing.network_security_simulator.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "security_events")
public class SecurityEvent {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String deviceId;
    private String action;
    private String reason;
    private String severity;
    private LocalDateTime createdAt;
    public SecurityEvent() { }
    public SecurityEvent(String deviceId, String action, String reason, String severity, LocalDateTime createdAt) {
        this.deviceId = deviceId; this.action = action; this.reason = reason; this.severity = severity; this.createdAt = createdAt;
    }
    public Long getId() { return id; }
    public String getDeviceId() { return deviceId; }
    public String getAction() { return action; }
    public String getReason() { return reason; }
    public String getSeverity() { return severity; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
