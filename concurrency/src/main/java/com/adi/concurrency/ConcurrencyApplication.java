package com.adi.concurrency;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ConcurrencyApplication implements CommandLineRunner {

	public static final Logger log = LoggerFactory.getLogger(ConcurrencyApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(ConcurrencyApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		log.info("Application started! ");

		log.info("Run sleep messags");
		Thread t = new Thread(new SleepMessages());
		t.start();

		Thread.sleep(5000);
		log.info("Try to interrupt the sleep messages thread");
		t.interrupt();

	}
}
