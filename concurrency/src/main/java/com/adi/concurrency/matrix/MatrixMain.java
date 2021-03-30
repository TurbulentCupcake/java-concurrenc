package com.adi.concurrency.matrix;

import java.time.Instant;
import java.util.Random;
import java.util.ArrayList;
import java.util.Vector;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MatrixMain {

    public static void main(String args[]) throws Exception{

        // (5^9 * 2^9)
        int numRows = 256*2;
        int numCols = 1953125;
        int numThreads = 11;

        ExecutorService executorService = Executors.newFixedThreadPool(numThreads);
//
        System.out.println("Size of integer: " + Integer.BYTES);
        System.out.println("Largest integer:  " + Integer.MAX_VALUE);
//        ArrayList<ArrayList<Integer>> m1 = initializedRandom2DArray(numRows, numCols);
//        ArrayList<ArrayList<Integer>> m2 = initializedRandom2DArray(numRows, numCols);

        System.out.println("Creating matrix 1");
        int [] m1IntArray = initializeRandom2DintArray(numRows, numCols);
        System.out.println("Matrix 1 created");

        System.out.println("Creating matrix 2");
        int [] m2IntArray = initializeRandom2DintArray(numRows, numCols);
        System.out.println("Matrix 2 created");

        int [] results = new int[numCols * numRows];

//        // initialize result matrix
//        ArrayList<ArrayList<Integer>> v = new ArrayList();
//        for(int i = 0; i < numCols ; i++) {
//            ArrayList<Integer> vM = new ArrayList<>();
//            for(int j = 0 ; j < numRows ; j++) vM.add(0);
//            v.add(vM);
//        }

        Instant start = Instant.now();

        MatrixMethods.addImprovementOne(
                m1IntArray,
                m2IntArray,
                results,
                numRows,
                numCols,
                executorService,
                numThreads);

//        MatrixMethods.addSlow(
//                m1,
//                m2,
//                v,
//                numRows,
//                numCols,
//                executorService
//        );

        executorService.shutdown();
        executorService.awaitTermination(1000, TimeUnit.MINUTES);

        long diff = Instant.now().toEpochMilli() - start.toEpochMilli();

        System.out.println("Time taken [ms] = " + diff);
        System.out.println("Result");

        return;

    }

    private static ArrayList<ArrayList<Integer>> initializedRandom2DArray(int numRows, int numCols) {
        ArrayList<ArrayList<Integer>> vM = new ArrayList<>();

        for(int i = 0 ; i < numCols ; i++) {
            ArrayList<Integer> v = new ArrayList<>(numRows);
            for(int j = 0 ; j < numRows ; j++) {
                v.add((new Random()).nextInt(1000));
            }
            vM.add(v);
        }

        return vM;

    }

    private static int [] initializeRandom2DintArray(int numRows, int numCols) {

        // create an int array
        int [] vM = new int[numCols * numRows];

        Random r = new Random(Instant.now().toEpochMilli());
        for(int i = 0 ; i < numCols ; i++) {
            for(int j = 0 ; j < numRows ; j++) {
                vM[(i*numRows) + j] = r.nextInt(10);
            }
        }

        return vM;

    }
}