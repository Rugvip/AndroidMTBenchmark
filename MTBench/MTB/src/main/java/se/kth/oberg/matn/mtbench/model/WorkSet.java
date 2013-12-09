package se.kth.oberg.matn.mtbench.model;

public class WorkSet {
    private int count;
    private WorkItem workItem;

    public WorkSet(int count, WorkItem workItem) {
        this.count = count;
        this.workItem = workItem;
    }

    public int getCount() {
        return count;
    }

    public WorkItem getWorkItem() {
        return workItem;
    }
}
