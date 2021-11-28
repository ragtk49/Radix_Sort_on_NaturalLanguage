/*
 * Copyright (c) 2018. Phasmid Software
 */

package main.java.edu.neu.coe.info6205.benchmark;

import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

import edu.neu.coe.info6205.sort.elementary.InsertionSort;

import static edu.neu.coe.info6205.util.Utilities.formatWhole;

/**
 * This class implements a simple Benchmark utility for measuring the running time of algorithms.
 * It is part of the repository for the INFO6205 class, taught by Prof. Robin Hillyard
 * <p>
 * It requires Java 8 as it uses function types, in particular, UnaryOperator&lt;T&gt; (a function of T => T),
 * Consumer&lt;T&gt; (essentially a function of T => Void) and Supplier&lt;T&gt; (essentially a function of Void => T).
 * <p>
 * In general, the benchmark class handles three phases of a "run:"
 * <ol>
 *     <li>The pre-function which prepares the input to the study function (field fPre) (may be null);</li>
 *     <li>The study function itself (field fRun) -- assumed to be a mutating function since it does not return a result;</li>
 *     <li>The post-function which cleans up and/or checks the results of the study function (field fPost) (may be null).</li>
 * </ol>
 * <p>
 * Note that the clock does not run during invocations of the pre-function and the post-function (if any).
 *
 * @param <T> The generic type T is that of the input to the function f which you will pass in to the constructor.
 */
public class Benchmark_Timer<T> implements Benchmark<T> {

    /**
     * Calculate the appropriate number of warmup runs.
     *
     * @param m the number of runs.
     * @return at least 2 and at most m/10.
     */
    static int getWarmupRuns(int m) {
        return Integer.max(2, Integer.min(10, m / 10));
    }

    /**
     * Run function f m times and return the average time in milliseconds.
     *
     * @param supplier a Supplier of a T
     * @param m        the number of times the function f will be called.
     * @return the average number of milliseconds taken for each run of function f.
     */
    @Override
    public double runFromSupplier(Supplier<T> supplier, int m) {
        logger.info("Begin run: " + description + " with " + formatWhole(m) + " runs");
        // Warmup phase
        final Function<T, T> function = t -> {
            fRun.accept(t);
            return t;
        };
        new Timer().repeat(getWarmupRuns(m), supplier, function, fPre, null);

        // Timed phase
        return new Timer().repeat(m, supplier, function, fPre, fPost);
    }

    /**
     * Constructor for a Benchmark_Timer with option of specifying all three functions.
     *
     * @param description the description of the benchmark.
     * @param fPre        a function of T => T.
     *                    Function fPre is run before each invocation of fRun (but with the clock stopped).
     *                    The result of fPre (if any) is passed to fRun.
     * @param fRun        a Consumer function (i.e. a function of T => Void).
     *                    Function fRun is the function whose timing you want to measure. For example, you might create a function which sorts an array.
     *                    When you create a lambda defining fRun, you must return "null."
     * @param fPost       a Consumer function (i.e. a function of T => Void).
     */
    public Benchmark_Timer(String description, UnaryOperator<T> fPre, Consumer<T> fRun, Consumer<T> fPost) {
        this.description = description;
        this.fPre = fPre;
        this.fRun = fRun;
        this.fPost = fPost;
    }

    /**
     * Constructor for a Benchmark_Timer with option of specifying all three functions.
     *
     * @param description the description of the benchmark.
     * @param fPre        a function of T => T.
     *                    Function fPre is run before each invocation of fRun (but with the clock stopped).
     *                    The result of fPre (if any) is passed to fRun.
     * @param fRun        a Consumer function (i.e. a function of T => Void).
     *                    Function fRun is the function whose timing you want to measure. For example, you might create a function which sorts an array.
     */
    public Benchmark_Timer(String description, UnaryOperator<T> fPre, Consumer<T> fRun) {
        this(description, fPre, fRun, null);
    }

    /**
     * Constructor for a Benchmark_Timer with only fRun and fPost Consumer parameters.
     *
     * @param description the description of the benchmark.
     * @param fRun        a Consumer function (i.e. a function of T => Void).
     *                    Function fRun is the function whose timing you want to measure. For example, you might create a function which sorts an array.
     *                    When you create a lambda defining fRun, you must return "null."
     * @param fPost       a Consumer function (i.e. a function of T => Void).
     */
    public Benchmark_Timer(String description, Consumer<T> fRun, Consumer<T> fPost) {
        this(description, null, fRun, fPost);
    }

    /**
     * Constructor for a Benchmark_Timer where only the (timed) run function is specified.
     *
     * @param description the description of the benchmark.
     * @param f           a Consumer function (i.e. a function of T => Void).
     *                    Function f is the function whose timing you want to measure. For example, you might create a function which sorts an array.
     */
    public Benchmark_Timer(String description, Consumer<T> f) {
        this(description, null, f, null);
    }

