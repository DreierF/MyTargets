package de.dreier.mytargets.utils.multiselector;

import android.support.v7.widget.RebindReportingHolder;
import android.view.View;

public abstract class ItemBindingHolder<T> extends RebindReportingHolder implements SelectableHolder, View.OnClickListener, View.OnLongClickListener {
    protected T mItem;

    ItemBindingHolder(View itemView) {
        super(itemView);
    }

    public T getItem() {
        return mItem;
    }

    void setItem(T mItem) {
        this.mItem = mItem;
    }

    @Override
    public boolean onLongClick(View v) {
        return true;
    }

    public void bindItem(T t) {
        setItem(t);
        bindItem();
    }

    public abstract void bindItem();
}
