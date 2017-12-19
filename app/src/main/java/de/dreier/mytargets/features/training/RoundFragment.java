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

import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.v4.content.LocalBroadcastManager;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.afollestad.materialdialogs.MaterialDialog;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

import de.dreier.mytargets.R;
import de.dreier.mytargets.base.adapters.SimpleListAdapterBase;
import de.dreier.mytargets.base.fragments.EditableListFragment;
import de.dreier.mytargets.base.fragments.ItemActionModeCallback;
import de.dreier.mytargets.databinding.FragmentListBinding;
import de.dreier.mytargets.databinding.ItemEndBinding;
import de.dreier.mytargets.features.scoreboard.ScoreboardActivity;
import de.dreier.mytargets.features.settings.SettingsManager;
import de.dreier.mytargets.features.statistics.StatisticsActivity;
import de.dreier.mytargets.features.training.input.InputActivity;
import de.dreier.mytargets.shared.models.db.End;
import de.dreier.mytargets.shared.models.db.Round;
import de.dreier.mytargets.shared.models.db.Shot;
import de.dreier.mytargets.utils.DividerItemDecoration;
import de.dreier.mytargets.utils.IntentWrapper;
import de.dreier.mytargets.utils.MobileWearableClient;
import de.dreier.mytargets.utils.SlideInItemAnimator;
import de.dreier.mytargets.utils.ToolbarUtils;
import de.dreier.mytargets.utils.multiselector.SelectableViewHolder;

import static de.dreier.mytargets.utils.MobileWearableClient.BROADCAST_UPDATE_TRAINING_FROM_REMOTE;

/**
 * Shows all ends of one round
 */
public class RoundFragment extends EditableListFragment<End> {

    @VisibleForTesting
    public static final String ROUND_ID = "round_id";

    private long roundId;
    private FragmentListBinding binding;
    @Nullable
    private Round round;

    public RoundFragment() {
        itemTypeDelRes = R.plurals.passe_deleted;
        actionModeCallback = new ItemActionModeCallback(this, selector,
                R.plurals.passe_selected);
        actionModeCallback.setEditCallback(this::onEdit);
        actionModeCallback.setDeleteCallback(this::onDelete);
    }

    @NonNull
    public static IntentWrapper getIntent(@NonNull Round round) {
        return new IntentWrapper(RoundActivity.class)
                .with(ROUND_ID, round.getId())
                .clearTopSingleTop();
    }

    @NonNull
    private BroadcastReceiver updateReceiver = new MobileWearableClient.EndUpdateReceiver() {

        @Override
        protected void onUpdate(Long trainingId, Long round, End end) {
            if (roundId == round) {
                reloadData();
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(updateReceiver,
                new IntentFilter(BROADCAST_UPDATE_TRAINING_FROM_REMOTE));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(updateReceiver);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_list, container, false);
        binding.recyclerView.setHasFixedSize(true);
        binding.recyclerView.addItemDecoration(
                new DividerItemDecoration(getContext(), R.drawable.full_divider));
        adapter = new EndAdapter();
        binding.recyclerView.setItemAnimator(new SlideInItemAnimator());
        binding.recyclerView.setAdapter(adapter);
        binding.fab.setVisibility(View.GONE);
        binding.fab.setOnClickListener(
                v -> InputActivity.Companion
                        .getIntent(round, binding.recyclerView.getAdapter().getItemCount())
                        .withContext(this)
                        .fromFab(binding.fab)
                        .start());

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
        round = Round.Companion.get(roundId);
        final List<End> ends = round.loadEnds();
        final boolean showFab = round.getMaxEndCount() == null || ends.size() <
                round.getMaxEndCount();

        return () -> {
            adapter.setList(ends);
            binding.fab.setVisibility(showFab ? View.VISIBLE : View.GONE);

            ToolbarUtils.setTitle(RoundFragment.this,
                    String.format(Locale.US, "%s %d", getString(R.string.round),
                            round.getIndex() + 1));
            ToolbarUtils.setSubtitle(RoundFragment.this, round.getReachedScore().toString());
        };
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.statistics_scoresheet, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_statistics:
                StatisticsActivity.Companion
                        .getIntent(Collections.singletonList(round.getId()))
                        .withContext(this)
                        .start();
                return true;
            case R.id.action_comment:
                new MaterialDialog.Builder(getContext())
                        .title(R.string.comment)
                        .inputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE)
                        .input("", round.getComment(), (dialog, input) -> {
                            round.setComment(input.toString());
                            round.save();
                        })
                        .negativeText(android.R.string.cancel)
                        .show();
                return true;
            case R.id.action_scoreboard:
                ScoreboardActivity.Companion
                        .getIntent(round.getTrainingId(), round.getId())
                        .withContext(this)
                        .start();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onItemSelected(@NonNull End item) {
        InputActivity.Companion.getIntent(round, item.getIndex())
                .withContext(this)
                .start();
    }

    protected void onEdit(Long itemId) {
        InputActivity.Companion.getIntent(round, adapter.getItemById(itemId).getIndex())
                .withContext(this)
                .start();
    }

    private class EndAdapter extends SimpleListAdapterBase<End> {

        @NonNull
        @Override
        protected SelectableViewHolder<End> onCreateViewHolder(@NonNull ViewGroup parent) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_end, parent, false);
            return new EndViewHolder(itemView);
        }
    }

    private class EndViewHolder extends SelectableViewHolder<End> {

        private final ItemEndBinding binding;

        EndViewHolder(@NonNull View itemView) {
            super(itemView, selector, RoundFragment.this, RoundFragment.this);
            binding = DataBindingUtil.bind(itemView);
        }

        @Override
        public void bindItem() {
            List<Shot> shots = item.loadShots();
            if (SettingsManager.INSTANCE.shouldSortTarget(round.getTarget())) {
                Collections.sort(shots);
            }
            binding.shoots.setShots(round.getTarget(), shots);
            binding.imageIndicator
                    .setVisibility(item.loadImages().isEmpty() ? View.INVISIBLE : View.VISIBLE);
            binding.end.setText(getString(R.string.end_n, item.getIndex() + 1));
        }
    }
}
