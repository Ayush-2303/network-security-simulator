package com.student.slicing;

import java.util.List;

public record CaptureAnalysis(List<TrafficAnalysis> devices, List<CapturedHost> capturedHosts) { }
