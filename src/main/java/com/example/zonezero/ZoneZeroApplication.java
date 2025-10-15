package com.example.zonezero;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * The entry point for the ZoneZero backend application.
 *
 * <p>This class boots up the Spring context and launches an embedded
 * servlet container (Tomcat by default) that serves the REST API.
 * Running the main method will start the backend on the port
 * configured in {@code application.properties} (default 8080).</p>
 */
@SpringBootApplication
public class ZoneZeroApplication {

    public static void main(String[] args) {
        SpringApplication.run(ZoneZeroApplication.class, args);
    }
}