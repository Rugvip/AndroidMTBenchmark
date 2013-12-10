package se.kth.oberg.matn.mtbench;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.LinkedList;
import java.util.List;

import se.kth.oberg.matn.mtbench.model.WorkerModelSelector;
import se.kth.oberg.matn.mtbench.persistence.Persistence;
import se.kth.oberg.matn.mtbench.persistence.ResultSummary;

public class ResultListAdapter extends BaseExpandableListAdapter {
    private ResultSummary resultSummary;
    private LayoutInflater inflater;
    private static ResultListAdapter instance;

    private ResultListAdapter(LayoutInflater inflater) {
        this.inflater = inflater;
    }

    public static void init(LayoutInflater inflater) {
        instance = new ResultListAdapter(inflater);
    }

    public static ResultListAdapter getInstance() {
        if (instance == null) {
            throw new IllegalStateException("ResultListAdapter not initialized");
        }
        return instance;
    }

    public static void update(Context context) {
        if (instance == null) {
            return;
        }
        instance.resultSummary = Persistence.getResultSummary(context);
        instance.notifyDataSetInvalidated();
    }

    @Override
    public int getGroupCount() {
        return resultSummary.getWorkers().size();
    }

    @Override
    public int getChildrenCount(int i) {
        return resultSummary.getWorkers().get(i).getWorkloads().size();
    }

    @Override
    public Object getGroup(int i) {
        return resultSummary.getWorkers().get(i);
    }

    @Override
    public Object getChild(int i, int i2) {
        return resultSummary.getWorkers().get(i).getWorkloads().get(i2);
    }

    @Override
    public long getGroupId(int i) {
        return resultSummary.getWorkers().get(i).getWorkerId();
    }

    @Override
    public long getChildId(int i, int i2) {
        return resultSummary.getWorkers().get(i).getWorkloads().get(i2).getExponent();
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = inflater.inflate(android.R.layout.simple_list_item_1, null);
        }
        TextView textView = (TextView) view;
        assert textView != null;
        textView.setText(WorkerModelSelector.getWorkerById(resultSummary.getWorkers().get(i).getWorkerId()).getName());
        return textView;
    }

    @Override
    public View getChildView(int i, int i2, boolean b, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = inflater.inflate(android.R.layout.simple_expandable_list_item_1, null);
        }
        TextView textView = (TextView) view;
        assert textView != null;
        ResultSummary.WorkloadSummary workloadSummary = resultSummary.getWorkers().get(i).getWorkloads().get(i2);
        textView.setText("Exponent: " + workloadSummary.getExponent() + " runs: " + workloadSummary.getBenchmarkCount());
        return textView;
    }

    @Override
    public boolean isChildSelectable(int i, int i2) {
        return true;
    }

    public interface OnBenchmarkSelectedListener {
        public void onBenchmarkSelected(int workerId, int exponent);
    }

    private List<OnBenchmarkSelectedListener> listeners = new LinkedList<>();

    private void notifyListeners(int workerId, int exponent) {
        for (OnBenchmarkSelectedListener listener : listeners) {
            listener.onBenchmarkSelected(workerId, exponent);
        }
    }

    public void addOnBenchmarkSelectedListener(OnBenchmarkSelectedListener listener) {
        listeners.add(listener);
    }

    public void removeOnBenchmarkSelectedListener(OnBenchmarkSelectedListener listener) {
        listeners.remove(listener);
    }
}
