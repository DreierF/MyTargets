/*
 * Copyright (C) 2017 Florian Dreier
 *
 * This file is part of MyTargets.
 *
 * MyTargets is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2
 * as published by the Free Software Foundation.
 *
 * MyTargets is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */
package de.dreier.mytargets.base.fragments;

import android.content.Context;
import android.os.Parcelable;
import android.support.annotation.Nullable;

import de.dreier.mytargets.base.adapters.ListAdapterBase;
import de.dreier.mytargets.utils.multiselector.OnItemClickListener;

public abstract class ListFragmentBase<T, U extends ListAdapterBase<?, T>> extends FragmentBase implements OnItemClickListener<T> {

    /**
     * Listener which gets called when item gets selected
     */
    protected OnItemSelectedListener listener;

    /**
     * Adapter for the fragment's RecyclerView
     */
    @Nullable
    protected U adapter;

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        if (activity instanceof OnItemSelectedListener) {
            listener = (OnItemSelectedListener) activity;
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
