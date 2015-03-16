package de.dreier.mytargets.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;

import de.dreier.mytargets.R;
import de.dreier.mytargets.fragments.PasseFragment;
import de.dreier.mytargets.fragments.RoundFragment;

public abstract class SimpleFragmentActivity extends ActionBarActivity {

    public abstract Fragment instantiateFragment();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frame_layout);
        Fragment childFragment = instantiateFragment();
        Bundle bundle = getIntent() != null ? getIntent().getExtras() : null;
        childFragment.setArguments(bundle);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content, childFragment).commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                overridePendingTransition(R.anim.left_in, R.anim.right_out);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.left_in, R.anim.right_out);
    }

    public static class RoundActivity extends SimpleFragmentActivity {

        @Override
        public Fragment instantiateFragment() {
            return new RoundFragment();
        }
    }

    public static class PasseActivity extends SimpleFragmentActivity {

        @Override
        public Fragment instantiateFragment() {
            return new PasseFragment();
        }
    }
}