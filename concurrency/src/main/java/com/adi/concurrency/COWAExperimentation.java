package com.adi.concurrency;

import java.lang.instrument.Instrumentation;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/*
    Test performance of the CopyOnWriteArray.
    Background: The CopyOnWriteArrayList mutates the underlying array by creating a new copy of the underlying array.
    According to the javadocs, this is technique may be more efficient if we are performing more traversal
    operations that write opertations. We don't have to synchronize traversals among threads.
 */
public class COWAExperimentation {

    CopyOnWriteArrayList<Integer> copyOnWriteArrayList = new CopyOnWriteArrayList<>();
    public static final int WRITER_PAUSE_TIME = 200; // ms
    public static final int TOTAL_WRITERS = 4;


    public static void main(String[] args) throws Exception {

        int numCores = Runtime.getRuntime().availableProcessors();
        System.out.println("Number of cores: " + numCores);
        ExecutorService executorService = Executors.newFixedThreadPool(numCores);

        Random rnd = new Random(100);
        CopyOnWriteArrayList<LargeObject> copyOnWriteArrayList = new CopyOnWriteArrayList<>();



        for(int i = 0 ; i < TOTAL_WRITERS; i++) {
            executorService.execute(() -> {
                while(true) {

                    if(Thread.currentThread().isInterrupted()) {
                        System.out.println("Thread interrupted. Exiting");
                        return;
                    }
                    copyOnWriteArrayList.add(new LargeObject()); // each write would create a new array
                    System.out.println("Created a large object at " + Thread.currentThread().getName());

                }
            });
        }



        for(int i = 0 ; i < numCores - TOTAL_WRITERS ; i++) {
            executorService.execute(() -> {

                while(true) {

                    if(Thread.currentThread().isInterrupted()) {
                        System.out.println("Thread interrupted. Exiting");
                        return;
                    }

                    Iterator<LargeObject> itr = copyOnWriteArrayList.iterator();
                    while(itr.hasNext()) {
                        LargeObject v = itr.next(); // do a read
//                        System.out.println("Reading " + v + " from " + Thread.currentThread().getName());
                    }
                }
            });
        }


    }

    public static class LargeObject {

        private ArrayList<Integer> bigArray;


        public LargeObject() {
            bigArray = new ArrayList<>();
            for(int i = 0 ; i < 3000; i++) {
                bigArray.add((new Random()).nextInt());
            }
        }
    }


}
