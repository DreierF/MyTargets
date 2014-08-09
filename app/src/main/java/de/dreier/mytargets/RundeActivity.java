package de.dreier.mytargets;

import android.app.ListActivity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

/**
 * Shows all passes of one round
 */
public class RundeActivity extends NowListActivity {

    private long mTraining;
    private long mRound;

    @Override
    protected void init(Intent intent, Bundle savedInstanceState) {
        itemSingular = getString(R.string.passe_singular);
        itemPlural = getString(R.string.passe_plural);
        if(intent!=null && intent.hasExtra(RUNDE_ID)) {
            mRound = intent.getLongExtra(RUNDE_ID,-1);
            mTraining = intent.getLongExtra(TRAINING_ID, -1);
        }

        if(db.getPasses(mRound).getCount()==0) {
            intent = new Intent(this,PasseActivity.class);
            intent.putExtra(PasseActivity.RUNDE_ID,mRound);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
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
            i.putExtra(PasseActivity.RUNDE_ID,mRound);
        } else {
            i.setClass(this,PasseActivity.class);
            i.putExtra(PasseActivity.RUNDE_ID,mRound);
            i.putExtra(PasseActivity.PASSE_IND,pos);
        }
    }
}
