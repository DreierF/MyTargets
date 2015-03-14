package com.bignerdranch.android.recyclerviewchoicemode;

/**
 * Created by Florian on 13.03.2015.
 */
public interface OnCardClickListener<T> {
    void onClick(CardViewHolder holder, T item);
    void onLongClick(CardViewHolder holder);
}
