package se.kth.oberg.matn.mtbench;

import android.content.SharedPreferences;
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
import java.util.Observable;
import java.util.Observer;

import se.kth.oberg.matn.mtbench.model.Benchmark;
import se.kth.oberg.matn.mtbench.model.BenchmarkResult;
import se.kth.oberg.matn.mtbench.model.WorkerModelSelector;
import se.kth.oberg.matn.mtbench.model.WorkCollection;
import se.kth.oberg.matn.mtbench.model.Worker;
import se.kth.oberg.matn.mtbench.persistence.Persistence;

public class BenchmarkFragment extends Fragment implements Employer, Observer {
    private static final String PREFERENCE_EXPONENT = "exponent";
    private static final String PREFERENCE_COUNT = "count";
    private static final int DEFAULT_EXPONENT = 16;
    private static final int DEFAULT_COUNT = 1;

    private TextView descriptionText;
    private TextView exponentText;
    private SeekBar exponentSeek;
    private TextView countText;
    private SeekBar countSeek;
    private Button runButton;
    private ProgressBar workProgress;
    private ProgressBar countProgress;

    private WorkerModelSelector workerModelSelector;
    private BenchmarkResult.Builder resultBuilder = null;
    private WorkCollection workCollection;
    private int testCount;

    public BenchmarkFragment() {
    }

    public void setWorkerModelSelector(WorkerModelSelector workerModelSelector) {
        this.workerModelSelector = workerModelSelector;
        workerModelSelector.addObserver(this);
        update(workerModelSelector, null);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle state) {
        View rootView = inflater.inflate(R.layout.fragment_benchmark, container, false);
        assert rootView != null;

        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        int exponent = sharedPreferences.getInt(PREFERENCE_EXPONENT, DEFAULT_EXPONENT);
        int count = sharedPreferences.getInt(PREFERENCE_COUNT, DEFAULT_COUNT);

        descriptionText = (TextView) rootView.findViewById(R.id.text_view_model_description);
        exponentText = (TextView) rootView.findViewById(R.id.text_view_exponent);
        exponentSeek = (SeekBar) rootView.findViewById(R.id.seek_bar_exponent);
        countText = (TextView) rootView.findViewById(R.id.text_view_count);
        countSeek = (SeekBar) rootView.findViewById(R.id.seek_bar_count);
        runButton = (Button) rootView.findViewById(R.id.button_run);
        workProgress = (ProgressBar) rootView.findViewById(R.id.progress_bar_work);
        countProgress = (ProgressBar) rootView.findViewById(R.id.progress_bar_count);

        if (workerModelSelector != null) {
            descriptionText.setText(workerModelSelector.getWorker().getDescriptionResource());
        }

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
        workerModelSelector.freeze();
        workCollection = WorkCollection.buildDefaultCollection(exponentSeek.getProgress());
        testCount = countSeek.getProgress() + 1;
        countProgress.setMax(testCount);
        countProgress.setProgress(0);
        exponentSeek.setEnabled(false);
        countSeek.setEnabled(false);
        runButton.setEnabled(false);
        runSingleBenchmark();
    }

    public void runSingleBenchmark() {
        new BenchmarkTask(workerModelSelector.getWorker()).execute(workCollection);
    }

    public void afterBenchmarkComplete() {
        exponentSeek.setEnabled(true);
        countSeek.setEnabled(true);
        runButton.setEnabled(true);

        Persistence.saveResult(getActivity(), workerModelSelector.getWorkerId(), resultBuilder.build());
        resultBuilder = null;
        workerModelSelector.thaw();
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
                    " 1st: " + benchmarkResult.getResults().get(0).getTimes()[0] +
                    " 2nd: " + benchmarkResult.getResults().get(1).getTimes()[0] +
                    " 3rd: " + benchmarkResult.getResults().get(2).getTimes()[0] +
                    " 4th: " + benchmarkResult.getResults().get(3).getTimes()[0]);

            if (resultBuilder == null) { // skip first result
                resultBuilder = BenchmarkResult.createBuilder(workCollection.getExponent());
            } else {
                resultBuilder.addResult(benchmarkResult);
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

    // / 1000000000.0
    private static final DecimalFormat format = new DecimalFormat("#.###");

    @Override
    public void update(Observable observable, Object o) {
        WorkerModelSelector workerModelSelector = (WorkerModelSelector) observable;

        if (descriptionText != null) {
            descriptionText.setText(workerModelSelector.getWorker().getDescriptionResource());
        }
    }
}
