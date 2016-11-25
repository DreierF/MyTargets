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

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import de.dreier.mytargets.shared.models.db.Passe;
import de.dreier.mytargets.shared.models.db.Round;
import de.dreier.mytargets.shared.models.db.StandardRound;
import de.dreier.mytargets.shared.models.db.Training;
import de.dreier.mytargets.shared.utils.StandardRoundFactory;
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
    protected LoaderUICallback onLoad(Bundle args) {
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
            binding.shoots.setPoints(mItem, round.getTarget());
            binding.passe.setText(getString(R.string.passe_n, (mItem.index + 1)));
        }
    }
}
