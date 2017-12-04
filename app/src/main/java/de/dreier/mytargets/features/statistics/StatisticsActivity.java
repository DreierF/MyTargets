/*
 * Copyright (C) 2017 Florian Dreier
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

package de.dreier.mytargets.features.statistics;

import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.Intent;
import android.content.Loader;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.util.LongSparseArray;
import android.support.v4.util.Pair;
import android.view.Menu;
import android.view.MenuItem;

import com.afollestad.materialdialogs.MaterialDialog;
import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.dreier.mytargets.R;
import de.dreier.mytargets.base.activities.ChildActivityBase;
import de.dreier.mytargets.databinding.ActivityStatisticsBinding;
import de.dreier.mytargets.shared.models.Target;
import de.dreier.mytargets.shared.models.db.Arrow;
import de.dreier.mytargets.shared.models.db.Bow;
import de.dreier.mytargets.shared.models.db.Round;
import de.dreier.mytargets.shared.models.db.Training;
import de.dreier.mytargets.shared.utils.FileUtils;
import de.dreier.mytargets.shared.utils.LongUtils;
import de.dreier.mytargets.shared.utils.ParcelsBundler;
import de.dreier.mytargets.shared.utils.SharedUtils;
import de.dreier.mytargets.utils.IntentWrapper;
import de.dreier.mytargets.utils.ToolbarUtils;
import icepick.Icepick;
import icepick.State;

import static android.support.v4.view.GravityCompat.END;

public class StatisticsActivity extends ChildActivityBase implements LoaderManager.LoaderCallbacks<List<Pair<Training, Round>>> {

    @VisibleForTesting
    public static final String ROUND_IDS = "round_ids";

    private ActivityStatisticsBinding binding;
    private List<Pair<Training, Round>> rounds;
    private List<Pair<Target, List<Round>>> filteredRounds;

    @State(ParcelsBundler.class)
    List<String> distanceTags;

    @State(ParcelsBundler.class)
    List<String> diameterTags;

    @State(ParcelsBundler.class)
    List<Long> arrowTags;

    @State(ParcelsBundler.class)
    List<Long> bowTags;

    @NonNull
    public static IntentWrapper getIntent(@NonNull List<Long> roundIds) {
        return new IntentWrapper(StatisticsActivity.class)
                .with(ROUND_IDS, LongUtils.toArray(roundIds));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_statistics);
        setSupportActionBar(binding.toolbar);

        binding.reset.setOnClickListener(v -> resetFilter());

        binding.progressBar.show();

        ToolbarUtils.showHomeAsUp(this);
        Icepick.restoreInstanceState(this, savedInstanceState);

        getLoaderManager().initLoader(0, getIntent().getExtras(), this).forceLoad();
    }

    @NonNull
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
        binding.diameterTags.setTags(getDiameterTags());
        binding.diameterTags.setOnTagClickListener(t -> applyFilter());
        binding.arrowTags.setTags(getArrowTags());
        binding.arrowTags.setOnTagClickListener(t -> applyFilter());
        binding.bowTags.setTags(getBowTags());
        binding.bowTags.setOnTagClickListener(t -> applyFilter());

        if (distanceTags != null && diameterTags != null && arrowTags != null && bowTags != null) {
            restoreCheckedStates();
        }

        applyFilter();
        invalidateOptionsMenu();
    }

    private void restoreCheckedStates() {
        Stream.of(binding.distanceTags.getTags())
                .forEach(tag -> tag.isChecked = Stream.of(distanceTags)
                        .anyMatch(d -> SharedUtils.equals(d, tag.text)));
        Stream.of(binding.diameterTags.getTags())
                .forEach(tag -> tag.isChecked = Stream.of(diameterTags)
                        .anyMatch(d -> SharedUtils.equals(d, tag.text)));
        Stream.of(binding.arrowTags.getTags())
                .forEach(tag -> tag.isChecked = Stream.of(arrowTags)
                        .anyMatch(a -> SharedUtils.equals(a, tag.id)));
        Stream.of(binding.bowTags.getTags())
                .forEach(tag -> tag.isChecked = Stream.of(bowTags)
                        .anyMatch(b -> SharedUtils.equals(b, tag.id)));
        binding.distanceTags.setTags(binding.distanceTags.getTags());
        binding.diameterTags.setTags(binding.diameterTags.getTags());
        binding.arrowTags.setTags(binding.arrowTags.getTags());
        binding.bowTags.setTags(binding.bowTags.getTags());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.export_filter, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(@NonNull Menu menu) {
        super.onPrepareOptionsMenu(menu);
        final MenuItem filter = menu.findItem(R.id.action_filter);
        final MenuItem export = menu.findItem(R.id.action_export);
        // only show filter if we have at least one category to filter by
        boolean filterAvailable = binding.distanceTags.getTags().size() > 1
                || binding.diameterTags.getTags().size() > 1
                || binding.bowTags.getTags().size() > 1
                || binding.arrowTags.getTags().size() > 1;
        filter.setVisible(rounds != null && filterAvailable);
        export.setVisible(rounds != null);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_export:
                export();
                return true;
            case R.id.action_filter:
                if (!binding.drawerLayout.isDrawerOpen(END)) {
                    binding.drawerLayout.openDrawer(END);
                } else {
                    binding.drawerLayout.closeDrawer(END);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void resetFilter() {
        Stream.of(binding.distanceTags.getTags())
                .forEach(tag -> tag.isChecked = true);
        Stream.of(binding.diameterTags.getTags())
                .forEach(tag -> tag.isChecked = true);
        Stream.of(binding.arrowTags.getTags())
                .forEach(tag -> tag.isChecked = true);
        Stream.of(binding.bowTags.getTags())
                .forEach(tag -> tag.isChecked = true);
        binding.distanceTags.setTags(binding.distanceTags.getTags());
        binding.diameterTags.setTags(binding.diameterTags.getTags());
        binding.arrowTags.setTags(binding.arrowTags.getTags());
        binding.bowTags.setTags(binding.bowTags.getTags());
        applyFilter();
    }

    private void applyFilter() {
        distanceTags = Stream.of(binding.distanceTags.getCheckedTags())
                .map(t -> t.text).collect(Collectors.toList());
        diameterTags = Stream.of(binding.diameterTags.getCheckedTags())
                .map(t -> t.text).collect(Collectors.toList());
        arrowTags = Stream.of(binding.arrowTags.getCheckedTags())
                .map(t -> t.id).collect(Collectors.toList());
        bowTags = Stream.of(binding.bowTags.getCheckedTags())
                .map(t -> t.id).collect(Collectors.toList());
        filteredRounds = Stream.of(rounds)
                .filter(pair -> distanceTags.contains(pair.second.distance.toString())
                        && diameterTags.contains(pair.second.getTarget().diameter.toString())
                        && arrowTags.contains(pair.first.arrowId)
                        && bowTags.contains(pair.first.bowId))
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
                .map(p -> p.first.bowId)
                .distinct()
                .map(bid -> {
                    if (bid != null) {
                        Bow bow = Bow.get(bid);
                        if (bow == null) {
                            return new ChipGroup.Tag(bid, "Deleted " + bid);
                        }
                        return new ChipGroup.Tag(bow.getId(), bow.getName(),
                                bow.thumbnail.getBlob().getBlob(), true);
                    } else {
                        return new ChipGroup.Tag(null, getString(R.string.unknown));
                    }
                })
                .collect(Collectors.toList());
    }

    private List<ChipGroup.Tag> getArrowTags() {
        return Stream.of(rounds)
                .map(p -> p.first.arrowId)
                .distinct()
                .map(aid -> {
                    if (aid != null) {
                        Arrow arrow = Arrow.get(aid);
                        if (arrow == null) {
                            return new ChipGroup.Tag(aid, "Deleted " + aid);
                        }
                        return new ChipGroup.Tag(arrow.getId(), arrow.getName(),
                                arrow.thumbnail.getBlob().getBlob(), true);
                    } else {
                        return new ChipGroup.Tag(null, getString(R.string.unknown));
                    }
                })
                .collect(Collectors.toList());
    }

    private List<ChipGroup.Tag> getDistanceTags() {
        return Stream.of(rounds)
                .map(p -> p.second.distance)
                .distinct()
                .sorted()
                .map(d -> new ChipGroup.Tag(d.getId(), d.toString()))
                .collect(Collectors.toList());
    }

    private List<ChipGroup.Tag> getDiameterTags() {
        return Stream.of(rounds)
                .map(p -> p.second.getTarget().diameter)
                .distinct()
                .sorted()
                .map(d -> new ChipGroup.Tag(d.getId(), d.toString()))
                .collect(Collectors.toList());
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Icepick.saveInstanceState(this, outState);
    }

    @Override
    public void onLoaderReset(Loader<List<Pair<Training, Round>>> loader) {

    }

    void export() {
        MaterialDialog progress = new MaterialDialog.Builder(this)
                .content(R.string.exporting)
                .progress(true, 0)
                .show();
        new AsyncTask<Void, Void, Uri>() {

            @Nullable
            @Override
            protected Uri doInBackground(Void... params) {
                try {
                    final File f = new File(getCacheDir(), getExportFileName());
                    new CsvExporter(getApplicationContext())
                            .exportAll(f, Stream.of(filteredRounds)
                                    .flatMap(p -> Stream.of(p.second))
                                    .map(Round::getId)
                                    .collect(Collectors.toList()));
                    return FileUtils.getUriForFile(StatisticsActivity.this, f);
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(@Nullable Uri uri) {
                super.onPostExecute(uri);
                progress.dismiss();
                if (uri != null) {
                    Intent email = new Intent(Intent.ACTION_SEND);
                    email.putExtra(Intent.EXTRA_STREAM, uri);
                    email.setType("text/csv");
                    startActivity(Intent.createChooser(email, getString(R.string.send_exported)));
                } else {
                    Snackbar.make(binding.getRoot(), R.string.exporting_failed,
                            Snackbar.LENGTH_LONG).show();
                }
            }
        }.execute();
    }

    @NonNull
    private static String getExportFileName() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.US);
        return "MyTargets_exported_data_" + format.format(new Date()) + ".csv";
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
