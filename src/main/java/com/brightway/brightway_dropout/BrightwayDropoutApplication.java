package com.brightway.brightway_dropout;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BrightwayDropoutApplication {

	public static void main(String[] args) {
		SpringApplication.run(BrightwayDropoutApplication.class, args);
	}

}
