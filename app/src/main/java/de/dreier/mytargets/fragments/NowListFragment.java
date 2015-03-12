package de.dreier.mytargets.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.PluralsRes;
import android.support.v4.app.Fragment;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.bignerdranch.android.recyclerviewchoicemode.ModalMultiSelectorCallback;
import com.bignerdranch.android.recyclerviewchoicemode.MultiSelector;
import com.melnykov.fab.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import de.dreier.mytargets.R;
import de.dreier.mytargets.managers.DatabaseManager;

/**
 * Shows all rounds of one settings_only day
 */
public abstract class NowListFragment<T> extends Fragment implements View.OnClickListener {

    @PluralsRes
    protected int itemTypeRes;

    DatabaseManager db;
    boolean mEditable = false;
    protected RecyclerView mRecyclerView;
    protected ArrayList<T> mList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_list, container, false);

        mRecyclerView = (RecyclerView) rootView.findViewById(android.R.id.list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setHasFixedSize(true);
        //mMultiSelector.setSelectable(false);

        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.setOnClickListener(this);
        fab.attachToRecyclerView(mRecyclerView);
        return rootView;
    }

    protected MultiSelector mMultiSelector = new MultiSelector();
    protected ActionMode actionMode = null;
    protected ActionMode.Callback mDeleteMode = new ModalMultiSelectorCallback(mMultiSelector) {
        /*@Override
        public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
            if (!mAdapter.isSelectable(position)) {
                if (checked)
                    mListView.setItemChecked(position, false);
                return;
            }
            count += checked ? 1 : -1;

            final String title = getResources().getQuantityString(itemTypeRes, count, count);
            mode.setTitle(title);
            mode.invalidate();
        }*/

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_edit:
                    mode.finish();
                    int id = mMultiSelector.getSelectedPositions().get(0);
                    onEdit(id);
                    //onResume();
                    return true;
                case R.id.action_delete:
                    mode.finish();

                    /*for (int i = mCrimes.size()-1; i >= 0; i--) {
                        if (mMultiSelector.isSelected(i, 0)) {
                            Crime crime = mCrimes.get(i);
                            CrimeLab.get(getActivity()).deleteCrime(crime);
                            mRecyclerView.getAdapter().notifyItemRemoved(i);
                        }
                    }*/

                    List<Integer> positions = mMultiSelector.getSelectedPositions();
                    onDelete(positions);
                    //onResume();
                    return true;
                default:
                    return false;
            }
        }

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            actionMode = mode;
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.context_menu_edit_delete, menu);
            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            super.onDestroyActionMode(mode);
            actionMode=null;
            mRecyclerView.getAdapter().notifyDataSetChanged();
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            MenuItem edit = menu.findItem(R.id.action_edit);
            edit.setVisible(getSelectedCount() == 1 && mEditable);
            return false;
        }
    };

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        db = DatabaseManager.getInstance(getActivity());
        init(getArguments(), savedInstanceState);
    }

    /* On FAB button clicked */
    @Override
    public void onClick(View v) {
        Intent i = new Intent();
        onNewClick(i);
        startActivity(i);
        getActivity().overridePendingTransition(R.anim.right_in, R.anim.left_out);
    }

    protected abstract void init(Bundle intent, Bundle savedInstanceState);

    protected abstract void onEdit(int pos);

    protected abstract void onDelete(List<Integer> positions);

    protected abstract void onNewClick(Intent i);
}
