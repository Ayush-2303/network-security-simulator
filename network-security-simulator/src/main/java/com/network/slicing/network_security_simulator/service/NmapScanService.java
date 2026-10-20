package com.network.slicing.network_security_simulator.service;

import com.network.slicing.network_security_simulator.model.Device;
import com.network.slicing.network_security_simulator.model.NetworkScan;
import com.network.slicing.network_security_simulator.repository.DeviceRepository;
import com.network.slicing.network_security_simulator.repository.NetworkScanRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class NmapScanService {
    private final DeviceRepository deviceRepository;
    private final NetworkScanRepository scanRepository;
    private final String nmapExecutable;

    public NmapScanService(DeviceRepository deviceRepository, NetworkScanRepository scanRepository,
                           @Value("${nmap.executable:nmap}") String nmapExecutable) {
        this.deviceRepository = deviceRepository;
        this.scanRepository = scanRepository;
        this.nmapExecutable = nmapExecutable;
    }

    public NetworkScan scanDevice(String deviceId) {
        Device device = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new IllegalArgumentException("Device not found: " + deviceId));
        String ip = device.getIpAddress();
        if (ip == null || ip.isBlank()) throw new IllegalArgumentException("Set a loopback IP address for device " + deviceId + " before scanning");
        if (!ip.equals("::1") && !ip.startsWith("127.")) {
            throw new IllegalArgumentException("For safety, this demo scans loopback targets only (127.0.0.0/8 or ::1)");
        }

        try {
            Process process = new ProcessBuilder(nmapExecutable, "-sV", "--version-light", "--host-timeout", "30s", "-oX", "-", ip)
                    .redirectErrorStream(true).start();
            boolean completed = process.waitFor(40, TimeUnit.SECONDS);
            if (!completed) {
                process.destroyForcibly();
                return save(deviceId, ip, "TIMEOUT", "UNKNOWN", "Nmap did not finish within 40 seconds");
            }
            String output = new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            if (process.exitValue() != 0) return save(deviceId, ip, "FAILED", "UNKNOWN", cleanError(output));
            List<String> ports = parseOpenPorts(output);
            String risk = riskFor(ports);
            String summary = ports.isEmpty() ? "No open ports detected" : String.join(", ", ports);
            return save(deviceId, ip, "COMPLETED", risk, summary);
        } catch (IOException exception) {
            return save(deviceId, ip, "UNAVAILABLE", "UNKNOWN", "Nmap is not installed or is not on PATH");
        } catch (Exception exception) {
            return save(deviceId, ip, "FAILED", "UNKNOWN", "Unable to parse Nmap result");
        }
    }

    public List<NetworkScan> recentScans() { return scanRepository.findTop20ByOrderByScannedAtDesc(); }

    private NetworkScan save(String deviceId, String ip, String status, String risk, String summary) {
        return scanRepository.save(new NetworkScan(deviceId, ip, status, risk, summary, LocalDateTime.now()));
    }

    private List<String> parseOpenPorts(String xml) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
        factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
        Document document = factory.newDocumentBuilder().parse(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));
        NodeList portNodes = document.getElementsByTagName("port");
        List<String> ports = new ArrayList<>();
        for (int i = 0; i < portNodes.getLength(); i++) {
            Element port = (Element) portNodes.item(i);
            Element state = (Element) port.getElementsByTagName("state").item(0);
            if (state != null && "open".equals(state.getAttribute("state"))) {
                NodeList services = port.getElementsByTagName("service");
                String service = services.getLength() == 0 ? "unknown" : ((Element) services.item(0)).getAttribute("name");
                ports.add(port.getAttribute("portid") + "/" + port.getAttribute("protocol") + " (" + service + ")");
            }
        }
        return ports;
    }

    private String riskFor(List<String> ports) {
        if (ports.stream().anyMatch(port -> port.startsWith("23/") || port.startsWith("445/") || port.startsWith("3389/"))) return "HIGH";
        return ports.isEmpty() ? "LOW" : "MEDIUM";
    }

    private String cleanError(String output) {
        String compact = output.replaceAll("\\s+", " ").trim();
        return compact.length() > 240 ? compact.substring(0, 240) : compact;
    }
}
