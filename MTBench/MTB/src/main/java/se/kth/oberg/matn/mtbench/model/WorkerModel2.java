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
//                        Log.i("Threading", "thread " + index + " start awaiting barrier");
                        startBarrier.await();
//                        Log.i("Threading", "thread " + index + " start passed barrier");
                        synchronized (WorkerModel2.this) {}
                        results[index] = workSet.getWorkItem().call();
//                        Log.i("Threading", "2 thread " + index + " end awaiting barrier");
                        endBarrier.await();
//                        Log.i("Threading", "2 thread " + index + " end passed barrier");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
//                    Log.i("Calculator", "thread " + index + " completed");
                }
            });
        }

        for (int i = 0; i < threadCount; i++) {
            threads[i].start();
//            Log.i("Calculator", "started thread " + i);
        }

        synchronized (this) {
            try {
//                Log.i("Threading", "main awaiting start barrier");
                startBarrier.await();
//                Log.i("Threading", "main passed start barrier");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        final long start = System.nanoTime();
//        Log.i("Threading", "notified threads");

        try {
//            Log.i("Threading", "main awaiting end barrier");
            endBarrier.await();
//            Log.i("Threading", "main passed end barrier");
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
//                Log.i("Threading", "joined thread " + i);
            } catch (InterruptedException e) {
//                Log.e("Calculator", "failed to join thread " + i);
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
        return "Model 2";
    }
}
