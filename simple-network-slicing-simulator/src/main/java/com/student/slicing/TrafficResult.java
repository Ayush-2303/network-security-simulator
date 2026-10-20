package com.student.slicing;

public record TrafficResult(String deviceId, String name, String slice, int requestedMbps,
                            int allocatedMbps, double latencyMs, int packets, String status) { }
