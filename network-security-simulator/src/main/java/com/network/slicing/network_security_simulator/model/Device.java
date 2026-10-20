package com.network.slicing.network_security_simulator.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Entity
@Table(name = "devices")
//class = blueprint
public class Device {

    @Id
    @NotBlank(message = "Device id is required")
    private String id;

    @NotBlank(message = "Device name is required")
    private String name;

    @NotBlank(message = "Device type is required")
    private String type;

    @Column(name = "slice_name")
    @Pattern(regexp = "eMBB|URLLC|mMTC", message = "Slice must be eMBB, URLLC, or mMTC")
    private String slice;

    @Min(value = 1, message = "Bandwidth must be at least 1 Mbps")
    @Max(value = 1000, message = "Bandwidth cannot exceed 1000 Mbps")
    private int bandwidth;

    @Column(name = "ip_address")
    @Pattern(regexp = "^$|^(?:(?:25[0-5]|2[0-4]\\d|1?\\d?\\d)\\.){3}(?:25[0-5]|2[0-4]\\d|1?\\d?\\d)$|^::1$",
            message = "IP address must be IPv4 or ::1")
    private String ipAddress;

    public Device() {
    }

    public Device(String id, String name, String type, String slice, int bandwidth) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.slice = slice;
        this.bandwidth = bandwidth;
    }

    public Device(String id, String name, String type, String slice, int bandwidth, String ipAddress) {
        this(id, name, type, slice, bandwidth);
        this.ipAddress = ipAddress;
    }

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) { this.name = name; }

    public String getType() {
        return type;
    }

    public void setType(String type) { this.type = type; }

    public String getSlice() {
        return slice;
    }

    public void setSlice(String slice) { this.slice = slice; }

    public int getBandwidth() {
        return bandwidth;
    }

    public void setBandwidth(int bandwidth) { this.bandwidth = bandwidth; }

    public String getIpAddress() { return ipAddress; }

    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }
}
