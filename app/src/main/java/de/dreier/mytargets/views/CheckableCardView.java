package de.dreier.mytargets.views;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Checkable;
import android.widget.LinearLayout;

/*
 * This class is useful for using inside of ListView that needs to have checkable items.
 */
public class CheckableCardView extends LinearLayout implements Checkable {
    private CardView cardView;

    public CheckableCardView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        // find checked text view
        int childCount = getChildCount();
        for (int i = 0; i < childCount; ++i) {
            View v = getChildAt(i);
            if (v instanceof CardView) {
                cardView = (CardView) v;
            }
        }
    }

    @Override
    public boolean isChecked() {
        return cardView != null && cardView.isActivated();
    }

    @Override
    public void setChecked(boolean checked) {
        if (cardView != null) {
            cardView.setActivated(checked);
        }
    }

    @Override
    public void toggle() {
        if (cardView != null) {
            cardView.setActivated(!cardView.isActivated());
        }
    }
}