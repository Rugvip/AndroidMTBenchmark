package se.kth.oberg.matn.mtbench.model;

import se.kth.oberg.matn.mtbench.R;

public class WorkerModel3 extends WorkerModel {
    @Override
    public long doWork(WorkSet workSet) {
        return 0;
    }

    @Override
    public String getName() {
        return "Model 3";
    }

    @Override
    public int getDescriptionResource() {
        return R.string.description_model3;
    }
}
