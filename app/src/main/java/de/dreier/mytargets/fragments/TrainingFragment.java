/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */

package de.dreier.mytargets.fragments;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.Snackbar;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import de.dreier.mytargets.ApplicationInstance;
import de.dreier.mytargets.R;
import de.dreier.mytargets.activities.InputActivity;
import de.dreier.mytargets.activities.ScoreboardActivity;
import de.dreier.mytargets.activities.SimpleFragmentActivityBase.EditRoundActivity;
import de.dreier.mytargets.activities.StatisticsActivity;
import de.dreier.mytargets.adapters.ExpandableNowListAdapter;
import de.dreier.mytargets.databinding.FragmentTrainingBinding;
import de.dreier.mytargets.databinding.ItemEndBinding;
import de.dreier.mytargets.databinding.ItemHeaderRoundBinding;
import de.dreier.mytargets.managers.dao.PasseDataSource;
import de.dreier.mytargets.managers.dao.RoundDataSource;
import de.dreier.mytargets.managers.dao.StandardRoundDataSource;
import de.dreier.mytargets.managers.dao.TrainingDataSource;
import de.dreier.mytargets.shared.models.Passe;
import de.dreier.mytargets.shared.models.Round;
import de.dreier.mytargets.shared.models.StandardRound;
import de.dreier.mytargets.shared.models.Training;
import de.dreier.mytargets.shared.utils.StandardRoundFactory;
import de.dreier.mytargets.utils.DataLoader;
import de.dreier.mytargets.utils.HeaderBindingHolder;
import de.dreier.mytargets.utils.HtmlUtils;
import de.dreier.mytargets.utils.Pair;
import de.dreier.mytargets.utils.ScoreboardImage;
import de.dreier.mytargets.utils.SelectableViewHolder;
import de.dreier.mytargets.utils.TargetImage;
import de.dreier.mytargets.utils.ToolbarUtils;

import static de.dreier.mytargets.utils.ActivityUtils.startActivityAnimated;

/**
 * Shows all passes of one training
 */
public class TrainingFragment extends ExpandableFragment<Round, Passe> {

    private final boolean[] equals = new boolean[2];
    protected FragmentTrainingBinding binding;
    private long mTraining;
    private ArrayList<Round> rounds;
    private RoundDataSource roundDataSource;
    private PasseDataSource passeDataSource;
    private StandardRoundDataSource standardRoundDataSource;
    private StandardRound standardRound;
    private TrainingDataSource trainingDataSource;

    public TrainingFragment() {
        itemTypeSelRes = R.plurals.passe_selected;
        itemTypeDelRes = R.plurals.passe_deleted;
        newStringRes = R.string.new_round;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_training, container, false);
        binding.recyclerView.setHasFixedSize(true);
        mAdapter = new PasseAdapter();
        binding.recyclerView.setAdapter(mAdapter);
        binding.fab.setOnClickListener(view -> {
            if (standardRound.club != StandardRoundFactory.CUSTOM_PRACTICE) {
                // Add new passe to training (standard round)
                for (int roundIndex = 0; roundIndex < rounds.size(); roundIndex++) {
                    Round round = rounds.get(roundIndex);
                    ArrayList<Passe> passes = passeDataSource.getAllByRound(round.getId());
                    if (passes.size() < round.info.passes) {
                        // Open InputActivity to add new passes
                        openPasse(round.getId(), passes.size());
                        return;
                    }
                }
            }
        });
        binding.fab1.setOnClickListener(view -> {
            // New passe to free training
            if (rounds.size() > 0) {
                Round round = rounds.get(rounds.size() - 1);
                ArrayList<Passe> passes = passeDataSource.getAllByRound(round.getId());
                openPasse(round.getId(), passes.size());
            }
        });
        binding.fab2.setOnClickListener(view -> {
            // New round to free training
            startActivityAnimated(getActivity(), EditRoundActivity.class, ITEM_ID, mTraining);
        });

        // Get training
        if (getArguments() != null) {
            mTraining = getArguments().getLong(ITEM_ID, -1);
        }

        // Set up toolbar
//        setToolbarTransitionName(binding.toolbar);
        ToolbarUtils.setSupportActionBar(this, binding.toolbar);
        ToolbarUtils.showHomeAsUp(this);
        setHasOptionsMenu(true);

