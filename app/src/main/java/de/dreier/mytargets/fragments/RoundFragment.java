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
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

import de.dreier.mytargets.R;
import de.dreier.mytargets.activities.InputActivity;
import de.dreier.mytargets.activities.ScoreboardActivity;
import de.dreier.mytargets.activities.SimpleFragmentActivityBase;
import de.dreier.mytargets.activities.StatisticsActivity;
import de.dreier.mytargets.adapters.ListAdapterBase;
import de.dreier.mytargets.databinding.FragmentListBinding;
import de.dreier.mytargets.databinding.ItemEndBinding;
import de.dreier.mytargets.managers.dao.PasseDataSource;
import de.dreier.mytargets.managers.dao.RoundDataSource;
import de.dreier.mytargets.managers.dao.StandardRoundDataSource;
import de.dreier.mytargets.managers.dao.TrainingDataSource;
import de.dreier.mytargets.shared.models.Passe;
import de.dreier.mytargets.shared.models.Round;
import de.dreier.mytargets.shared.models.StandardRound;
import de.dreier.mytargets.shared.utils.StandardRoundFactory;
import de.dreier.mytargets.utils.DataLoader;
import de.dreier.mytargets.utils.DividerItemDecoration;
import de.dreier.mytargets.utils.IntentWrapper;
import de.dreier.mytargets.utils.SlideInItemAnimator;
import de.dreier.mytargets.utils.ToolbarUtils;
import de.dreier.mytargets.utils.multiselector.SelectableViewHolder;

/**
 * Shows all passes of one round
 */
public class RoundFragment extends EditableListFragment<Passe> {

    private static final String ROUND_ID = "round_id";

    private long mRound;
    private PasseDataSource passeDataSource;
    private FragmentListBinding binding;
    private Round round;

    public RoundFragment() {
        itemTypeSelRes = R.plurals.passe_selected;
        itemTypeDelRes = R.plurals.passe_deleted;
        newStringRes = R.string.new_end;
    }

    @NonNull
    public static IntentWrapper getIntent(Fragment fragment, Round round) {
        Intent i = new Intent(fragment.getContext(), SimpleFragmentActivityBase.RoundActivity.class);
        i.putExtra(ROUND_ID, round.getId());
        return new IntentWrapper(fragment, i);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_list, container, false);
        binding.recyclerView.setHasFixedSize(true);
        binding.recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), R.drawable.full_divider));
        mAdapter = new EndAdapter(getContext());
        binding.recyclerView.setItemAnimator(new SlideInItemAnimator());
        binding.recyclerView.setAdapter(mAdapter);
        binding.fab.setVisibility(View.GONE);
        binding.fab.setOnClickListener(
                v -> InputActivity.getIntent(this, round,
                        binding.recyclerView.getAdapter().getItemCount())
                                .fromFab(binding.fab)
                                .start());
        // Get round
        if (getArguments() != null) {
            mRound = getArguments().getLong(ROUND_ID, -1);
        }

        round = new RoundDataSource().get(mRound);
        ToolbarUtils.setTitle(this, String.format(Locale.ENGLISH, "%s %d", getString(R.string.round), round.info.index + 1));
        ToolbarUtils.setSubtitle(this, round.getReachedPointsFormatted());
        setHasOptionsMenu(true);

        passeDataSource = new PasseDataSource();
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ToolbarUtils.showHomeAsUp(this);
    }

    @Override
    public Loader<List<Passe>> onCreateLoader(int id, Bundle args) {
        return new DataLoader<>(getContext(), new PasseDataSource(),
                () -> passeDataSource.getAllByRound(mRound));
    }

    @Override
    public void onLoadFinished(Loader<List<Passe>> loader, List<Passe> data) {
        // Set round info
        mAdapter.setList(data);
        dataSource = new PasseDataSource();

        StandardRound standardRound = new StandardRoundDataSource()
                .get(new TrainingDataSource().get(round.trainingId).standardRoundId);
        boolean showFab = data.size() < round.info.endCount || standardRound.club == StandardRoundFactory.CUSTOM_PRACTICE;
        binding.fab.setVisibility(showFab ? View.VISIBLE : View.GONE);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.statistics_scoresheet, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_scoreboard:
                ScoreboardActivity
                        .getIntent(this, round.trainingId, round.getId())
                        .start();
                return true;
            case R.id.action_statistics:
                StatisticsActivity
                        .getIntent(this,
                        Collections.singletonList(round.getId())).start();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onItemSelected(Passe item) {
        InputActivity.getIntent(this, round, item.index)
                .start();
    }

    @Override
    protected void onEdit(Passe item) {
        InputActivity.getIntent(this, round, item.index)
                .start();
    }

    private class EndAdapter extends ListAdapterBase<Passe> {

        EndAdapter(Context context) {
            super(context);
        }

        @Override
        protected SelectableViewHolder<Passe> onCreateViewHolder(ViewGroup parent) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_end, parent, false);
            return new EndViewHolder(itemView);
        }
    }

    private class EndViewHolder extends SelectableViewHolder<Passe> {

        private final ItemEndBinding binding;

        EndViewHolder(View itemView) {
            super(itemView, mSelector, RoundFragment.this);
            binding = DataBindingUtil.bind(itemView);
        }

        @Override
        public void bindItem() {
            binding.shoots.setPoints(mItem, round.info.target);
            binding.passe.setText(getString(R.string.passe_n, (mItem.index + 1)));
        }
    }
}
