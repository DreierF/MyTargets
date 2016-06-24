/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */
package de.dreier.mytargets.fragments;

import android.content.Context;
import android.os.Parcelable;
import android.support.annotation.CallSuper;
import android.support.annotation.PluralsRes;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v7.view.ActionMode;

import java.util.List;

import de.dreier.mytargets.shared.models.IIdProvider;
import de.dreier.mytargets.utils.OnCardClickListener;

/**
 * Generic fragment class used as base for most fragments.
 * <p>
 * The parent activity must implement {@link de.dreier.mytargets.fragments.FragmentBase.ContentListener}.
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

    /**
     * Holds the ContentListener called when the fragment's content changes
     */
    private ContentListener listener;

    /**
     * {@inheritDoc}
     */
    @Override
    @CallSuper
    public void onAttach(Context activity) {
        super.onAttach(activity);
        if (activity instanceof ContentListener) {
            listener = (ContentListener) activity;
        }
        Fragment fragment = getParentFragment();
        if (listener == null && fragment instanceof ContentListener) {
            listener = (ContentListener) fragment;
        }
    }

    /**
     * Tells the parent to update its FAB button
     *
     * @param list The list of items that is currently displayed in the fragment
     */
    void updateFabButton(List list) {
        listener.onContentChanged(list.isEmpty(), newStringRes);
    }

    /**
     * Used for communication with FragmentBase's parent activity
     */
    public interface ContentListener {

        /**
         * Called whenever the fragment's content changes.
         * This callback should be used to show a label for the
         * main floating action button if the content is empty.
         *
         * @param empty     Indicates if the fragment is currently empty
         * @param stringRes String resource id which describes the FAB action
         */
        void onContentChanged(boolean empty, int stringRes);
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
