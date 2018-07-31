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

package android.support.v7.widget

import android.view.View

/**
 * ViewHolder with a callback for when it is rebound.
 *
 *
 * This lives in [android.support.v7.widget] so that it can override
 * [.setFlags], [.offsetPosition], and
 * [.addFlags], all of which are package private. This is currently
 * the only way to automatically detect when a ViewHolder has been rebound
 * to a new item.
 */
abstract class RebindReportingHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    /**
     * Called when this instance is rebound to another item in the RecyclerView.
     */
    protected abstract fun onRebind()

    internal override fun setFlags(flags: Int, mask: Int) {
        super.setFlags(flags, mask)
        val setFlags = mask and flags
        checkFlags(setFlags)
    }

    internal override fun addFlags(flags: Int) {
        super.addFlags(flags)
        checkFlags(flags)
    }

    private fun checkFlags(setFlags: Int) {
        if (isRelevantFlagSet(setFlags)) {
            onRebind()
        }
    }

    /**
     * Check if the view is due for rebinding
     *
     * @param flag
     * @return
     */
    private fun isRelevantFlagSet(flag: Int): Boolean {
        return intArrayOf(RecyclerView.ViewHolder.FLAG_BOUND,
                RecyclerView.ViewHolder.FLAG_INVALID,
                RecyclerView.ViewHolder.FLAG_UPDATE,
                RecyclerView.ViewHolder.FLAG_RETURNED_FROM_SCRAP)
                .any { (flag and it) == it }
    }

    internal override fun offsetPosition(offset: Int, applyToPreLayout: Boolean) {
        super.offsetPosition(offset, applyToPreLayout)
        onRebind()
    }
}
