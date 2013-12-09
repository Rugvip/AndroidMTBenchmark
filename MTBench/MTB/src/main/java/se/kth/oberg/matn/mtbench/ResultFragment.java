package se.kth.oberg.matn.mtbench;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Observable;
import java.util.Observer;

import se.kth.oberg.matn.mtbench.model.BenchmarkResult;
import se.kth.oberg.matn.mtbench.model.WorkerModelSelector;
import se.kth.oberg.matn.mtbench.persistence.Persistence;

public class ResultFragment extends Fragment implements Employer, Observer {
    private static final String PREFERENCE_EXPONENT = "exponent";
    private static final String PREFERENCE_COUNT = "count";
    private static final int DEFAULT_EXPONENT = 16;
    private static final int DEFAULT_COUNT = 1;

    private TextView descriptionText;

    private WorkerModelSelector workerModelSelector;

    public ResultFragment() {
    }

    public void setWorkerModelSelector(WorkerModelSelector workerModelSelector) {
        this.workerModelSelector = workerModelSelector;
        workerModelSelector.addObserver(this);
        update(workerModelSelector, null);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle state) {
        View rootView = inflater.inflate(R.layout.fragment_result, container, false);
        assert rootView != null;

        descriptionText = (TextView) rootView.findViewById(R.id.sec);

        if (workerModelSelector != null) {
            descriptionText.setText(workerModelSelector.getWorker().getDescriptionResource());
        }

        rootView.findViewById(R.id.button_email).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mail();
            }
        });

        return rootView;
    }

    private void mail() {
        BenchmarkResult resultList = Persistence.getResult(getActivity(), workerModelSelector.getWorkerId(), 13);

        Intent send = new Intent(Intent.ACTION_SENDTO);
        String uriText = "mailto:" + Uri.encode("poldsberg@gmail.com") +
                "?subject=" + Uri.encode("Multithreading Benchmark Result - " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())) +
                "&body=" + Uri.encode("" + resultList);
        Uri uri = Uri.parse(uriText);

        send.setData(uri);
        startActivity(Intent.createChooser(send, "Send mail..."));
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
