//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package de.dreier.mytargets.utils.multiselector;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.dreier.mytargets.shared.utils.LongUtils;

public class MultiSelector {
    private static final String SELECTION_IDS = "ids";
    private static final String SELECTIONS_STATE = "state";
    private Set<Long> mSelections = new HashSet<>();
    private WeakHolderTracker mTracker = new WeakHolderTracker();
    private boolean mIsSelectable;

    public void setSelectable(boolean isSelectable) {
        this.mIsSelectable = isSelectable;
        refreshAllHolders();
    }

    public void setSelected(SelectableHolder holder, boolean isSelected) {
        this.setSelected(holder.getAdapterPosition(), holder.getItemId(), isSelected);
    }

    public void setSelected(int position, long id, boolean isSelected) {
        if (isSelected) {
            mSelections.add(id);
        } else {
            mSelections.remove(id);
        }
        this.refreshHolder(mTracker.getHolder(position));
    }

    private boolean isSelected(long id) {
        return mSelections.contains(id);
    }

    public void clearSelections() {
        this.mSelections.clear();
        this.refreshAllHolders();
    }

    public ArrayList<Long> getSelectedIds() {
        return new ArrayList<>(mSelections);
    }

    public void bindHolder(SelectableHolder holder, int position, long id) {
        this.mTracker.bindHolder(holder, position);
        this.refreshHolder(holder);
    }

    public boolean tapSelection(SelectableHolder holder) {
        return tapSelection(holder.getAdapterPosition(), holder.getItemId());
    }

    private boolean tapSelection(int position, long itemId) {
        if (mIsSelectable) {
            boolean isSelected = isSelected(itemId);
            this.setSelected(position, itemId, !isSelected);
            return true;
        } else {
            return false;
        }
    }

    private void refreshAllHolders() {
        for (SelectableHolder holder : mTracker.getTrackedHolders()) {
            this.refreshHolder(holder);
        }
    }

    private void refreshHolder(SelectableHolder holder) {
        if (holder != null) {
            if (holder instanceof ItemBindingHolder) {
                if (((ItemBindingHolder) holder).getItem() != null) {
                    ((ItemBindingHolder) holder).bindItem();
                }
            }
            holder.setSelectable(mIsSelectable);
            boolean isActivated = mSelections.contains(holder.getItemId());
            holder.setActivated(isActivated);
        }
    }

    public Bundle saveSelectionStates() {
        Bundle information = new Bundle();
        information.putLongArray(SELECTION_IDS, LongUtils.toArray(getSelectedIds()));
        information.putBoolean(SELECTIONS_STATE, this.mIsSelectable);
        return information;
    }

    public void restoreSelectionStates(Bundle savedStates) {
        long[] selectedPositions = savedStates.getLongArray(SELECTION_IDS);
        restoreSelections(LongUtils.toList(selectedPositions));
        this.mIsSelectable = savedStates.getBoolean(SELECTIONS_STATE);
    }

    private void restoreSelections(List<Long> selected) {
        if (selected != null) {
            mSelections.clear();
            mSelections.addAll(selected);
            this.refreshAllHolders();
        }
    }
}
