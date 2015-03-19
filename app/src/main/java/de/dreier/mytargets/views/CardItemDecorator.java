package de.dreier.mytargets.views;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.github.ksoichiro.android.observablescrollview.ObservableRecyclerView;

import de.dreier.mytargets.R;

/**
 * Created by Florian on 13.03.2015.
 */
public class CardItemDecorator extends RecyclerView.ItemDecoration {
    private final int spaceHorizontal;
    private final int spaceVertical;

    public CardItemDecorator(Context context) {
        this.spaceHorizontal = (int) context.getResources()
                .getDimension(R.dimen.card_padding_horizontal);
        this.spaceVertical = (int) context.getResources()
                .getDimension(R.dimen.card_padding_vertical);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.left = spaceHorizontal;
        outRect.right = spaceHorizontal;
        outRect.bottom = spaceVertical;

        // Add top margin only for the first item to avoid double space between items
        if (parent.getChildPosition(view) == 0 && !(parent instanceof ObservableRecyclerView)) {
            outRect.top = spaceVertical;
        }
    }
}