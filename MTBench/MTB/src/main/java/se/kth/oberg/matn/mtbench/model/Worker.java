package se.kth.oberg.matn.mtbench.model;

import java.io.Serializable;

public abstract class Worker implements Serializable {
    public abstract long doWork(WorkSet workSet);

    public abstract String getName();

    public abstract int getId();

    public abstract int getDescriptionResource();

    @Override
    public String toString() {
        return getName();
    }
}
