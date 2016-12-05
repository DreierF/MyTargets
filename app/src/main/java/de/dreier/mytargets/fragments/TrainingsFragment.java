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

package de.dreier.mytargets.fragments;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;

import java.util.Collections;
import java.util.List;

import de.dreier.mytargets.R;
import de.dreier.mytargets.activities.StatisticsActivity;
import de.dreier.mytargets.adapters.ExpandableListAdapter;
import de.dreier.mytargets.databinding.FragmentTrainingsBinding;
import de.dreier.mytargets.databinding.ItemHeaderMonthBinding;
import de.dreier.mytargets.databinding.ItemTrainingBinding;
import de.dreier.mytargets.models.Month;
import de.dreier.mytargets.shared.models.db.Round;
import de.dreier.mytargets.shared.models.db.Training;
import de.dreier.mytargets.utils.SlideInItemAnimator;
import de.dreier.mytargets.utils.Utils;
import de.dreier.mytargets.utils.multiselector.HeaderBindingHolder;
import de.dreier.mytargets.utils.multiselector.SelectableViewHolder;

import static de.dreier.mytargets.fragments.EditTrainingFragment.CREATE_FREE_TRAINING_ACTION;
import static de.dreier.mytargets.fragments.EditTrainingFragment.CREATE_TRAINING_WITH_STANDARD_ROUND_ACTION;


/**
 * Shows an overview over all training days
 */
public class TrainingsFragment extends ExpandableListFragment<Month, Training> {

    protected FragmentTrainingsBinding binding;

    public TrainingsFragment() {
        itemTypeSelRes = R.plurals.training_selected;
        itemTypeDelRes = R.plurals.training_deleted;
        newStringRes = R.string.new_training;
        supportsStatistics = true;
    }

    @Override
    public void onResume() {
        super.onResume();
        binding.fab.close(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_trainings, container, false);
        binding.recyclerView.setHasFixedSize(true);
        mAdapter = new TrainingAdapter();
        binding.recyclerView.setItemAnimator(new SlideInItemAnimator());
        binding.recyclerView.setAdapter(mAdapter);
        binding.fab1.setOnClickListener(view -> EditTrainingFragment
                .createIntent(CREATE_FREE_TRAINING_ACTION)
                .withContext(this)
                .fromFab(binding.fab1, 0xFF4CAF50, R.drawable.fab_trending_up_white_24dp)
                .start());
        binding.fab2.setOnClickListener(view -> EditTrainingFragment
                .createIntent(CREATE_TRAINING_WITH_STANDARD_ROUND_ACTION)
                .withContext(this)
                .fromFab(binding.fab2, 0xFF2196F3, R.drawable.fab_album_24dp)
                .start());
        return binding.getRoot();
    }

    @Override
    public void onSelected(Training item) {
        TrainingFragment.getIntent(item)
                .withContext(this)
                .start();
    }

    @Override
    protected void onStatistics(List<Training> trainings) {
        StatisticsActivity.getIntent(Stream.of(trainings)
                .flatMap(t -> Stream.of(t.getRounds()))
                .map(Round::getId)
                .collect(Collectors.toList()))
                .withContext(this)
                .start();
    }

    @Override
    protected void onEdit(final Training item) {
        EditTrainingFragment.editIntent(item)
                .withContext(this)
                .start();
    }

    @NonNull
    @Override
    protected LoaderUICallback onLoad(Bundle args) {
        final List<Training> trainings = Training.getAll();

        return () -> setList(trainings, false);
    }

    private class TrainingAdapter extends ExpandableListAdapter<Month, Training> {

        TrainingAdapter() {
            super(child -> new Month(Utils.getMonthId(child.date)),
                    Collections.reverseOrder(),
                    Collections.reverseOrder());
        }

        @Override
        protected HeaderViewHolder getTopLevelViewHolder(ViewGroup parent) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_header_month, parent, false);
            return new HeaderViewHolder(itemView);
        }

        @Override
        protected ViewHolder getSecondLevelViewHolder(ViewGroup parent) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_training, parent, false);
            return new ViewHolder(itemView);
        }
    }

    private class ViewHolder extends SelectableViewHolder<Training> {
        ItemTrainingBinding binding;

        public ViewHolder(View itemView) {
            super(itemView, mSelector, TrainingsFragment.this);
            binding = DataBindingUtil.bind(itemView);
        }

        @Override
        public void bindItem() {
            binding.training.setText(mItem.title);
            binding.trainingDate.setText(mItem.getFormattedDate());
            List<Round> rounds = mItem.getRounds();
            binding.gesTraining.setText(mItem.getReachedPointsFormatted(rounds, false));
        }
    }

    private class HeaderViewHolder extends HeaderBindingHolder<Month> {
        private final ItemHeaderMonthBinding binding;

        HeaderViewHolder(View itemView) {
            super(itemView, R.id.expand_collapse);
            binding = DataBindingUtil.bind(itemView);
        }

        @Override
        public void bindItem() {
            binding.month.setText(mItem.toString());
        }
    }
}
