/*
 * Copyright (C) 2016 Florian Dreier
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
package de.dreier.mytargets.fragments;

import android.os.Parcelable;
import android.support.annotation.PluralsRes;
import android.support.annotation.StringRes;
import android.support.v7.view.ActionMode;

import de.dreier.mytargets.shared.models.IIdProvider;
import de.dreier.mytargets.utils.OnCardClickListener;

/**
 * Generic fragment class used as base for most fragments.
 *
 * @param <T> Model of the item which is managed within the fragment
 */
public abstract class ListFragmentBase<T extends IIdProvider> extends FragmentBase
        implements OnCardClickListener<T> {

    protected static final String ITEM_ID = "id";

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
