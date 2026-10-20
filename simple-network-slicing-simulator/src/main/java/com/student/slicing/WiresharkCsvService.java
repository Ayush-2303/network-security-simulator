package com.student.slicing;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/** Reads a CSV exported by Wireshark: No., Time, Source, Destination, Protocol, Length, Info. */
@Service
public class WiresharkCsvService {
    private final DeviceStore deviceStore;
    private Map<String, Stats> latestStats = Map.of();
    public WiresharkCsvService(DeviceStore deviceStore) { this.deviceStore = deviceStore; }

    public synchronized CaptureAnalysis analyze(MultipartFile file) {
        if (file.isEmpty()) throw new IllegalArgumentException("Choose a Wireshark CSV file first");
        Map<String, Stats> stats = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            List<String> headers = csv(reader.readLine());
            int source = index(headers, "Source"), destination = index(headers, "Destination");
            int protocol = index(headers, "Protocol"), length = index(headers, "Length"), time = index(headers, "Time");
            if (source < 0 || destination < 0 || protocol < 0 || length < 0) throw new IllegalArgumentException("CSV must contain Source, Destination, Protocol, and Length columns");
            String line;
            while ((line = reader.readLine()) != null) {
                List<String> row = csv(line);
                if (row.size() <= Math.max(Math.max(source, destination), Math.max(protocol, length))) continue;
                int bytes = integer(row.get(length));
                double timestamp = time >= 0 && row.size() > time ? decimal(row.get(time)) : Double.NaN;
                for (String ip : List.of(row.get(source), row.get(destination))) {
                    Stats value = stats.computeIfAbsent(ip, ignored -> new Stats());
                    value.count++; value.bytes += bytes; value.protocols.merge(row.get(protocol), 1, Integer::sum);
                    value.recordTime(timestamp);
                }
            }
        } catch (IOException exception) { throw new IllegalArgumentException("Could not read the CSV file"); }
        latestStats = stats;
        return latest();
    }

    public synchronized CaptureAnalysis latest() {
        List<TrafficAnalysis> devices = deviceStore.all().stream()
                .map(device -> toAnalysis(device, latestStats.get(device.ipAddress())))
                .toList();
        List<CapturedHost> capturedHosts = latestStats.entrySet().stream()
                .filter(entry -> isPrivateOrLoopback(entry.getKey()))
                .sorted((left, right) -> Integer.compare(right.getValue().count, left.getValue().count))
                .limit(15)
                .map(entry -> new CapturedHost(entry.getKey(), entry.getValue().count,
                        entry.getValue().bytes, observedMbps(entry.getValue()), dominantProtocol(entry.getValue())))
                .toList();
        return new CaptureAnalysis(devices, capturedHosts);
    }

    private TrafficAnalysis toAnalysis(Device device, Stats stats) {
        if (stats == null) return new TrafficAnalysis(device.id(), device.name(), device.ipAddress(), 0, 0, 0, "None", device.slice(), "No packets for this device IP in the imported capture");
        String protocol = dominantProtocol(stats);
        String suggested = suggestSlice(stats);
        String reason = sliceReason(stats, suggested);
        return new TrafficAnalysis(device.id(), device.name(), device.ipAddress(), stats.count, stats.bytes, observedMbps(stats), protocol, suggested, reason);
    }

    public synchronized Device addFromCapture(DeviceStore.DeviceRequest request) {
        Stats stats = latestStats.get(request.ipAddress());
        if (stats == null) throw new IllegalArgumentException("Choose an IP address from the uploaded Wireshark capture");
        String slice = suggestSlice(stats);
        String type = trafficProfile(slice);
        return deviceStore.add(request.name(), request.ipAddress(), type, slice, maximumBandwidth(slice));
    }

    public synchronized ObservedTraffic observedTraffic(String ipAddress) {
        Stats stats = latestStats.get(ipAddress);
        return stats == null ? new ObservedTraffic(0, 0, 0, 0) : observedTraffic(stats);
    }

    private String suggestSlice(Stats stats) {
        ObservedTraffic traffic = observedTraffic(stats);
        // Thresholds are calibrated for a short classroom Wireshark capture, not a long carrier trace.
        if (traffic.mbps() >= 0.05 || stats.bytes >= 50_000) return "eMBB";
        if (traffic.packets() >= 10 && traffic.averagePacketBytes() <= 600) return "URLLC";
        return "mMTC";
    }
    private String trafficProfile(String slice) {
        return switch (slice) { case "URLLC" -> "Low-latency traffic pattern"; case "eMBB" -> "High-throughput traffic pattern"; default -> "Low-rate IoT traffic pattern"; };
    }
    private int maximumBandwidth(String slice) { return switch (slice) { case "URLLC" -> 40; case "eMBB" -> 70; default -> 20; }; }
    private String sliceReason(Stats stats, String slice) {
        ObservedTraffic traffic = observedTraffic(stats);
        return switch (slice) {
            case "eMBB" -> "High-volume pattern: " + stats.bytes + " bytes and " + traffic.mbps() + " Mbps";
            case "URLLC" -> "Frequent small-packet pattern: " + traffic.packets() + " packets with average size " + traffic.averagePacketBytes() + " bytes";
            default -> "Low-rate, low-frequency traffic: " + traffic.mbps() + " Mbps and " + traffic.packetsPerSecond() + " packets/second";
        };
    }
    private double observedMbps(Stats stats) {
        double seconds = stats.lastTime - stats.firstTime;
        if (!Double.isFinite(seconds) || seconds <= 0) return 0;
        return Math.round((stats.bytes * 8.0 / seconds / 1_000_000.0) * 1000.0) / 1000.0;
    }
    private ObservedTraffic observedTraffic(Stats stats) {
        double seconds = stats.lastTime - stats.firstTime;
        double packetsPerSecond = !Double.isFinite(seconds) || seconds <= 0 ? 0 : Math.round(stats.count / seconds * 1000.0) / 1000.0;
        double averagePacketBytes = stats.count == 0 ? 0 : Math.round(stats.bytes / (double) stats.count * 10.0) / 10.0;
        return new ObservedTraffic(stats.count, observedMbps(stats), packetsPerSecond, averagePacketBytes);
    }
    private String dominantProtocol(Stats stats) { return stats.protocols.entrySet().stream().max(Map.Entry.comparingByValue()).map(Map.Entry::getKey).orElse("Unknown"); }
    private boolean isPrivateOrLoopback(String ip) {
        if (ip.equals("::1") || ip.startsWith("127.") || ip.startsWith("10.") || ip.startsWith("192.168.")) return true;
        if (!ip.matches("^172\\.(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})$")) return false;
        int second = integer(ip.split("\\.")[1]);
        return second >= 16 && second <= 31;
    }
    private int index(List<String> headers, String name) { for (int i=0;i<headers.size();i++) if (headers.get(i).trim().equalsIgnoreCase(name)) return i; return -1; }
    private int integer(String value) { try { return Integer.parseInt(value.trim()); } catch (NumberFormatException ignored) { return 0; } }
    private double decimal(String value) { try { return Double.parseDouble(value.trim()); } catch (NumberFormatException ignored) { return Double.NaN; } }
    private List<String> csv(String line) {
        List<String> fields = new ArrayList<>(); if (line == null) return fields;
        StringBuilder field = new StringBuilder(); boolean quoted = false;
        for (int i=0;i<line.length();i++) { char c=line.charAt(i); if(c=='"') { quoted=!quoted; } else if(c==','&&!quoted) { fields.add(field.toString()); field.setLength(0); } else field.append(c); }
        fields.add(field.toString()); return fields;
    }
    public record ObservedTraffic(int packets, double mbps, double packetsPerSecond, double averagePacketBytes) { }
    private static class Stats {
        int count; long bytes; double firstTime = Double.POSITIVE_INFINITY; double lastTime = Double.NEGATIVE_INFINITY; Map<String,Integer> protocols = new HashMap<>();
        void recordTime(double time) { if (Double.isFinite(time)) { firstTime = Math.min(firstTime, time); lastTime = Math.max(lastTime, time); } }
    }
}
