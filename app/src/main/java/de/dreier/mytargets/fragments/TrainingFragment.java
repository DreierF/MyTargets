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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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
import de.dreier.mytargets.managers.dao.RoundDataSource;
import de.dreier.mytargets.shared.models.db.Passe;
import de.dreier.mytargets.shared.models.db.Round;
import de.dreier.mytargets.shared.models.db.StandardRound;
import de.dreier.mytargets.shared.models.db.Training;
import de.dreier.mytargets.shared.utils.StandardRoundFactory;
import de.dreier.mytargets.utils.DataLoader;
import de.dreier.mytargets.utils.FlowDataLoader;
import de.dreier.mytargets.utils.DividerItemDecoration;
import de.dreier.mytargets.utils.HtmlUtils;
import de.dreier.mytargets.shared.utils.Pair;
import de.dreier.mytargets.utils.ScoreboardImage;
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
    private StandardRound standardRound;

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

    @Override
    public Loader<List<Round>> onCreateLoader(int id, Bundle args) {
        return new FlowDataLoader<>(getContext(), () -> training.getRounds());
    }

    public void onLoadFinished(Loader<List<Round>> loader, List<Round> data) {
        rounds = training.getRounds();
        training = Training.get(mTraining);
        standardRound = StandardRound.get(training.standardRoundId);

        // Hide fab for standard rounds
        StandardRound standardRound = standardRoundDataSource.get(training.standardRoundId);
        binding.fab.setVisibility(standardRound.club == StandardRoundFactory.CUSTOM_PRACTICE ? View.VISIBLE : View.GONE);

        // Set round info
        binding.weatherIcon.setImageResource(training.environment.weather.getColorDrawable());
        binding.detailRoundInfo.setText(HtmlUtils.fromHtml(HtmlUtils.getTrainingInfoHTML(training, data, equals, false)));
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
                Intent i = new Intent(getContext(), StatisticsActivity.class);
                i.putExtra(StatisticsActivity.TRAINING_ID, mTraining);
                startActivity(i);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /* Called after the user selected with items he wants to share */
    private void shareImage(int typ) {
        // Construct share intent
        new Thread(() -> {
            try {
                File dir = getContext().getExternalCacheDir();
                final File f = File.createTempFile("target", ".png", dir);
                if (typ == 2) {
                    new TargetImage().generateTrainingBitmap(800, mTraining, f);
                } else {
                    new ScoreboardImage().generateBitmap(getActivity(), mTraining, f);
                }

                // Build and fire intent to ask for share provider
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(f));
                shareIntent.setType("*/*");
                startActivity(shareIntent);
            } catch (IOException e) {
                e.printStackTrace();
                Snackbar.make(binding.getRoot(), R.string.sharing_failed, Snackbar.LENGTH_SHORT)
                        .show();
            }
        }).start();
    }

    private void shareText() {
        List<Pair<String, Integer>> scoreCount = Passe
                .getTopScoreDistribution(mTraining);
        String scoreText = "";
        for (Pair<String, Integer> score : scoreCount.subList(0, 3)) {
            scoreText += getString(R.string.d_times_s, score.getSecond(), score.getFirst());
        }
        int maxPoints = 0;
        int reachedPoints = 0;
        for (Round r : rounds) {
            maxPoints += r.info.getMaxPoints();
            reachedPoints += r.reachedPoints;
        }
        final String text = getString(R.string.my_share_text,
                scoreText, reachedPoints, maxPoints);
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, text);
        shareIntent.setType("text/plain");
        startActivity(shareIntent);
    }

    @Override
    public void onSelected(Passe item) {
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

    @Override
    public void onClick(View v) {

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
            Round r = Round.get(mItem.roundId);
            binding.shoots.setPoints(mItem, r.info.target);
            binding.passe.setText(ApplicationInstance.getContext()
                    .getString(R.string.passe_n, (mItem.index + 1)));
        }
    }

    private class HeaderViewHolder extends HeaderBindingHolder<Round> {
        private final ItemHeaderRoundBinding binding;

        HeaderViewHolder(View itemView) {
            super(itemView, R.id.expand_collapse);
            binding = DataBindingUtil.bind(itemView);
            itemView.setLongClickable(true);
            itemView.setOnLongClickListener(this);
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
