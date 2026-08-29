package com.network.slicing.network_security_simulator.repository;

import com.network.slicing.network_security_simulator.model.Device;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeviceRepository extends JpaRepository<Device, String> {

}