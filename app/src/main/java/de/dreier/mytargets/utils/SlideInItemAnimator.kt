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

package de.dreier.mytargets.utils

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.os.Handler
import androidx.interpolator.view.animation.LinearOutSlowInInterpolator
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.RecyclerView
import android.view.Gravity
import android.view.View
import java.util.*

/**
 * A [RecyclerView.ItemAnimator] that fades & slides newly added items in from a given
 * direction.
 */
class SlideInItemAnimator @JvmOverloads constructor(
    slideFromEdge: Int = Gravity.BOTTOM,
    layoutDirection: Int = -1
) : DefaultItemAnimator() {

    private val pendingAdds = ArrayList<RecyclerView.ViewHolder>()
    private val slideFromEdge: Int = Gravity.getAbsoluteGravity(slideFromEdge, layoutDirection)
    private var useDefaultAnimator = false

    init {
        addDuration = 160L
    }

    override fun animateAdd(holder: RecyclerView.ViewHolder): Boolean {
        if (useDefaultAnimator) {
            return super.animateAdd(holder)
        }
        holder.itemView.alpha = 0f
        when (slideFromEdge) {
            Gravity.START -> holder.itemView.translationX = (-holder.itemView.width / 3).toFloat()
            Gravity.TOP -> holder.itemView.translationY = (-holder.itemView.height / 3).toFloat()
            Gravity.END -> holder.itemView.translationX = (holder.itemView.width / 3).toFloat()
            else // Gravity.BOTTOM
            -> holder.itemView.translationY = (holder.itemView.height / 3).toFloat()
        }
        pendingAdds.add(holder)
        return true
    }

    override fun runPendingAnimations() {
        super.runPendingAnimations()
        if (!pendingAdds.isEmpty()) {
            for (i in pendingAdds.indices.reversed()) {
                val holder = pendingAdds[i]
                Handler().postDelayed(
                    {
                        holder.itemView.animate()
                            .alpha(1f)
                            .translationX(0f)
                            .translationY(0f)
                            .setDuration(addDuration)
                            .setListener(object : AnimatorListenerAdapter() {
                                override fun onAnimationStart(animation: Animator) {
                                    dispatchAddStarting(holder)
                                }

                                override fun onAnimationEnd(animation: Animator) {
                                    animation.listeners.remove(this)
                                    dispatchAddFinished(holder)
                                    dispatchFinishedWhenDone()
                                }

                                override fun onAnimationCancel(animation: Animator) {
                                    clearAnimatedValues(holder.itemView)
                                }
                            }).interpolator = LinearOutSlowInInterpolator()
                    },
                    (holder.adapterPosition * 30).toLong()
                )
                pendingAdds.removeAt(i)
            }
            useDefaultAnimator = true
        }
    }

    override fun endAnimation(holder: RecyclerView.ViewHolder) {
        holder.itemView.animate().cancel()
        if (pendingAdds.remove(holder)) {
            dispatchAddFinished(holder)
            clearAnimatedValues(holder.itemView)
        }
        super.endAnimation(holder)
    }

    override fun endAnimations() {
        for (i in pendingAdds.indices.reversed()) {
            val holder = pendingAdds[i]
            clearAnimatedValues(holder.itemView)
            dispatchAddFinished(holder)
            pendingAdds.removeAt(i)
        }
        super.endAnimations()
    }

    override fun isRunning(): Boolean {
        return !pendingAdds.isEmpty() || super.isRunning()
    }

    private fun dispatchFinishedWhenDone() {
        if (!isRunning) {
            dispatchAnimationsFinished()
        }
    }

    private fun clearAnimatedValues(view: View) {
        view.alpha = 1f
        view.translationX = 0f
        view.translationY = 0f
        view.animate().startDelay = 0
    }

}
/**
 * Default to sliding in upward.
 */// undefined layout dir; bottom isn't relative
