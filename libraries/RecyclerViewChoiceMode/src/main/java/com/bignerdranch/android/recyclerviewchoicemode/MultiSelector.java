package com.bignerdranch.android.recyclerviewchoicemode;

import android.util.SparseBooleanArray;

import java.util.ArrayList;
import java.util.List;

public class MultiSelector {
    private SparseBooleanArray mSelections = new SparseBooleanArray();
    private WeakHolderTracker mTracker = new WeakHolderTracker();

    private boolean mIsSelectable;

    public void setSelectable(boolean isSelectable) {
        mIsSelectable = isSelectable;
        refreshAllHolders();
    }

    public boolean isSelectable() {
        return mIsSelectable;
    }

    private void refreshAllHolders() {
        for (SelectableHolder holder : mTracker.getTrackedHolders()) {
            refreshHolder(holder);
        }
    }

    private void refreshHolder(SelectableHolder holder) {
        if (holder == null) {
            return;
        }
        holder.setSelectable(mIsSelectable);

        boolean isActivated = mSelections.get(holder.getPosition());
        holder.setActivated(isActivated);
    }

    public boolean isSelected(int position, long id) {
        return mSelections.get(position);
    }

    public void setSelected(int position, long id, boolean isSelected) {
        mSelections.put(position, isSelected);
        refreshHolder(mTracker.getHolder(position));
    }

    public void clearSelections() {
        mSelections.clear();
        refreshAllHolders();
    }

    public List<Integer> getSelectedPositions() {
        List<Integer> positions = new ArrayList<Integer>();

        for (int i = 0; i < mSelections.size(); i++) {
            if (mSelections.valueAt(i)) {
                positions.add(mSelections.keyAt(i));
            }
        }

        return positions;
    }

    public void bindHolder(SelectableHolder holder, int position, long id) {
        mTracker.bindHolder(holder, position);
        refreshHolder(holder);
    }

    public void setSelected(SelectableHolder holder, boolean isSelected) {
        setSelected(holder.getPosition(), holder.getItemId(), isSelected);
    }

    public boolean tapSelection(SelectableHolder holder) {
        return tapSelection(holder.getPosition(), holder.getItemId());
    }

    private boolean tapSelection(int position, long itemId) {
        if (mIsSelectable) {
            boolean isSelected = isSelected(position, itemId);
            setSelected(position, itemId, !isSelected);
            return true;
        } else {
            return false;
        }

    }
}
