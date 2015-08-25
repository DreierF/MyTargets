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


public class CardItemDecorator extends RecyclerView.ItemDecoration {
    private final int spaceHorizontal;
    private final int spaceVertical;
    private final int gridSize;

    public CardItemDecorator(Context context) {
        this(context, 1);
    }

    public CardItemDecorator(Context context, int gridSize) {
        this.spaceHorizontal = (int) context.getResources()
                .getDimension(R.dimen.card_padding_horizontal);
        this.spaceVertical = (int) context.getResources()
                .getDimension(R.dimen.card_padding_vertical);
        this.gridSize = gridSize;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view);
        if (position % gridSize == 0) {
            outRect.left = spaceHorizontal;
        } else {
            outRect.left = spaceHorizontal / 4;
        }
        if ((position + 1) % gridSize == 0) {
            outRect.right = spaceHorizontal;
        } else {
            outRect.right = spaceHorizontal / 4;
        }
        outRect.bottom = spaceVertical;

        // Add top margin only for the first item to avoid double space between items
        if (position < gridSize) {
            outRect.top = spaceVertical;
        }
    }
}