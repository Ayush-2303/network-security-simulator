package com.network.slicing.network_security_simulator.repository;

import com.network.slicing.network_security_simulator.model.TrafficLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;

public interface TrafficLogRepository
        extends JpaRepository<TrafficLog, Long> {

    Optional<TrafficLog> findTopByDeviceIdOrderByTimestampDesc(String deviceId);
    List<TrafficLog> findTop5ByDeviceIdOrderByTimestampDesc(String deviceId);
}
