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
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bignerdranch.android.recyclerviewchoicemode.CardViewHolder;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.github.ksoichiro.android.observablescrollview.ObservableRecyclerView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.github.ksoichiro.android.observablescrollview.ScrollUtils;
import com.nineoldandroids.view.ViewHelper;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;

import de.dreier.mytargets.R;
import de.dreier.mytargets.activities.InputActivity;
import de.dreier.mytargets.activities.ScoreboardActivity;
import de.dreier.mytargets.activities.SimpleFragmentActivity;
import de.dreier.mytargets.activities.StatisticsActivity;
import de.dreier.mytargets.adapters.ExpandableNowListAdapter;
import de.dreier.mytargets.shared.models.Arrow;
import de.dreier.mytargets.shared.models.Bow;
import de.dreier.mytargets.shared.models.Passe;
import de.dreier.mytargets.shared.models.Round;
import de.dreier.mytargets.shared.models.Target;
import de.dreier.mytargets.shared.models.Training;
import de.dreier.mytargets.utils.ScoreboardImage;
import de.dreier.mytargets.utils.TargetImage;
import de.dreier.mytargets.utils.ToolbarUtils;
import de.dreier.mytargets.views.PasseView;
import de.dreier.mytargets.views.TargetPasseView;

/**
 * Shows all passes of one round
 */
