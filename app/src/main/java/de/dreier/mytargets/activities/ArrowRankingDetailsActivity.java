package de.dreier.mytargets.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import de.dreier.mytargets.R;
import de.dreier.mytargets.models.ArrowStatistic;
import de.dreier.mytargets.views.ArrowDispersionView;

public class ArrowRankingDetailsActivity extends AppCompatActivity {

    public static final String ITEM = "item";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arrow_ranking_details);

        ArrowStatistic item = (ArrowStatistic) getIntent().getSerializableExtra(ITEM);
        ArrowDispersionView adv = (ArrowDispersionView) findViewById(R.id.dispersionView);
        adv.setShoots(item.shots);
        adv.setTarget(item.target);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Arrow number " + item.arrowNumber);
        actionBar.setSubtitle(item.arrowName);
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.left_in, R.anim.right_out);
    }
}
