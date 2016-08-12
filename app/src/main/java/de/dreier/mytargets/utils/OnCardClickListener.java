package de.dreier.mytargets.utils;

import de.dreier.mytargets.utils.multiselector.SelectableViewHolder;

public interface OnCardClickListener<T> {
    void onClick(SelectableViewHolder holder, T item);

    void onLongClick(SelectableViewHolder holder);
}
