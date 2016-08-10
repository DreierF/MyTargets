/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */

package de.dreier.mytargets.fragments;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.dreier.mytargets.R;
import de.dreier.mytargets.activities.SimpleFragmentActivityBase.EditTrainingActivity;
import de.dreier.mytargets.activities.SimpleFragmentActivityBase.TrainingActivity;
import de.dreier.mytargets.adapters.ExpandableNowListAdapter;
import de.dreier.mytargets.databinding.FragmentTrainingsBinding;
import de.dreier.mytargets.databinding.ItemHeaderMonthBinding;
import de.dreier.mytargets.databinding.ItemTrainingBinding;
import de.dreier.mytargets.models.ETrainingType;
import de.dreier.mytargets.models.Month;
import de.dreier.mytargets.shared.models.db.Round;
import de.dreier.mytargets.shared.models.db.Training;
import de.dreier.mytargets.utils.HeaderBindingHolder;
import de.dreier.mytargets.utils.SelectableViewHolder;
import de.dreier.mytargets.utils.Utils;

import static de.dreier.mytargets.fragments.EditTrainingFragment.TRAINING_TYPE;
import static de.dreier.mytargets.utils.ActivityUtils.startActivityAnimated;

/**
 * Shows an overview over all training days
 */
public class TrainingsFragment extends ExpandableFragment<Month, Training> {

    protected FragmentTrainingsBinding binding;

    public TrainingsFragment() {
        itemTypeSelRes = R.plurals.training_selected;
        itemTypeDelRes = R.plurals.training_deleted;
        newStringRes = R.string.new_training;
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
        binding.recyclerView.setAdapter(mAdapter);
        binding.fab1.setOnClickListener(view -> startActivityAnimated(getActivity(),
            EditTrainingActivity.class, TRAINING_TYPE, ETrainingType.FREE_TRAINING.toString()));
        binding.fab2.setOnClickListener(view -> startActivityAnimated(getActivity(),
                EditTrainingActivity.class, TRAINING_TYPE, ETrainingType.TRAINING_WITH_STANDARD_ROUND.toString()));
        return binding.getRoot();
    }

    @Override
    public void onSelected(Training item) {
        startActivityAnimated(getActivity(), TrainingActivity.class, ITEM_ID, item.getId());
    }

    @Override
    protected void onEdit(final Training item) {
        startActivityAnimated(getActivity(), EditTrainingActivity.class, ITEM_ID, item.getId());
    }

    @NonNull
    @Override
    protected LoaderUICallback onLoad() {
        final List<Training> trainings = Training.getAll();
        Set<Long> monthMap = new HashSet<>();
        List<Month> months = new ArrayList<>();
        for (Training t : trainings) {
            long parentId = Utils.getMonthId(t.date);
            if (!monthMap.contains(parentId)) {
                monthMap.add(parentId);
                months.add(new Month(parentId));
            }
        }
        Collections.sort(months, Collections.reverseOrder());
        return () -> setList(months, trainings, child -> Utils.getMonthId(child.date), false);
    }

    private class TrainingAdapter extends ExpandableNowListAdapter<Month, Training> {

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
        public void bindCursor() {
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
        public void bindCursor() {
            binding.month.setText(mItem.toString());
        }
    }
}
