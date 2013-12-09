package se.kth.oberg.matn.mtbench.model;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

class ThreadPool {
    private static int NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();

    private ThreadPoolExecutor executor;
    private ExecutorCompletionService<Double> completionService;
    private int count;

    public ThreadPool(boolean limited) {
        this(limited, limited ? NUMBER_OF_CORES : -1);
    }

    public ThreadPool(boolean limited, int limit) {
        executor = new ThreadPoolExecutor(
                limit < 0 ? NUMBER_OF_CORES : limit,
                limit < 0 ? Integer.MAX_VALUE : limit,
                1, TimeUnit.MINUTES,
                limited ? new LinkedBlockingDeque<Runnable>() : new SynchronousQueue<Runnable>());
        completionService = new ExecutorCompletionService<>(executor);
        count = 0;
    }

    public Future<Double> submit(WorkItem workItem) {
        count++;
        return completionService.submit(workItem);
    }

    public List<Double> getResults() {
        List<Double> results = new ArrayList<>(count);

        while (count --> 0) {
            try {
                Future<Double> future = completionService.take();
                Double result = future.get();
                results.add(result);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }

        count = 0;

        return results;
    }
}
