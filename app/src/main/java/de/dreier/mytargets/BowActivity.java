package de.dreier.mytargets;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import de.dreier.mytargets.R;

public class BowActivity extends NowListActivity {

    @Override
    protected void init(Intent intent, Bundle savedInstanceState) {
        itemSingular = getString(R.string.bow_singular);
        itemPlural = getString(R.string.bow_plural);
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter = new BowAdapter(this);
        setListAdapter(adapter);
    }

    @Override
    protected void onDelete(long[] ids) {
        db.deleteBows(ids);
    }

    @Override
    public void onItemClick(Intent i, int pos, long id) {
        if(pos==0) {
            i.setClass(this,EditBowActivity.class);
        } else {
            i.setClass(this,EditBowActivity.class);
            i.putExtra(EditBowActivity.BOGEN_ID,id);
        }
    }
}
