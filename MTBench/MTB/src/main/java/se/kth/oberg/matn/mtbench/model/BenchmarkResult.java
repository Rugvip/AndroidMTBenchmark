package se.kth.oberg.matn.mtbench.model;

import java.util.ArrayList;
import java.util.HashMap;
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

/*
{
    worker: 1,
    exponent: 6,
    results: [{
        workerCount: 10,
        workItems: 12,
        time: 1287312948
    }, {
        workerCount: 12,
        workItems: 10,
        time: 1287323948
    }]
}
*/

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("{\n");
        stringBuilder.append("    worker: " + workerId + "\n");
        stringBuilder.append("    exponent: " + exponent + "\n");
        stringBuilder.append("    results: [");

        for (int i = 0; i < results.size(); i++) {
            Result result = results.get(i);
            stringBuilder.append("{\n");
            stringBuilder.append("        workerCount: " + result.getWorkerCount() + ",\n");
            stringBuilder.append("        workItems: " + result.getWorkItems() + ",\n");
            stringBuilder.append("        times: [");
            Long[] times = result.getTimes();
            for (int j = 0; j < times.length; j++) {
                stringBuilder.append(times[j]);
                if (j < times.length - 1) {
                    stringBuilder.append(", ");
                }
            }
            stringBuilder.append("]\n");
//            + result.getTime() + "\n");
            stringBuilder.append("    }");
            if (i < results.size() - 1) {
                stringBuilder.append(", ");
            }
        }

        stringBuilder.append("]\n");
        stringBuilder.append("}\n");

        return stringBuilder.toString();
    }

    public static class Result {
        private List<Long> times;
        private int workerCount;
        private int workItems;

        public Result(int workerCount, int workItems) {
            this.workerCount = workerCount;
            this.workItems = workItems;
            times = new ArrayList<>();
        }

        private void addTime(long time) {
            times.add(time);
        }

        public Long[] getTimes() {
            return times.toArray(new Long[0]);
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
        private HashMap<Integer, Result> resultMap;

        public Builder(int workerId, int exponent) {
            result = new BenchmarkResult();
            resultMap = new HashMap<>();
            result.workerId = workerId;
            result.exponent = exponent;
        }

        private Builder(Worker worker, WorkCollection workCollection) {
            this(worker.getId(), workCollection.getExponent());
        }

        public Builder addResult(long time, WorkSet workSet) {
            return addResult(time, workSet.getCount(), workSet.getWorkItem().getLoops());
        }

        public Builder addResult(long time, int workerCount, int workItems) {
            int key = workerCount * 2000 + workItems;
            Result r = resultMap.get(key);
            if (r != null) {
                r.addTime(time);
            } else {
                r = new Result(workerCount, workItems);
                r.addTime(time);
                resultMap.put(key, r);
                result.results.add(r);
            }
            return this;
        }

        public Builder addResult(BenchmarkResult benchmarkResult) {
            for (Result r : benchmarkResult.results) {
                for (Long time : r.times) {
                    addResult(time, r.workerCount, r.workItems);
                }
            }
            return this;
        }

        public BenchmarkResult build() {
            BenchmarkResult result = this.result;
            this.result = null;
            return result;
        }
    }
}
