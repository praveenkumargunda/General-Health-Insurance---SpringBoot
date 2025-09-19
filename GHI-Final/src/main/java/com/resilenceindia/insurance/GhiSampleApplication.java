package com.resilenceindia.insurance;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
//import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@EnableScheduling
@SpringBootApplication
public class GhiSampleApplication {

	public static void main(String[] args) {
		SpringApplication.run(GhiSampleApplication.class, args);
	}

}