        trainingDataSource = new TrainingDataSource();
        roundDataSource = new RoundDataSource();
        passeDataSource = new PasseDataSource();
        standardRoundDataSource = new StandardRoundDataSource();
        return binding.getRoot();
    }

    @Override
    public Loader<List<Passe>> onCreateLoader(int id, Bundle args) {
        return new DataLoader<>(getContext(), new PasseDataSource(),
                () -> passeDataSource.getAllByTraining(mTraining));
    }

    @Override
    public void onLoadFinished(Loader<List<Passe>> loader, List<Passe> data) {
        rounds = roundDataSource.getAll(mTraining);
        Training training = trainingDataSource.get(mTraining);
        standardRound = standardRoundDataSource.get(training.standardRoundId);

        // Set round info
        binding.detailScore.setText(HtmlUtils.fromHtml(HtmlUtils.getTrainingTopScoreDistribution(mTraining)));
        binding.detailRoundInfo.setText(HtmlUtils.fromHtml(HtmlUtils.getTrainingInfoHTML(training, rounds, equals, false)));
        setList(passeDataSource, rounds, data, child -> child.roundId, true);
        getActivity().supportInvalidateOptionsMenu();

        ToolbarUtils.setTitle(this, training.title);
        ToolbarUtils.setSubtitle(this, training.getFormattedDate());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.training, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        boolean hasPasses = mAdapter != null && mAdapter.getItemCount() > 1;
        menu.findItem(R.id.action_scoreboard).setVisible(hasPasses);
        menu.findItem(R.id.action_share).setVisible(hasPasses);
        menu.findItem(R.id.action_statistics).setVisible(hasPasses);
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
            case R.id.action_share:
                showShareDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showShareDialog() {
        BottomSheetDialog mBottomSheetDialog = new BottomSheetDialog(getContext());
        View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_share_sheet, null);
        view.findViewById(R.id.text).setOnClickListener(v -> shareText());
        view.findViewById(R.id.scoreboard).setOnClickListener(v -> shareImage(1));
        view.findViewById(R.id.dispersion_pattern).setOnClickListener(v -> shareImage(2));
        mBottomSheetDialog.setContentView(view);
        mBottomSheetDialog.show();
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
        List<Pair<String, Integer>> scoreCount = passeDataSource
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

    private void openPasse(long roundId, int passeIndex) {
        Intent i = new Intent(getContext(), InputActivity.class);
        i.putExtra(InputActivity.ROUND_ID, roundId);
        i.putExtra(InputActivity.PASSE_IND, passeIndex);
        startActivity(i);
    }

    private class PasseAdapter extends ExpandableNowListAdapter<Round, Passe> {

        @Override
        protected HeaderViewHolder getTopLevelViewHolder(ViewGroup parent) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_header_round, parent, false);
            return new HeaderViewHolder(itemView);
        }

        @Override
        protected SelectableViewHolder<Passe> getSecondLevelViewHolder(ViewGroup parent) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_end, parent, false);
            return new PasseViewHolder(itemView);
        }
    }

    private class PasseViewHolder extends SelectableViewHolder<Passe> {
        private final ItemEndBinding binding;

        PasseViewHolder(View itemView) {
            super(itemView, mSelector, TrainingFragment.this);
            binding = DataBindingUtil.bind(itemView);
        }

        @Override
        public void bindCursor() {
            Round r = roundDataSource.get(mItem.roundId);
            binding.shoots.setPoints(mItem, r.info.target);
            binding.passe.setText(ApplicationInstance.getContext().getString(R.string.passe_n, (mItem.index + 1)));
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
            binding.title.setText(String.format(Locale.ENGLISH, "%s %d", ApplicationInstance.getContext().getString(R.string.round),
                    rounds.indexOf(mItem) + 1));
            binding.subtitle.setText(HtmlUtils.fromHtml(HtmlUtils.getRoundInfo(mItem, equals)));
        }

        @Override
        public boolean onLongClick(View v) {
            Intent i = new Intent(getContext(), EditRoundActivity.class);
            i.putExtra(ITEM_ID, mTraining);
            i.putExtra(EditRoundFragment.ROUND_ID, mItem.getId());
            startActivity(i);
            return true;
        }
    }
}
