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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.dreier.mytargets.R;
import de.dreier.mytargets.databinding.ActivityStatisticsBinding;
import de.dreier.mytargets.fragments.StatisticsFragment;
import de.dreier.mytargets.managers.dao.ArrowDataSource;
import de.dreier.mytargets.managers.dao.BowDataSource;
import de.dreier.mytargets.managers.dao.PasseDataSource;
import de.dreier.mytargets.managers.dao.RoundDataSource;
import de.dreier.mytargets.managers.dao.TrainingDataSource;
import de.dreier.mytargets.shared.models.Arrow;
import de.dreier.mytargets.shared.models.Bow;
import de.dreier.mytargets.shared.models.Round;
import de.dreier.mytargets.shared.models.Target;
import de.dreier.mytargets.shared.models.Training;
import de.dreier.mytargets.utils.Pair;
import de.dreier.mytargets.utils.ToolbarUtils;
import de.dreier.mytargets.utils.Utils;
import de.dreier.mytargets.views.TagGroup;
import icepick.Icepick;
import icepick.State;

public class StatisticsActivity extends ChildActivityBase {

    public static final String TRAINING_ID = "training_id";
    public static final String ROUND_IDS = "round_id";
    @State
    boolean showFilter = false;
    private ActivityStatisticsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil
                .setContentView(this, R.layout.activity_statistics);

        long trainingId = getIntent().getLongExtra(TRAINING_ID, -1);
        long roundId = getIntent().getLongExtra(ROUND_IDS, -1);

        setSupportActionBar(binding.toolbar);

        List<Round> rounds;
        if (roundId == -1) {
            if (trainingId == -1) {
                rounds = new RoundDataSource().getAll();
            } else {
                rounds = new RoundDataSource().getAll(trainingId);
            }
        } else {
            rounds = Collections.singletonList(new RoundDataSource().get(roundId));
        }

        ToolbarUtils.showHomeAsUp(this);
        Icepick.restoreInstanceState(this, savedInstanceState);
        binding.distanceTags.setTags(getDistanceTags(rounds));
        binding.distanceTags.setOnTagClickListener(t -> applyFilter(rounds));
        binding.arrowTags.setTags(getArrowTags(rounds));
        binding.arrowTags.setOnTagClickListener(t -> applyFilter(rounds));
        binding.bowTags.setTags(getBowTags(rounds));
        binding.bowTags.setOnTagClickListener(t -> applyFilter(rounds));

        updateFilter();
        applyFilter(rounds);
    }

    private List<TagGroup.Tag> getBowTags(List<Round> rounds) {
        return Stream.of(rounds).map(r -> r.trainingId)
                .distinct()
                .map(tid -> new TrainingDataSource().get(tid).bow)
                .distinct()
                .map(bid -> {
                    if (bid > 0) {
                        Bow bow = new BowDataSource().get(bid);
                        return new TagGroup.Tag(bow.getId(), bow.getName(), true);
                    } else {
                        return new TagGroup.Tag(bid, getString(R.string.unknown), true);
                    }
                })
                .collect(Collectors.toList());
    }

    private List<TagGroup.Tag> getArrowTags(List<Round> rounds) {
        return Stream.of(rounds).map(r -> r.trainingId)
                .distinct()
                .map(tid -> new TrainingDataSource().get(tid).arrow)
                .distinct()
                .map(aid -> {
                    if (aid > 0) {
                        Arrow arrow = new ArrowDataSource().get(aid);
                        return new TagGroup.Tag(arrow.getId(), arrow.getName(), true);
                    } else {
                        return new TagGroup.Tag(aid, getString(R.string.unknown), true);
                    }
                })
                .collect(Collectors.toList());
    }

    private void applyFilter(List<Round> rounds) {
        List<String> distanceTags = Stream.of(binding.distanceTags.getCheckedTags())
                .map(t -> t.text).collect(Collectors.toList());
        List<Long> arrowTags = Stream.of(binding.arrowTags.getCheckedTags())
                .map(t -> t.id).collect(Collectors.toList());
        List<Long> bowTags = Stream.of(binding.bowTags.getCheckedTags())
                .map(t -> t.id).collect(Collectors.toList());
        List<Round> filteredRounds = new ArrayList<>();
        for (Round round : rounds) {
            Training training = new TrainingDataSource().get(round.trainingId);
            if (distanceTags.contains(round.info.distance.toString())
                    && arrowTags.contains(training.arrow)
                    && bowTags.contains(training.bow)) {
                filteredRounds.add(round);
            }
        }
        binding.viewPager.setAdapter(new StatisticsPagerAdapter(getSupportFragmentManager(), new PasseDataSource().groupByTarget(filteredRounds)));
    }

    private List<TagGroup.Tag> getDistanceTags(List<Round> rounds) {
        return Stream.of(rounds).map(r -> r.info.distance)
                .distinct()
                .sorted()
                .map(d -> new TagGroup.Tag(d.getId(), d.toString(), true))
                .collect(Collectors.toList());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.filter, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_filter:
                showFilter = !showFilter;
                updateFilter();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    protected void updateFilter() {
        binding.filterView.setVisibility(showFilter ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Icepick.saveInstanceState(this, outState);
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
