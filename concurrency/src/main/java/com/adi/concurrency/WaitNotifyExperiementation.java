package com.adi.concurrency;


import java.util.Arrays;
import java.util.stream.Stream;

public class WaitNotifyExperiementation {

    private static final int NUM_THREADS = 2;


    public static void main(String args[]) {


        /*
            calling wait() release the lock, so any thread using synchronized and waiting for the
            lock could acquire it. However, remember that just because you notify doesn't mean that
            the lock gets released. The thread owning the lock and calling notify has to complete
            its computation and release the lock for it to be acquired either by the thread that
            called wait() or the thread waiting on the synchronized(o);
         */
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
                            o.wait(); // WAIT RELEASES LOCK
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
