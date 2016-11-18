package forktest;

import org.joda.time.Instant;
import org.joda.time.Duration;
import java.util.concurrent.ForkJoinPool;

public class ForkTest
{

    public static void main(String[] args) throws Exception
    {

        int n = 47;

        Instant oldTime = new Instant();
        FibonacciSingleThread fibonacciSingleThread = new FibonacciSingleThread(n);
        long result = fibonacciSingleThread.solve();

        Duration duration = new Duration(oldTime, new Instant());
        
        oldTime = new Instant();
        int processors = Runtime.getRuntime().availableProcessors();
        FibonacciSingleThread fibonacciSingleThread2 = new FibonacciSingleThread(n);        
        FibonacciMultiThread fibonacciMultiThread = new FibonacciMultiThread(fibonacciSingleThread2);
        ForkJoinPool pool = new ForkJoinPool(processors);
        pool.invoke(fibonacciMultiThread);
        long result2 = fibonacciMultiThread.result;
        Duration duration2 = new Duration(oldTime, new Instant());




        System.out.println("Computing in single thread Fibonacci number: " + n + " result:" + result + " duration: " + duration.getMillis() + "ms");
        System.out.println("Computing in multi thread Fibonacci number: " + n + " result:" + result2 + " duration: " + duration2.getMillis() + "ms");
        System.out.println("duration: " + duration.getMillis() + "ms");

    }
}
