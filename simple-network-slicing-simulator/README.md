# 5G Network Slicing with Wireshark Traffic Analysis

An intentionally simple Java/Spring Boot project for **Network Slicing with Intelligent Traffic Management and Network Security**.

## Current 60% milestone

- Manual device creation using only a device name and an IP selected from the capture. The project starts with no example devices.
- Wireshark CSV import, captured-local-IP list, and real packet/byte/protocol analysis.
- Automatic traffic-based assignment, calibrated for a short classroom capture: throughput of at least 0.05 Mbps or 50,000 bytes → eMBB; at least 10 small packets → URLLC candidate; other low-rate traffic → mMTC. The device name is only a label.
- Observed Mbps is calculated from Wireshark packet bytes and timestamps.
- A transparent predicted-demand model adjusts a slice baseline using observed packet count and Mbps, then allocates the 100 Mbps 5G resource pool in URLLC → eMBB → mMTC priority order. It has no random input.

## How to demonstrate Wireshark

1. Open Wireshark and choose the active network adapter.
2. Start capture.
3. Generate a little traffic: open a website or run the Java dashboard.
4. Stop capture.
5. Choose **File → Export Packet Dissections → As CSV**.
6. Upload that CSV in this dashboard. It displays the private or loopback IP addresses found in the capture.
7. Click **Use this IP**, enter any device label such as `1`, `2`, or `3`, and click **Classify from Traffic**. The project assigns the slice using capture measurements, not the name.

The app matches Wireshark's Source/Destination values to the device IP. If you add `127.0.0.1` but the capture contains your Wi-Fi address (for example `192.168.1.5`), the result would correctly be zero; use the address shown by the captured-IP list instead.

## Future work (remaining 40%)

- Detect abnormal behaviour from unusually high packet count, traffic volume, or unexpected protocol.
- Add alert messages and a quarantine/block action for suspicious devices.
- Store device and capture-analysis history in a database.

## Viva explanation

> Wireshark is the external networking tool integrated with my project. It captures real packets. I export the packet capture as CSV and upload it to the dashboard. The program analyzes the real packet count, traffic volume, protocol, IP address, and observed Mbps. The device name is only a label. The program assigns eMBB to high-throughput traffic, identifies a URLLC candidate from frequent small packets, and assigns mMTC to remaining low-rate traffic. A transparent prediction model uses the observed packet count and Mbps to adjust the baseline demand for that slice. The bandwidth manager then allocates the simulated 100 Mbps 5G pool in priority order: URLLC, then eMBB, then mMTC. Abnormal-behaviour detection is planned as the next phase.

## Run

### IntelliJ IDEA

Open this `simple-network-slicing-simulator` folder as a Maven project, and run `SimpleNetworkSlicingApplication.java` using Java 21 or newer.

### PowerShell

```powershell
$env:JAVA_HOME='C:\Program Files\Java\jdk-24'
$env:Path="$env:JAVA_HOME\bin;$env:Path"
..\network-security-simulator\mvnw.cmd -f pom.xml spring-boot:run
```

Open `http://localhost:8080`.

## Key code to understand

- `DeviceStore.java`: manual device data.
- `WiresharkCsvService.java`: reads Wireshark CSV and applies simple rules.
- `NetworkService.java`: generates simulated traffic and allocates the 100 Mbps pool.
- `DashboardController.java`: API endpoints connecting the dashboard to Java code.
