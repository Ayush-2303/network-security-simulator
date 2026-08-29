package com.network.slicing.network_security_simulator.repository;
import com.network.slicing.network_security_simulator.model.SecurityEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface SecurityEventRepository extends JpaRepository<SecurityEvent, Long> {
    List<SecurityEvent> findTop50ByOrderByCreatedAtDesc();
}
