package com.network.slicing.network_security_simulator.model;

public class SecurityResponse {

    private String deviceId;
    private String action;
    private String reason;
    private String severity;

    public SecurityResponse(
            String deviceId,
            String action,
            String reason,
            String severity) {

        this.deviceId = deviceId;
        this.action = action;
        this.reason = reason;
        this.severity = severity;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public String getAction() {
        return action;
    }

    public String getReason() {
        return reason;
    }

    public String getSeverity() {
        return severity;
    }
}