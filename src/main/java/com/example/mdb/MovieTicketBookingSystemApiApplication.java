package com.example.mdb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "auditorAwareImpl")
@EnableScheduling
public class MovieTicketBookingSystemApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(MovieTicketBookingSystemApiApplication.class, args);
	}
}
