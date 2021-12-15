package com.skg.apimonkey;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Slf4j
public class ApiMonkeyApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiMonkeyApplication.class, args);
		log.info("*API Monkey started*");
	}
}