    private final String description;
    private final UnaryOperator<T> fPre;
    private final Consumer<T> fRun;
    private final Consumer<T> fPost;

    final static LazyLogger logger = new LazyLogger(Benchmark_Timer.class);
    
    public static void main(String[] args) {
        Random random = new Random();
        int m = 50;

        System.out.println("Benchmarking the randomly ordered array for atleast 5 values of n");
        System.out.println();
        int randArrLen=5000;
        for (int i = 0; i < 5; i++) {
        	randArrLen *= 2;
            //Filling the array with random numbers
            Integer[] arr=new Integer[randArrLen];
            for(int j=0;j<arr.length;j++) arr[j]=random.nextInt();
            //consumer function to be passed as an argument of the constructor of Benchmark_Timer class
            InsertionSort<Integer> insertionSort = new InsertionSort<>();
            Consumer<Integer[]> consumer = (ar) -> {
            	insertionSort.sort(ar, 0, ar.length);
            };
            consumer.accept(arr);
            Benchmark_Timer<Integer[]> benchmarkTimer = new Benchmark_Timer<>("Benchmarking sort(Insertion) function for array with random elements of length : " + randArrLen, consumer);
            System.out.println(benchmarkTimer.run(arr, m));
            
        }
        System.out.println("----------------------------------------------------------------------------------------------------------------------------------");
        
        System.out.println("Benchmarking the ordered array for atleast 5 values of n");
        System.out.println();
        randArrLen=5000;
        for (int i = 0; i < 5; i++) {
        	randArrLen *= 2;
            //Filling the array with sorted numbers
            Integer[] arr=new Integer[randArrLen];
            for(int j=0;j<arr.length;j++) arr[j]=j;
            //consumer function to be passed as an argument of the constructor of Benchmark_Timer class
            InsertionSort<Integer> insertionSort = new InsertionSort<>();
            Consumer<Integer[]> consumer = (ar) -> {
            	insertionSort.sort(ar, 0, ar.length);
            };
            consumer.accept(arr);
            Benchmark_Timer<Integer[]> benchTimer = new Benchmark_Timer<>("Benchmarking sort(Insertion) function for array with ordered elements of length : " + randArrLen, consumer);
            System.out.println(benchTimer.run(arr, m));
        }
        System.out.println("----------------------------------------------------------------------------------------------------------------------------------");
        
        System.out.println("Benchmarking the reverse ordered array for atleast 5 values of n");
        System.out.println();
        randArrLen=5000;
        for (int i = 0; i < 5; i++) {
        	randArrLen *= 2;
            //Filling the array with reverse ordered numbers
            Integer[] arr=new Integer[randArrLen];
            int index=0;
            for(int j=arr.length-1;j>=0;j--) arr[index++]=j;
            //consumer function to be passed as an argument of the constructor of Benchmark_Timer class
            InsertionSort<Integer> insertionSort = new InsertionSort<>();
            Consumer<Integer[]> consumer = (ar) -> {
            	insertionSort.sort(ar, 0, ar.length);
            };
            consumer.accept(arr);
            Benchmark_Timer<Integer[]> benchTimer = new Benchmark_Timer<>("Benchmarking sort(Insertion) function for array with reverse ordered elements of length : " + randArrLen, consumer);
            System.out.println(benchTimer.run(arr, m));
        }
        System.out.println("----------------------------------------------------------------------------------------------------------------------------------");
        
        System.out.println("Benchmarking the partially ordered array for atleast 5 values of n");
        System.out.println();
        randArrLen=5000;
        for (int i = 0; i < 5; i++) {
        	randArrLen *= 2;
            //Filling the array with partially ordered numbers
            Integer[] arr=new Integer[randArrLen];
            int index=0;
            for(int j=0;j<arr.length;j++) {
            	if(j<arr.length/2) {
            		arr[j]=random.nextInt();
            	}else {
            		arr[j]=j;
            	}
            }
            //consumer function to be passed as an argument of the constructor of Benchmark_Timer class
            InsertionSort<Integer> insertionSort = new InsertionSort<>();
            Consumer<Integer[]> consumer = (ar) -> {
            	insertionSort.sort(ar, 0, ar.length);
            };
            consumer.accept(arr);
            Benchmark_Timer<Integer[]> benchTimer = new Benchmark_Timer<>("Benchmarking sort(Insertion) function for array with partially ordered elements of length : " + randArrLen, consumer);
            System.out.println(benchTimer.run(arr, m));
        }
        System.out.println("-----------------------------------------------------------END-----------------------------------------------------------------------");
    }
}
