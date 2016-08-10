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
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;
import java.util.Locale;

import de.dreier.mytargets.ApplicationInstance;
import de.dreier.mytargets.R;
import de.dreier.mytargets.activities.ScoreboardActivity;
import de.dreier.mytargets.activities.SimpleFragmentActivityBase.EditRoundActivity;
import de.dreier.mytargets.activities.SimpleFragmentActivityBase.RoundActivity;
import de.dreier.mytargets.activities.StatisticsActivity;
import de.dreier.mytargets.adapters.NowListAdapter;
import de.dreier.mytargets.databinding.FragmentTrainingBinding;
import de.dreier.mytargets.databinding.ItemRoundBinding;
import de.dreier.mytargets.shared.models.db.Round;
import de.dreier.mytargets.shared.models.db.StandardRound;
import de.dreier.mytargets.shared.models.db.Training;
import de.dreier.mytargets.shared.utils.StandardRoundFactory;
import de.dreier.mytargets.utils.DividerItemDecoration;
import de.dreier.mytargets.utils.HtmlUtils;
import de.dreier.mytargets.utils.SelectableViewHolder;
import de.dreier.mytargets.utils.ToolbarUtils;

import static de.dreier.mytargets.fragments.RoundFragment.ROUND_ID;
import static de.dreier.mytargets.utils.ActivityUtils.startActivityAnimated;

/**
 * Shows all passes of one training
 */
public class TrainingFragment extends EditableFragment<Round> {

    private final boolean[] equals = new boolean[2];
    protected FragmentTrainingBinding binding;
    private long mTraining;
    private Training training;

    public TrainingFragment() {
        itemTypeSelRes = R.plurals.round_selected;
        itemTypeDelRes = R.plurals.round_deleted;
        newStringRes = R.string.new_round;
    }

    @Override
    @CallSuper
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_training, container, false);

        binding.recyclerView.setHasFixedSize(true);
        binding.recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), R.drawable.full_divider));
        mAdapter = new RoundAdapter(getContext());
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

        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ToolbarUtils.setSupportActionBar(this, binding.toolbar);
        ToolbarUtils.showHomeAsUp(this);
        setHasOptionsMenu(true);
    }

    @NonNull
    @Override
    protected LoaderUICallback onLoad(Bundle args) {
        training = Training.get(mTraining);
        List<Round> rounds = training.getRounds();
        StandardRound standardRound = StandardRound.get(training.standardRoundId);
        return new LoaderUICallback() {
            @Override
            public void applyData() {
                // Hide fab for standard rounds
                binding.fab.setVisibility(standardRound.club == StandardRoundFactory.CUSTOM_PRACTICE ? View.VISIBLE : View.GONE);

                // Set round info
                binding.weatherIcon.setImageResource(training.weather.getColorDrawable());
                final Spanned details = HtmlUtils.fromHtml(HtmlUtils.getTrainingInfoHTML(training, rounds, equals, false));
                binding.detailRoundInfo.setText(details);
                mAdapter.setList(rounds);
                TrainingFragment.this.getActivity().supportInvalidateOptionsMenu();

                ToolbarUtils.setTitle(TrainingFragment.this, training.title);
                ToolbarUtils.setSubtitle(TrainingFragment.this, training.getFormattedDate());
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
                intent.putExtra(ScoreboardActivity.TRAINING_ID, mTraining);
                startActivity(intent);
                return true;
            case R.id.action_statistics:
                Intent i = new Intent(getContext(), StatisticsActivity.class);
                i.putExtra(StatisticsActivity.TRAINING_ID, mTraining);
                startActivity(i);
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

    private class RoundAdapter extends NowListAdapter<Round> {

        RoundAdapter(Context context) {
            super(context);
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
