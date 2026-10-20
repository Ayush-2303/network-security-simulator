package com.student.slicing;

public record TrafficAnalysis(String deviceId, String deviceName, String ipAddress, int packetCount,
                              long totalBytes, double observedMbps, String dominantProtocol, String suggestedSlice,
                              String reason) { }
