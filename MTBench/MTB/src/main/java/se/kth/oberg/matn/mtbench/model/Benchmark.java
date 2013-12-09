package se.kth.oberg.matn.mtbench.model;

import android.os.AsyncTask;

public class Benchmark extends AsyncTask<WorkCollection, Integer, BenchmarkResult> {
    private Worker worker;

    public Benchmark(Worker worker) {
        this.worker = worker;
    }

    @Override
    protected BenchmarkResult doInBackground(WorkCollection... workCollections) {
        if (workCollections.length != 1) {
            throw new IllegalArgumentException("There should be one and only one WorkCollection");
        }

        WorkCollection workCollection = workCollections[0];

        BenchmarkResult.Builder result = BenchmarkResult.createBuilder(worker, workCollection);

        int size = workCollection.size();

        publishProgress(0);
        int count = 0;

        for (WorkSet workSet : workCollection) {
            result.addWorkResult(worker.doWork(workSet), workSet);
            publishProgress((++count * 100) / size);
        }

        System.gc();
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return result.build();
    }
}
