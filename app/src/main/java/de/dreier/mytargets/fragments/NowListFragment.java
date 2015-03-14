package de.dreier.mytargets.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.PluralsRes;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.LinearLayoutManager;
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
import com.melnykov.fab.FloatingActionButton;

import java.util.Collections;
import java.util.List;

import de.dreier.mytargets.R;
import de.dreier.mytargets.adapters.NowListAdapter;
import de.dreier.mytargets.managers.DatabaseManager;
import de.dreier.mytargets.models.IdProvider;
import de.dreier.mytargets.views.CardItemDecorator;

/**
 * Shows all rounds of one settings_only day
 */
public abstract class NowListFragment<T extends IdProvider> extends Fragment implements View.OnClickListener, OnCardClickListener<T> {

    @PluralsRes
    protected int itemTypeRes;

    DatabaseManager db;
    boolean mEditable = false;
    protected RecyclerView mRecyclerView;
    protected NowListAdapter<T> mAdapter;

    // Action mode handling
    protected MultiSelector mMultiSelector = new MultiSelector();
    protected ActionMode actionMode = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_list, container, false);

        mRecyclerView = (RecyclerView) rootView.findViewById(android.R.id.list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.addItemDecoration(new CardItemDecorator(getActivity()));
        mRecyclerView.setHasFixedSize(true);

        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.setOnClickListener(this);
        fab.attachToRecyclerView(mRecyclerView);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        db = DatabaseManager.getInstance(getActivity());
        init(getArguments(), savedInstanceState);
    }

    protected ActionMode.Callback mDeleteMode = new ModalMultiSelectorCallback(mMultiSelector) {

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
                    onEdit(mAdapter.getItem(id));
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

    public void remove(List<Integer> positions) {
        Collections.sort(positions);
        Collections.reverse(positions);
        for (int pos : positions) {
            db.deleteTraining(mAdapter.getItemId(pos)); //TODO generalize
            mAdapter.remove(pos);
        }
    }

    protected void updateTitle() {
        if (actionMode == null)
            return;
        int count = mMultiSelector.getSelectedPositions().size();
        if (count == 0) {
            actionMode.finish();
        } else {
            final String title = getResources().getQuantityString(itemTypeRes, count, count);
            actionMode.setTitle(title);
            actionMode.invalidate();
        }
    }

    /* On FAB button clicked */
    @Override
    public void onClick(View v) {
        Intent i = new Intent();
        onNew(i);
        startActivity(i);
        getActivity().overridePendingTransition(R.anim.right_in, R.anim.left_out);
    }

    @Override
    public void onClick(CardViewHolder holder, T mItem) {
        if (mItem == null)
            return;
        if (!mMultiSelector.tapSelection(holder)) {
            onSelected(mItem);
        } else {
            updateTitle();
        }
    }

    @Override
    public void onLongClick(CardViewHolder holder) {
        if (actionMode == null) {
            ActionBarActivity activity = (ActionBarActivity) getActivity();
            activity.startSupportActionMode(mDeleteMode);
            mMultiSelector.setSelectable(true);
        }
        mMultiSelector.setSelected(holder, true);
        updateTitle();
    }

    protected abstract void init(Bundle intent, Bundle savedInstanceState);

    protected abstract void onNew(Intent i);

    protected abstract void onSelected(T item);

    protected abstract void onEdit(T item);
}
