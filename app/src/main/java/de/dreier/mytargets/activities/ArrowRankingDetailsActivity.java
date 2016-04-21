package de.dreier.mytargets.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import org.parceler.Parcels;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.dreier.mytargets.R;
import de.dreier.mytargets.models.ArrowStatistic;
import de.dreier.mytargets.views.ArrowDispersionView;

public class ArrowRankingDetailsActivity extends AppCompatActivity {

    public static final String ITEM = "item";

    @Bind(R.id.dispersionView)
    ArrowDispersionView adv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arrow_ranking_details);
        ButterKnife.bind(this);

        ArrowStatistic item = Parcels.unwrap(getIntent().getParcelableExtra(ITEM));
        adv.setShoots(item.shots);
        adv.setTarget(item.target.getDrawable());

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle(getString(R.string.arrow_number_x, item.arrowNumber));
        actionBar.setSubtitle(item.arrowName);
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
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
