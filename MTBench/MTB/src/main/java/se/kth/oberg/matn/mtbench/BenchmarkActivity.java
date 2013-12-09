package se.kth.oberg.matn.mtbench;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;

import se.kth.oberg.matn.mtbench.model.Model1Worker;
import se.kth.oberg.matn.mtbench.model.Model2Worker;
import se.kth.oberg.matn.mtbench.model.Worker;

public class BenchmarkActivity extends ActionBarActivity implements ActionBar.OnNavigationListener {
    private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";

    private static final Worker[] workers = new Worker[] {
            new Model1Worker(),
            new Model2Worker()
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_benchmark);

        final ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

        actionBar.setListNavigationCallbacks(
                new ArrayAdapter<>(
                        actionBar.getThemedContext(),
                        android.R.layout.simple_list_item_1,
                        android.R.id.text1,
                        workers),
                this);
    }

    @Override
    public void onRestoreInstanceState(Bundle state) {
        if (state.containsKey(STATE_SELECTED_NAVIGATION_ITEM)) {
            getSupportActionBar().setSelectedNavigationItem(state.getInt(STATE_SELECTED_NAVIGATION_ITEM));
        }
    }

    @Override
    public void onSaveInstanceState(Bundle state) {
        state.putInt(STATE_SELECTED_NAVIGATION_ITEM, getSupportActionBar().getSelectedNavigationIndex());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.model1, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(int position, long id) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, WorkerFragment.newInstance(workers[position]))
                .commit();
        return true;
    }
}
