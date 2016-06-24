/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */

package de.dreier.mytargets.fragments;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import de.dreier.mytargets.R;
import de.dreier.mytargets.activities.SimpleFragmentActivityBase;
import de.dreier.mytargets.adapters.ExpandableNowListAdapter;
import de.dreier.mytargets.databinding.FragmentListBinding;
import de.dreier.mytargets.managers.dao.RoundDataSource;
import de.dreier.mytargets.managers.dao.TrainingDataSource;
import de.dreier.mytargets.models.Month;
import de.dreier.mytargets.shared.models.Round;
import de.dreier.mytargets.shared.models.Training;
import de.dreier.mytargets.utils.ActivityUtils;
import de.dreier.mytargets.utils.DataLoader;
import de.dreier.mytargets.utils.HeaderBindingHolder;
import de.dreier.mytargets.utils.SelectableViewHolder;
import de.dreier.mytargets.utils.Utils;

/**
 * Shows an overview over all training days
 */
public class TrainingsFragment extends ExpandableFragment<Month, Training> {

    protected FragmentListBinding binding;
    private TrainingDataSource trainingDataSource;

    public TrainingsFragment() {
        itemTypeSelRes = R.plurals.training_selected;
        itemTypeDelRes = R.plurals.training_deleted;
        newStringRes = R.string.new_training;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_list, container, false);
        binding.recyclerView.setHasFixedSize(true);
        return binding.getRoot();
    }

    @Override
    public void onSelected(Training item) {
        Intent i = new Intent(getContext(), SimpleFragmentActivityBase.TrainingActivity.class);
        i.putExtra(ITEM_ID, item.getId());
        startActivity(i);
        getActivity().overridePendingTransition(R.anim.right_in, R.anim.left_out);
    }

    @Override
    protected void onEdit(final Training item) {
        ActivityUtils.startActivityAnimated(getActivity(),
                SimpleFragmentActivityBase.EditTrainingActivity.class, ITEM_ID, item.getId());
    }

    @Override
    public Loader<List<Training>> onCreateLoader(int id, Bundle args) {
        trainingDataSource = new TrainingDataSource();
        return new DataLoader<>(getContext(), trainingDataSource, trainingDataSource::getAll);
    }

    @Override
    public void onLoadFinished(Loader<List<Training>> loader, List<Training> data) {
        Set<Long> monthMap = new HashSet<>();
        List<Month> months = new ArrayList<>();
        for (Training t : data) {
            long parentId = Utils.getMonthId(t.date);
            if (!monthMap.contains(parentId)) {
                monthMap.add(parentId);
                months.add(new Month(parentId));
            }
        }
        Collections.sort(months, Collections.reverseOrder());
        setList(binding.recyclerView, trainingDataSource, months, data,
                child -> Utils.getMonthId(child.date), false,
                new TrainingAdapter());
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
        private final TextView mTitle;
        private final TextView mSubtitle;
        private final TextView mGes;

        public ViewHolder(View itemView) {
            super(itemView, mSelector, TrainingsFragment.this);
            mTitle = (TextView) itemView.findViewById(R.id.training);
            mSubtitle = (TextView) itemView.findViewById(R.id.trainingDate);
            mGes = (TextView) itemView.findViewById(R.id.gesTraining);
        }

        @Override
        public void bindCursor() {
            mTitle.setText(mItem.title);
            mSubtitle.setText(mItem.getFormattedDate());
            int maxPoints = 0;
            int reachedPoints = 0;
            RoundDataSource roundDataSource = new RoundDataSource();
            ArrayList<Round> rounds = roundDataSource.getAll(mItem.getId());
            for (Round r : rounds) {
                maxPoints += r.info.getMaxPoints();
                reachedPoints += r.reachedPoints;
            }
            mGes.setText(String.format(Locale.ENGLISH, "%d/%d", reachedPoints, maxPoints));
        }
    }

    private class HeaderViewHolder extends HeaderBindingHolder<Month> {
        private final TextView mTitle;

        HeaderViewHolder(View itemView) {
            super(itemView, R.id.expand_collapse);
            mTitle = (TextView) itemView.findViewById(android.R.id.text1);
        }

        @Override
        public void bindCursor() {
            mTitle.setText(mItem.toString());
        }
    }
}