package de.dreier.mytargets.utils;

/**
 * Created by Florian on 13.03.2015.
 */
public interface OnCardClickListener<T> {
    void onClick(SelectableViewHolder holder, T item);
    void onLongClick(SelectableViewHolder holder);
}
