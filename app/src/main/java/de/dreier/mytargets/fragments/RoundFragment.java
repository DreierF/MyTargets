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
import android.support.annotation.Nullable;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;
import java.util.Locale;

import de.dreier.mytargets.R;
import de.dreier.mytargets.activities.InputActivity;
import de.dreier.mytargets.activities.ScoreboardActivity;
import de.dreier.mytargets.activities.StatisticsActivity;
import de.dreier.mytargets.adapters.NowListAdapter;
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
import de.dreier.mytargets.utils.SelectableViewHolder;
import de.dreier.mytargets.utils.ToolbarUtils;

/**
 * Shows all passes of one round
 */
public class RoundFragment extends EditableFragment<Passe> {

    static final String ROUND_ID = "round_id";

    private long mRound;
    private PasseDataSource passeDataSource;
    private FragmentListBinding binding;
    private Round round;

    public RoundFragment() {
        itemTypeSelRes = R.plurals.passe_selected;
        itemTypeDelRes = R.plurals.passe_deleted;
        newStringRes = R.string.new_end;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_list, container, false);
        binding.recyclerView.setHasFixedSize(true);
        binding.recyclerView.addItemDecoration(
                new DividerItemDecoration(getContext(), R.drawable.inset_divider));
        mAdapter = new EndAdapter(getContext());
        binding.recyclerView.setAdapter(mAdapter);
        binding.fab.setVisibility(View.GONE);
        binding.fab.setOnClickListener(v -> openPasse(round.getId(), binding.recyclerView.getAdapter().getItemCount()));
        // Get round
        if (getArguments() != null) {
            mRound = getArguments().getLong(ROUND_ID, -1);
        }

        round = new RoundDataSource().get(mRound);
        ToolbarUtils.setTitle(this,
                String.format(Locale.ENGLISH, "%s %d", getString(R.string.round),
                        round.info.index + 1));
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

        StandardRound standardRound = new StandardRoundDataSource()
                .get(new TrainingDataSource().get(round.trainingId).standardRoundId);
        boolean showFab = data.size() < round.info.passes || standardRound.club == StandardRoundFactory.CUSTOM_PRACTICE;
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
                Intent intent = new Intent(getContext(), ScoreboardActivity.class);
                intent.putExtra(ScoreboardActivity.TRAINING_ID, round.trainingId);
                intent.putExtra(ScoreboardActivity.ROUND_ID, round.getId());
                startActivity(intent);
                return true;
            case R.id.action_statistics:
                Intent i = new Intent(getContext(), StatisticsActivity.class);
                i.putExtra(StatisticsActivity.TRAINING_ID, round.trainingId);
                i.putExtra(StatisticsActivity.ROUND_ID, round.getId());
                startActivity(i);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected NowListAdapter<Passe> getAdapter() {
        return new EndAdapter(getContext());
    }

    @Override
    protected void onItemSelected(Passe item) {
        openInputActivityForPasse(item);
    }

    @Override
    protected void onEdit(Passe item) {
        openInputActivityForPasse(item);
    }

    private void openInputActivityForPasse(Passe item) {
        Intent i = new Intent(getContext(), InputActivity.class);
        i.putExtra(InputActivity.ROUND_ID, item.roundId);
        i.putExtra(InputActivity.PASSE_IND, item.index);
        startActivity(i);
        getActivity().overridePendingTransition(R.anim.right_in, R.anim.left_out);
    }

    private void openPasse(long roundId, int passeIndex) {
        Intent i = new Intent(getContext(), InputActivity.class);
        i.putExtra(InputActivity.ROUND_ID, roundId);
        i.putExtra(InputActivity.PASSE_IND, passeIndex);
        startActivity(i);
    }

    private class EndAdapter extends NowListAdapter<Passe> {

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
        public void bindCursor() {
            binding.shoots.setPoints(mItem, round.info.target);
            binding.passe.setText(getString(R.string.passe_n, (mItem.index + 1)));
        }
    }
}
