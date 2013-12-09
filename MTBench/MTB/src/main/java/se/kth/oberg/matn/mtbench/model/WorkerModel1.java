package se.kth.oberg.matn.mtbench.model;

import android.util.Log;

import se.kth.oberg.matn.mtbench.R;

public class WorkerModel1 extends WorkerModel {
    @Override
    public long doWork(final WorkSet workSet) {
        final int threadCount = workSet.getCount();

        final Thread threads[] = new Thread[threadCount];
        final double results[] = new double[threadCount];

        for (int i = 0; i < threadCount; i++) {
            final int index = i;
            threads[i] = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        results[index] = workSet.getWorkItem().call();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        final long start = System.nanoTime();

        for (int i = 0; i < threadCount; i++) {
            threads[i].start();
        }

        for (int i = 0; i < threadCount; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
            }
        }

        double sum = 0;
        for (int i = 0; i < threadCount; i++) {
            sum += results[i];
        }

        final long end = System.nanoTime();

        Log.e("ResultSum", getName() + ": threadCount: " + workSet.getCount() + " sum: " + sum);

        return end - start;
    }

    @Override
    public int getDescriptionResource() {
        return R.string.description_model1;
    }

    @Override
    public String getName() {
        return "Plain Threads";
    }
}
