package com.network.slicing.network_security_simulator.allocation;

import com.network.slicing.network_security_simulator.model.Device;
import com.network.slicing.network_security_simulator.model.TrafficLog;
import com.network.slicing.network_security_simulator.service.SecurityResponseService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class BandwidthManager {

    private final int totalBandwidth = 100;
    private final int minimumBandwidth = 10;

    private final SecurityResponseService securityResponseService;

    public BandwidthManager(
            SecurityResponseService securityResponseService) {

        this.securityResponseService = securityResponseService;
    }

    public List<BandwidthAllocation> allocateBandwidth(
            List<Device> devices,
            List<TrafficLog> trafficLogs) {

        List<BandwidthAllocation> allocations = new ArrayList<>();

        if (devices.isEmpty()) {
            return allocations;
        }

        int remainingBandwidth = totalBandwidth;

        // STEP 1: reserve a fair baseline without exceeding the 100 Mbps pool
        long eligibleDevices = devices.stream().filter(device -> !securityResponseService.isBlocked(device.getId())).count();
        int fairMinimum = eligibleDevices == 0 ? 0 : Math.min(minimumBandwidth, totalBandwidth / (int) eligibleDevices);
        for (Device device : devices) {

            // Blocked devices receive zero bandwidth
            if (securityResponseService.isBlocked(device.getId())) {

                allocations.add(
                        new BandwidthAllocation(
                                device.getId(),
                                device.getSlice(),
                                0,
                                0
                        )
                );

                continue;
            }

            TrafficLog latestLog =
                    findLatestLog(
                            device.getId(),
                            trafficLogs
                    );

            int requestedBandwidth = 0;

            if (latestLog != null) {
                requestedBandwidth =
                        latestLog.getBandwidthUsed();
            }

            int allocatedBandwidth = Math.min(Math.min(requestedBandwidth, device.getBandwidth()), fairMinimum);

            BandwidthAllocation allocation =
                    new BandwidthAllocation(
                            device.getId(),
                            device.getSlice(),
                            requestedBandwidth,
                            allocatedBandwidth
                    );

            allocations.add(allocation);

            remainingBandwidth -= allocatedBandwidth;
        }

        // STEP 2: Highest priority first
        allocations.sort(
                Comparator.comparingInt(
                        allocation ->
                                getPriority(
                                        allocation.getSlice()
                                )
                )
        );

        // STEP 3: Allocate remaining bandwidth
        for (BandwidthAllocation allocation : allocations) {

            if (remainingBandwidth <= 0) {
                break;
            }

            if (securityResponseService.isBlocked(
                    allocation.getDeviceId())) {

                continue;
            }

            Device device = devices.stream().filter(item -> item.getId().equals(allocation.getDeviceId())).findFirst().orElse(null);
            int deviceLimit = device == null ? 0 : device.getBandwidth();
            int remainingDemand = Math.min(allocation.getRequestedBandwidth(), deviceLimit) - allocation.getAllocatedBandwidth();

            if (remainingDemand <= 0) {
                continue;
            }

            int allocationAmount =
                    Math.min(
                            remainingDemand,
                            remainingBandwidth
                    );

            allocation.setAllocatedBandwidth(
                    allocation.getAllocatedBandwidth()
                            + allocationAmount
            );

            remainingBandwidth -= allocationAmount;
        }

        return allocations;
    }

    private TrafficLog findLatestLog(
            String deviceId,
            List<TrafficLog> trafficLogs) {

        TrafficLog latestLog = null;

        for (TrafficLog trafficLog : trafficLogs) {

            if (!trafficLog.getDeviceId().equals(deviceId)) {
                continue;
            }

            if (latestLog == null) {
                latestLog = trafficLog;
            } else if (
                    trafficLog.getTimestamp() != null
                            && latestLog.getTimestamp() != null
                            && trafficLog.getTimestamp()
                            .isAfter(latestLog.getTimestamp())
            ) {
                latestLog = trafficLog;
            }
        }

        return latestLog;
    }

    private int getPriority(String slice) {

        switch (slice) {

            case "URLLC":
                return 1;

            case "eMBB":
                return 2;

            case "mMTC":
                return 3;

            default:
                return 4;
        }
    }
}
