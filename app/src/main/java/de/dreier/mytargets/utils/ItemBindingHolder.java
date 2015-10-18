package de.dreier.mytargets.utils;

import android.support.v7.widget.RebindReportingHolder;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.bignerdranch.android.multiselector.SelectableHolder;

/**
 * Created by florian on 18.10.15.
 */
public abstract class ItemBindingHolder<T> extends RebindReportingHolder implements SelectableHolder, View.OnClickListener, View.OnLongClickListener {
    protected T mItem;

    public ItemBindingHolder(View itemView) {
        super(itemView);
    }

    public T getItem() {
        return mItem;
    }

    public void setItem(T mItem) {
        this.mItem = mItem;
    }

    @Override
    public boolean onLongClick(View v) {
        return true;
    }

    public void bindCursor(T t) {
        setItem(t);
        bindCursor();
    }

    public abstract void bindCursor();
}
