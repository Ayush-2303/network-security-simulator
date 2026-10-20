# 5G Network Security Simulator

An educational Spring Boot dashboard that simulates 5G device traffic, detects anomalies, assigns a constrained 100 Mbps bandwidth pool, and records automated security responses.

## Run

Use Java 21 and MySQL. Create a database named `network_security_db`, then set credentials without committing them:

```powershell
$env:DB_URL='jdbc:mysql://localhost:3306/network_security_db'
$env:DB_USERNAME='root'
$env:DB_PASSWORD='your-password'
$env:APP_API_KEY='choose-a-long-random-secret' # optional locally; required outside local demos
.\mvnw.cmd spring-boot:run
```

Open `http://localhost:8080`. Hibernate creates `blocked_devices` and `security_events` automatically. Existing `devices` and `traffic_logs` tables are retained.

## Main API

- `GET /devices`, `POST /devices`, `PUT /devices/{id}`, `DELETE /devices/{id}`
- `GET /traffic`, `POST /traffic`; `GET /bandwidth`; `GET /anomalies`
- `POST /attack/{deviceId}` and `POST /security/respond/{deviceId}`
- `GET /security/blocked`, `DELETE /security/blocked/{deviceId}`, `GET /security/events`

## Nmap network scan feature

Install [Nmap](https://nmap.org/download.html) and make sure the `nmap` command is available on your PATH. The dashboard's **Nmap Network Scans** section then lets you scan a configured device.

For a safe classroom demo, this application only permits loopback targets: `127.x.x.x` or `::1`. Add an `ipAddress` field to a device request, for example:

```json
{
  "id": "sensor-01",
  "name": "Temperature Sensor",
  "type": "IoT Sensor",
  "slice": "mMTC",
  "bandwidth": 20,
  "ipAddress": "127.0.0.1"
}
```

Use `POST /network-scans/{deviceId}` to start a scan and `GET /network-scans` to view the latest 20 results. Each result includes detected open services and a basic LOW/MEDIUM/HIGH risk rating.

This is a simulator, not a production security control plane. Put it behind authentication and TLS before exposing it to a network.

When `APP_API_KEY` is set, send it as `X-API-Key` for all state-changing API calls. The built-in dashboard is intended for a local demo; use a reverse proxy/identity provider for real user authentication.
