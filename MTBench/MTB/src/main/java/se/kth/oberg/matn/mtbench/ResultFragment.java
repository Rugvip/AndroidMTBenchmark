package se.kth.oberg.matn.mtbench;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Observable;
import java.util.Observer;

import se.kth.oberg.matn.mtbench.model.BenchmarkResult;
import se.kth.oberg.matn.mtbench.model.WorkerModelSelector;
import se.kth.oberg.matn.mtbench.persistence.Persistence;

public class ResultFragment extends Fragment {
    private static final String PREFERENCE_EXPONENT = "exponent";
    private static final String PREFERENCE_COUNT = "count";
    private static final int DEFAULT_EXPONENT = 16;
    private static final int DEFAULT_COUNT = 1;

    public ResultFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle state) {
        View rootView = inflater.inflate(R.layout.fragment_result, container, false);
        assert rootView != null;

        ResultListAdapter.getInstance().init(getLayoutInflater(state));

        ExpandableListView expandableListView = (ExpandableListView) rootView.findViewById(R.id.expandable_list_view_result_summary);
        expandableListView.setAdapter(ResultListAdapter.getInstance());
        ResultListAdapter.getInstance().update(getActivity());

        rootView.findViewById(R.id.button_drop_base).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Persistence.resetDatabase(getActivity());
                return false;
            }
        });

        return rootView;
    }

    private void mail(int workerId, int exponent) {
        BenchmarkResult result = Persistence.getResult(getActivity(), workerId, exponent);

        Intent send = new Intent(Intent.ACTION_SENDTO);
        String uriText = "mailto:" + Uri.encode("poldsberg@gmail.com") +
                "?subject=" + Uri.encode("Multithreading Benchmark Result | " +
                WorkerModelSelector.getWorkerById(workerId) + " | " +
                result.getExponent() + " | " +
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())) +
                "&body=" + Uri.encode("" + result);
        Uri uri = Uri.parse(uriText);

        send.setData(uri);
        startActivity(Intent.createChooser(send, "Send mail..."));
    }

    // / 1000000000.0
    private static final DecimalFormat format = new DecimalFormat("#.###");
}
