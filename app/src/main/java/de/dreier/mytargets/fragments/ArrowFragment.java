package de.dreier.mytargets.fragments;

import android.content.Intent;
import android.os.Bundle;

import java.util.List;

import de.dreier.mytargets.R;
import de.dreier.mytargets.activities.EditArrowActivity;
import de.dreier.mytargets.adapters.ArrowAdapter;
import de.dreier.mytargets.models.Arrow;

public class ArrowFragment extends NowListFragment<Arrow> {

    ArrowAdapter adapter;

    @Override
    protected void init(Bundle intent, Bundle savedInstanceState) {
        itemTypeRes = R.plurals.arrow;
    }

    @Override
    protected void onEdit(int pos) {
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter = new ArrowAdapter(getActivity());
        mRecyclerView.setAdapter(adapter);
    }

    @Override
    protected void onDelete(List<Integer> ids) {
        //TODO db.deleteArrows(ids);
    }

    @Override
    protected void onNewClick(Intent i) {
        i.setClass(getActivity(), EditArrowActivity.class);
    }

    public void onItemClick(Intent i, int pos) {
        i.setClass(getActivity(), EditArrowActivity.class);
        i.putExtra(EditArrowActivity.ARROW_ID, adapter.getItemId(pos));
    }
}
