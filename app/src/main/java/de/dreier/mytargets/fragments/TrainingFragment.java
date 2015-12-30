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
import android.support.design.widget.FloatingActionButton;
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

import com.cocosw.bottomsheet.BottomSheet;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;

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
import de.dreier.mytargets.utils.DataLoader;
import de.dreier.mytargets.utils.HeaderBindingHolder;
import de.dreier.mytargets.utils.Pair;
import de.dreier.mytargets.utils.ScoreboardImage;
import de.dreier.mytargets.utils.HTMLUtils;
import de.dreier.mytargets.utils.SelectableViewHolder;
import de.dreier.mytargets.utils.TargetImage;
import de.dreier.mytargets.views.PasseView;
import de.dreier.mytargets.views.TargetPasseView;

/**
 * Shows all passes of one training
 */
public class TrainingFragment extends ExpandableFragment<Round, Passe>
        implements View.OnClickListener, MenuItem.OnMenuItemClickListener {

    private final boolean mTargetViewMode = false;
    private final boolean[] equals = new boolean[2];
    private long mTraining;
    private ArrayList<Round> mRounds;
    private FloatingActionButton mFab;
    private View mNewLayout;
    private TextView mNewText;
    private Training training;
    private RoundDataSource roundDataSource;
    private PasseDataSource passeDataSource;

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
        mFab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        mFab.setOnClickListener(this);
        mNewLayout = rootView.findViewById(R.id.new_layout);
        mNewText = (TextView) rootView.findViewById(R.id.new_text);

        // Get training
        if (getArguments() != null) {
            mTraining = getArguments().getLong(ITEM_ID, -1);
        }
        if (savedInstanceState != null) {
            mTraining = savedInstanceState.getLong(ITEM_ID, -1);
        }
        TrainingDataSource trainingDataSource = new TrainingDataSource(getContext());
        training = trainingDataSource.get(mTraining);

        // Set up toolbar
        Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(toolbar);
        ActionBar actionBar = activity.getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        setHasOptionsMenu(true);

        actionBar.setTitle(training.title);
        actionBar.setSubtitle(DateFormat.getDateInstance().format(training.date));

        roundDataSource = new RoundDataSource(getContext());
        passeDataSource = new PasseDataSource(getContext());
    }

    @Override
    public Loader<List<Passe>> onCreateLoader(int id, Bundle args) {
        return new DataLoader<>(getContext(), new PasseDataSource(getContext()),
                () -> passeDataSource.getAllByTraining(mTraining));
    }

    @Override
    public void onLoadFinished(Loader<List<Passe>> loader, List<Passe> data) {
        // Set round info
        mRounds = roundDataSource.getAll(mTraining);
        setRoundInfo();

        setList(passeDataSource, mRounds, data, true, new PasseAdapter());
        mAdapter.notifyDataSetChanged();
        getActivity().supportInvalidateOptionsMenu();
    }

    @Override
    protected void updateFabButton(List list) {
        if (newStringRes != 0) {
            mNewLayout.setVisibility(list.isEmpty() ? View.VISIBLE : View.GONE);
            mNewText.setText(newStringRes);
            mFab.setVisibility(View.VISIBLE);
        } else {
            mFab.setVisibility(View.GONE);
        }
    }

    private void setRoundInfo() {
        TextView info = (TextView) rootView.findViewById(R.id.detail_round_info);
        TextView tvScore = (TextView) rootView.findViewById(R.id.detail_score);

        tvScore.setText(Html.fromHtml(HTMLUtils.getTrainingTopScoreDistribution(getContext(), mTraining)));

        // Set training info
        info.setText(Html.fromHtml(HTMLUtils.getTrainingInfoHTML(getActivity(), training, mRounds, equals)));
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
        //menu.findItem(R.id.action_view_mode).setVisible(hasPasses);
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
                setList(mRounds, db.getPasses(mTraining), true,
                        new PasseAdapter());
                return true;*/
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showShareDialog() {
        new BottomSheet.Builder(getActivity())
                .title(R.string.share)
                .grid()
                .sheet(R.menu.share)
                .listener(this)
                .show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.text:
                shareText();
                return true;
            case R.id.scoreboard:
                shareImage(1);
                return true;
            case R.id.dispersion_pattern:
                shareImage(2);
                return true;
        }
        return false;
    }

    /* Called after the user selected with items he wants to share */
    private void shareImage(int typ) {
        // Construct share intent
        new Thread(() -> {
            try {
                File dir = getContext().getExternalCacheDir();
                final File f = File.createTempFile("target", ".png", dir);
                if (typ == 2) {
                    new TargetImage().generateTrainingBitmap(getContext(), 800, mTraining, f);
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
        mRounds = roundDataSource.getAll(mTraining);
        ArrayList<Pair<String, Integer>> scoreCount = passeDataSource
                .getTopScoreDistribution(mTraining);
        String scoreText = "";
        for (Pair<String, Integer> score : scoreCount) {
            scoreText += getString(R.string.d_times_s, score.getSecond(), score.getFirst());
        }
        int maxPoints = 0;
        int reachedPoints = 0;
        for (Round r : mRounds) {
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
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(ITEM_ID, mTraining);
    }

    @Override
    public void onClick(View v) {
        ArrayList<Round> rounds = roundDataSource.getAll(mTraining);
        for (int roundIndex = 0; roundIndex < rounds.size(); roundIndex++) {
            Round mRound = rounds.get(roundIndex);
            ArrayList<Passe> passes = passeDataSource.getAllByRound(mRound.getId());
            if (passes.size() < mRound.info.passes) {
                // Open InputActivity to add new passes
                Intent i = new Intent(getContext(), InputActivity.class);
                i.putExtra(InputActivity.ROUND_ID, mRound.getId());
                i.putExtra(InputActivity.PASSE_IND, passes.size());
                startActivity(i);
                return;
            }

            // Open dialog to create new round
            StandardRoundDataSource standardRoundDataSource = new StandardRoundDataSource(getContext());
            StandardRound standardRound = standardRoundDataSource.get(training.standardRoundId);
            if (roundIndex + 1 == rounds.size() &&
                    standardRound.club == StandardRound.CUSTOM_PRACTICE) {
                Intent i = new Intent(getContext(), SimpleFragmentActivity.EditRoundActivity.class);
                i.putExtra(EditRoundFragment.TRAINING_ID, mTraining);
                startActivity(i);
                return;
            }
        }
    }

    public class PasseAdapter extends ExpandableNowListAdapter<Round, Passe> {

        @Override
        protected HeaderViewHolder getTopLevelViewHolder(ViewGroup parent) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_round, parent, false);
            return new HeaderViewHolder(itemView);
        }

        @Override
        protected SelectableViewHolder<Passe> getSecondLevelViewHolder(ViewGroup parent) {
            if (mTargetViewMode) {
                View itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.card_target_end, parent, false);
                return new TargetViewHolder(itemView);
            } else {
                View itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.card_end, parent, false);
                return new PasseViewHolder(itemView);
            }
        }

        @Override
        public int getMaxSpan() {
            return mTargetViewMode ? 2 : 1;
        }

        @Override
        public int getItemViewType(int position) {
            int type = super.getItemViewType(position);
            if (type == ITEM_TYPE && mTargetViewMode) {
                type = ITEM_TYPE_2;
            }
            return type;
        }
    }

    public class TargetViewHolder extends SelectableViewHolder<Passe> {
        public final TargetPasseView mShots;
        public final TextView mSubtitle;

        public TargetViewHolder(View itemView) {
            super(itemView, mSelector, TrainingFragment.this);
            mShots = (TargetPasseView) itemView.findViewById(R.id.shoots);
            mSubtitle = (TextView) itemView.findViewById(R.id.passe);
        }

        @Override
        public void bindCursor() {
            Context context = mSubtitle.getContext();
            Round r = roundDataSource.get(mItem.roundId);
            mShots.setPasse(mItem, r.info.target);
            mSubtitle.setText(context.getString(R.string.passe_n, (mItem.index + 1)));
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
            mTitle.setText(context.getString(R.string.round) + " " + (mRounds.indexOf(mItem) + 1));

            String infoText = HTMLUtils.getRoundInfoHTML(context, mItem, equals);
            mSubtitle.setText(Html.fromHtml(infoText));
        }

        @Override
        public boolean onLongClick(View v) {
            Intent i = new Intent(getContext(), SimpleFragmentActivity.EditRoundActivity.class);
            i.putExtra(EditRoundFragment.TRAINING_ID, mTraining);
            i.putExtra(EditRoundFragment.ROUND_ID, mItem.getId());
            startActivity(i);
            return true;
        }
    }
}
