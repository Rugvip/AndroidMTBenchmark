package se.kth.oberg.matn.mtbench;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import se.kth.oberg.matn.mtbench.model.BenchmarkResult;
import se.kth.oberg.matn.mtbench.model.Worker;
import se.kth.oberg.matn.mtbench.persistence.Persistence;

public class ResultFragment extends Fragment implements Employer {
    private static final String PREFERENCE_EXPONENT = "exponent";
    private static final String PREFERENCE_COUNT = "count";
    private static final int DEFAULT_EXPONENT = 16;
    private static final int DEFAULT_COUNT = 1;

    private TextView descriptionText;

    private Worker worker;
    private List<BenchmarkResult> results = null;

    public ResultFragment() {
    }

    @Override
    public void setWorker(Worker worker) {
        this.worker = worker;
        Log.e("setWorker", "worker: " + worker + " text: " + descriptionText);
        if (descriptionText != null) {
            descriptionText.setText(worker.getDescriptionResource());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle state) {
        View rootView = inflater.inflate(R.layout.fragment_result, container, false);
        assert rootView != null;

        descriptionText = (TextView) rootView.findViewById(R.id.sec);

        if (worker != null) {
            descriptionText.setText(worker.getDescriptionResource());
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
        BenchmarkResult resultList = Persistence.getResult(getActivity(), worker.getId(), 14);

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
}
