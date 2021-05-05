package com.adi.concurrency.matrix;

import java.io.File;
import java.io.PrintStream;
import java.time.Instant;
import java.util.Arrays;
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
        int numRows = 10000;
        int numCols = 1000;
        int numThreads = 4;
        boolean print = false;
        boolean printToFile = false;

        if(printToFile) System.setOut(new PrintStream(new File("outfile.txt")));
        ExecutorService executorService = Executors.newFixedThreadPool(numThreads);
//
        System.out.println("Size of integer: " + Integer.BYTES);
        System.out.println("Largest integer:  " + Integer.MAX_VALUE);
//        ArrayList<ArrayList<Integer>> m1 = initializedRandom2DArray(numRows, numCols);
//        ArrayList<ArrayList<Integer>> m2 = initializedRandom2DArray(numRows, numCols);

        System.out.println("Creating matrix 1");
        int [] m1IntArray = initializeRandom2DintArray(numRows, numCols, 1);
//        int[] m1IntArray = {11, 12, 13, 14, 21, 22, 23, 24};
//        System.out.println("Matrix 1 created: "  + Arrays.toString(m1IntArray));
        if(print) printMatrix(m1IntArray, numCols, numRows);


//        System.out.println("Creating matrix 2");
//        int [] m2IntArray = initializeRandom2DintArray(numRows, numCols);
//        System.out.println("Matrix 2 created");

//        int [] results = new int[numCols * numRows];



        Instant start = Instant.now();

//        MatrixMethods.addImprovementOne(
//                m1IntArray,
//                m2IntArray,
//                results,
//                numRows,
//                numCols,
//                executorService,
//                numThreads);

        MatrixMethods.transpose(m1IntArray, numRows, numCols, numThreads, executorService);

        executorService.shutdown();
        executorService.awaitTermination(1000, TimeUnit.MINUTES);

        System.out.println("Transposed matrix");
        if(print) printMatrix(m1IntArray, numRows, numCols);
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

    private static int [] initializeRandom2DintArray(int numRows, int numCols, int seed) {

        // create an int array
        int [] vM = new int[numCols * numRows];

        Random r = new Random(seed);
        for(int i = 0 ; i < numCols ; i++) {
            for(int j = 0 ; j < numRows ; j++) {
                vM[(i*numRows) + j] = r.nextInt(10);
            }
        }

        return vM;

    }


    private static void printMatrix(int [] m, int numCols, int numRows) {
        int valueCount = numCols * numRows;
        for(int start = 0; start < numRows ; start++) {
            System.out.print("|");
            for(int i = start ; i < valueCount ; i += numRows) {
                System.out.print(String.format(" %d", m[i]));
            }
            System.out.println(" |");
        }
    }
}
