package se.kth.oberg.matn.mtbench.persistence;

import java.util.ArrayList;
import java.util.List;

public class ResultSummary {
    private List<WorkerSummary> workers = new ArrayList<>();

    public List<WorkerSummary> getWorkers() {
        return workers;
    }

    public static class WorkerSummary {
        private List<WorkloadSummary> workloads = new ArrayList<>();
        private int workerId;

        public List<WorkloadSummary> getWorkloads() {
            return workloads;
        }

        public int getWorkerId() {
            return workerId;
        }
    }

    public static class WorkloadSummary {
        private int exponent;
        private int benchmarkCount;

        public int getExponent() {
            return exponent;
        }

        public int getBenchmarkCount() {
            return benchmarkCount;
        }
    }

    public static Builder createBuilder() {
        return new Builder();
    }

    public static class Builder {
        private ResultSummary result = new ResultSummary();

        private WorkerSummary findWorker(int workerId) {
            for (WorkerSummary worker : result.workers) {
                if (worker.workerId == workerId) {
                    return worker;
                }
            }
            return null;
        }

        public void add(int workerId, int exponent, int lapCount) {
            WorkerSummary worker = findWorker(workerId);
            if (worker == null) {
                worker = new WorkerSummary();
                worker.workerId = workerId;
                result.workers.add(worker);
            }
            WorkloadSummary workloadSummary = new WorkloadSummary();
            workloadSummary.exponent = exponent;
            workloadSummary.benchmarkCount = lapCount;
            worker.workloads.add(workloadSummary);
        }

        public ResultSummary build() {
            ResultSummary summary = result;
            result = null;
            return summary;
        }
    }
}
