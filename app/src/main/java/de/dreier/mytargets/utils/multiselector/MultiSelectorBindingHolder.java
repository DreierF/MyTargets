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

import android.support.v7.widget.RebindReportingHolder;
import android.view.View;

/**
 * <p>A {@link android.support.v7.widget.RecyclerView.ViewHolder} that will automatically
 * bind itself to items in a {@link MultiSelector}.
 * This is like a {@link SwappingHolder}, but without
 * any background swapping. If you want to implement {@link SelectableHolder},
 * this is usually the best place to start.</p>
 */
public abstract class MultiSelectorBindingHolder extends RebindReportingHolder implements SelectableHolder {
    private final MultiSelector mMultiSelector;

    public MultiSelectorBindingHolder(View itemView, MultiSelector multiSelector) {
        super(itemView);
        mMultiSelector = multiSelector;
    }

    @Override
    protected void onRebind() {
        mMultiSelector.bindHolder(this, getAdapterPosition(), getItemId());
    }
}