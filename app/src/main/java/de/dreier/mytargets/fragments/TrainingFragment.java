/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */

package de.dreier.mytargets.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.Snackbar;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.dreier.mytargets.R;
import de.dreier.mytargets.activities.InputActivity;
import de.dreier.mytargets.activities.ScoreboardActivity;
import de.dreier.mytargets.activities.SimpleFragmentActivity;
import de.dreier.mytargets.activities.StatisticsActivity;
import de.dreier.mytargets.adapters.ExpandableNowListAdapter;
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
import de.dreier.mytargets.utils.FABMenu;
import de.dreier.mytargets.utils.HTMLUtils;
import de.dreier.mytargets.utils.HeaderBindingHolder;
import de.dreier.mytargets.utils.Pair;
import de.dreier.mytargets.utils.ScoreboardImage;
import de.dreier.mytargets.utils.SelectableViewHolder;
import de.dreier.mytargets.utils.TargetImage;
import de.dreier.mytargets.views.PasseView;

/**
 * Shows all passes of one training
 */
public class TrainingFragment extends ExpandableFragment<Round, Passe>
        implements View.OnClickListener, FABMenu.Listener {

    private final boolean[] equals = new boolean[2];
    private long mTraining;
    private ArrayList<Round> rounds;

    private FABMenu fabMenu;

    private Training training;
    private RoundDataSource roundDataSource;
    private PasseDataSource passeDataSource;
    private StandardRoundDataSource standardRoundDataSource;
    private StandardRound standardRound;
    private TrainingDataSource trainingDataSource;

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    public TrainingFragment() {
        itemTypeSelRes = R.plurals.passe_selected;
        itemTypeDelRes = R.plurals.passe_deleted;
        newStringRes = R.string.new_round;
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_training;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ButterKnife.bind(this, getView());

        fabMenu = new FABMenu(getContext(), rootView);
        fabMenu.setFABItem(1, R.drawable.ic_adjust_white_24dp, R.string.passe);
        fabMenu.setFABItem(2, R.drawable.ic_refresh_white_24dp, R.string.round);
        fabMenu.setListener(this);

        // Get training
        if (getArguments() != null) {
            mTraining = getArguments().getLong(ITEM_ID, -1);
        }

        // Set up toolbar
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(toolbar);
        setToolbarTransitionName(toolbar);
        ActionBar actionBar = activity.getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        setHasOptionsMenu(true);

        trainingDataSource = new TrainingDataSource();
        roundDataSource = new RoundDataSource();
        passeDataSource = new PasseDataSource();
        standardRoundDataSource = new StandardRoundDataSource();
    }

    @Override
    public void onResume() {
        super.onResume();
        fabMenu.reset();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        fabMenu.unbind();
    }

    @Override
    public Loader<List<Passe>> onCreateLoader(int id, Bundle args) {
        return new DataLoader<>(getContext(), new PasseDataSource(),
                () -> passeDataSource.getAllByTraining(mTraining));
    }

    @Override
    public void onLoadFinished(Loader<List<Passe>> loader, List<Passe> data) {
        rounds = roundDataSource.getAll(mTraining);
        training = trainingDataSource.get(mTraining);
        standardRound = standardRoundDataSource.get(training.standardRoundId);

        // Set round info
        setRoundInfo();
        setList(passeDataSource, rounds, data, child -> child.roundId, true, new PasseAdapter());
        getActivity().supportInvalidateOptionsMenu();

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        ActionBar actionBar = activity.getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle(training.title);
        actionBar.setSubtitle(DateFormat.getDateInstance().format(training.date));
    }

    @Override
    protected void updateFabButton(List list) {
        fabMenu.setFABHelperTitle(list.isEmpty() ? newStringRes : 0);
    }

    private void setRoundInfo() {
        TextView info = (TextView) rootView.findViewById(R.id.detail_round_info);
        TextView tvScore = (TextView) rootView.findViewById(R.id.detail_score);

        tvScore.setText(Html.fromHtml(HTMLUtils.getTrainingTopScoreDistribution(mTraining)));

        // Set training info
        info.setText(Html.fromHtml(HTMLUtils.getTrainingInfoHTML(training, rounds, equals, false)));
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
            /*case R.id.action_view_mode:
                mTargetViewMode = !mTargetViewMode;
                setList(rounds, db.getPasses(mTraining), true,
                        new PasseAdapter());
                return true;*/
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
                shareIntent.setType("image/png");
                startActivity(shareIntent);
            } catch (IOException e) {
                e.printStackTrace();
                Snackbar.make(rootView, R.string.sharing_failed, Snackbar.LENGTH_SHORT).show();
            }
        }).start();
    }

    private void shareText() {
        List<Pair<String, Integer>> scoreCount = passeDataSource
                .getTopScoreDistribution(mTraining);
        String scoreText = "";
        for (Pair<String, Integer> score : scoreCount.subList(0,3)) {
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
        Intent i = new Intent(getContext(), InputActivity.class);
        i.putExtra(InputActivity.ROUND_ID, item.roundId);
        i.putExtra(InputActivity.PASSE_IND, item.index);
        startActivity(i);
        getActivity().overridePendingTransition(R.anim.right_in, R.anim.left_out);
    }

    @Override
    protected void onEdit(Passe item) {
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public boolean isFABExpandable() {
        return standardRound.club == StandardRoundFactory.CUSTOM_PRACTICE;
    }

    @Override
    public void onFabClicked(int index) {
        switch (index) {
            case 0:
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
                break;
            case 1:
                // New passe to free training
                if (rounds.size() > 0) {
                    Round round = rounds.get(rounds.size() - 1);
                    ArrayList<Passe> passes = passeDataSource.getAllByRound(round.getId());
                    openPasse(round.getId(), passes.size());
                    return;
                }
                // Intended fall trough
            case 2:
                // New round to free training
                Intent i = new Intent(getContext(), SimpleFragmentActivity.EditRoundActivity.class);
                i.putExtra(ITEM_ID, mTraining);
                startActivity(i);
                break;
        }
    }

    private void openPasse(long roundId, int passeIndex) {
        Intent i = new Intent(getContext(), InputActivity.class);
        i.putExtra(InputActivity.ROUND_ID, roundId);
        i.putExtra(InputActivity.PASSE_IND, passeIndex);
        startActivity(i);
    }

    public class PasseAdapter extends ExpandableNowListAdapter<Round, Passe> {

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

        @Override
        public int getMaxSpan() {
            return 1;
        }
    }

    private class PasseViewHolder extends SelectableViewHolder<Passe> {
        private final PasseView mShots;
        private final TextView mSubtitle;

        public PasseViewHolder(View itemView) {
            super(itemView, mSelector, TrainingFragment.this);
            mShots = (PasseView) itemView.findViewById(R.id.shoots);
            mSubtitle = (TextView) itemView.findViewById(R.id.passe);
        }

        @Override
        public void bindCursor() {
            Context context = mSubtitle.getContext();
            Round r = roundDataSource.get(mItem.roundId);
            mShots.setPoints(mItem, r.info.target);
            mSubtitle.setText(context.getString(R.string.passe_n, (mItem.index + 1)));
        }
    }

    private class HeaderViewHolder extends HeaderBindingHolder<Round> {
        private final TextView mTitle;
        private final TextView mSubtitle;

        public HeaderViewHolder(View itemView) {
            super(itemView, R.id.expand_collapse);
            itemView.setLongClickable(true);
            itemView.setOnLongClickListener(this);
            mTitle = (TextView) itemView.findViewById(R.id.round);
            mSubtitle = (TextView) itemView.findViewById(R.id.dist);
        }

        @Override
        public void bindCursor() {
            Context context = mTitle.getContext();
            mTitle.setText(String.format(Locale.ENGLISH, "%s %d", context.getString(R.string.round), rounds.indexOf(mItem) + 1));

            String infoText = HTMLUtils.getRoundInfo(mItem, equals);
            mSubtitle.setText(Html.fromHtml(infoText));
        }

        @Override
        public boolean onLongClick(View v) {
            Intent i = new Intent(getContext(), SimpleFragmentActivity.EditRoundActivity.class);
            i.putExtra(ITEM_ID, mTraining);
            i.putExtra(EditRoundFragment.ROUND_ID, mItem.getId());
            startActivity(i);
            return true;
        }
    }
}
