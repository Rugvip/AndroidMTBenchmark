package se.kth.oberg.matn.mtbench.model;

import android.util.Log;

import java.util.List;

import se.kth.oberg.matn.mtbench.R;

public class WorkerModel4 extends WorkerModel {
    private static ThreadPool threadPool = new ThreadPool(true);

    @Override
    public long doWork(WorkSet workSet) {
        final long start = System.nanoTime();

        for (int i = 0; i < workSet.getCount(); i++) {
            threadPool.submit(workSet.getWorkItem());
        }

        List<Double> results = threadPool.getResults();

        double sum = 0;
        for (Double result : results) {
            sum += result;
        }

        final long end = System.nanoTime();

        Log.e("ResultSum", getName() + ": threadCount: " + workSet.getCount() + " sum: " + sum);

        return end - start;
    }

    @Override
    public String getName() {
        return "Thread Pool, limit = # cores";
    }

    @Override
    public int getDescriptionResource() {
        return R.string.description_model4;
    }

}
