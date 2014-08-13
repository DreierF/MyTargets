package de.dreier.mytargets;

import android.app.ListActivity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

/**
 * Shows an overview over all tring days
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
