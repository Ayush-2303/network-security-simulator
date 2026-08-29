package com.network.slicing.network_security_simulator.repository;
import com.network.slicing.network_security_simulator.model.BlockedDevice;
import org.springframework.data.jpa.repository.JpaRepository;
public interface BlockedDeviceRepository extends JpaRepository<BlockedDevice, String> { }
