package de.dreier.mytargets.fragments;

import android.content.Intent;
import android.os.Bundle;

import de.dreier.mytargets.R;
import de.dreier.mytargets.activities.EditBowActivity;
import de.dreier.mytargets.adapters.BowAdapter;

public class BowFragment extends NowListFragment {

    @Override
    protected void init(Bundle intent, Bundle savedInstanceState) {
        itemTypeRes = R.plurals.bow;
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter = new BowAdapter(getActivity());
        setListAdapter(adapter);
    }

    @Override
    protected void onDelete(long[] ids) {
        db.deleteBows(ids);
    }

    @Override
    public void onNewClick(Intent i) {
        i.setClass(getActivity(), EditBowActivity.class);
    }

    @Override
    public void onItemClick(Intent i, int pos, long id) {
        i.setClass(getActivity(), EditBowActivity.class);
        i.putExtra(EditBowActivity.BOW_ID, id);
    }
}
