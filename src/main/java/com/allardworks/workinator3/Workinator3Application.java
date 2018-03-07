package com.allardworks.workinator3;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

@SpringBootApplication
public class Workinator3Application {
	public static void main(String[] args) {
		SpringApplication.run(Workinator3Application.class, args);
	}
}
