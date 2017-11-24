/*
 * Copyright (C) 2017 Florian Dreier
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

package de.dreier.mytargets.utils.multiselector;

import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public abstract class SelectorBase {
    private static final String SELECTIONS_STATE = "state";
    @NonNull
    protected WeakHolderTracker tracker = new WeakHolderTracker();
    private boolean isSelectable;

    public void setSelectable(boolean isSelectable) {
        this.isSelectable = isSelectable;
        refreshAllHolders();
    }

    protected void refreshAllHolders() {
        for (SelectableHolder holder : tracker.getTrackedHolders()) {
            refreshHolder(holder);
        }
    }

    protected void refreshHolder(@Nullable SelectableHolder holder) {
        if (holder != null) {
            if (holder instanceof ItemBindingHolder &&
                    ((ItemBindingHolder) holder).getItem() != null) {
                ((ItemBindingHolder) holder).bindItem();
            }
            holder.setSelectable(isSelectable);
            boolean isActivated = isSelected(holder.getItemId());
            holder.setActivated(isActivated);
        }
    }

    public void setSelected(@NonNull SelectableHolder holder, boolean isSelected) {
        setSelected(holder.getItemId(), isSelected);
    }

    public abstract void setSelected(long id, boolean isSelected);

    protected abstract boolean isSelected(long id);

    public boolean tapSelection(@NonNull SelectableHolder holder) {
        long itemId = holder.getItemId();
        if (isSelectable) {
            boolean isSelected = isSelected(itemId);
            setSelected(itemId, !isSelected);
            return true;
        } else {
            return false;
        }
    }

    @NonNull
    public final Bundle saveSelectionStates() {
        Bundle bundle = new Bundle();
        bundle.putBoolean(SELECTIONS_STATE, isSelectable);
        saveSelectionStates(bundle);
        return bundle;
    }

    public void bindHolder(SelectableHolder holder, long id) {
        tracker.bindHolder(holder, id);
        refreshHolder(holder);
    }

    protected abstract void saveSelectionStates(Bundle bundle);

    @CallSuper
    public void restoreSelectionStates(@NonNull Bundle savedStates) {
        isSelectable = savedStates.getBoolean(SELECTIONS_STATE);
    }
}
