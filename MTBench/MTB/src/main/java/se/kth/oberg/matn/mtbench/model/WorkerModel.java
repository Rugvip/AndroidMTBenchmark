package se.kth.oberg.matn.mtbench.model;

public abstract class WorkerModel extends Worker {
    public abstract String getName();

    public abstract int getDescriptionResource();

    @Override
    public String toString() {
        return getName();
    }
}
