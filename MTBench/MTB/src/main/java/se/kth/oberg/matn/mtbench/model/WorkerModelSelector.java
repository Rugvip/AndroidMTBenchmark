package se.kth.oberg.matn.mtbench.model;

import java.util.Observable;

import se.kth.oberg.matn.mtbench.R;

public class WorkerModelSelector extends Observable {
    private static final WorkerModel[] workers = new WorkerModel[] {
        new WorkerModel1(),
        new WorkerModel2(),
        new WorkerModel3(),
        new WorkerModel4(),
        new WorkerModel5()
    };

    private int workerId = 0;
    private int frozenWorkerId = workerId;
    private boolean frozen;

    public WorkerModel[] getWorkers() {
        return workers;
    }

    public WorkerModel getWorker() {
        return workers[frozenWorkerId];
    }

    public int getWorkerId() {
        return frozenWorkerId;
    }

    public void setWorkerId(int workerId) {
        this.workerId = workerId;
        if (!frozen) {
            frozenWorkerId = workerId;
            setChanged();
            notifyObservers();
        }
    }

    public void freeze() {
        frozen = true;
    }

    public void thaw() {
        frozen = false;
        if (frozenWorkerId != workerId) {
            setWorkerId(workerId);
        }
    }

    public static WorkerModel getWorkerById(int id) {
        return workers[id];
    }
}
