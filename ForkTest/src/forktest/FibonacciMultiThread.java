package forktest;

import java.util.concurrent.RecursiveTask;

public class FibonacciMultiThread extends RecursiveTask<Long>
{

    private static final long serialVersionUID = 6136927121059165206L;

    private static final int THRESHOLD = 5;

    private FibonacciSingleThread problem;
    public long result;

    public FibonacciMultiThread(FibonacciSingleThread problem)
    {
        this.problem = problem;
    }

    @Override
    public Long compute()
    {
        if (problem.n < THRESHOLD)
        { // easy problem, don't bother with parallelism
            result = problem.solve();
        } else
        {
            FibonacciMultiThread worker1 = new FibonacciMultiThread(new FibonacciSingleThread(problem.n - 1));
            FibonacciMultiThread worker2 = new FibonacciMultiThread(new FibonacciSingleThread(problem.n - 2));
            worker1.fork();
            result = worker2.compute() + worker1.join();

        }
        return result;
    }
}
