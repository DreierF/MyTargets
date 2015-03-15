package de.dreier.mytargets.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bignerdranch.android.recyclerviewchoicemode.CardViewHolder;

import java.text.DateFormat;
import java.util.ArrayList;

import de.dreier.mytargets.R;
import de.dreier.mytargets.activities.EditRoundActivity;
import de.dreier.mytargets.activities.PasseActivity;
import de.dreier.mytargets.activities.StatisticsActivity;
import de.dreier.mytargets.adapters.NowListAdapter;
import de.dreier.mytargets.models.Round;
import de.dreier.mytargets.models.Training;

/**
 * Shows all rounds of one training day
 */
public class RoundFragment extends NowListFragment<Round> {

    private long mTraining;

    @Override
    protected void init(Bundle intent, Bundle savedInstanceState) {
        itemTypeRes = R.plurals.round;
        newStringRes = R.string.new_round;

        if (intent != null) {
            mTraining = intent.getLong(TRAINING_ID, -1);
        }
        if (savedInstanceState != null) {
            mTraining = savedInstanceState.getLong(TRAINING_ID, -1);
        }
        if (mTraining == -1)
            throw new IllegalStateException();

        Training tr = db.getTraining(mTraining);
        ActionBar actionBar = activity.getSupportActionBar();
        actionBar.setTitle(tr.title);
        actionBar.setSubtitle(DateFormat.getDateInstance().format(tr.date));
        actionBar.setDisplayHomeAsUpEnabled(true);
        setHasOptionsMenu(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        ArrayList<Round> list = db.getRounds(mTraining);
        setList(list, new RoundAdapter());
    }

    @Override
    protected void onNew(Intent i) {
        i.setClass(getActivity(), EditRoundActivity.class);
        i.putExtra(EditRoundActivity.TRAINING_ID, mTraining);
    }

    @Override
    public void onSelected(Round item) {
        Intent i = new Intent(getActivity(), PasseActivity.class);
        i.putExtra(PasseFragment.TRAINING_ID, mTraining);
        i.putExtra(PasseFragment.ROUND_ID, item.id);
        startActivity(i);
        getActivity().overridePendingTransition(R.anim.right_in, R.anim.left_out);
    }

    @Override
    protected void onEdit(Round item) {
        Intent i = new Intent(getActivity(), EditRoundActivity.class);
        i.putExtra(EditRoundActivity.TRAINING_ID, mTraining);
        i.putExtra(EditRoundActivity.ROUND_ID, item.id);
        startActivity(i);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(TRAINING_ID, mTraining);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.training, menu);
        super.onCreateOptionsMenu(menu,inflater);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        boolean hasPasses = mAdapter.getItemCount() > 0;
        menu.findItem(R.id.action_statistics).setVisible(hasPasses);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_statistics:
                Intent i = new Intent(getActivity(), StatisticsActivity.class);
                i.putExtra(StatisticsActivity.TRAINING_ID, mTraining);
                startActivity(i);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public class RoundAdapter extends NowListAdapter<Round> {
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.round_card, parent, false);
            return new ViewHolder(itemView);
        }
    }

    public class ViewHolder extends CardViewHolder<Round> {
        public TextView mTitle;
        public TextView mSubtitle;
        public TextView mGes;

        public ViewHolder(View itemView) {
            super(itemView, mMultiSelector, RoundFragment.this);
            mTitle = (TextView) itemView.findViewById(R.id.round);
            mSubtitle = (TextView) itemView.findViewById(R.id.dist);
            mGes = (TextView) itemView.findViewById(R.id.gesRound);
        }

        @Override
        public void bindCursor() {
            Context context = mTitle.getContext();
            mTitle.setText(context.getString(R.string.round) + " " + (1 + getPosition()));
            mSubtitle.setText(mItem.distance + " - " + context.getString(mItem.indoor ? R.string.indoor : R.string.outdoor));
            mGes.setText(mItem.reachedPoints + "/" + mItem.maxPoints);
        }
    }
}
