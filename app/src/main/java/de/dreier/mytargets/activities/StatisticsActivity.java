/*
 * Copyright (C) 2016 Florian Dreier
 *
 * This file is part of MyTargets.
 *
 * MyTargets is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2
 * as published by the Free Software Foundation.
 *
 * MyTargets is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */

package de.dreier.mytargets.activities;

import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.Intent;
import android.content.Loader;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.util.LongSparseArray;
import android.support.v4.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;

import java.util.Collections;
import java.util.List;

import de.dreier.mytargets.R;
import de.dreier.mytargets.databinding.ActivityStatisticsBinding;
import de.dreier.mytargets.fragments.StatisticsFragment;
import de.dreier.mytargets.shared.models.Target;
import de.dreier.mytargets.shared.models.db.Arrow;
import de.dreier.mytargets.shared.models.db.Bow;
import de.dreier.mytargets.shared.models.db.Round;
import de.dreier.mytargets.shared.models.db.Training;
import de.dreier.mytargets.shared.utils.LongUtils;
import de.dreier.mytargets.shared.utils.Pair;
import de.dreier.mytargets.utils.IntentWrapper;
import de.dreier.mytargets.utils.ToolbarUtils;
import de.dreier.mytargets.views.ChipGroup;
import icepick.Icepick;
import icepick.State;

public class StatisticsActivity extends ChildActivityBase implements LoaderManager.LoaderCallbacks<List<Pair<Training, Round>>> {

    private static final String ROUND_IDS = "round_ids";

    @State
    boolean showFilter = false;
    private ActivityStatisticsBinding binding;
    private List<Pair<Training, Round>> rounds;

    @NonNull
    public static IntentWrapper getIntent(Fragment fragment, List<Long> roundIds) {
        Intent i = new Intent(fragment.getContext(), StatisticsActivity.class);
        i.putExtra(ROUND_IDS, LongUtils.toArray(roundIds));
        return new IntentWrapper(fragment, i);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_statistics);
        setSupportActionBar(binding.toolbar);

        binding.progressBar.show();

        ToolbarUtils.showHomeAsUp(this);
        Icepick.restoreInstanceState(this, savedInstanceState);

        getLoaderManager().initLoader(0, getIntent().getExtras(), this).forceLoad();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.filter, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        final MenuItem filter = menu.findItem(R.id.action_filter);
        filter.setIcon(showFilter ?
                R.drawable.ic_clear_filter_white_24dp :
                R.drawable.ic_filter_white_24dp);
        filter.setVisible(rounds != null);
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

    private void applyFilter() {
        List<String> distanceTags = Stream.of(binding.distanceTags.getCheckedTags())
                .map(t -> t.text).collect(Collectors.toList());
        List<Long> arrowTags = Stream.of(binding.arrowTags.getCheckedTags())
                .map(t -> t.id).collect(Collectors.toList());
        List<Long> bowTags = Stream.of(binding.bowTags.getCheckedTags())
                .map(t -> t.id).collect(Collectors.toList());
        List<Pair<Target, List<Round>>> filteredRounds = Stream.of(rounds)
                .filter(pair -> distanceTags.contains(pair.second.info.distance.toString())
                        && arrowTags.contains(pair.first.arrow)
                        && bowTags.contains(pair.first.bow))
                .map(p -> p.second)
                .groupBy(value -> new Pair<>(value.getTarget().getId(),
                        value.getTarget().scoringStyle))
                .map(value1 -> new Pair<>(value1.getValue().get(0).getTarget(), value1.getValue()))
                .collect(Collectors.toList());
        boolean animate = binding.viewPager.getAdapter() == null;
        final StatisticsPagerAdapter adapter = new StatisticsPagerAdapter(
                getSupportFragmentManager(), filteredRounds, animate);
        binding.viewPager.setAdapter(adapter);
    }

