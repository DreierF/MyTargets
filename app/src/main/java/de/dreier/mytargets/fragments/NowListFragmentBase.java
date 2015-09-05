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
import android.support.annotation.PluralsRes;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bignerdranch.android.recyclerviewchoicemode.OnCardClickListener;

import java.util.List;

import de.dreier.mytargets.R;
import de.dreier.mytargets.shared.models.IIdProvider;

public abstract class NowListFragmentBase<T extends IIdProvider> extends Fragment
        implements OnCardClickListener<T> {
    public static final String TRAINING_ID = "training_id";

    public interface ContentListener {
        void onContentChanged(boolean empty, int stringRes);
    }

    public interface OnItemSelectedListener {
        void onItemSelected(IIdProvider e);
    }

    @PluralsRes
    int itemTypeRes;

    @StringRes
    int newStringRes;

    protected ActionMode actionMode = null;

    RecyclerView mRecyclerView;
    View rootView;
    private ContentListener listener;

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        if (activity instanceof ContentListener) {
            listener = (ContentListener) activity;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(getLayoutResource(), container, false);
        mRecyclerView = (RecyclerView) rootView.findViewById(android.R.id.list);
        mRecyclerView.setHasFixedSize(true);
        return rootView;
    }

    protected int getLayoutResource() {
        return R.layout.fragment_list;
    }

    protected void updateFabButton(List list) {
        listener.onContentChanged(list.isEmpty(), newStringRes);
    }

    protected void startActivity(Class<?> activityClass) {
        Intent i = new Intent(getActivity(), activityClass);
        startActivity(i);
        getActivity().overridePendingTransition(R.anim.right_in, R.anim.left_out);
    }

    protected abstract T getItem(int id);

    protected abstract void onSelected(T item);
}
