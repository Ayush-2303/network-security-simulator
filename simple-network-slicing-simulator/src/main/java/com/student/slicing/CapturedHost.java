package com.student.slicing;

public record CapturedHost(String ipAddress, int packetCount, long totalBytes, double observedMbps, String dominantProtocol) { }