    private List<ChipGroup.Tag> getBowTags() {
        return Stream.of(rounds)
                .map(p -> p.first.bow)
                .distinct()
                .map(bid -> {
                    if (bid > 0) {
                        Bow bow = Bow.get(bid);
                        if (bow == null) {
                            return new ChipGroup.Tag(bid, "Deleted " + bid, true);
                        }
                        return new ChipGroup.Tag(bow.getId(), bow.getName(), bow.thumbnail.getBlob().getBlob(), true);
                    } else {
                        return new ChipGroup.Tag(bid, getString(R.string.unknown), true);
                    }
                })
                .collect(Collectors.toList());
    }

    private List<ChipGroup.Tag> getArrowTags() {
        return Stream.of(rounds)
                .map(p -> p.first.arrow)
                .distinct()
                .map(aid -> {
                    if (aid > 0) {
                        Arrow arrow = Arrow.get(aid);
                        if (arrow == null) {
                            return new ChipGroup.Tag(aid, "Deleted " + aid, true);
                        }
                        return new ChipGroup.Tag(arrow.getId(), arrow.getName(), arrow.thumbnail.getBlob().getBlob(), true);
                    } else {
                        return new ChipGroup.Tag(aid, getString(R.string.unknown), true);
                    }
                })
                .collect(Collectors.toList());
    }

    private List<ChipGroup.Tag> getDistanceTags() {
        return Stream.of(rounds)
                .map(p -> p.second.info.distance)
                .distinct()
                .sorted()
                .map(d -> new ChipGroup.Tag(d.getId(), d.toString(), true))
                .collect(Collectors.toList());
    }

    protected void updateFilter() {
        if (!showFilter) {
            resetFilter();
        }
        binding.filterView.setVisibility(showFilter ? View.VISIBLE : View.GONE);
        applyFilter();
        invalidateOptionsMenu();
    }

    private void resetFilter() {
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

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Icepick.saveInstanceState(this, outState);
    }

    @Override
    public Loader<List<Pair<Training, Round>>> onCreateLoader(int i, Bundle bundle) {
        final long[] roundIds = getIntent().getLongArrayExtra(ROUND_IDS);
        return new AsyncTaskLoader<List<Pair<Training, Round>>>(this) {
            @Override
            public List<Pair<Training, Round>> loadInBackground() {
                final List<Round> rounds = Round.getAll(roundIds);
                LongSparseArray<Training> trainingsMap = new LongSparseArray<>();
                Stream.of(rounds).map(round -> round.trainingId)
                        .distinct()
                        .map(Training::get)
                        .forEach(training -> trainingsMap.append(training.getId(), training));
                return Stream.of(rounds)
                        .map(round -> new Pair<>(trainingsMap.get(round.trainingId), round))
                        .collect(Collectors.toList());
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<List<Pair<Training, Round>>> loader, List<Pair<Training, Round>> data) {
        rounds = data;
        binding.progressBar.hide();
        binding.distanceTags.setTags(getDistanceTags());
        binding.distanceTags.setOnTagClickListener(t -> applyFilter());
        binding.arrowTags.setTags(getArrowTags());
        binding.arrowTags.setOnTagClickListener(t -> applyFilter());
        binding.bowTags.setTags(getBowTags());
        binding.bowTags.setOnTagClickListener(t -> applyFilter());
        //TODO save filter on rotation

        updateFilter();
    }

    @Override
    public void onLoaderReset(Loader<List<Pair<Training, Round>>> loader) {

    }

    private class StatisticsPagerAdapter extends FragmentStatePagerAdapter {
        private final List<Pair<Target, List<Round>>> targets;
        private final boolean animate;

        StatisticsPagerAdapter(FragmentManager fm, List<Pair<Target, List<Round>>> pairs, boolean animate) {
            super(fm);
            targets = pairs;
            this.animate = animate;
            Collections.sort(targets, (p1, p2) -> p2.second.size() - p1.second.size());
        }

        @Override
        public Fragment getItem(int position) {
            final Pair<Target, List<Round>> item = targets.get(position);
            final List<Long> roundIds = Stream.of(item.second)
                    .map(Round::getId)
                    .collect(Collectors.toList());
            return StatisticsFragment.newInstance(roundIds, item.first, animate);
        }

        @Override
        public int getCount() {
            return targets.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return targets.get(position).first.toString();
        }
    }
}
