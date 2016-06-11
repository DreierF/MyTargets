/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */
package de.dreier.mytargets.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.CallSuper;
import android.support.annotation.PluralsRes;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.dreier.mytargets.R;
import de.dreier.mytargets.shared.models.IIdProvider;
import de.dreier.mytargets.utils.OnCardClickListener;

/**
 * Generic fragment class used as base for most fragments.
 * Takes care of inflating the main layout file.
 * Layout may be changed by overriding {@link #getLayoutResource()}.
 * Each layout must contain a {@link RecyclerView} element with android.R.id.list as id.
 * <p>
 * The parent activity must implement {@link de.dreier.mytargets.fragments.FragmentBase.ContentListener}.
 *
 * @param <T> Model of the item which is managed within the fragment
 */
public abstract class FragmentBase<T extends IIdProvider> extends Fragment
        implements OnCardClickListener<T> {

    static final String ITEM_ID = "id";

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
     * Main {@link RecyclerView} of the fragment
     */
    @Bind(android.R.id.list)
    RecyclerView recyclerView;

    /**
     * Root view of the fragment
     */
    View rootView;

    /**
     * Holds the ContentListener called when the fragment's content changes
     */
    private ContentListener listener;

    /**
     * {@inheritDoc}
     */
    @Override
    @CallSuper
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(getLayoutResource(), container, false);
        ButterKnife.bind(this, rootView);
        recyclerView.setHasFixedSize(true);
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    /**
     * Gets the fragments main layout, which is inflated in {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}
     *
     * @return Layout resource id
     */
    int getLayoutResource() {
        return R.layout.fragment_list;
    }

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
     * Starts the given activity with the standard animation
     *
     * @param activity Activity to start
     */
    void startActivityAnimated(Class<?> activity) {
        Intent i = new Intent(getContext(), activity);
        startActivity(i);
        getActivity().overridePendingTransition(R.anim.right_in, R.anim.left_out);
    }

    void startActivityAnimated(Class<?> activity, String key, long value) {
        Intent i = new Intent(getContext(), activity);
        i.putExtra(key, value);
        startActivity(i);
        getActivity().overridePendingTransition(R.anim.right_in, R.anim.left_out);
    }

    static void setToolbarTransitionName(Toolbar toolbar) {
        TextView textViewTitle = null;
        for (int i = 0; i < toolbar.getChildCount(); i++) {
            View view = toolbar.getChildAt(i);
            if (view instanceof TextView) {
                textViewTitle = (TextView) view;
                break;
            }
        }
        ViewCompat.setTransitionName(textViewTitle, "title");
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
