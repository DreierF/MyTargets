package de.dreier.mytargets;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;

import java.text.DateFormat;

/**
 * Shows all rounds of one training day
 */
public class TrainingActivity extends NowListActivity {

    private long mTraining;

    @Override
    protected void init(Intent intent, Bundle savedInstanceState) {
        itemSingular = getString(R.string.round_singular);
        itemPlural = getString(R.string.round_plural);

        if(intent!=null && intent.hasExtra(TRAINING_ID)) {
            mTraining = intent.getLongExtra(TRAINING_ID,-1);
        }
        if(savedInstanceState!=null) {
            mTraining = savedInstanceState.getLong(TRAINING_ID,-1);
        }
        if(mTraining==-1)
            finish();

        TargetOpenHelper.Training tr = db.getTraining(mTraining);
        getActionBar().setTitle(tr.title);
        getActionBar().setSubtitle(DateFormat.getDateInstance().format(tr.date));
    }

    @Override
    protected void onDelete(long[] ids) {
        db.deleteRounds(ids);
    }

    @Override
    public void onItemClick(Intent i, int pos, long id) {
        if(pos==0) {
            i.setClass(this, NewRoundActivity.class);
            i.putExtra(NewRoundActivity.TRAINING_ID, mTraining);
        } else {
            i.setClass(this, RundeActivity.class);
            i.putExtra(RundeActivity.RUNDE_ID, getListAdapter().getItemId(pos));
            i.putExtra(RundeActivity.TRAINING_ID, mTraining);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter = new RundenAdapter(this,mTraining);
        setListAdapter(adapter);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(TRAINING_ID,mTraining);
    }
}
