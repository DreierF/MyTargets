package de.dreier.mytargets;

import android.content.Intent;
import android.os.Bundle;

/**
 * Shows an overview over all trying days
 * */
public class MainActivity extends NowListActivity {

    @Override
    protected void init(Intent intent, Bundle savedInstanceState) {
        itemSingular = getString(R.string.training_singular);
        itemPlural = getString(R.string.training_plural);
        mEnableBackAnimation = false;
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter = new TrainingAdapter(this);
        setListAdapter(adapter);
    }

    @Override
    protected void onDelete(long[] ids) {
        db.deleteTrainings(ids);
    }

    @Override
    public boolean onItemClick(Intent i, int pos, long id) {
        if(pos==0) {
            i.setClass(this,NewRoundActivity.class);
        } else if(pos==1) {
            i.setClass(this,BowActivity.class);
        } else {
            i.setClass(this,TrainingActivity.class);
            i.putExtra(TrainingActivity.TRAINING_ID,getListAdapter().getItemId(pos));
        }
        return true;
    }
}
