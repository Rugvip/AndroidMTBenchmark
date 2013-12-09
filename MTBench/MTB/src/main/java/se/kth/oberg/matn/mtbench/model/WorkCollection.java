package se.kth.oberg.matn.mtbench.model;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class WorkCollection implements Iterable<WorkSet> {
    private int exponent;
    private List<WorkSet> workSets = new LinkedList<>();

    private WorkCollection(int exponent) {
        this.exponent = exponent;
    }

    public int getExponent() {
        return exponent;
    }

    public int size() {
        return workSets.size();
    }

    public static Builder createBuilder(int exponent) {
        return new Builder(exponent);
    }

    @Override
    public Iterator<WorkSet> iterator() {
        return workSets.iterator();
    }

    public static WorkCollection buildDefaultCollection(int exponent) {
        return createBuilder(exponent)
                .addWorkSet(  1, 120)
                .addWorkSet(  2,  60)
                .addWorkSet(  3,  40)
                .addWorkSet(  4,  30)
                .addWorkSet(  5,  24)
                .addWorkSet(  6,  20)
                .addWorkSet(  8,  15)
                .addWorkSet( 10,  12)
                .addWorkSet( 12,  10)
                .addWorkSet( 15,   8)
                .addWorkSet( 20,   6)
                .addWorkSet( 24,   5)
                .addWorkSet( 30,   4)
                .addWorkSet( 40,   3)
                .addWorkSet( 60,   2)
                .addWorkSet(120,   1)
                .build();
    }

    public static class Builder {
        private WorkCollection workCollection;
        private WorkItem.Builder itemBuilder;

        private Builder(int exponent) {
            workCollection = new WorkCollection(exponent);
            itemBuilder = WorkItem.createBuilder(exponent);
        }

        public Builder addWorkSet(int workers, int work) {
            workCollection.workSets.add(new WorkSet(workers, itemBuilder.build(work)));
            return this;
        }

        public WorkCollection build() {
            WorkCollection workCollection = this.workCollection;
            this.workCollection = null;
            return workCollection;
        }
    }
}
