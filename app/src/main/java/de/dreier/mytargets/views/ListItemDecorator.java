/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */

package de.dreier.mytargets.views;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import de.dreier.mytargets.R;
import de.dreier.mytargets.adapters.ExpandableNowListAdapter;


class ListItemDecorator extends RecyclerView.ItemDecoration {
    private final int paddingBottom;

    public ListItemDecorator(Context context) {
        this.paddingBottom = (int) context.getResources()
                .getDimension(R.dimen.listheader_padding_bottom);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view);
        RecyclerView.Adapter adapter = parent.getAdapter();
        if(adapter instanceof ExpandableNowListAdapter) {
            ExpandableNowListAdapter expAdapter = (ExpandableNowListAdapter) adapter;
            if (expAdapter.isHeader(position)) {
                outRect.bottom = paddingBottom;
            }
        }
    }
}