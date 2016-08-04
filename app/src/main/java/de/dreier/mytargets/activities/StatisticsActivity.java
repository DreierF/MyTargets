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

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;

import org.parceler.Parcels;

import java.util.Collections;
import java.util.List;

import de.dreier.mytargets.R;
import de.dreier.mytargets.databinding.ActivityStatisticsBinding;
import de.dreier.mytargets.fragments.StatisticsFragment;
import de.dreier.mytargets.shared.models.Target;
import de.dreier.mytargets.shared.models.db.Passe;
import de.dreier.mytargets.shared.models.db.Round;
import de.dreier.mytargets.shared.models.db.Training;
import de.dreier.mytargets.shared.utils.Pair;
import de.dreier.mytargets.utils.ToolbarUtils;
import de.dreier.mytargets.utils.Utils;

public class StatisticsActivity extends ChildActivityBase {

    public static final String TRAINING_ID = "training_id";
    public static final String ROUND_ID = "round_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityStatisticsBinding binding = DataBindingUtil
                .setContentView(this, R.layout.activity_statistics);

        long trainingId = getIntent().getLongExtra(TRAINING_ID, -1);
        long roundId = getIntent().getLongExtra(ROUND_ID, -1);

        List<Round> rounds;
        if (roundId == -1) {
            if (trainingId == -1) {
                rounds = Stream.of(Training.getAll())
                        .flatMap(t -> Stream.of(t.getRounds()))
                        .collect(Collectors.toList());
            } else {
                rounds = Training.get(trainingId).getRounds();
            }
        } else {
            rounds = Collections.singletonList(Round.get(roundId));
        }
        binding.viewPager.setAdapter(new StatisticsPagerAdapter(getSupportFragmentManager(), Passe.groupByTarget(rounds)));

        ToolbarUtils.showHomeAsUp(this);
    }

    private class StatisticsPagerAdapter extends FragmentStatePagerAdapter {
        private final List<Pair<Target, List<Round>>> targets;

        StatisticsPagerAdapter(FragmentManager fm, List<Pair<Target, List<Round>>> pairs) {
            super(fm);
            targets = pairs;
            Collections.sort(targets, (p1, p2) -> p2.getSecond().size() - p1.getSecond().size());
        }

        @Override
        public Fragment getItem(int position) {
            StatisticsFragment fragment = new StatisticsFragment();
            Bundle bundle = new Bundle();
            final Pair<Target, List<Round>> item = targets.get(position);
            bundle.putParcelable(StatisticsFragment.ARG_TARGET, Parcels.wrap(item.getFirst()));
            final List<Long> roundIds = Stream.of(item.getSecond())
                    .map(Round::getId)
                    .collect(Collectors.toList());
            bundle.putLongArray(StatisticsFragment.ARG_ROUND_IDS, Utils.toArray(roundIds));
            fragment.setArguments(bundle);
            return fragment;
        }

        @Override
        public int getCount() {
            return targets.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return targets.get(position).getFirst().toString();
        }
    }
}
