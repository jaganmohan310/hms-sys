package com.pulsesquare.hms.ws;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.pulsesquare.hms")
public class PulseSquareHmsApplication {

    public static void main(String[] args) {
        SpringApplication.run(PulseSquareHmsApplication.class, args);
    }
}
