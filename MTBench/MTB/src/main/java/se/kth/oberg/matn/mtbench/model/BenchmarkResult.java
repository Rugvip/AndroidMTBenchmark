package se.kth.oberg.matn.mtbench.model;

import java.util.LinkedList;
import java.util.List;

public class BenchmarkResult {
    private int exponent;
    private int workerId;
    private List<Result> results = new LinkedList<>();

    public int getExponent() {
        return exponent;
    }

    public int getWorkerId() {
        return workerId;
    }

    public List<Result> getResults() {
        return results;
    }

    public static class Result {
        private long time;
        private int workerCount;
        private int workItems;

        public Result(long time, int workerCount, int workItems) {
            this.time = time;
            this.workerCount = workerCount;
            this.workItems = workItems;
        }

        public long getTime() {
            return time;
        }

        public int getWorkerCount() {
            return workerCount;
        }

        public int getWorkItems() {
            return workItems;
        }
    }

    public static Builder createBuilder(Worker worker, WorkCollection workCollection) {
        return new Builder(worker, workCollection);
    }

    public static Builder createBuilder(int workerId, int exponent) {
        return new Builder(workerId, exponent);
    }

    public static class Builder {
        private BenchmarkResult result;

        public Builder(int workerId, int exponent) {
            result = new BenchmarkResult();
            result.workerId = workerId;
            result.exponent = exponent;
        }

        private Builder(Worker worker, WorkCollection workCollection) {
            result = new BenchmarkResult();
            result.workerId = worker.getId();
            result.exponent = workCollection.getExponent();
        }

        public Builder addWorkResult(long time, WorkSet workSet) {
            result.results.add(new Result(time, workSet.getCount(), workSet.getWorkItem().getLoops()));
            return this;
        }

        public Builder addWorkResult(long time,int workerCount, int workItems) {
            result.results.add(new Result(time, workerCount, workItems));
            return this;
        }

        public BenchmarkResult build() {
            BenchmarkResult result = this.result;
            this.result = null;
            return result;
        }
    }
}
