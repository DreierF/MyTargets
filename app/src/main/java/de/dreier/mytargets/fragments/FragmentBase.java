/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */
package de.dreier.mytargets.fragments;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.PluralsRes;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v7.view.ActionMode;

import de.dreier.mytargets.shared.models.IIdProvider;
import de.dreier.mytargets.utils.OnCardClickListener;
import icepick.Icepick;

/**
 * Generic fragment class used as base for most fragments.
 *
 * @param <T> Model of the item which is managed within the fragment
 */
public abstract class FragmentBase<T extends IIdProvider> extends Fragment
        implements OnCardClickListener<T> {

    public static final String ITEM_ID = "id";

    /**
     * Resource used to set title when items are selected
     */
    @PluralsRes
    int itemTypeSelRes;

    /**
     * Resource describing FAB action
     */
    @StringRes
    int newStringRes;

    /**
     * Action mode manager
     */
    ActionMode actionMode = null;


    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Icepick.restoreInstanceState(this, savedInstanceState);
    }

    @Override public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Icepick.saveInstanceState(this, outState);
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