public class PasseFragment extends ExpandableNowListFragment<Round, Passe>
        implements ShareDialogFragment.ShareDialogListener, ObservableScrollViewCallbacks {

    private long mTraining;

    private ArrayList<Round> mRounds;
    private Toolbar mHeader;
    private int mActionBarSize;
    private int mHeaderHeight;
    private View mDetails;
    private View mShadow;

    boolean indoor_equals = true, target_equals = true;
    boolean bow_equals = true, arrow_equals = true;
    boolean distance_equals = true;
    private FloatingActionButton passe;
    private boolean mTargetViewMode = false;
    protected FloatingActionsMenu mFab;

    @Override
    protected void setFabVisibility(int visibility) {
        mFab.setVisibility(visibility);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_round;
    }

    @Override
    protected void init(Bundle intent, Bundle savedInstanceState) {
        itemTypeRes = R.plurals.passe_selected;
        itemTypeDelRes = R.plurals.passe_deleted;
        newStringRes = R.string.new_round;

        mFab = (FloatingActionsMenu) rootView.findViewById(R.id.fab);
        mFab.setOnClickListener(this);

        if (intent != null) {
            mTraining = intent.getLong(TRAINING_ID, -1);
        }
        if (savedInstanceState != null) {
            mTraining = savedInstanceState.getLong(TRAINING_ID, -1);
        }

        // Get UI elements
        Toolbar toolbar = (Toolbar) activity.findViewById(R.id.toolbar);
        mHeader = (Toolbar) activity.findViewById(R.id.round_container);
        mDetails = activity.findViewById(R.id.round_container_content);
        mShadow = activity.findViewById(R.id.shadow);
        ObservableRecyclerView recyclerView = (ObservableRecyclerView) mRecyclerView;

        passe = (FloatingActionButton) mFab.findViewById(R.id.new_passe);
        passe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(activity, InputActivity.class);
                i.putExtra(InputActivity.ROUND_ID, mRounds.get(mRounds.size() - 1).getId());
                startActivity(i);
                mFab.collapse();
            }
        });
        FloatingActionButton round = (FloatingActionButton) mFab.findViewById(R.id.new_round);
        round.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(activity, SimpleFragmentActivity.EditRoundActivity.class);
                i.putExtra(TRAINING_ID, mTraining);
                startActivity(i);
                mFab.collapse();
            }
        });

        // Set listeners
        recyclerView.setScrollViewCallbacks(this);
        onScrollChanged(0, true, true);

        // Set up toolbar
        activity.setSupportActionBar(toolbar);
        Training tr = db.getTraining(mTraining);
        ActionBar actionBar = activity.getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle(tr.title);
        actionBar.setSubtitle(DateFormat.getDateInstance().format(tr.date));
        actionBar.setDisplayHomeAsUpEnabled(true);
        setHasOptionsMenu(true);
    }

    @Override
    public void onResume() {
        super.onResume();

        // Set round info
        mRounds = db.getRounds(mTraining);
        setRoundInfo();

        if (mRounds.size() == 0) {
            passe.setVisibility(View.GONE);
        } else {
            passe.setVisibility(View.VISIBLE);
        }

        // Load values for animations
        mActionBarSize = ToolbarUtils.getActionBarSize(activity);
        mHeaderHeight = getResources().getDimensionPixelSize(R.dimen.ext_toolbar_round_height);

        if (mRecyclerView.getAdapter() != null) {
            mAdapter.setHeaderHeight(mHeaderHeight + mActionBarSize);
        }
        setList(mRounds, db.getPasses(mTraining), true,
                new PasseAdapter(mHeaderHeight + mActionBarSize));
        onScrollChanged(mRecyclerView.getScrollY(), true, true);

        activity.supportInvalidateOptionsMenu();
    }

    void setRoundInfo() {
        if (mRounds.size() == 0) {
            return;
        }
        // Aggregate round information
        Round round = mRounds.get(0);
        boolean indoor = round.indoor;
        String distance = round.distance.toString();
        int target = round.target;
        long bowId = round.bow;
        long arrowId = round.arrow;
        indoor_equals = true;
        target_equals = true;
        bow_equals = true;
        arrow_equals = true;
        distance_equals = true;
        int reached = 0, max = 0;
        for (Round r : mRounds) {
            reached += r.reachedPoints;
            max += r.maxPoints;
            indoor_equals = r.indoor == indoor && indoor_equals;
            distance_equals = r.distance.toString().equals(distance) && distance_equals;
            target_equals = r.target == target && target_equals;
            bow_equals = r.bow == bowId && bow_equals;
            arrow_equals = r.arrow == arrowId && arrow_equals;
        }

        TextView info = (TextView) activity.findViewById(R.id.detail_round_info);
        TextView score = (TextView) activity.findViewById(R.id.detail_score);

        // Set round info
        String percent = max == 0 ? "" : " (" + (reached * 100 / max) + "%)";
        String infoText =
                "<font color=#ffffff>" + getString(R.string.points) + ": <b>" + reached + "/" +
                        max + percent + "</b>";
        if (distance_equals && indoor_equals) {
            infoText += "<br>" + getString(R.string.distance) + ": <b>" +
                    distance + " - " +
                    getString(indoor ? R.string.indoor : R.string.outdoor) + "</b>";
        }
        if (target_equals) {
            infoText += "<br>" + getString(R.string.target_round) + ": <b>" +
                    Target.list.get(target).name + "</b>";
        }
        if (bow_equals) {
            Bow bow = db.getBow(bowId, true);
            if (bow != null) {
                infoText += "<br>" + getString(R.string.bow) +
                        ": <b>" + TextUtils.htmlEncode(bow.name) + "</b>";
            }
        }
        if (arrow_equals) {
            Arrow arrow = db.getArrow(arrowId, true);
            if (arrow != null) {
                infoText += "<br>" + getString(R.string.arrow) +
                        ": <b>" + TextUtils.htmlEncode(arrow.name) + "</b>";
            }
        }
        infoText += "</font>";
        info.setText(Html.fromHtml(infoText));

        // Set number of X, 10, 9 shoots
        Training training = db.getTraining(mTraining);
        infoText = "<font color=#ffffff>X: <b>" + training.scoreCount[0] + "</b><br>" +
                getString(R.string.ten_x) + ": <b>" +
                (training.scoreCount[0] + training.scoreCount[1]) + "</b><br>" +
                getString(R.string.nine) + ": <b>" + training.scoreCount[2] + "</b></font>";
        score.setText(Html.fromHtml(infoText));
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.round, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        boolean hasPasses = mAdapter.getItemCount() > 1;
        menu.findItem(R.id.action_scoreboard).setVisible(hasPasses);
        menu.findItem(R.id.action_share).setVisible(hasPasses);
        menu.findItem(R.id.action_statistics).setVisible(hasPasses);
        menu.findItem(R.id.action_view_mode).setVisible(hasPasses);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_scoreboard:
                Intent intent = new Intent(activity, ScoreboardActivity.class);
                intent.putExtra(ScoreboardActivity.TRAINING_ID, mTraining);
                startActivity(intent);
                return true;
            case R.id.action_statistics:
                Intent i = new Intent(activity, StatisticsActivity.class);
                i.putExtra(StatisticsActivity.TRAINING_ID, mTraining);
                startActivity(i);
                return true;
            case R.id.action_share:
                showShareDialog();
                return true;
            case R.id.action_view_mode:
                mTargetViewMode = !mTargetViewMode;
                setList(mRounds, db.getPasses(mTraining), true,
                        new PasseAdapter(mHeaderHeight + mActionBarSize));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    void showShareDialog() {
        // Create an instance of the dialog fragment and show it
        ShareDialogFragment dialog = new ShareDialogFragment();
        dialog.setTargetFragment(this, 0);
        dialog.show(activity.getSupportFragmentManager(), "share_dialog");
    }

    /* Called after the user selected with items he wants to share */
    @Override
    public void onShareDialogConfirmed(final boolean include_text, final boolean dispersion_pattern, final boolean scoreboard, final boolean comments) {
        // Construct share intent
        mRounds = db.getRounds(mTraining);
        Training training = db.getTraining(mTraining);

        final String text = getString(R.string.my_share_text,
                training.scoreCount[0], training.scoreCount[1],
                training.scoreCount[2], training.reachedPoints, training.maxPoints);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final File f = File
                            .createTempFile("target", ".png", activity.getExternalCacheDir());
                    if (dispersion_pattern && !scoreboard && !comments) {
                        new TargetImage().generateBitmap(activity, 800, mTraining, f);
                    } else {
                        new ScoreboardImage()
                                .generateBitmap(activity, mTraining, scoreboard, dispersion_pattern,
                                        comments, f);
                    }

                    // Build and fire intent to ask for share provider
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    if (include_text) {
                        shareIntent.putExtra(Intent.EXTRA_TEXT, text);
                    }
                    if (dispersion_pattern || scoreboard || comments) {
                        shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(f));
                    }
                    shareIntent.setType("*/*");
                    startActivity(shareIntent);
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(activity, getString(R.string.sharing_failed), Toast.LENGTH_SHORT)
                            .show();
                }
            }
        }).start();
    }

    /*private final View.OnClickListener headerClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent i = new Intent(activity, EditRoundActivity.class);
            i.putExtra(EditRoundActivity.TRAINING_ID, mTraining);
            i.putExtra(EditRoundActivity.ROUND_ID, mRound);
            startActivity(i);
        }
    };*/

    @Override
    protected void onNew(Intent i) {
    }

    @Override
    public void onSelected(Passe item) {
        Intent i = new Intent(activity, InputActivity.class);
        i.putExtra(InputActivity.ROUND_ID, item.roundId);
        i.putExtra(InputActivity.PASSE_IND, item.index);
        startActivity(i);
        activity.overridePendingTransition(R.anim.right_in, R.anim.left_out);
    }

    @Override
    protected void onEdit(Passe item) {
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(TRAINING_ID, mTraining);
    }

    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
        int translationShadow = Math.max(mActionBarSize, mActionBarSize + mHeaderHeight - scrollY);
        ViewHelper.setTranslationY(mShadow, translationShadow);
        ViewHelper.setTranslationY(mHeader, mActionBarSize - scrollY);
        ViewHelper.setAlpha(mDetails, ScrollUtils
                .getFloat((float) (mHeaderHeight - scrollY * 2) / mHeaderHeight, 0, 1));
    }

    @Override
    public void onDownMotionEvent() {
    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {
    }

    public class PasseAdapter extends ExpandableNowListAdapter<Round, Passe> {
        public PasseAdapter(int header) {
            headerHeight = header;
        }

        @Override
        protected HeaderViewHolder getTopLevelViewHolder(ViewGroup parent) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_round, parent, false);
            return new HeaderViewHolder(itemView);
        }

        @Override
        protected CardViewHolder<Passe> getSecondLevelViewHolder(ViewGroup parent) {
            if (mTargetViewMode) {
                View itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.target_passe_card, parent, false);
                return new TargetViewHolder(itemView);
            } else {
                View itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.passe_card, parent, false);
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

    public class TargetViewHolder extends CardViewHolder<Passe> {
        public final TargetPasseView mShots;
        public final TextView mSubtitle;

        public TargetViewHolder(View itemView) {
            super(itemView, mMultiSelector, PasseFragment.this);
            mShots = (TargetPasseView) itemView.findViewById(R.id.shoots);
            mSubtitle = (TextView) itemView.findViewById(R.id.passe);
        }

        @Override
        public void bindCursor() {
            Context context = mSubtitle.getContext();
            Round r = db.getRound(mItem.roundId);
            mShots.setPasse(mItem, r.target);
            mSubtitle.setText(context.getString(R.string.passe_n, (mItem.index + 1)));
        }
    }

    public class PasseViewHolder extends CardViewHolder<Passe> {
        public final PasseView mShots;
        public final TextView mSubtitle;

        public PasseViewHolder(View itemView) {
            super(itemView, mMultiSelector, PasseFragment.this);
            mShots = (PasseView) itemView.findViewById(R.id.shoots);
            mSubtitle = (TextView) itemView.findViewById(R.id.passe);
        }

        @Override
        public void bindCursor() {
            Context context = mSubtitle.getContext();
            Round r = db.getRound(mItem.roundId);
            mShots.setPoints(mItem, r.target);
            mSubtitle.setText(context.getString(R.string.passe_n, (mItem.index + 1)));
        }
    }


    public class HeaderViewHolder extends CardViewHolder<Round> {
        public final TextView mTitle;
        public final TextView mSubtitle;
        private final TextView mPoints;
        private final TextView mPercentage;

        public HeaderViewHolder(View itemView) {
            super(itemView, null, null);
            itemView.setLongClickable(true);
            itemView.setOnLongClickListener(this);
            mTitle = (TextView) itemView.findViewById(R.id.round);
            mSubtitle = (TextView) itemView.findViewById(R.id.dist);
            mPoints = (TextView) itemView.findViewById(R.id.totalPoints);
            mPercentage = (TextView) itemView.findViewById(R.id.totalPercentage);
        }

        @Override
        public void bindCursor() {
            Context context = mTitle.getContext();
            mTitle.setText(context.getString(R.string.round) + " " + (mRounds.indexOf(mItem) + 1));
            mPoints.setText(mItem.reachedPoints + "/" + mItem.maxPoints);
            String percent = mItem.maxPoints == 0 ? "" : (mItem.reachedPoints * 100 / mItem.maxPoints) + "%";
            mPercentage.setText(percent);

            String infoText = "";
            if (!distance_equals || !indoor_equals) {
                infoText += "<br>" + getString(R.string.distance) + ": <b>" +
                        mItem.distance + " - " +
                        getString(mItem.indoor ? R.string.indoor : R.string.outdoor) + "</b>";
            }
            if (!target_equals) {
                infoText += "<br>" + getString(R.string.target_round) + ": <b>" +
                        Target.list.get(mItem.target).name + "</b>";
            }
            if (!bow_equals) {
                Bow bow = db.getBow(mItem.bow, true);
                if (bow != null) {
                    infoText += "<br>" + getString(R.string.bow) +
                            ": <b>" + TextUtils.htmlEncode(bow.name) + "</b>";
                }
            }
            if (!arrow_equals) {
                Arrow arrow = db.getArrow(mItem.arrow, true);
                if (arrow != null) {
                    infoText += "<br>" + getString(R.string.arrow) +
                            ": <b>" + TextUtils.htmlEncode(arrow.name) + "</b>";
                }
            }
            if (!mItem.comment.isEmpty()) {
                infoText += "<br>" + getString(R.string.comment) +
                        ": <b>" + TextUtils.htmlEncode(mItem.comment) + "</b>";
            }

            if (infoText.startsWith("<br>")) {
                infoText = infoText.substring(4);
            }

            mSubtitle.setText(Html.fromHtml(infoText));
        }

        @Override
        public boolean onLongClick(View v) {
            Intent i = new Intent(getActivity(), SimpleFragmentActivity.EditRoundActivity.class);
            i.putExtra(EditRoundFragment.TRAINING_ID, mTraining);
            i.putExtra(EditRoundFragment.ROUND_ID, mItem.id);
            startActivity(i);
            return true;
        }
    }
}
