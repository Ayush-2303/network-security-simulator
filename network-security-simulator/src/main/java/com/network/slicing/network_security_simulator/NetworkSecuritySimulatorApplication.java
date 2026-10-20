package com.network.slicing.network_security_simulator;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
@EnableScheduling
@SpringBootApplication
//entry point of your application
public class NetworkSecuritySimulatorApplication {

	public static void main(String[] args) {

		//srping run starts the SpringBoot application
		SpringApplication.run(NetworkSecuritySimulatorApplication.class, args);
		//Spring scans for @Restcontroller, Service, repo etc
	}

}
