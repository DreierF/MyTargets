/*
 * Copyright (C) 2018 Florian Dreier
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

package de.dreier.mytargets.utils.multiselector

import android.view.View

/**
 * A holder extended to support having a selectable mode with a different
 * background and state list animator.
 *
 *
 * When [.setSelectable] is set to true, itemView's
 * background is set to the value of selectionModeBackgroundDrawable,
 * and its StateListAnimator is set to selectionModeStateListAnimator.
 * When it is set to false, the defaultModeBackgroundDrawable and
 * defaultModeStateListAnimator are used.
 *
 *
 * defaultModeBackgroundDrawable and defaultModeStateListAnimator
 * default to the values on itemView at the time the holder was constructed.
 *
 *
 * selectionModeBackgroundDrawable defaults to a StateListDrawable that displays
 * your colorAccent theme color when state_activated=true, and nothing otherwise.
 * selectionModeStateListAnimator defaults to a raise animation that animates selection
 * items to a 12dp translationZ.
 *
 *
 * (Thanks to Kurt Nelson for examples and discussion on approaches here.
 *
 * @see [https://github.com/kurtisnelson/](https://github.com/kurtisnelson/))
 */
abstract class SelectableViewHolder<T> : ItemBindingHolder<T>, View.OnClickListener,
    View.OnLongClickListener {
    private val selector: SelectorBase
    private var clickListener: OnItemClickListener<T>? = null
    private var longClickListener: OnItemLongClickListener<T>? = null
    override var isSelectable = false

    override var isActivated: Boolean
        get() = itemView.isActivated
        set(value) {
            itemView.isActivated = value
        }

    /**
     * Construct a new SelectableHolder hooked up to be controlled by a SingleSelector.
     *
     * @param itemView Item view for this ViewHolder
     * @param selector A selector set to bind this holder to
     */
    constructor(itemView: View, selector: SingleSelector, listener: OnItemClickListener<T>) : super(
        itemView
    ) {
        this.selector = selector
        itemView.setOnClickListener(this)
        this.clickListener = listener
    }

    /**
     * Construct a new SelectableHolder hooked up to be controlled by a SingleSelector.
     *
     * @param itemView Item view for this ViewHolder
     * @param selector A selector set to bind this holder to
     */
    constructor(
        itemView: View,
        selector: SingleSelector,
        listener: OnItemClickListener<T>,
        longClickListener: OnItemLongClickListener<T>
    ) : super(itemView) {
        this.selector = selector
        itemView.isLongClickable = true
        itemView.setOnClickListener(this)
        itemView.setOnLongClickListener(this)
        this.clickListener = listener
        this.longClickListener = longClickListener
    }

    /**
     * Construct a new SelectableHolder hooked up to be controlled by a MultiSelector.
     *
     * @param itemView Item view for this ViewHolder
     * @param selector A selector set to bind this holder to
     */
    constructor(
        itemView: View,
        selector: MultiSelector,
        clickListener: OnItemClickListener<T>,
        longClickListener: OnItemLongClickListener<T>
    ) : super(itemView) {
        this.selector = selector
        itemView.isLongClickable = true
        itemView.setOnClickListener(this)
        itemView.setOnLongClickListener(this)
        this.clickListener = clickListener
        this.longClickListener = longClickListener
    }

    override fun onRebind() {
        selector.bindHolder(this, itemIdentifier)
    }

    override fun onClick(v: View) {
        clickListener?.onClick(this, item!!)
    }

    override fun onLongClick(v: View): Boolean {
        longClickListener!!.onLongClick(this)
        return true
    }

    override fun internalBindItem(t: T) {
        item = t
        bindItem(t)
    }

    abstract override fun bindItem(item: T)
}
