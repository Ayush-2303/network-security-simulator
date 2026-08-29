package com.network.slicing.network_security_simulator.allocation;

public class BandwidthAllocation {

    private String deviceId;
    private String slice;
    private int requestedBandwidth;
    private int allocatedBandwidth;

    public BandwidthAllocation(String deviceId, String slice,
                               int requestedBandwidth, int allocatedBandwidth) {
        this.deviceId = deviceId;
        this.slice = slice;
        this.requestedBandwidth = requestedBandwidth;
        this.allocatedBandwidth = allocatedBandwidth;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public String getSlice() {
        return slice;
    }

    public int getRequestedBandwidth() {
        return requestedBandwidth;
    }

    public int getAllocatedBandwidth() {
        return allocatedBandwidth;
    }

    public void setAllocatedBandwidth(int allocatedBandwidth) {
        this.allocatedBandwidth = allocatedBandwidth;
    }
}