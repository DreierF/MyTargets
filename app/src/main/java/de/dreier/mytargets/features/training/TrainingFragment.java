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

package de.dreier.mytargets.features.training;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;

import junit.framework.Assert;

import java.util.List;
import java.util.Locale;

import de.dreier.mytargets.R;
import de.dreier.mytargets.base.adapters.SimpleListAdapterBase;
import de.dreier.mytargets.base.fragments.EditableListFragment;
import de.dreier.mytargets.databinding.FragmentTrainingBinding;
import de.dreier.mytargets.databinding.ItemRoundBinding;
import de.dreier.mytargets.features.rounds.EditRoundFragment;
import de.dreier.mytargets.features.scoreboard.HtmlUtils;
import de.dreier.mytargets.features.scoreboard.ScoreboardActivity;
import de.dreier.mytargets.features.settings.SettingsManager;
import de.dreier.mytargets.features.statistics.StatisticsActivity;
import de.dreier.mytargets.shared.models.db.Round;
import de.dreier.mytargets.shared.models.db.Training;
import de.dreier.mytargets.utils.DividerItemDecoration;
import de.dreier.mytargets.utils.IntentWrapper;
import de.dreier.mytargets.utils.SlideInItemAnimator;
import de.dreier.mytargets.utils.ToolbarUtils;
import de.dreier.mytargets.utils.Utils;
import de.dreier.mytargets.utils.multiselector.SelectableViewHolder;

/**
 * Shows all rounds of one training.
 */
public class TrainingFragment extends EditableListFragment<Round> {

    private final boolean[] equals = new boolean[2];
    protected FragmentTrainingBinding binding;
    private long trainingId;
    private Training training;

    public TrainingFragment() {
        itemTypeSelRes = R.plurals.round_selected;
        itemTypeDelRes = R.plurals.round_deleted;
        supportsStatistics = true;
    }

    @NonNull
    public static IntentWrapper getIntent(Training training) {
        return new IntentWrapper(TrainingActivity.class)
                .with(ITEM_ID, training.getId());
    }

    @Override
    @CallSuper
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_training, container, false);

        binding.recyclerView.setHasFixedSize(true);
        binding.recyclerView.addItemDecoration(
                new DividerItemDecoration(getContext(), R.drawable.full_divider));
        adapter = new RoundAdapter();
        binding.recyclerView.setItemAnimator(new SlideInItemAnimator());
        binding.recyclerView.setAdapter(adapter);

        // Get training
        Assert.assertNotNull(getArguments());
        trainingId = getArguments().getLong(ITEM_ID);

        binding.fab.setVisibility(View.GONE);
        binding.fab.setOnClickListener(view -> {
            // New round to free training
            EditRoundFragment.createIntent(training)
                    .withContext(this)
                    .fromFab(binding.fab)
                    .start();
        });
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ToolbarUtils.setSupportActionBar(this, binding.toolbar);
        ToolbarUtils.showHomeAsUp(this);
        setHasOptionsMenu(true);
    }

    @NonNull
    @Override
    protected LoaderUICallback onLoad(Bundle args) {
        training = Training.get(trainingId);
        List<Round> rounds = training.getRounds();
        return new LoaderUICallback() {
            @Override
            public void applyData() {

                // Hide fab for standard rounds
                supportsDeletion = training.standardRoundId == null;
                binding.fab.setVisibility(supportsDeletion ? View.VISIBLE : View.GONE);

                // Set round info
                binding.weatherIcon.setImageResource(training.getEnvironment().getColorDrawable());
                binding.detailRoundInfo.setText(Utils
                        .fromHtml(HtmlUtils.getTrainingInfoHTML(training, rounds, equals, false)));
                adapter.setList(rounds);

                getActivity().supportInvalidateOptionsMenu();

                ToolbarUtils.setTitle(TrainingFragment.this, training.title);
                ToolbarUtils.setSubtitle(TrainingFragment.this, training.getFormattedDate());
            }
        };
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.statistics_scoresheet, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_scoreboard:
                ScoreboardActivity.getIntent(trainingId)
                        .withContext(this)
                        .start();
                return true;
            case R.id.action_statistics:
                StatisticsActivity.getIntent(
                        Stream.of(training.getRounds())
                                .map(Round::getId)
                                .collect(Collectors.toList()))
                        .withContext(this)
                        .start();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onItemSelected(Round item) {
        RoundFragment.getIntent(item)
                .withContext(this)
                .start();
    }

    @Override
    protected void onEdit(Round item) {
        EditRoundFragment.editIntent(training, item)
                .withContext(this)
                .start();
    }

    @Override
    protected void onStatistics(List<Round> rounds) {
        StatisticsActivity
                .getIntent(Stream.of(rounds).map(Round::getId).collect(Collectors.toList()))
                .withContext(this)
                .start();
    }

    private class RoundAdapter extends SimpleListAdapterBase<Round> {

        @Override
        protected SelectableViewHolder<Round> onCreateViewHolder(ViewGroup parent) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_round, parent, false);
            return new ViewHolder(itemView);
        }
    }

    private class ViewHolder extends SelectableViewHolder<Round> {
        private final ItemRoundBinding binding;

        ViewHolder(View itemView) {
            super(itemView, selector, TrainingFragment.this);
            binding = DataBindingUtil.bind(itemView);
        }

        @Override
        public void bindItem() {
            binding.title.setText(String.format(Locale.ENGLISH, "%s %d",
                    getContext().getString(R.string.round),
                    item.index + 1));
            binding.subtitle.setText(Utils.fromHtml(HtmlUtils.getRoundInfo(item, equals)));
            if (binding.subtitle.getText().toString().isEmpty()) {
                binding.subtitle.setVisibility(View.GONE);
            } else {
                binding.subtitle.setVisibility(View.VISIBLE);
            }
            binding.points.setText(
                    item.getReachedScore().format(SettingsManager.getScoreConfiguration()));
        }
    }
}
