package de.dreier.mytargets.activities;

import android.content.Intent;
import android.os.Bundle;

import de.dreier.mytargets.adapters.BowAdapter;
import de.dreier.mytargets.R;

public class BowActivity extends NowListActivity {

    @Override
    protected void init(Intent intent, Bundle savedInstanceState) {
        itemSingular = getString(R.string.bow_singular);
        itemPlural = getString(R.string.bow_plural);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
    public boolean onItemClick(Intent i, int pos, long id) {
        if(pos==0) {
            i.setClass(this,EditBowActivity.class);
        } else {
            i.setClass(this,EditBowActivity.class);
            i.putExtra(EditBowActivity.BOW_ID,id);
        }
        return true;
    }
}
