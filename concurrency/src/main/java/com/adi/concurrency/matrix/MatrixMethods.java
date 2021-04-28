package com.adi.concurrency.matrix;


import java.math.BigInteger;
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

    public static void addImprovementOne(
            int[] m1, int[] m2, int[] result, int numRows, int numCols, ExecutorService executorService, int numThreads) {

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

    public static void multiply(
            int[] m1,
            int[] m2,
            int m1NumRows,
            int m1NumCols,
            int m2NumRows,
            int m2NumCols,
            ExecutorService executorService,
            int numThreads) throws Exception {

        if(m1NumCols != m2NumRows) {
            throw new Exception("Row-col match constraint violated");
        }

        int[] result = new int[m1NumRows * m2NumCols];

    }

    /**
     * Performs in-place matrix transpose by locating cycles
     * in the matrix using method outlined here: https://en.wikipedia.org/wiki/In-place_matrix_transposition
     * While performing the in-place transpose, we use a "scratch" byte array of size numCols*numRows/8
     * to keep track of visited elements in the matrix.
     * For a full description: <link to eventual blog post>
     * @param m
     * @param numRows
     * @param numCols
     */
    public static void transpose(int[] m, int numRows, int numCols, int numThreads, ExecutorService executorService) {


        int valueCount = numRows * numCols;
        System.out.println("size of valueCount " + valueCount);
        assert valueCount == m.length;

        // mark array creation
        int nByteVars = valueCount/Byte.SIZE + 1;
        byte[] visitedArray = new byte[nByteVars];

        ArrayList<Runnable> runnables = new ArrayList<>();
        int numValuesPerRunnable = valueCount / numThreads;

        // create a new runnable for each chunk
        int i;
        for(i = 0 ; i < valueCount ; i = i + numValuesPerRunnable) {
            int finalI = i;
            runnables.add(() ->
                transposeChunk(m, numCols, valueCount, visitedArray, finalI, finalI + numValuesPerRunnable)
            );
        }

        // create a single runnable for the remaining chunk
        int finalI1 = i; // i  at this point would be at the last position that i was incrememnted by
        runnables.add(() ->
            transposeChunk(m, numCols, valueCount, visitedArray, finalI1, valueCount)
        );

        for(Runnable r : runnables) {
            executorService.execute(r);
        }


    }

    private static void transposeChunk(int[] m, int numCols, int valueCount, byte[] visitedArray, int start, int end) {
        // the first and last positions in the array remain constant
        // we determine a potential cycle for each element.
        for(int i = start ; i < end ; i++) { // <- split work into several runnables.

            int bytePos = i/Byte.SIZE; // locate the position at which the byte for this array is located
            if((visitedArray[bytePos] & 1 << (i%Byte.SIZE)) == 0) {
                int firstPos = i;
                int nextPos = getNextPosition(numCols, firstPos, valueCount);
                int terminalPos = nextPos;
                int v1 = m[firstPos];
                int v2 = m[nextPos];
                int temp = 0;
                do { // replace with more concrete condition

                    // fix value
                    m[nextPos] = v1;

                    // set nextPos bit in visitedArray
                    bytePos = nextPos/Byte.SIZE;
                    visitedArray[bytePos] |= 1 << (nextPos%Byte.SIZE);

                    // compute next position
                    nextPos = getNextPosition(numCols, nextPos, valueCount);

                    // do the swap
                    temp = v2;
                    v2 = m[nextPos];
                    v1 = temp;

                } while (nextPos != terminalPos);

            }
        }
    }


    /**
     * Gets the next position in the cycle
     * @param numCols
     * @param index
     * @param valueCount
     * @return
     */
    private static int getNextPosition(int numCols, int index, int valueCount) {

        try {
            int product = Math.multiplyExact(numCols, index);
            return product % (valueCount - 1);
        } catch (ArithmeticException e) {

            // overflow case -- switch to using BigInteger -- gonna take a performance hit here
            BigInteger numColsBigInt = new BigInteger(String.valueOf(numCols));
            BigInteger indexBigInt = new BigInteger(String.valueOf(index));
            int t = valueCount - 1;
            BigInteger valCountBigInt = new BigInteger(String.valueOf(t));

            BigInteger productBigInt = numColsBigInt.multiply(indexBigInt);
            BigInteger posBigInt = productBigInt.mod(valCountBigInt);

            /*
                this returned value HAS to be less than (valueCount - 1).
                If it can be modded by valueCount - 1, which could fit in an int
                then the modded value can also fit in an int
             */
            return posBigInt.intValue();
        }

    }



}
