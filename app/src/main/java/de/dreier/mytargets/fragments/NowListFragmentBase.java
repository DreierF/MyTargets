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
import android.support.annotation.Nullable;
import android.support.annotation.PluralsRes;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bignerdranch.android.recyclerviewchoicemode.MultiSelector;
import com.bignerdranch.android.recyclerviewchoicemode.OnCardClickListener;
import com.bignerdranch.android.recyclerviewchoicemode.SelectableViewHolder;

import java.util.List;

import de.dreier.mytargets.R;
import de.dreier.mytargets.shared.models.IIdProvider;

public abstract class NowListFragmentBase<T extends IIdProvider> extends Fragment
        implements OnCardClickListener<T> {
    public static final String TRAINING_ID = "training_id";

    void startActivity(Class<?> activityClass) {
        Intent i = new Intent(getActivity(), activityClass);
        startActivity(i);
        getActivity().overridePendingTransition(R.anim.right_in, R.anim.left_out);
    }

    public interface ContentListener {
        void onContentChanged(boolean empty, int stringRes);
    }

    public interface OnItemSelectedListener {
        void onItemSelected(IIdProvider e);
    }

    @PluralsRes
    int itemTypeRes;
    @PluralsRes
    int itemTypeDelRes;
    @StringRes
    int newStringRes;

    // Action mode handling
    final MultiSelector mMultiSelector = new MultiSelector();
    private ActionMode actionMode = null;

    AppCompatActivity activity;
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

    int getLayoutResource() {
        return R.layout.fragment_list;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init(getArguments(), savedInstanceState);
    }

    void updateFabButton(List list) {
        listener.onContentChanged(list.isEmpty(), newStringRes);
    }

    private void updateTitle() {
        if (actionMode == null) {
            return;
        }
        int count = mMultiSelector.getSelectedPositions().size();
        if (count == 0) {
            actionMode.finish();
        } else {
            final String title = getResources().getQuantityString(itemTypeRes, count, count);
            actionMode.setTitle(title);
            actionMode.invalidate();
        }
    }

    @Override
    public void onClick(SelectableViewHolder holder, T mItem) {
        if (mItem == null) {
            return;
        }
        if (!mMultiSelector.tapSelection(holder)) {
            onSelected(mItem);
        } else {
            updateTitle();
        }
    }

    protected abstract T getItem(int id);

    protected abstract void init(Bundle intent, Bundle savedInstanceState);

    protected abstract void onSelected(T item);
}
