package com.adi.concurrency;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

		Counter counter = new Counter();

		Runnable r = new Runnable() {
			@Override
			public void run() {
				for(int i = 0 ; i < 10000 ; i ++) {
					counter.increment();
				}
			}
		};

		Runnable r2 = new Runnable() {
			@Override
			public void run() {
				for(int i = 0 ; i < 10000 ; i++) {
					counter.decrement();
				}
			}
		};

		new Thread(r).start();
		new Thread(r2).start();

		Thread.sleep(100);
		log.info("Final counter value = " + counter.getValue());

	}
}
