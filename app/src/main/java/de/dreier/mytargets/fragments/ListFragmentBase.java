package de.dreier.mytargets.fragments;

import android.content.Context;
import android.os.Parcelable;

import de.dreier.mytargets.utils.OnItemClickListener;

public abstract class ListFragmentBase<T> extends FragmentBase implements OnItemClickListener<T> {

    /**
     * Listener which gets called when item gets selected
     */
    protected OnItemSelectedListener listener;

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        if (activity instanceof OnItemSelectedListener) {
            listener = (OnItemSelectedListener) activity;
        }
        if (getParentFragment() instanceof OnItemSelectedListener) {
            listener = (OnItemSelectedListener) getParentFragment();
        }
    }

    /**
     * Used for communicating item selection
     */
    public interface OnItemSelectedListener {
        /**
         * Called when a item has been selected.
         *
         * @param item Item that has been selected
         */
        void onItemSelected(Parcelable item);
    }
}
