package edu.cit.auditor.paluto;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class PalutoApplication {

	public static void main(String[] args) {
		SpringApplication.run(PalutoApplication.class, args);
	}

}
