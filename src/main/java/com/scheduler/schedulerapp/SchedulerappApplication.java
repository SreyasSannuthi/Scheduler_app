package com.scheduler.schedulerapp;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SchedulerappApplication {

	static {
		try {
			Dotenv dotenv = Dotenv.configure()
					.directory(".")
					.filename(".env")
					.ignoreIfMissing()
					.load();

			dotenv.entries().forEach(entry -> {
				if (System.getenv(entry.getKey()) == null) {
					System.setProperty(entry.getKey(), entry.getValue());
					System.out.println("Loaded: " + entry.getKey());
				}
			});

			System.out.println("Environment variables loaded from .env file");
		} catch (Exception e) {
			System.out.println("Could not load .env file: " + e.getMessage());
		}
	}

	public static void main(String[] args) {
		SpringApplication.run(SchedulerappApplication.class, args);
	}
}