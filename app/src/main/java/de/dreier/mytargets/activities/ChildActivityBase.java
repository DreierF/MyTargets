package de.dreier.mytargets.activities;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.view.MenuItem;

import de.dreier.mytargets.R;
import de.dreier.mytargets.utils.Utils;

public class ChildActivityBase extends AppCompatActivity {

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (Utils.isLollipop()) {
            finishAfterTransition();
        } else {
            finish();
            overridePendingTransition(R.anim.left_in, R.anim.right_out);
        }
    }
}
