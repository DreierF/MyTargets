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
import com.github.ksoichiro.android.observablescrollview.ObservableRecyclerView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.github.ksoichiro.android.observablescrollview.ScrollUtils;
import com.nineoldandroids.view.ViewHelper;

import java.io.File;
import java.io.IOException;

import de.dreier.mytargets.R;
import de.dreier.mytargets.activities.EditRoundActivity;
import de.dreier.mytargets.activities.InputActivity;
import de.dreier.mytargets.activities.ScoreboardActivity;
import de.dreier.mytargets.activities.StatisticsActivity;
import de.dreier.mytargets.adapters.NowListAdapter;
import de.dreier.mytargets.adapters.TargetItemAdapter;
import de.dreier.mytargets.models.Arrow;
import de.dreier.mytargets.models.Bow;
import de.dreier.mytargets.models.Passe;
import de.dreier.mytargets.models.Round;
import de.dreier.mytargets.models.Target;
import de.dreier.mytargets.utils.ScoreboardImage;
import de.dreier.mytargets.utils.TargetImage;
import de.dreier.mytargets.utils.ToolbarUtils;
import de.dreier.mytargets.views.PassesView;

/**
 * Shows all passes of one round
 */
public class PasseFragment extends NowListFragment<Passe> implements ShareDialogFragment.ShareDialogListener, ObservableScrollViewCallbacks {

    private long mTraining;
    private long mRound;

