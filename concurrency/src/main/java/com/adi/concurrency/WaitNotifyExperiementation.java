package com.adi.concurrency;


import java.util.Arrays;
import java.util.stream.Stream;

public class WaitNotifyExperiementation {

    private static final int NUM_THREADS = 2;


    public static void main(String args[]) {


        Object o = new Object();

        Thread [] consumers = new Thread[NUM_THREADS];
        Thread [] producers = new Thread[NUM_THREADS];

        for(int i = 0 ; i < NUM_THREADS; i++) {
            consumers[i] = new Thread(new Runnable() {
                @Override
                public void run() {

                    try {
                        synchronized (o) {
                            System.out.println(Thread.currentThread().getName() + " going to wait for object lock ");
                            o.wait();
                            System.out.println("Woke up! " + Thread.currentThread().getName());
                        }
                    } catch (InterruptedException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            });
        }

        Thread producerThread = new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    synchronized (o) {
//                        Thread.sleep(5000);
                        System.out.println("About to wake up all the consumers!");
                        o.notify();
                        Thread.sleep(5000);
                        System.out.println("Woke up one of the waiting threads!");
                    }

                }   catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }
        });


        Arrays.stream(consumers).forEach(Thread::start);

        producerThread.start();
        System.out.println("Something");


    }


}
