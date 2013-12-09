package se.kth.oberg.matn.mtbench;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import se.kth.oberg.matn.mtbench.model.Benchmark;
import se.kth.oberg.matn.mtbench.model.BenchmarkResult;
import se.kth.oberg.matn.mtbench.model.WorkCollection;
import se.kth.oberg.matn.mtbench.model.Worker;
import se.kth.oberg.matn.mtbench.persistence.Persistence;

public class WorkerFragment extends Fragment {
    private static final String PREFERENCE_EXPONENT = "exponent";
    private static final String PREFERENCE_COUNT = "count";
    private static final int DEFAULT_EXPONENT = 16;
    private static final int DEFAULT_COUNT = 1;
    private static final String ARG_WORKER = "worker";

    private TextView exponentText;
    private SeekBar exponentSeek;
    private TextView countText;
    private SeekBar countSeek;
    private Button runButton;
    private ProgressBar workProgress;
    private ProgressBar countProgress;

    private Worker worker;
    private List<BenchmarkResult> results = null;
    private WorkCollection workCollection;
    private int testCount;

    public static WorkerFragment newInstance(Worker worker) {
        WorkerFragment fragment = new WorkerFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_WORKER, worker);
        fragment.setArguments(args);
        return fragment;
    }

    public WorkerFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle state) {
        View rootView = inflater.inflate(R.layout.fragment_benchmark, container, false);
        assert rootView != null;

        worker = (Worker) getArguments().getSerializable(ARG_WORKER);
        assert worker != null;

        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        int exponent = sharedPreferences.getInt(PREFERENCE_EXPONENT, DEFAULT_EXPONENT);
        int count = sharedPreferences.getInt(PREFERENCE_COUNT, DEFAULT_COUNT);

        TextView textView = (TextView) rootView.findViewById(R.id.section_label);
        textView.setText(worker.getDescriptionResource());

        exponentText = (TextView) rootView.findViewById(R.id.text_view_exponent);
        exponentSeek = (SeekBar) rootView.findViewById(R.id.seek_bar_exponent);
        countText = (TextView) rootView.findViewById(R.id.text_view_count);
        countSeek = (SeekBar) rootView.findViewById(R.id.seek_bar_count);
        runButton = (Button) rootView.findViewById(R.id.button_run);
        workProgress = (ProgressBar) rootView.findViewById(R.id.progress_bar_work);
        countProgress = (ProgressBar) rootView.findViewById(R.id.progress_bar_count);

        exponentText.setText("" + exponent);
        exponentSeek.setProgress(exponent);
        countText.setText("" + count);
        countSeek.setProgress(count - 1);

        exponentSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                exponentText.setText("" + progress);
                sharedPreferences.edit().putInt(PREFERENCE_EXPONENT, progress).commit();
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) { }
            @Override public void onStopTrackingTouch(SeekBar seekBar) { }
        });
        countSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                countText.setText("" + (progress + 1));
                sharedPreferences.edit().putInt(PREFERENCE_COUNT, progress + 1).commit();
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) { }
            @Override public void onStopTrackingTouch(SeekBar seekBar) { }
        });

        runButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                runBenchmark();
            }
        });

        rootView.findViewById(R.id.button_reset).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Persistence.resetDatabase(getActivity());
            }
        });

        return rootView;
    }

    public void runBenchmark() {
        workCollection = WorkCollection.buildDefaultCollection(exponentSeek.getProgress());
        testCount = countSeek.getProgress() + 1;
        countProgress.setMax(testCount);
        countProgress.setProgress(0);
        exponentSeek.setEnabled(false);
        countSeek.setEnabled(false);
        runSingleBenchmark();
    }

    public void runSingleBenchmark() {
        new BenchmarkTask(worker).execute(workCollection);
    }

    public void afterBenchmarkComplete() {
        exponentSeek.setEnabled(true);
        countSeek.setEnabled(true);

        for (BenchmarkResult result : results) {
            Persistence.saveResult(getActivity(), result);
        }
        results = null;
    }

    private class BenchmarkTask extends Benchmark {
        public BenchmarkTask(Worker worker) {
            super(worker);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            workProgress.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(BenchmarkResult benchmarkResult) {
            Log.e("Result", "size: " + benchmarkResult.getResults().size() +
                    " 1st: " + benchmarkResult.getResults().get(0).getTime() +
                    " 2nd: " + benchmarkResult.getResults().get(1).getTime() +
                    " 3rd: " + benchmarkResult.getResults().get(2).getTime() +
                    " 4th: " + benchmarkResult.getResults().get(3).getTime());

            if (results == null) { // skip first result
                results = new LinkedList<>();
            } else {
                results.add(benchmarkResult);
            }

            workProgress.setProgress(0);
            countProgress.setProgress(countProgress.getMax() - (testCount - 1));

            if (testCount --> 0) {
                runSingleBenchmark();
            } else {
                afterBenchmarkComplete();
            }
        }
    }

    private void mail() {
        Intent send = new Intent(Intent.ACTION_SENDTO);
        String uriText = "mailto:" + Uri.encode("poldsberg@gmail.com,axel.odelberg@gmail.com") +
                "?subject=" + Uri.encode("Multithreading Benchmark Result - " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())) +
                "&body=" + Uri.encode("ze body");
        Uri uri = Uri.parse(uriText);

        send.setData(uri);
        startActivity(Intent.createChooser(send, "Send mail..."));
    }

    // / 1000000000.0
    private static final DecimalFormat format = new DecimalFormat("#.###");
}