    private Round mRoundInfo;
    private Toolbar mHeader;
    private int mActionBarSize;
    private int mHeaderHeight;
    private View mDetails;
    private View mShadow;

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_round;
    }

    @Override
    protected void init(Bundle intent, Bundle savedInstanceState) {
        itemTypeRes = R.plurals.passe;
        newStringRes = R.string.new_passe;
        if (intent != null) {
            mTraining = intent.getLong(TRAINING_ID, -1);
            mRound = intent.getLong(ROUND_ID, -1);
        }
        if (savedInstanceState != null) {
            mTraining = savedInstanceState.getLong(TRAINING_ID, -1);
            mRound = savedInstanceState.getLong(ROUND_ID, -1);
        }

        // Get UI elements
        Toolbar toolbar = (Toolbar) activity.findViewById(R.id.toolbar);
        mHeader = (Toolbar) activity.findViewById(R.id.round_container);
        mDetails = activity.findViewById(R.id.round_container_content);
        mShadow = activity.findViewById(R.id.shadow);
        ObservableRecyclerView recyclerView = (ObservableRecyclerView) mRecyclerView;

        // Set listeners
        recyclerView.setScrollViewCallbacks(this);
        onScrollChanged(0, true, true);
        mHeader.setOnClickListener(headerClickListener);

        // Set up toolbar
        activity.setSupportActionBar(toolbar);
        ActionBar actionBar = activity.getSupportActionBar();
        actionBar.setTitle(getString(R.string.round) + " " + db.getRoundInd(mTraining, mRound));
        actionBar.setDisplayHomeAsUpEnabled(true);
        setHasOptionsMenu(true);
    }

    @Override
    public void onResume() {
        super.onResume();

        // Set round info
        mRoundInfo = db.getRound(mRound);
        setRoundInfo();

        // Load values for animations
        mActionBarSize = ToolbarUtils.getActionBarSize(activity);
        mHeaderHeight = getResources().getDimensionPixelSize(R.dimen.ext_toolbar_round_height);

        if(mRecyclerView.getAdapter() != null) {
            mAdapter.setHeaderHeight(mHeaderHeight + mActionBarSize);
        }
        setList(db.getPasses(mRound), new PasseAdapter(mHeaderHeight + mActionBarSize));
        onScrollChanged(mRecyclerView.getScrollY(), true, true);

        activity.supportInvalidateOptionsMenu();
    }

    public void setRoundInfo() {
        int reached = mRoundInfo.reachedPoints;
        int max = mRoundInfo.maxPoints;

        TextView info = (TextView) activity.findViewById(R.id.detail_round_info);
        TextView score = (TextView) activity.findViewById(R.id.detail_score);

        // Set round info
        String percent = max == 0 ? "" : " (" + (reached * 100 / max) + "%)";
        String infoText = "<font color=#ffffff>" + getString(R.string.distance) + "</font>: <font color=#ff9100><b>" +
                mRoundInfo.distance + " - " +
                getString(mRoundInfo.indoor ? R.string.indoor : R.string.outdoor) + "</b></font><br>" +
                "<font color=#ffffff>" + getString(R.string.points) + "</font>: <font color=#ff9100><b>" + reached + "/" + max + percent + "</b></font><br>" +
                "<font color=#ffffff>" + getString(R.string.target_round) + "</font>: <font color=#ff9100><b>" + TargetItemAdapter.targets[mRoundInfo.target] + "</b></font>";
        Bow bow = db.getBow(mRoundInfo.bow, true);
        if (bow != null) {
            infoText += "<br><font color=#ffffff>" + getString(R.string.bow) +
                    "</font>: <font color=#ff9100><b>" + TextUtils.htmlEncode(bow.name) + "</b></font>";
        }
        Arrow arrow = db.getArrow(mRoundInfo.arrow, true);
        if (arrow != null) {
            infoText += "<br><font color=#ffffff>" + getString(R.string.arrow) +
                    "</font>: <font color=#ff9100><b>" + TextUtils.htmlEncode(arrow.name) + "</b></font>";
        }
        if (!mRoundInfo.comment.isEmpty()) {
            infoText += "<br><font color=#ffffff>" + getString(R.string.comment) +
                    "</font>: <font color=#ff9100><b>" + TextUtils.htmlEncode(mRoundInfo.comment) + "</b></font>";
        }
        info.setText(Html.fromHtml(infoText));

        // Set number of X, 10, 9 shoots
        infoText = "<font color=#ffffff>X</font>: <font color=#ff9100><b>" + mRoundInfo.scoreCount[0] + "</b></font><br>" +
                "<font color=#ffffff>" + getString(R.string.ten_x) + "</font>: <font color=#ff9100><b>" + (mRoundInfo.scoreCount[0] + mRoundInfo.scoreCount[1]) + "</b></font><br>" +
                "<font color=#ffffff>" + getString(R.string.nine) + "</font>: <font color=#ff9100><b>" + mRoundInfo.scoreCount[2] + "</b></font>";
        score.setText(Html.fromHtml(infoText));
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.round, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        boolean hasPasses = mAdapter.getItemCount() > 0;
        menu.findItem(R.id.action_scoreboard).setVisible(hasPasses);
        menu.findItem(R.id.action_share).setVisible(hasPasses);
        menu.findItem(R.id.action_statistics).setVisible(hasPasses);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_scoreboard:
                Intent intent = new Intent(activity, ScoreboardActivity.class);
                intent.putExtra(ScoreboardActivity.ROUND_ID, mRound);
                startActivity(intent);
                return true;
            case R.id.action_statistics:
                Intent i = new Intent(activity, StatisticsActivity.class);
                i.putExtra(StatisticsActivity.TRAINING_ID, mTraining);
                i.putExtra(StatisticsActivity.ROUND_ID, mRound);
                startActivity(i);
                return true;
            case R.id.action_share:
                showShareDialog();
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
        mRoundInfo = db.getRound(mRound);
        int max = Target.getMaxPoints(mRoundInfo.target);
        int reached = db.getRoundPoints(mRound);
        int maxP = mRoundInfo.ppp * max * db.getPasses(mRound).size();
        final String text = getString(R.string.my_share_text,
                mRoundInfo.scoreCount[0], mRoundInfo.scoreCount[1], mRoundInfo.scoreCount[2], reached, maxP);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final File f = File.createTempFile("target", ".png", activity.getExternalCacheDir());
                    if (dispersion_pattern && !scoreboard && !comments) {
                        new TargetImage().generateBitmap(activity, 800, mRoundInfo, mRound, f);
                    } else {
                        new ScoreboardImage().generateBitmap(activity, mRound, scoreboard, dispersion_pattern, comments, f);
                    }

                    // Build and fire intent to ask for share provider
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    if (include_text)
                        shareIntent.putExtra(Intent.EXTRA_TEXT, text);
                    if (dispersion_pattern || scoreboard || comments)
                        shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(f));
                    shareIntent.setType("*/*");
                    startActivity(shareIntent);
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(activity, getString(R.string.sharing_failed), Toast.LENGTH_SHORT).show();
                }
            }
        }).start();
    }

    private View.OnClickListener headerClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent i = new Intent(activity, EditRoundActivity.class);
            i.putExtra(EditRoundActivity.TRAINING_ID, mTraining);
            i.putExtra(EditRoundActivity.ROUND_ID, mRound);
            startActivity(i);
        }
    };

    @Override
    protected void onNew(Intent i) {
        i.setClass(activity, InputActivity.class);
        i.putExtra(InputActivity.ROUND_ID, mRound);
    }

    @Override
    public void onSelected(Passe item) {
        Intent i = new Intent(activity, InputActivity.class);
        i.putExtra(InputActivity.ROUND_ID, mRound);
        i.putExtra(InputActivity.PASSE_IND, db.getPasseInd(mRound, item.getId()));
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
        outState.putLong(ROUND_ID, mRound);
    }

    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
        int translationShadow = Math.max(mActionBarSize, mActionBarSize + mHeaderHeight - scrollY);
        ViewHelper.setTranslationY(mShadow, translationShadow);
        ViewHelper.setTranslationY(mHeader, mActionBarSize - scrollY);
        ViewHelper.setAlpha(mDetails, ScrollUtils.getFloat((float) (mHeaderHeight - scrollY * 2) / mHeaderHeight, 0, 1));
    }

    @Override
    public void onDownMotionEvent() {
    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {
    }

    public class PasseAdapter extends NowListAdapter<Passe> {
        public PasseAdapter(int header) {
            headerHeight = header;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.passe_card, parent, false);
            return new ViewHolder(itemView);
        }
    }

    public class ViewHolder extends CardViewHolder<Passe> {
        public PassesView mShots;
        public TextView mSubtitle;

        public ViewHolder(View itemView) {
            super(itemView, mMultiSelector, PasseFragment.this);
            mShots = (PassesView) itemView.findViewById(R.id.shoots);
            mSubtitle = (TextView) itemView.findViewById(R.id.passe);
        }

        @Override
        public void bindCursor() {
            Context context = mSubtitle.getContext();
            mShots.setPoints(mItem.shot, mRoundInfo.target);
            mSubtitle.setText(context.getString(R.string.passe_n, getPosition()));
        }
    }
}
