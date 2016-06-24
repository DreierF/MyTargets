/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */

package de.dreier.mytargets.activities;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import de.dreier.mytargets.R;
import de.dreier.mytargets.databinding.ActivityStatisticsBinding;
import de.dreier.mytargets.fragments.ArrowRankingFragment;
import de.dreier.mytargets.fragments.StatisticsFragment;
import de.dreier.mytargets.utils.ToolbarUtils;

public class StatisticsActivity extends ChildActivityBase {

    public static final String TRAINING_ID = "training_id";
    private static final String ROUND_ID = "round_id";

    private long mTraining;
    private long mRound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityStatisticsBinding binding = DataBindingUtil
                .setContentView(this, R.layout.activity_statistics);

        mTraining = getIntent().getLongExtra(TRAINING_ID, -1);
        mRound = getIntent().getLongExtra(ROUND_ID, -1);

        binding.pager.setAdapter(new StatisticsPagerAdapter(getSupportFragmentManager()));
        binding.pager.setCurrentItem(mRound == -1 ? 1 : 2, false);

        ToolbarUtils.showHomeAsUp(this);
    }

    private class StatisticsPagerAdapter extends FragmentStatePagerAdapter {
        StatisticsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 2) {
                return new ArrowRankingFragment();
            } else {
                StatisticsFragment fragment = new StatisticsFragment();
                Bundle bundle = new Bundle();
                bundle.putInt(StatisticsFragment.ARG_POSITION, position);
                bundle.putLong(StatisticsFragment.ARG_TRAINING_ID, mTraining);
                bundle.putLong(StatisticsFragment.ARG_ROUND_ID, mRound);
                fragment.setArguments(bundle);
                return fragment;
            }
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getString(R.string.all_trainings);
                case 1:
                    return getString(R.string.entire_training);
                default:
                    return getString(R.string.arrow_ranking);
            }
        }
    }
}
