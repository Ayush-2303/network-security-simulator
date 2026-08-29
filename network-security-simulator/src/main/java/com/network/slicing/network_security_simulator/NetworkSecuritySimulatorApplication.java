package com.network.slicing.network_security_simulator;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
@EnableScheduling
@SpringBootApplication
public class NetworkSecuritySimulatorApplication {

	public static void main(String[] args) {
		SpringApplication.run(NetworkSecuritySimulatorApplication.class, args);
	}

}
