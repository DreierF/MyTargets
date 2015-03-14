package de.dreier.mytargets.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bignerdranch.android.recyclerviewchoicemode.CardViewHolder;

import java.text.DateFormat;
import java.util.ArrayList;

import de.dreier.mytargets.R;
import de.dreier.mytargets.adapters.NowListAdapter;
import de.dreier.mytargets.models.Round;
import de.dreier.mytargets.models.Training;

/**
 * Shows all rounds of one training day
 */
public class TrainingActivity extends NowListActivity<Round> {

    private long mTraining;

    @Override
    protected void init(Bundle intent, Bundle savedInstanceState) {
        itemTypeRes = R.plurals.round;
        mEditable = true;

        if (intent != null) {
            mTraining = intent.getLong(TRAINING_ID, -1);
        }
        if (savedInstanceState != null) {
            mTraining = savedInstanceState.getLong(TRAINING_ID, -1);
        }
        if (mTraining == -1)
            finish();

        Training tr = db.getTraining(mTraining);
        getSupportActionBar().setTitle(tr.title);
        getSupportActionBar().setSubtitle(DateFormat.getDateInstance().format(tr.date));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        ArrayList<Round> list = db.getRounds(mTraining);
        if (mRecyclerView.getAdapter() == null) {
            mAdapter = new RoundAdapter();
            mAdapter.setList(list);
            mRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.setList(list);
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onNew(Intent i) {
        i.setClass(this, EditRoundActivity.class);
        i.putExtra(EditRoundActivity.TRAINING_ID, mTraining);
    }

    @Override
    public void onSelected(Round item) {
        Intent i = new Intent(this, RoundActivity.class);
        i.putExtra(RoundActivity.TRAINING_ID, mTraining);
        i.putExtra(RoundActivity.ROUND_ID, item.id);
        startActivity(i);
        overridePendingTransition(R.anim.right_in, R.anim.left_out);
    }

    @Override
    protected void onEdit(Round item) {
        Intent i = new Intent(this, EditRoundActivity.class);
        i.putExtra(EditRoundActivity.TRAINING_ID, mTraining);
        i.putExtra(EditRoundActivity.ROUND_ID, item.id);
        startActivity(i);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(TRAINING_ID, mTraining);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.training, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        boolean hasPasses = mAdapter.getItemCount() > 0;
        menu.findItem(R.id.action_statistics).setVisible(hasPasses);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_statistics:
                Intent i = new Intent(this, StatisticsActivity.class);
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
            super(itemView, mMultiSelector, TrainingActivity.this);
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
