package de.dreier.mytargets.activities;

import android.content.Intent;
import android.os.Bundle;

import java.text.DateFormat;

import de.dreier.mytargets.R;
import de.dreier.mytargets.activities.NewRoundActivity;
import de.dreier.mytargets.activities.NowListActivity;
import de.dreier.mytargets.activities.RoundActivity;
import de.dreier.mytargets.adapters.RoundsAdapter;
import de.dreier.mytargets.utils.TargetOpenHelper;

/**
 * Shows all rounds of one training day
 */
public class TrainingActivity extends NowListActivity {

    private long mTraining;

    @Override
    protected void init(Intent intent, Bundle savedInstanceState) {
        itemSingular = getString(R.string.round_singular);
        itemPlural = getString(R.string.round_plural);
        mEditable = true;

        if(intent!=null && intent.hasExtra(TRAINING_ID)) {
            mTraining = intent.getLongExtra(TRAINING_ID,-1);
        }
        if(savedInstanceState!=null) {
            mTraining = savedInstanceState.getLong(TRAINING_ID,-1);
        }
        if(mTraining==-1)
            finish();

        TargetOpenHelper.Training tr = db.getTraining(mTraining);
        getSupportActionBar().setTitle(tr.title);
        getSupportActionBar().setSubtitle(DateFormat.getDateInstance().format(tr.date));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onEdit(long id) {
        Intent i = new Intent(this, NewRoundActivity.class);
        i.putExtra(NewRoundActivity.TRAINING_ID, mTraining);
        i.putExtra(NewRoundActivity.ROUND_ID, id);
        startActivity(i);
    }

    @Override
    protected void onDelete(long[] ids) {
        db.deleteRounds(ids);
    }

    @Override
    public boolean onItemClick(Intent i, int pos, long id) {
        if(pos==0) {
            i.setClass(this, NewRoundActivity.class);
            i.putExtra(NewRoundActivity.TRAINING_ID, mTraining);
        } else {
            i.setClass(this, RoundActivity.class);
            i.putExtra(RoundActivity.ROUND_ID, getListAdapter().getItemId(pos));
            i.putExtra(RoundActivity.TRAINING_ID, mTraining);
        }
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter = new RoundsAdapter(this,mTraining);
        setListAdapter(adapter);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(TRAINING_ID,mTraining);
    }
}
