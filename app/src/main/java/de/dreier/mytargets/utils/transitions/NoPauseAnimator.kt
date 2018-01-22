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

package de.dreier.mytargets.utils.transitions

import android.animation.Animator
import android.animation.TimeInterpolator
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.v4.util.ArrayMap
import java.util.*

/**
 * https://halfthought.wordpress.com/2014/11/07/reveal-transition/
 *
 *
 * Interrupting Activity transitions can yield an OperationNotSupportedException when the
 * transition tries to pause the animator. Yikes! We can fix this by wrapping the Animator:
 */
@RequiresApi(Build.VERSION_CODES.KITKAT)
class NoPauseAnimator(private val animator: Animator) : Animator() {
    private val listeners = ArrayMap<Animator.AnimatorListener, Animator.AnimatorListener>()

    override fun addListener(listener: Animator.AnimatorListener) {
        val wrapper = AnimatorListenerWrapper(this, listener)
        if (!listeners.containsKey(listener)) {
            listeners[listener] = wrapper
            animator.addListener(wrapper)
        }
    }

    override fun cancel() {
        animator.cancel()
    }

    override fun end() {
        animator.end()
    }

    override fun getDuration(): Long {
        return animator.duration
    }

    override fun getInterpolator(): TimeInterpolator {
        return animator.interpolator
    }

    override fun setInterpolator(timeInterpolator: TimeInterpolator) {
        animator.interpolator = timeInterpolator
    }

    override fun getListeners(): ArrayList<Animator.AnimatorListener> {
        return ArrayList(listeners.keys)
    }

    override fun getStartDelay(): Long {
        return animator.startDelay
    }

    override fun setStartDelay(delayMS: Long) {
        animator.startDelay = delayMS
    }

    override fun isPaused(): Boolean {
        return animator.isPaused
    }

    override fun isRunning(): Boolean {
        return animator.isRunning
    }

    override fun isStarted(): Boolean {
        return animator.isStarted
    }

    /* We don't want to override pause or resume methods because we don't want them
         * to affect animator.
        public void pause();
        public void resume();
        public void addPauseListener(AnimatorPauseListener listener);
        public void removePauseListener(AnimatorPauseListener listener);
        */

    override fun removeAllListeners() {
        listeners.clear()
        animator.removeAllListeners()
    }

    override fun removeListener(listener: Animator.AnimatorListener) {
        val wrapper = listeners[listener]
        if (wrapper != null) {
            listeners.remove(listener)
            animator.removeListener(wrapper)
        }
    }

    override fun setDuration(durationMS: Long): Animator {
        animator.duration = durationMS
        return this
    }

    override fun setTarget(target: Any?) {
        animator.setTarget(target)
    }

    override fun setupEndValues() {
        animator.setupEndValues()
    }

    override fun setupStartValues() {
        animator.setupStartValues()
    }

    override fun start() {
        animator.start()
    }

    private class AnimatorListenerWrapper(private val mAnimator: Animator, private val mListener: Animator.AnimatorListener) : Animator.AnimatorListener {

        override fun onAnimationStart(animator: Animator) {
            mListener.onAnimationStart(mAnimator)
        }

        override fun onAnimationEnd(animator: Animator) {
            mListener.onAnimationEnd(mAnimator)
        }

        override fun onAnimationCancel(animator: Animator) {
            mListener.onAnimationCancel(mAnimator)
        }

        override fun onAnimationRepeat(animator: Animator) {
            mListener.onAnimationRepeat(mAnimator)
        }
    }
}
