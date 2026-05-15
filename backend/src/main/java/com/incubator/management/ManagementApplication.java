package com.incubator.management;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main Application Class
 * ---------------------
 * This is the entry point of the Startup Incubator Management System.
 * It initializes and runs the Spring Boot application.
 */
@SpringBootApplication
public class ManagementApplication {

	public static void main(String[] args) {
		// Launch the Spring Boot application
		SpringApplication.run(ManagementApplication.class, args);
	}

}
