package se.kth.oberg.matn.mtbench;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

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

    public static void init(LayoutInflater inflater, Context context) {
        instance = new ResultListAdapter(inflater);
        update(context);
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
            view = inflater.inflate(R.layout.expand_list_item_worker, null);
        }
        TextView textView = (TextView) view.findViewById(android.R.id.text1);
        assert textView != null;
        textView.setText(WorkerModelSelector.getWorkerById(resultSummary.getWorkers().get(i).getWorkerId()).getName());
        return view;
    }

    @Override
    public View getChildView(final int i, final int i2, boolean b, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = inflater.inflate(R.layout.expand_list_item_workload, null);
        }

        final ResultSummary.WorkloadSummary workloadSummary = resultSummary.getWorkers().get(i).getWorkloads().get(i2);

        final TextView textView = (TextView) view.findViewById(android.R.id.text1);
        assert textView != null;
        textView.setText("Exponent: " + workloadSummary.getExponent() + " runs: " + workloadSummary.getBenchmarkCount());

        final View button = view.findViewById(android.R.id.button1);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {
                    listener.onBenchmarkSelected(resultSummary.getWorkers().get(i).getWorkerId(), workloadSummary.getExponent());
                }
            }
        });

        return view;
    }

    @Override
    public boolean isChildSelectable(int i, int i2) {
        return true;
    }

    public interface OnBenchmarkSelectedListener {
        public void onBenchmarkSelected(int workerId, int exponent);
    }

    private OnBenchmarkSelectedListener listener = null;

    public void setOnBenchmarkSelectedListener(OnBenchmarkSelectedListener listener) {
        this.listener = listener;
    }
}
