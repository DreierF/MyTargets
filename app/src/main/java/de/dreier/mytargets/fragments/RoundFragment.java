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
import de.dreier.mytargets.shared.models.db.Passe;
import de.dreier.mytargets.shared.models.db.Round;
import de.dreier.mytargets.shared.models.db.StandardRound;
import de.dreier.mytargets.shared.models.db.Training;
import de.dreier.mytargets.shared.utils.StandardRoundFactory;
import de.dreier.mytargets.utils.DividerItemDecoration;
import de.dreier.mytargets.utils.SelectableViewHolder;
import de.dreier.mytargets.utils.ToolbarUtils;

/**
 * Shows all passes of one round
 */
public class RoundFragment extends EditableFragment<Passe> {

    public static final String ROUND_ID = "round_id";

    private long mRound;
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
        binding.recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), R.drawable.full_divider));
        mAdapter = new EndAdapter(getContext());
        binding.recyclerView.setAdapter(mAdapter);
        binding.fab.setVisibility(View.GONE);
        binding.fab.setOnClickListener(v -> openPasse(round.getId(), binding.recyclerView.getAdapter().getItemCount()));
        // Get round
        if (getArguments() != null) {
            mRound = getArguments().getLong(ROUND_ID, -1);
        }

        round = Round.get(mRound);
        ToolbarUtils.setTitle(this, String.format(Locale.ENGLISH, "%s %d", getString(R.string.round), round.info.index + 1));
        ToolbarUtils.setSubtitle(this, round.getReachedPointsFormatted());
        setHasOptionsMenu(true);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ToolbarUtils.showHomeAsUp(this);
    }

    @NonNull
    @Override
    protected LoaderUICallback onLoad() {
        final List<Passe> passes = round.getPasses();
        StandardRound standardRound = StandardRound
                .get(Training.get(round.trainingId).standardRoundId);
        final boolean showFab = passes.size() < round.info.endCount || standardRound.club == StandardRoundFactory.CUSTOM_PRACTICE;
        return new LoaderUICallback() {
            @Override
            public void applyData() {
                // Set round info
                mAdapter.setList(passes);
                binding.fab.setVisibility(showFab ? View.VISIBLE : View.GONE);
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
