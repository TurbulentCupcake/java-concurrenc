package com.adi.concurrency.producerconsuemr;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Drop {

    private static final Logger log = LoggerFactory.getLogger(Drop.class);

    // Message sent from producer
    // to consumer.
    private String message;
    // True if consumer should wait
    // for producer to send message,
    // false if producer should wait for
    // consumer to retrieve message.
    private boolean empty = true;

    public synchronized String take() {
        // Wait until message is
        // available.
        while (empty) {
            try {
                wait();
            } catch (InterruptedException e) {}
        }
        // Toggle status.
        empty = true;
        // Notify producer that
        // status has changed.
        notifyAll();

        log.info("Message consumed at " + Thread.currentThread().getName());
        return message;
    }

    public synchronized void put(String message) {
        // Wait until message has
        // been retrieved.
        while (!empty) {
            try {
                wait();
            } catch (InterruptedException e) {}
        }
        // Toggle status.
        empty = false;
        // Store message.

        log.info("Message being input at " + Thread.currentThread().getName());
        this.message = message;
        // Notify consumer that status
        // has changed.
        notifyAll();
    }
}