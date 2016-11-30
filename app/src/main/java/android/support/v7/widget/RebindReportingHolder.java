/*
 * Copyright (C) 2016 Florian Dreier
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

package android.support.v7.widget;

import android.view.View;

/**
 * ViewHolder with a callback for when it is rebound.
 *
 * This lives in {@link android.support.v7.widget} so that it can override
 * {@link #setFlags(int, int)}, {@link #offsetPosition(int, boolean)}, and
 * {@link #addFlags(int)}, all of which are package private. This is currently
 * the only way to automatically detect when a ViewHolder has been rebound
 * to a new item.
 *
 * If you intend to subclass for the purpose of interfacing with
 * a {@link de.dreier.mytargets.utils.multiselector.MultiSelector},
 * use {@link de.dreier.mytargets.utils.multiselector.MultiSelectorBindingHolder}
 * instead.
 */
public abstract class RebindReportingHolder extends RecyclerView.ViewHolder {

    public RebindReportingHolder(View itemView) {
        super(itemView);
    }

    /**
     * Called when this instance is rebound to another item in the RecyclerView.
     */
    protected abstract void onRebind();

    @Override
    void setFlags(int flags, int mask) {
        super.setFlags(flags, mask);
        int setFlags = mask & flags;
        checkFlags(setFlags);
    }

    @Override
    void addFlags(int flags) {
        super.addFlags(flags);
        checkFlags(flags);
    }

    private void checkFlags(int setFlags) {
        if (isRelevantFlagSet(setFlags)) {
            onRebind();
        }
    }

    /**
     * check if the view is due for rebiding
     * @param flag
     * @return
     */
    private static boolean isRelevantFlagSet(int flag) {
        for (Integer value : new int[] { FLAG_BOUND, FLAG_INVALID, FLAG_UPDATE, FLAG_RETURNED_FROM_SCRAP }) {
            if ((flag & value) == value) {
                return true;
            }
        }

        return false;
    }

    @Override
    void offsetPosition(int offset, boolean applyToPreLayout) {
        super.offsetPosition(offset, applyToPreLayout);
        onRebind();
    }
}