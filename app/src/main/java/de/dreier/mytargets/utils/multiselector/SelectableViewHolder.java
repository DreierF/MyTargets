/*
 * Copyright (C) 2017 Florian Dreier
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

import android.support.annotation.NonNull;
import android.view.View;

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
    @NonNull
    private final SelectorBase selector;
    private OnItemClickListener<T> clickListener;
    private OnItemLongClickListener<T> longClickListener;
    private boolean isSelectable = false;

    /**
     * Construct a new SelectableHolder hooked up to be controlled by a SingleSelector.
     *
     * @param itemView Item view for this ViewHolder
     * @param selector A selector set to bind this holder to
     */
    public SelectableViewHolder(@NonNull View itemView, @NonNull SingleSelector selector, OnItemClickListener<T> listener) {
        super(itemView);
        this.selector = selector;
        itemView.setOnClickListener(this);
        this.clickListener = listener;
    }

    /**
     * Construct a new SelectableHolder hooked up to be controlled by a SingleSelector.
     *
     * @param itemView Item view for this ViewHolder
     * @param selector A selector set to bind this holder to
     */
    public SelectableViewHolder(@NonNull View itemView, @NonNull SingleSelector selector, OnItemClickListener<T> listener,  OnItemLongClickListener<T> longClickListener) {
        super(itemView);
        this.selector = selector;
        itemView.setLongClickable(true);
        itemView.setOnClickListener(this);
        itemView.setOnLongClickListener(this);
        this.clickListener = listener;
        this.longClickListener = longClickListener;
    }

    /**
     * Construct a new SelectableHolder hooked up to be controlled by a MultiSelector.
     *
     * @param itemView Item view for this ViewHolder
     * @param selector A selector set to bind this holder to
     */
    public SelectableViewHolder(@NonNull View itemView, @NonNull MultiSelector selector, OnItemClickListener<T> clickListener,  OnItemLongClickListener<T> longClickListener) {
        super(itemView);
        this.selector = selector;
        itemView.setLongClickable(true);
        itemView.setOnClickListener(this);
        itemView.setOnLongClickListener(this);
        this.clickListener = clickListener;
        this.longClickListener = longClickListener;
    }

    @Override
    protected void onRebind() {
        selector.bindHolder(this, getItemId());
    }

    /**
     * Returns whether {@link #itemView} is currently in a
     * selectable mode.
     *
     * @return True if selectable.
     */
    public boolean isSelectable() {
        return isSelectable;
    }

    /**
     * Turns selection mode on and off.
     *
     * @param isSelectable True if selectable.
     */
    public void setSelectable(boolean isSelectable) {
        this.isSelectable = isSelectable;
    }

    public T getItem() {
        return item;
    }

    void setItem(T mItem) {
        this.item = mItem;
    }

    @Override
    public void onClick(View v) {
        if (clickListener != null) {
            clickListener.onClick(this, item);
        }
    }

    @Override
    public boolean onLongClick(View v) {
        longClickListener.onLongClick(this);
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
