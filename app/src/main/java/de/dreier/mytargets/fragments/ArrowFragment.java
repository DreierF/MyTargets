package de.dreier.mytargets.fragments;

import android.content.Intent;
import android.os.Bundle;

import de.dreier.mytargets.R;
import de.dreier.mytargets.activities.EditArrowActivity;
import de.dreier.mytargets.adapters.ArrowAdapter;

public class ArrowFragment extends NowListFragment {

    @Override
    protected void init(Bundle intent, Bundle savedInstanceState) {
        itemTypeRes = R.plurals.arrow;
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter = new ArrowAdapter(getActivity());
        setListAdapter(adapter);
    }

    @Override
    protected void onDelete(long[] ids) {
        db.deleteArrows(ids);
    }

    @Override
    public void onNewClick(Intent i) {
        i.setClass(getActivity(), EditArrowActivity.class);
    }

    @Override
    public void onItemClick(Intent i, int pos, long id) {
        i.setClass(getActivity(), EditArrowActivity.class);
        i.putExtra(EditArrowActivity.ARROW_ID, id);
    }
}
