package com.learnreactivespring;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class LearnReactivespringApplication {

	public static void main(String[] args) {
		SpringApplication.run(LearnReactivespringApplication.class, args);
	}

	@Bean
	ApplicationRunner commandLineRunner(){
		return new ApplicationRunner() {
			@Override
			public void run(ApplicationArguments args) throws Exception {
				System.out.println("Hello with App runner!");
			}
		};
	}

}
