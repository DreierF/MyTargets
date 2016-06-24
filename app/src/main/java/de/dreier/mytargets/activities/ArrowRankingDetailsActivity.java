package de.dreier.mytargets.activities;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.ActionBar;

import org.parceler.Parcels;

import de.dreier.mytargets.R;
import de.dreier.mytargets.databinding.ActivityArrowRankingDetailsBinding;
import de.dreier.mytargets.models.ArrowStatistic;

public class ArrowRankingDetailsActivity extends ChildActivityBase {

    public static final String ITEM = "item";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityArrowRankingDetailsBinding binding = DataBindingUtil
                .setContentView(this, R.layout.activity_arrow_ranking_details);

        ArrowStatistic item = Parcels.unwrap(getIntent().getParcelableExtra(ITEM));
        binding.dispersionView.setShoots(item.shots);
        binding.dispersionView.setTarget(item.target.getDrawable());

        ActionBar actionBar = setupHomeAsUpActionBar();
        actionBar.setTitle(getString(R.string.arrow_number_x, item.arrowNumber));
        actionBar.setSubtitle(item.arrowName);
    }

}
