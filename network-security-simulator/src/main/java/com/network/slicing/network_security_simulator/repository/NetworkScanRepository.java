package com.network.slicing.network_security_simulator.repository;

import com.network.slicing.network_security_simulator.model.NetworkScan;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NetworkScanRepository extends JpaRepository<NetworkScan, Long> {
    List<NetworkScan> findTop20ByOrderByScannedAtDesc();
}
