/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */

package de.dreier.mytargets.activities;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import de.dreier.mytargets.utils.IntentWrapper;
import de.dreier.mytargets.utils.Pair;
import de.dreier.mytargets.utils.ToolbarUtils;
import de.dreier.mytargets.utils.Utils;
import de.dreier.mytargets.views.ChipGroup;
import icepick.Icepick;
import icepick.State;

public class StatisticsActivity extends ChildActivityBase {

    private static final String ROUND_IDS = "round_ids";

    @State
    boolean showFilter = false;
    private ActivityStatisticsBinding binding;
    private List<Round> rounds;

    @NonNull
    public static IntentWrapper showStatisticsIntent(Activity context, List<Long> roundIds) {
        Intent i = new Intent(context, StatisticsActivity.class);
        i.putExtra(ROUND_IDS, Utils.toArray(roundIds));
        return new IntentWrapper(context, i);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_statistics);
        setSupportActionBar(binding.toolbar);

        long[] roundIds = getIntent().getLongArrayExtra(ROUND_IDS);
        rounds = new RoundDataSource().getAll(roundIds);

        ToolbarUtils.showHomeAsUp(this);
        Icepick.restoreInstanceState(this, savedInstanceState);
        binding.distanceTags.setTags(getDistanceTags(rounds));
        binding.distanceTags.setOnTagClickListener(t -> applyFilter(rounds));
        binding.arrowTags.setTags(getArrowTags(rounds));
        binding.arrowTags.setOnTagClickListener(t -> applyFilter(rounds));
        binding.bowTags.setTags(getBowTags(rounds));
        binding.bowTags.setOnTagClickListener(t -> applyFilter(rounds));
        //TODO save filter on rotation

        updateFilter();
        applyFilter(rounds);
    }

    private List<ChipGroup.Tag> getBowTags(List<Round> rounds) {
        return Stream.of(rounds).map(r -> r.trainingId)
                .distinct()
                .map(tid -> new TrainingDataSource().get(tid).bow)
                .distinct()
                .map(bid -> {
                    if (bid > 0) {
                        Bow bow = new BowDataSource().get(bid);
                        if (bow == null) {
                            return new ChipGroup.Tag(0, "Deleted " + bid, true);
                        }
                        return new ChipGroup.Tag(bow.getId(), bow.getName(), bow.thumb, true);
                    } else {
                        return new ChipGroup.Tag(bid, getString(R.string.unknown), true);
                    }
                })
                .collect(Collectors.toList());
    }

    private List<ChipGroup.Tag> getArrowTags(List<Round> rounds) {
        return Stream.of(rounds).map(r -> r.trainingId)
                .distinct()
                .map(tid -> new TrainingDataSource().get(tid).arrow)
                .distinct()
                .map(aid -> {
                    if (aid > 0) {
                        Arrow arrow = new ArrowDataSource().get(aid);
                        if (arrow == null) {
                            return new ChipGroup.Tag(0, "Deleted " + aid, true);
                        }
                        return new ChipGroup.Tag(arrow.getId(), arrow.getName(), arrow.thumb, true);
                    } else {
                        return new ChipGroup.Tag(aid, getString(R.string.unknown), true);
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
        binding.viewPager.setAdapter(new StatisticsPagerAdapter(getSupportFragmentManager(),
                new PasseDataSource().groupByTarget(filteredRounds)));
    }

    private List<ChipGroup.Tag> getDistanceTags(List<Round> rounds) {
        return Stream.of(rounds).map(r -> r.info.distance)
                .distinct()
                .sorted()
                .map(d -> new ChipGroup.Tag(d.getId(), d.toString(), true))
                .collect(Collectors.toList());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.filter, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.action_filter).setIcon(showFilter ?
                R.drawable.ic_clear_filter_white_24dp :
                R.drawable.ic_filter_white_24dp);
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
        if (!showFilter) {
            Stream.of(binding.distanceTags.getTags())
                    .forEach(tag -> tag.isChecked = true);
            Stream.of(binding.arrowTags.getTags())
                    .forEach(tag -> tag.isChecked = true);
            Stream.of(binding.bowTags.getTags())
                    .forEach(tag -> tag.isChecked = true);
            binding.distanceTags.setTags(binding.distanceTags.getTags());
            binding.arrowTags.setTags(binding.arrowTags.getTags());
            binding.bowTags.setTags(binding.bowTags.getTags());
        }
        binding.filterView.setVisibility(showFilter ? View.VISIBLE : View.GONE);
        applyFilter(rounds);
        invalidateOptionsMenu();
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
