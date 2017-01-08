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

import android.content.Context;
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

import java.util.Collections;
import java.util.List;
import java.util.Locale;

import de.dreier.mytargets.R;
import de.dreier.mytargets.base.adapters.SimpleListAdapterBase;
import de.dreier.mytargets.databinding.FragmentListBinding;
import de.dreier.mytargets.databinding.ItemEndBinding;
import de.dreier.mytargets.features.scoreboard.ScoreboardActivity;
import de.dreier.mytargets.features.statistics.StatisticsActivity;
import de.dreier.mytargets.features.training.input.InputActivity;
import de.dreier.mytargets.base.fragments.EditableListFragment;
import de.dreier.mytargets.shared.models.db.End;
import de.dreier.mytargets.shared.models.db.Round;
import de.dreier.mytargets.utils.DividerItemDecoration;
import de.dreier.mytargets.utils.IntentWrapper;
import de.dreier.mytargets.utils.SlideInItemAnimator;
import de.dreier.mytargets.utils.ToolbarUtils;
import de.dreier.mytargets.utils.multiselector.SelectableViewHolder;

/**
 * Shows all ends of one round
 */
public class RoundFragment extends EditableListFragment<End> {

    private static final String ROUND_ID = "round_id";

    private long roundId;
    private FragmentListBinding binding;
    private Round round;

    public RoundFragment() {
        itemTypeSelRes = R.plurals.passe_selected;
        itemTypeDelRes = R.plurals.passe_deleted;
        newStringRes = R.string.new_end;
    }

    @NonNull
    public static IntentWrapper getIntent(Round round) {
        return new IntentWrapper(RoundActivity.class)
                .with(ROUND_ID, round.getId());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_list, container, false);
        binding.recyclerView.setHasFixedSize(true);
        binding.recyclerView.addItemDecoration(
                new DividerItemDecoration(getContext(), R.drawable.full_divider));
        adapter = new EndAdapter(getContext());
        binding.recyclerView.setItemAnimator(new SlideInItemAnimator());
        binding.recyclerView.setAdapter(adapter);
        binding.fab.setVisibility(View.GONE);
        binding.fab.setOnClickListener(
                v -> InputActivity
                        .getIntent(round, binding.recyclerView.getAdapter().getItemCount())
                        .withContext(this)
                        .fromFab(binding.fab)
                        .start());
        // Get round
        if (getArguments() != null) {
            roundId = getArguments().getLong(ROUND_ID, -1);
        }

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
    protected LoaderUICallback onLoad(Bundle args) {
        round = Round.get(roundId);
        final List<End> ends = round.getEnds();
        final boolean showFab = round.maxEndCount == null || ends.size() < round.maxEndCount;

        return new LoaderUICallback() {
            @Override
            public void applyData() {
                // Set round info
                adapter.setList(ends);
                binding.fab.setVisibility(showFab ? View.VISIBLE : View.GONE);

                ToolbarUtils.setTitle(RoundFragment.this,
                        String.format(Locale.ENGLISH, "%s %d", getString(R.string.round),
                                round.index + 1));
                ToolbarUtils.setSubtitle(RoundFragment.this, round.getReachedScore().toString());
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
                ScoreboardActivity
                        .getIntent(round.trainingId, round.getId())
                        .withContext(this)
                        .start();
                return true;
            case R.id.action_statistics:
                StatisticsActivity
                        .getIntent(Collections.singletonList(round.getId()))
                        .withContext(this)
                        .start();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onItemSelected(End item) {
        InputActivity.getIntent(round, item.index)
                .withContext(this)
                .start();
    }

    @Override
    protected void onEdit(End item) {
        InputActivity.getIntent(round, item.index)
                .withContext(this)
                .start();
    }

    private class EndAdapter extends SimpleListAdapterBase<End> {

        EndAdapter(Context context) {
            super(context);
        }

        @Override
        protected SelectableViewHolder<End> onCreateViewHolder(ViewGroup parent) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_end, parent, false);
            return new EndViewHolder(itemView);
        }
    }

    private class EndViewHolder extends SelectableViewHolder<End> {

        private final ItemEndBinding binding;

        EndViewHolder(View itemView) {
            super(itemView, mSelector, RoundFragment.this);
            binding = DataBindingUtil.bind(itemView);
        }

        @Override
        public void bindItem() {
            binding.shoots.setShots(round.getTarget(), item.getShots());
            binding.end.setText(getString(R.string.passe_n, (item.index + 1)));
        }
    }
}
