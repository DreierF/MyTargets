package de.dreier.mytargets.utils;

public interface OnCardClickListener<T> {
    void onClick(SelectableViewHolder holder, T item);
    void onLongClick(SelectableViewHolder holder);
}
