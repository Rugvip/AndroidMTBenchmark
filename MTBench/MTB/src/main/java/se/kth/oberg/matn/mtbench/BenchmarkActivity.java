package se.kth.oberg.matn.mtbench;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;

import se.kth.oberg.matn.mtbench.model.WorkerModelSelector;

public class BenchmarkActivity extends ActionBarActivity implements ActionBar.OnNavigationListener {
    private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";
    private static final int PAGE_INDEX_BENCHMARK = 0;
    private static final int PAGE_INDEX_RESULT = 1;

    private ViewPagerAdapter viewPagerAdapter;
    private ViewPager viewPager;
    private final WorkerModelSelector workerSelector = new WorkerModelSelector();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_benchmark);

        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager = (ViewPager) findViewById(R.id.container);
        viewPager.setAdapter(viewPagerAdapter);

        final ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

        actionBar.setListNavigationCallbacks(
                new ArrayAdapter<>(
                        actionBar.getThemedContext(),
                        android.R.layout.simple_list_item_1,
                        android.R.id.text1,
                        workerSelector.getWorkers()),
                this);

        //TODO Worker factory
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
        workerSelector.setWorkerId(position);
        return true;
    }

    private class ViewPagerAdapter extends FragmentPagerAdapter {
        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            switch (i) {
                case PAGE_INDEX_BENCHMARK: {
                    BenchmarkFragment fragment = new BenchmarkFragment();
                    fragment.setWorkerModelSelector(workerSelector);
                    return fragment;
                }
                case PAGE_INDEX_RESULT: {
                    ResultFragment fragment = new ResultFragment();
                    fragment.setWorkerModelSelector(workerSelector);
                    return fragment;
                }
                default:
                    throw new IllegalStateException("Illegal fragment index requested");
            }
        }
        @Override
        public int getCount() {
            return 2;
        }
    }
}
