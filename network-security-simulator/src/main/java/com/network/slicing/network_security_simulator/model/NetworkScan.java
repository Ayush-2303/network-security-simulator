package com.network.slicing.network_security_simulator.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "network_scans")
public class NetworkScan {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String deviceId;
    private String targetIp;
    private String status;
    private String riskLevel;
    private String summary;
    private LocalDateTime scannedAt;

    public NetworkScan() { }
    public NetworkScan(String deviceId, String targetIp, String status, String riskLevel, String summary, LocalDateTime scannedAt) {
        this.deviceId = deviceId; this.targetIp = targetIp; this.status = status;
        this.riskLevel = riskLevel; this.summary = summary; this.scannedAt = scannedAt;
    }
    public Long getId() { return id; }
    public String getDeviceId() { return deviceId; }
    public String getTargetIp() { return targetIp; }
    public String getStatus() { return status; }
    public String getRiskLevel() { return riskLevel; }
    public String getSummary() { return summary; }
    public LocalDateTime getScannedAt() { return scannedAt; }
}
