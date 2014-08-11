package de.dreier.mytargets;

import android.content.Intent;
import android.os.Bundle;

/**
 * Shows all passes of one round
 */
public class RoundActivity extends NowListActivity {

    private long mTraining;
    private long mRound;

    @Override
    protected void init(Intent intent, Bundle savedInstanceState) {
        itemSingular = getString(R.string.passe_singular);
        itemPlural = getString(R.string.passe_plural);
        if(intent!=null && intent.hasExtra(ROUND_ID)) {
            mTraining = intent.getLongExtra(TRAINING_ID, -1);
            mRound = intent.getLongExtra(ROUND_ID,-1);
        }
        if(savedInstanceState!=null) {
            mTraining = savedInstanceState.getLong(TRAINING_ID,-1);
            mRound = savedInstanceState.getLong(ROUND_ID,-1);
        }

        setTitle(getString(R.string.round)+" "+db.getRoundInd(mTraining,mRound));
    }

    @Override
    protected void onDelete(long[] ids) {
        db.deletePasses(ids);
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter = new PasseAdapter(this,mTraining,mRound);
        setListAdapter(adapter);
    }

    @Override
    public void onItemClick(Intent i, int pos, long id) {
        if(pos==0) {
            i.setClass(this,PasseActivity.class);
            i.putExtra(PasseActivity.ROUND_ID,mRound);
        } else {
            i.setClass(this,PasseActivity.class);
            i.putExtra(PasseActivity.ROUND_ID,mRound);
            i.putExtra(PasseActivity.PASSE_IND,pos);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(TRAINING_ID,mTraining);
        outState.putLong(ROUND_ID,mRound);
    }
}
