package com.network.slicing.network_security_simulator.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

@Entity
@Table(name = "traffic_logs")
public class TrafficLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "log_id")
    private Long logId;

    @Column(name = "device_id")
    @NotBlank(message = "Device id is required")
    private String deviceId;

    @Column(name = "bandwidth_used")
    @Min(value = 0, message = "Bandwidth usage cannot be negative")
    private int bandwidthUsed;

    @Column(name = "latency_ms")
    @Min(value = 0, message = "Latency cannot be negative")
    private double latencyMs;

    @Column(name = "packet_count")
    @Min(value = 0, message = "Packet count cannot be negative")
    private int packetCount;

    private String status;

    private LocalDateTime timestamp;

    public TrafficLog() {
    }

    public TrafficLog(String deviceId, int bandwidthUsed,
                      double latencyMs, int packetCount,
                      String status, LocalDateTime timestamp) {

        this.deviceId = deviceId;
        this.bandwidthUsed = bandwidthUsed;
        this.latencyMs = latencyMs;
        this.packetCount = packetCount;
        this.status = status;
        this.timestamp = timestamp;
    }

    public Long getLogId() {
        return logId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public int getBandwidthUsed() {
        return bandwidthUsed;
    }

    public double getLatencyMs() {
        return latencyMs;
    }

    public int getPacketCount() {
        return packetCount;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
