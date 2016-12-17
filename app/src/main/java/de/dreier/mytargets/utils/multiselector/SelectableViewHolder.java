/*
 * Copyright (C) 2016 Florian Dreier
 *
 * This file is part of MyTargets.
 *
 * MyTargets is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2
 * as published by the Free Software Foundation.
 *
 * MyTargets is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */

package de.dreier.mytargets.utils.multiselector;

import android.view.View;

import de.dreier.mytargets.utils.OnItemClickListener;

/**
 * A holder extended to support having a selectable mode with a different
 * background and state list animator.
 * <p/>
 * When {@link #setSelectable(boolean)} is set to true, itemView's
 * background is set to the value of selectionModeBackgroundDrawable,
 * and its StateListAnimator is set to selectionModeStateListAnimator.
 * When it is set to false, the defaultModeBackgroundDrawable and
 * defaultModeStateListAnimator are used.
 * <p/>
 * defaultModeBackgroundDrawable and defaultModeStateListAnimator
 * default to the values on itemView at the time the holder was constructed.
 * <p/>
 * selectionModeBackgroundDrawable defaults to a StateListDrawable that displays
 * your colorAccent theme color when state_activated=true, and nothing otherwise.
 * selectionModeStateListAnimator defaults to a raise animation that animates selection
 * items to a 12dp translationZ.
 * <p/>
 * (Thanks to Kurt Nelson for examples and discussion on approaches here.
 *
 * @see <a href="https://github.com/kurtisnelson/">https://github.com/kurtisnelson/</a>)
 */
public abstract class SelectableViewHolder<T> extends ItemBindingHolder<T>
        implements View.OnClickListener, View.OnLongClickListener {
    private final MultiSelector mMultiSelector;
    private OnItemClickListener<T> mListener;
    private boolean mIsSelectable = false;

    /**
     * Construct a new SelectableHolder hooked up to be controlled by a MultiSelector.
     * <p/>
     * If the MultiSelector is not null, the SelectableHolder can be selected by
     * calling {@link MultiSelector#setSelected(SelectableHolder, boolean)}.
     * <p/>
     * If the MultiSelector is null, the SelectableHolder acts as a standalone
     * ViewHolder that you can control manually by setting {@link #setSelectable(boolean)}
     * and {@link #setActivated(boolean)}
     *
     * @param itemView      Item view for this ViewHolder
     * @param multiSelector A selector set to bind this holder to
     */
    public SelectableViewHolder(View itemView, MultiSelector multiSelector, OnItemClickListener<T> listener) {
        super(itemView);
        this.mMultiSelector = multiSelector;
        itemView.setOnClickListener(this);
        if (multiSelector != null) {
            itemView.setLongClickable(true);
            itemView.setOnLongClickListener(this);
            mListener = listener;
        }
    }

    @Override
    protected void onRebind() {
        this.mMultiSelector.bindHolder(this, this.getAdapterPosition(), this.getItemId());
    }

    /**
     * Returns whether {@link #itemView} is currently in a
     * selectable mode.
     *
     * @return True if selectable.
     */
    public boolean isSelectable() {
        return mIsSelectable;
    }

    /**
     * Turns selection mode on and off.
     *
     * @param isSelectable True if selectable.
     */
    public void setSelectable(boolean isSelectable) {
        mIsSelectable = isSelectable;
    }

    public T getItem() {
        return item;
    }

    void setItem(T mItem) {
        this.item = mItem;
    }

    @Override
    public void onClick(View v) {
        if (mListener != null) {
            mListener.onClick(this, item);
        }
    }

    @Override
    public boolean onLongClick(View v) {
        mListener.onLongClick(this);
        return true;
    }

    public void bindItem(T t) {
        setItem(t);
        bindItem();
    }

    public abstract void bindItem();

    /**
     * Calls through to {@link #itemView#setActivated}.
     *
     * @param isActivated True to activate the view.
     */
    public void setActivated(boolean isActivated) {
        itemView.setActivated(isActivated);
    }

    /**
     * Calls through to {@link #itemView#isActivated}.
     *
     * @return True if the view is activated.
     */
    public boolean isActivated() {
        return itemView.isActivated();
    }
}
