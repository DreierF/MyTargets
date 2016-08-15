/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */

package de.dreier.mytargets.fragments;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;

import java.util.Collections;
import java.util.List;

import de.dreier.mytargets.R;
import de.dreier.mytargets.activities.SimpleFragmentActivityBase.EditTrainingActivity;
import de.dreier.mytargets.activities.SimpleFragmentActivityBase.TrainingActivity;
import de.dreier.mytargets.adapters.ExpandableListAdapter;
import de.dreier.mytargets.databinding.FragmentTrainingsBinding;
import de.dreier.mytargets.databinding.ItemHeaderMonthBinding;
import de.dreier.mytargets.databinding.ItemTrainingBinding;
import de.dreier.mytargets.managers.dao.RoundDataSource;
import de.dreier.mytargets.managers.dao.TrainingDataSource;
import de.dreier.mytargets.models.Month;
import de.dreier.mytargets.shared.models.Round;
import de.dreier.mytargets.shared.models.Training;
import de.dreier.mytargets.utils.DataLoader;
import de.dreier.mytargets.utils.Utils;
import de.dreier.mytargets.utils.multiselector.HeaderBindingHolder;
import de.dreier.mytargets.utils.multiselector.SelectableViewHolder;

import static de.dreier.mytargets.fragments.EditTrainingFragment.FREE_TRAINING;
import static de.dreier.mytargets.fragments.EditTrainingFragment.TRAINING_TYPE;
import static de.dreier.mytargets.fragments.EditTrainingFragment.TRAINING_WITH_STANDARD_ROUND;
import static de.dreier.mytargets.utils.ActivityUtils.showStatistics;
import static de.dreier.mytargets.utils.ActivityUtils.startActivityAnimated;

/**
 * Shows an overview over all training days
 */
public class TrainingsFragment extends ExpandableFragment<Month, Training> {

    protected FragmentTrainingsBinding binding;
    private TrainingDataSource trainingDataSource;

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
        binding.recyclerView.setAdapter(mAdapter);
        binding.fab1.setOnClickListener(view -> startActivityAnimated(getActivity(),
                EditTrainingActivity.class, TRAINING_TYPE, FREE_TRAINING));
        binding.fab2.setOnClickListener(view -> startActivityAnimated(getActivity(),
                EditTrainingActivity.class, TRAINING_TYPE, TRAINING_WITH_STANDARD_ROUND));
        return binding.getRoot();
    }

    @Override
    public void onSelected(Training item) {
        startActivityAnimated(getActivity(), TrainingActivity.class, ITEM_ID, item.getId());
    }

    @Override
    protected void onStatistics(List<Long> trainingIds) {
        showStatistics(getActivity(),
                Stream.of(trainingIds)
                        .flatMap(tid -> Stream.of(new RoundDataSource().getAll(tid)))
                        .map(Round::getId)
                        .collect(Collectors.toList()));
    }

    @Override
    protected void onEdit(final Training item) {
        startActivityAnimated(getActivity(), EditTrainingActivity.class, ITEM_ID, item.getId());
    }

    @Override
    public Loader<List<Training>> onCreateLoader(int id, Bundle args) {
        trainingDataSource = new TrainingDataSource();
        return new DataLoader<>(getContext(), trainingDataSource, trainingDataSource::getAll);
    }

    @Override
    public void onLoadFinished(Loader<List<Training>> loader, List<Training> data) {
        setList(trainingDataSource,data, false);
    }

    private class TrainingAdapter extends ExpandableListAdapter<Month, Training> {

        TrainingAdapter() {
            super(child -> new Month(Utils.getMonthId(child.date)),
                    Collections.reverseOrder(),
                    Collections.reverseOrder((l, r) -> {
                        if (l.date.equals(r.date)) {
                            return (int) (l.getId() - r.getId());
                        }
                        return l.date.compareTo(r.date);
                    }));
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
        public void bindCursor() {
            binding.training.setText(mItem.title);
            binding.trainingDate.setText(mItem.getFormattedDate());
            List<Round> rounds = new RoundDataSource().getAll(mItem.getId());
            binding.gesTraining.setText(mItem.getReachedPoints(rounds));
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