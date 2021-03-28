package com.adi.concurrency;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SleepMessages implements Runnable {

    public static final Logger log = LoggerFactory.getLogger(SleepMessages.class);

    public void run() {
        String importantInfo[] = {
                "Mares eat oats",
                "Does eat oats",
                "Little lambs eat ivy",
                "A kid will eat ivy too"
        };

        for (int i = 0;
             i < importantInfo.length;
             i++) {
            //Pause for 4 seconds
            try {
                Thread.sleep(4000);
            } catch (InterruptedException ex) {
                log.warn("Thread was interrupted.Returning now");
                return;
            }
            //Print a message
            log.info("Sleep messages says: " + importantInfo[i]);
        }
    }
}