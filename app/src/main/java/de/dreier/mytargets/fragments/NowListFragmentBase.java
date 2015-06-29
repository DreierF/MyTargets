/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */
package de.dreier.mytargets.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.PluralsRes;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.bignerdranch.android.recyclerviewchoicemode.CardViewHolder;
import com.bignerdranch.android.recyclerviewchoicemode.ModalMultiSelectorCallback;
import com.bignerdranch.android.recyclerviewchoicemode.MultiSelector;
import com.bignerdranch.android.recyclerviewchoicemode.OnCardClickListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.dreier.mytargets.R;
import de.dreier.mytargets.managers.DatabaseManager;
import de.dreier.mytargets.shared.models.DatabaseSerializable;
import de.dreier.mytargets.shared.models.IdProvider;

public abstract class NowListFragmentBase<T extends IdProvider> extends Fragment
        implements OnCardClickListener<T> {
    public static final String TRAINING_ID = "training_id";

    protected void startActivity(Class<?> activityClass) {
        Intent i = new Intent(getActivity(), activityClass);
        startActivity(i);
        getActivity().overridePendingTransition(R.anim.right_in, R.anim.left_out);
    }

    public interface ContentListener {
        void onContentChanged(boolean empty, int stringRes);
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
    DatabaseManager db;
    boolean mEditable = false;
    RecyclerView mRecyclerView;
    protected View rootView;
    private ContentListener listener;

    @Override
    public void onAttach(Activity activity) {
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

    @Override
    public void onResume() {
        super.onResume();
        if (activity != null) {
            db = DatabaseManager.getInstance(activity);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        db = DatabaseManager.getInstance(activity);
        init(getArguments(), savedInstanceState);
    }

    protected void updateFabButton(List list) {
        listener.onContentChanged(list.isEmpty(), newStringRes);
    }

    private final ActionMode.Callback mDeleteMode = new ModalMultiSelectorCallback(
            mMultiSelector) {

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            MenuItem edit = menu.findItem(R.id.action_edit);
            edit.setVisible(getSelectedCount() == 1 && mEditable);
            return false;
        }

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            actionMode = mode;
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.context_menu_edit_delete, menu);
            return true;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_edit:
                    int id = mMultiSelector.getSelectedPositions().get(0);
                    onEdit(getItem(id));
                    mode.finish();
                    return true;
                case R.id.action_delete:
                    List<Integer> positions = mMultiSelector.getSelectedPositions();
                    remove(positions);
                    mode.finish();
                    return true;
                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            super.onDestroyActionMode(mode);
            actionMode = null;
        }
    };

    void remove(List<Integer> positions) {
        Collections.sort(positions);
        Collections.reverse(positions);
        final ArrayList<T> deleted = new ArrayList<>();
        for (int pos : positions) {
            T item = getItem(pos);
            deleted.add(item);
            removeItem(pos);
        }
        String message = getActivity().getResources()
                .getQuantityString(itemTypeDelRes, deleted.size(), deleted.size());
        Snackbar.make(rootView, message,
                Snackbar.LENGTH_LONG)
                .setAction(R.string.undo, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        for (T item : deleted) {
                            db.update((DatabaseSerializable) item);
                        }
                    }
                }).show();
        for (T item : deleted) {
            db.delete((DatabaseSerializable) item);
        } //TODO make this more intelligent and support animations
    }

    protected abstract void removeItem(int pos);

    void updateTitle() {
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
    public void onClick(CardViewHolder holder, T mItem) {
        if (mItem == null) {
            return;
        }
        if (!mMultiSelector.tapSelection(holder)) {
            onSelected(mItem);
        } else {
            updateTitle();
        }
    }

    @Override
    public void onLongClick(CardViewHolder holder) {
        if (actionMode == null) {
            AppCompatActivity activity = (AppCompatActivity) getActivity();
            activity.startSupportActionMode(mDeleteMode);
            mMultiSelector.setSelectable(true);
        }
        mMultiSelector.setSelected(holder, true);
        updateTitle();
    }

    protected abstract T getItem(int id);

    protected abstract void init(Bundle intent, Bundle savedInstanceState);

    protected abstract void onEdit(T item);

    protected abstract void onSelected(T item);
}
