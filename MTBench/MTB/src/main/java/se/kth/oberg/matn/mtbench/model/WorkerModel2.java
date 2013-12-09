package se.kth.oberg.matn.mtbench.model;

import android.util.Log;

import java.util.concurrent.CyclicBarrier;

import se.kth.oberg.matn.mtbench.R;

public class WorkerModel2 extends WorkerModel {
    @Override
    public long doWork(final WorkSet workSet) {
        final int threadCount = workSet.getCount();

        final Thread threads[] = new Thread[threadCount];
        final double results[] = new double[threadCount];

        final CyclicBarrier startBarrier = new CyclicBarrier(threadCount + 1);
        final CyclicBarrier endBarrier = new CyclicBarrier(threadCount + 1);

        for (int i = 0; i < threadCount; i++) {
            final int index = i;
            threads[i] = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        startBarrier.await();
                        synchronized (WorkerModel2.this) {}
                        results[index] = workSet.getWorkItem().call();
                        endBarrier.await();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        for (int i = 0; i < threadCount; i++) {
            threads[i].start();
        }

        synchronized (this) {
            try {
                startBarrier.await();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        final long start = System.nanoTime();

        try {
            endBarrier.await();
        } catch (Exception e) {
            e.printStackTrace();
        }

        double sum = 0;
        for (int i = 0; i < threadCount; i++) {
            sum += results[i];
        }

        final long end = System.nanoTime();

        Log.e("ResultSum", getName() + ": threadCount: " + workSet.getCount() + " sum: " + sum);

        for (int i = 0; i < threadCount; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
            }
        }

        return end - start;
    }

    @Override
    public int getDescriptionResource() {
        return R.string.description_model2;
    }

    @Override
    public String getName() {
        return "Threads with barrier";
    }
}
