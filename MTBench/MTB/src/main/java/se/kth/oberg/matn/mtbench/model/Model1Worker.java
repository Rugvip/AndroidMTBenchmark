package se.kth.oberg.matn.mtbench.model;

import se.kth.oberg.matn.mtbench.R;

public class Model1Worker extends Worker {
    @Override
    public long doWork(final WorkSet workSet) {
        final int threadCount = workSet.getCount();

        final Thread threads[] = new Thread[threadCount];

        for (int i = 0; i < threadCount; i++) {
            final int index = i;
            threads[i] = new Thread(workSet.getWorkItem());
        }

        final long start = System.nanoTime();

        for (int i = 0; i < threadCount; i++) {
            threads[i].start();
//            Log.i("Calculator", "started thread " + i);
        }

        for (int i = 0; i < threadCount; i++) {
            try {
                threads[i].join();
//                Log.i("Threading", "joined thread " + i);
            } catch (InterruptedException e) {
//                Log.e("Calculator", "failed to join thread " + i);
            }
        }

        final long end = System.nanoTime();

        return end - start;
    }

    @Override
    public int getDescriptionResource() {
        return R.string.description_model1;
    }

    @Override
    public String getName() {
        return "Model 1";
    }

    @Override
    public int getId() {
        return 0;
    }
}
