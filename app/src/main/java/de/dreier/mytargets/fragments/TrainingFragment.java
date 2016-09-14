/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */

package de.dreier.mytargets.fragments;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;

import java.util.List;
import java.util.Locale;

import de.dreier.mytargets.ApplicationInstance;
import de.dreier.mytargets.R;
import de.dreier.mytargets.activities.ScoreboardActivity;
import de.dreier.mytargets.activities.SimpleFragmentActivityBase.EditRoundActivity;
import de.dreier.mytargets.activities.SimpleFragmentActivityBase.RoundActivity;
import de.dreier.mytargets.adapters.ListAdapterBase;
import de.dreier.mytargets.databinding.FragmentTrainingBinding;
import de.dreier.mytargets.databinding.ItemRoundBinding;
import de.dreier.mytargets.managers.dao.RoundDataSource;
import de.dreier.mytargets.managers.dao.StandardRoundDataSource;
import de.dreier.mytargets.managers.dao.TrainingDataSource;
import de.dreier.mytargets.shared.models.Round;
import de.dreier.mytargets.shared.models.StandardRound;
import de.dreier.mytargets.shared.models.Training;
import de.dreier.mytargets.shared.utils.StandardRoundFactory;
import de.dreier.mytargets.utils.DataLoader;
import de.dreier.mytargets.utils.DividerItemDecoration;
import de.dreier.mytargets.utils.HtmlUtils;
import de.dreier.mytargets.utils.SlideInItemAnimator;
import de.dreier.mytargets.utils.ToolbarUtils;
import de.dreier.mytargets.utils.multiselector.SelectableViewHolder;

import static de.dreier.mytargets.fragments.RoundFragment.ROUND_ID;
import static de.dreier.mytargets.utils.ActivityUtils.showStatistics;
import static de.dreier.mytargets.utils.ActivityUtils.startActivityAnimated;

/**
 * Shows all passes of one training
 */
public class TrainingFragment extends EditableListFragment<Round> {

    private final boolean[] equals = new boolean[2];
    protected FragmentTrainingBinding binding;
    private long mTraining;
    private Training training;
    private RoundDataSource roundDataSource;
    private StandardRoundDataSource standardRoundDataSource;
    private TrainingDataSource trainingDataSource;

    public TrainingFragment() {
        itemTypeSelRes = R.plurals.round_selected;
        itemTypeDelRes = R.plurals.round_deleted;
        newStringRes = R.string.new_round;
        supportsStatistics = true;
    }

    @Override
    @CallSuper
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_training, container, false);

        binding.recyclerView.setHasFixedSize(true);
        binding.recyclerView.addItemDecoration(
                new DividerItemDecoration(getContext(), R.drawable.full_divider));
        mAdapter = new RoundAdapter(getContext());
        binding.recyclerView.setItemAnimator(new SlideInItemAnimator());
        binding.recyclerView.setAdapter(mAdapter);


        // Get training
        if (getArguments() != null) {
            mTraining = getArguments().getLong(ITEM_ID, -1);
        }
        binding.fab.setVisibility(View.GONE);
        binding.fab.setOnClickListener(view -> {
            // New round to free training
            startActivityAnimated(getActivity(), EditRoundActivity.class, ITEM_ID, mTraining);
        });

        trainingDataSource = new TrainingDataSource();
        roundDataSource = new RoundDataSource();
        standardRoundDataSource = new StandardRoundDataSource();

        training = trainingDataSource.get(mTraining);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ToolbarUtils.setSupportActionBar(this, binding.toolbar);
        ToolbarUtils.showHomeAsUp(this);
        setHasOptionsMenu(true);
    }

    @Override
    public Loader<List<Round>> onCreateLoader(int id, Bundle args) {
        return new DataLoader<>(getContext(), new RoundDataSource(),
                () -> roundDataSource.getAll(mTraining));
    }

    public void onLoadFinished(Loader<List<Round>> loader, List<Round> data) {
        // Hide fab for standard rounds
        StandardRound standardRound = standardRoundDataSource.get(training.standardRoundId);
        binding.fab.setVisibility(
                standardRound.club == StandardRoundFactory.CUSTOM_PRACTICE ? View.VISIBLE : View.GONE);

        // Set round info
        int weatherDrawable = R.drawable.ic_house_24dp;
        if (!standardRound.indoor) {
            weatherDrawable = training.environment.weather.getColorDrawable();
        }
        binding.weatherIcon.setImageResource(weatherDrawable);
        binding.detailRoundInfo.setText(
                HtmlUtils.fromHtml(HtmlUtils.getTrainingInfoHTML(training, data, equals, false)));
        mAdapter.setList(data);
        dataSource = new RoundDataSource();
        getActivity().supportInvalidateOptionsMenu();

        ToolbarUtils.setTitle(this, training.title);
        ToolbarUtils.setSubtitle(this, training.getFormattedDate());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.statistics_scoresheet, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_scoreboard:
                Intent intent = new Intent(getContext(), ScoreboardActivity.class);
                intent.putExtra(ScoreboardActivity.TRAINING_ID, mTraining);
                startActivity(intent);
                return true;
            case R.id.action_statistics:
                showStatistics(getActivity(),
                        Stream.of(new RoundDataSource().getAll(training.getId()))
                                .map(Round::getId)
                                .collect(Collectors.toList()));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onItemSelected(Round item) {
        startActivityAnimated(getActivity(), RoundActivity.class, ROUND_ID, item.getId());
    }

    @Override
    protected void onEdit(Round item) {
        Intent i = new Intent(getContext(), EditRoundActivity.class);
        i.putExtra(ITEM_ID, mTraining);
        i.putExtra(EditRoundFragment.ROUND_ID, item.getId());
        startActivity(i);
    }

    @Override
    protected void onStatistics(List<Long> roundIds) {
        showStatistics(getActivity(), roundIds);
    }

    private class RoundAdapter extends ListAdapterBase<Round> {

        RoundAdapter(Context context) {
            super(context, (lhs, rhs) -> lhs.info.index - rhs.info.index);
        }

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
            super(itemView, mSelector, TrainingFragment.this);
            binding = DataBindingUtil.bind(itemView);
        }

        @Override
        public void bindCursor() {
            binding.title.setText(String.format(Locale.ENGLISH, "%s %d",
                    ApplicationInstance.getContext().getString(R.string.round),
                    mItem.info.index + 1));
            binding.subtitle.setText(HtmlUtils.fromHtml(HtmlUtils.getRoundInfo(mItem, equals)));
            if (binding.subtitle.getText().toString().isEmpty()) {
                binding.subtitle.setVisibility(View.GONE);
            } else {
                binding.subtitle.setVisibility(View.VISIBLE);
            }
            binding.points.setText(mItem.getReachedPointsFormatted());
        }
    }
}
