package de.dreier.mytargets.utils;

import de.dreier.mytargets.utils.multiselector.SelectableViewHolder;

public interface OnItemClickListener<T> {
    void onClick(SelectableViewHolder<T> holder, T item);
    void onLongClick(SelectableViewHolder<T> holder);
}