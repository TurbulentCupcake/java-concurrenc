package com.adi.concurrency.matrix;


import java.time.Instant;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;

public class MatrixMethods {

    /**
     * Using this method to add two matrices is slow because
     * https://stackoverflow.com/questions/66848183/multithreaded-matrix-addition-taking-longer-than-single-threaded-version-in-java
     * @param m1
     * @param m2
     * @param result
     * @param numRows
     * @param numCols
     * @param executorService
     */
    public static void addSlow(
            ArrayList<ArrayList<Integer>> m1,
            ArrayList<ArrayList<Integer>> m2,
            ArrayList<ArrayList<Integer>> result,
            int numRows,
            int numCols,
            ExecutorService executorService) {



        // create a runnable that just takes two ArrayLists and adds their values and
        // creates a new ArrayList.
        for(int i = 0; i < numCols ; i++) {
            for(int j = 0 ; j < numRows ; j++) {
                int finalI = i;
                int finalJ = j;
                executorService.execute(
                        new Runnable() {
                            @Override
                            public void run()  {
                                    ArrayList<Integer> v1 = m1.get(finalI);
                                    Integer m1Val = v1.get(finalJ);
                                    ArrayList<Integer> v2 = m2.get(finalI);
                                    Integer m2Val = v2.get(finalJ);
                                    result.get(finalI).add(finalJ,  m1Val + m2Val);
                            }
                        }
                );
            }
        }
    }

    public static void addImprovementOne(int[] m1, int[] m2, int[] result, int numRows, int numCols, ExecutorService executorService, int numThreads) {

        // obtain number of processors  on this machine
        int numProcessors = numThreads;

        // partition the array as digestible through the number of processors
        int elementsPerThread = (numCols * numRows)/numProcessors;

        System.out.println("Num processors = " + numProcessors);
        System.out.println("Elements per thread " + elementsPerThread);

        // create a new runnable for each partition
        ArrayList<Runnable> runnables = new ArrayList<>();
        for(int i = 0 ; i < numProcessors ; i++) {
            int finalI = i;
            runnables.add(() -> {
                int start = finalI * elementsPerThread;
                int end = start + elementsPerThread;
                for(int j = start ; j < end ; j++) {
                    result[j] = m1[j] + m2[j];
                }
            });
        }

        // create single runnable to process remaining elements
        int diff = (numCols * numRows) - (numProcessors * elementsPerThread);
        if(diff > 0) {
            runnables.add(() -> {
                int start = (numCols * numRows) - diff;
                int end = (numCols * numRows);
                for(int j = start ; j < end ; j++) {
                    result[j] = m1[j] + m2[j];
                }
            });
        }

        // run each runnable
        for(Runnable runnable : runnables) {
            executorService.execute(runnable);
        }



    }

}
