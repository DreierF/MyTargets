package de.dreier.mytargets.adapters;

import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public abstract class DynamicItemHolder<T> extends RecyclerView.ViewHolder {
    protected T item;

    public DynamicItemHolder(View itemView) {
        super(itemView);
    }

    public abstract void onBind(T item, int position, Fragment fragment, View.OnClickListener removeListener);
}
