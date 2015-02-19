package de.dreier.mytargets.fragments;

import android.content.Intent;
import android.os.Bundle;

import de.dreier.mytargets.R;
import de.dreier.mytargets.activities.EditArrowActivity;
import de.dreier.mytargets.adapters.ArrowAdapter;

public class ArrowFragment extends NowListFragment {

    @Override
    protected void init(Bundle intent, Bundle savedInstanceState) {
        itemSingular = getString(R.string.arrow_singular);
        itemPlural = getString(R.string.arrow_plural);
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter = new ArrowAdapter(getActivity());
        setListAdapter(adapter);
    }

    @Override
    protected void onDelete(long[] ids) {
        db.deleteBows(ids);
    }

    @Override
    public boolean onItemClick(Intent i, int pos, long id) {
        if (pos == 0) {
            i.setClass(getActivity(), EditArrowActivity.class);
        } else {
            i.setClass(getActivity(), EditArrowActivity.class);
            i.putExtra(EditArrowActivity.ARROW_ID, id);
        }
        return true;
    }
}
