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

package de.dreier.mytargets.features.training.overview;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import de.dreier.mytargets.shared.streamwrapper.Stream;

import java.util.Collections;
import java.util.List;

import de.dreier.mytargets.R;
import de.dreier.mytargets.base.adapters.header.ExpandableListAdapter;
import de.dreier.mytargets.base.fragments.ItemActionModeCallback;
import de.dreier.mytargets.databinding.FragmentTrainingsBinding;
import de.dreier.mytargets.databinding.ItemTrainingBinding;
import de.dreier.mytargets.features.settings.SettingsManager;
import de.dreier.mytargets.features.statistics.StatisticsActivity;
import de.dreier.mytargets.features.training.details.TrainingFragment;
import de.dreier.mytargets.features.training.edit.EditTrainingFragment;
import de.dreier.mytargets.shared.models.db.Round;
import de.dreier.mytargets.shared.models.db.Training;
import de.dreier.mytargets.utils.DividerItemDecoration;
import de.dreier.mytargets.utils.SlideInItemAnimator;
import de.dreier.mytargets.utils.Utils;
import de.dreier.mytargets.utils.multiselector.SelectableViewHolder;

import static de.dreier.mytargets.features.training.edit.EditTrainingFragment.CREATE_FREE_TRAINING_ACTION;
import static de.dreier.mytargets.features.training.edit.EditTrainingFragment.CREATE_TRAINING_WITH_STANDARD_ROUND_ACTION;
import static de.dreier.mytargets.utils.MobileWearableClient.BROADCAST_CREATE_TRAINING_FROM_REMOTE;
import static de.dreier.mytargets.utils.MobileWearableClient.BROADCAST_UPDATE_TRAINING_FROM_REMOTE;

/**
 * Shows an overview over all training days
 */
public class TrainingsFragment extends ExpandableListFragment<Header, Training> {

    protected FragmentTrainingsBinding binding;

    public TrainingsFragment() {
        itemTypeDelRes = R.plurals.training_deleted;
        actionModeCallback = new ItemActionModeCallback(this, selector,
                R.plurals.training_selected);
        actionModeCallback.setEditCallback(this::onEdit);
        actionModeCallback.setDeleteCallback(this::onDelete);
        actionModeCallback.setStatisticsCallback(this::onStatistics);
    }

    @Override
    public void onResume() {
        super.onResume();
        binding.fabSpeedDial.closeMenu();
    }

    @NonNull
    private BroadcastReceiver updateReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            reloadData();
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        IntentFilter filter = new IntentFilter();
        filter.addAction(BROADCAST_UPDATE_TRAINING_FROM_REMOTE);
        filter.addAction(BROADCAST_CREATE_TRAINING_FROM_REMOTE);
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(updateReceiver, filter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(updateReceiver);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_trainings, container, false);
        binding.recyclerView.setHasFixedSize(true);
        adapter = new TrainingAdapter(getContext());
        binding.recyclerView.setItemAnimator(new SlideInItemAnimator());
        binding.recyclerView.setAdapter(adapter);
        binding.recyclerView.addItemDecoration(
                new DividerItemDecoration(getContext(), R.drawable.full_divider));
        binding.fabSpeedDial.setMenuListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.fab1:
                    EditTrainingFragment
                            .createIntent(CREATE_FREE_TRAINING_ACTION)
                            .withContext(TrainingsFragment.this)
                            .fromFab(binding.fabSpeedDial
                                            .getFabFromMenuId(R.id.fab1), R.color.fabFreeTraining,
                                    R.drawable.fab_trending_up_white_24dp)
                            .start();
                    break;
                case R.id.fab2:
                    EditTrainingFragment
                            .createIntent(CREATE_TRAINING_WITH_STANDARD_ROUND_ACTION)
                            .withContext(TrainingsFragment.this)
                            .fromFab(binding.fabSpeedDial
                                            .getFabFromMenuId(R.id.fab2), R.color.fabTrainingWithStandardRound,
                                    R.drawable.fab_album_24dp)
                            .start();
                    break;
                default:
                    break;
            }
            return false;
        });
        setHasOptionsMenu(true);
        return binding.getRoot();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.statistics, menu);
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        super.onPrepareOptionsMenu(menu);
        boolean showStatistics = adapter != null && adapter.getItemCount() > 0;
        menu.findItem(R.id.action_statistics).setVisible(showStatistics);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_statistics:
                StatisticsActivity
                        .getIntent(Stream.of(Training.getAll())
                                .flatMap((training) -> Stream.of(training.getRounds()))
                                .map(Round::getId)
                                .toList()).withContext(this)
                        .start();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onSelected(Training item) {
        TrainingFragment.getIntent(item)
                .withContext(this)
                .start();
    }

    public void onStatistics(@NonNull List<Long> ids) {
        StatisticsActivity.getIntent(Stream.of(ids)
                .map(Training::get)
                .flatMap(t -> Stream.of(t.getRounds()))
                .map(Round::getId)
                .toList())
                .withContext(this)
                .start();
    }

    protected void onEdit(Long itemId) {
        EditTrainingFragment.editIntent(itemId)
                .withContext(this)
                .start();
    }

    @NonNull
    @Override
    protected LoaderUICallback onLoad(Bundle args) {
        final List<Training> trainings = Training.getAll();
        return () -> {
            TrainingsFragment.this.setList(trainings, false);
            FragmentActivity activity = getActivity();
            if (activity != null) {
                activity.invalidateOptionsMenu();
            }
            binding.emptyState.getRoot()
                    .setVisibility(trainings.isEmpty() ? View.VISIBLE : View.GONE);
        };
    }

    private class TrainingAdapter extends ExpandableListAdapter<Header, Training> {

        TrainingAdapter(@NonNull Context context) {
            super(child -> Utils.getMonthHeader(context, child.date),
                    Collections.reverseOrder(),
                    Collections.reverseOrder());
        }

        @NonNull
        @Override
        protected ViewHolder getSecondLevelViewHolder(@NonNull ViewGroup parent) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_training, parent, false);
            return new ViewHolder(itemView);
        }
    }

    private class ViewHolder extends SelectableViewHolder<Training> {
        ItemTrainingBinding binding;

        public ViewHolder(@NonNull View itemView) {
            super(itemView, selector, TrainingsFragment.this, TrainingsFragment.this);
            binding = DataBindingUtil.bind(itemView);
        }

        @Override
        public void bindItem() {
            binding.training.setText(item.title);
            binding.trainingDate.setText(item.getFormattedDate());
            binding.gesTraining.setText(item.getReachedScore()
                    .format(Utils.getCurrentLocale(getContext()), SettingsManager
                            .getScoreConfiguration()));
        }
    }
}
