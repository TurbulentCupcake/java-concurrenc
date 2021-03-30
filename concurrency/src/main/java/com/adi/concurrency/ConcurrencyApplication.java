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

		boolean f = false;

		Runnable r = new Runnable() {
			@Override
			public void run()  {
				try {
					synchronized (this) {
						log.info("Gonna wait now");
						while(!f) {
							wait();
						}
						log.info("Woke up!");
					}
				} catch (InterruptedException ex) {
					log.info("Just got notified at " + Thread.currentThread().getName());
				}
			}
		};

		Runnable r2 = new Runnable() {
			@Override
			public void run() {
				try {
					synchronized (this) {
						Thread.sleep(1000);
						notifyAll();
						log.info("Making a notification from " + Thread.currentThread().getName());

					}
				} catch (InterruptedException ex) {
					log.info("Got interrupted here");
				}
			}
		};

		new Thread(r).start();
		new Thread(r2).start();

		Thread.sleep(100);
		log.info("Final counter value = " + counter.getValue());

	}
}
