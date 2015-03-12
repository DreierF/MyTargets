package de.dreier.mytargets.fragments;

import android.content.Intent;
import android.os.Bundle;

import java.util.List;

import de.dreier.mytargets.R;
import de.dreier.mytargets.activities.EditBowActivity;
import de.dreier.mytargets.adapters.BowAdapter;
import de.dreier.mytargets.models.Bow;

public class BowFragment extends NowListFragment<Bow> {

    BowAdapter adapter;

    @Override
    protected void init(Bundle intent, Bundle savedInstanceState) {
        itemTypeRes = R.plurals.bow;
    }

    @Override
    protected void onEdit(int pos) {}

    @Override
    public void onResume() {
        super.onResume();
        adapter = new BowAdapter(getActivity());
        mRecyclerView.setAdapter(adapter);
    }

    @Override
    protected void onDelete(List<Integer> ids) {
        //TODO db.deleteBows(ids);
    }

    @Override
    protected void onNewClick(Intent i) {
        i.setClass(getActivity(), EditBowActivity.class);
    }

    public void onItemClick(Intent i, int pos) {
        i.setClass(getActivity(), EditBowActivity.class);
        i.putExtra(EditBowActivity.BOW_ID, adapter.getItemId(pos));
    }
}
