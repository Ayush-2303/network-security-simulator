package com.network.slicing.network_security_simulator.model;

public class AnomalyResult {

    private String deviceId;
    private String deviceName;
    private String slice;
    private boolean anomalous;
    private String severity;
    private String reason;
    private int bandwidthUsed;
    private double latencyMs;
    private int packetCount;

    public AnomalyResult(
            String deviceId,
            String deviceName,
            String slice,
            boolean anomalous,
            String severity,
            String reason,
            int bandwidthUsed,
            double latencyMs,
            int packetCount) {

        this.deviceId = deviceId;
        this.deviceName = deviceName;
        this.slice = slice;
        this.anomalous = anomalous;
        this.severity = severity;
        this.reason = reason;
        this.bandwidthUsed = bandwidthUsed;
        this.latencyMs = latencyMs;
        this.packetCount = packetCount;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public String getSlice() {
        return slice;
    }

    public boolean isAnomalous() {
        return anomalous;
    }

    public String getSeverity() {
        return severity;
    }

    public String getReason() {
        return reason;
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
}