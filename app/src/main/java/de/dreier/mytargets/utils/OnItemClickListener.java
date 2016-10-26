package de.dreier.mytargets.utils;

import de.dreier.mytargets.utils.multiselector.SelectableViewHolder;

public interface OnItemClickListener<T> {
    void onClick(SelectableViewHolder holder, T item);
    void onLongClick(SelectableViewHolder holder);
}