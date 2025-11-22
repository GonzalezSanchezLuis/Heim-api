package com.heim.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;


@SpringBootApplication(scanBasePackages = {"com.heim.api","com.heim.api.exceptions","com.heim.api.admin"})
//@EnableAsync
public class HeimApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(HeimApiApplication.class, args);
	}

}
