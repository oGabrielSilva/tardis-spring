package com.noble.tardis;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

@SpringBootApplication
public class Startup {
	@Value("${server.port}")
	int port;

	@EventListener(ApplicationReadyEvent.class)
	void startup() {
		System.out.println("Application on http://127.0.0.1:" + port + "\n");
	}

	public static void main(String[] args) {
		SpringApplication.run(Startup.class, args);
	}

}
