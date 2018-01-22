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

package de.dreier.mytargets.shared.views

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.annotation.TargetApi
import android.content.Context
import android.graphics.Canvas
import android.graphics.PointF
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.support.annotation.VisibleForTesting
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewCompat
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat
import android.support.v4.widget.ExploreByTouchHelper
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.accessibility.AccessibilityEvent
import android.view.animation.AccelerateDecelerateInterpolator
import de.dreier.mytargets.shared.R
import de.dreier.mytargets.shared.models.SelectableZone
import de.dreier.mytargets.shared.models.Target
import de.dreier.mytargets.shared.models.db.End
import de.dreier.mytargets.shared.models.db.RoundTemplate
import de.dreier.mytargets.shared.models.db.Shot
import de.dreier.mytargets.shared.targets.drawable.TargetImpactAggregationDrawable
import de.dreier.mytargets.shared.targets.models.WAFull
import de.dreier.mytargets.shared.utils.EndRenderer
import de.dreier.mytargets.shared.utils.RectUtils
import java.util.*

abstract class TargetViewBase : View, View.OnTouchListener {
    private val touchHelper = TargetAccessibilityTouchHelper(
            this)
    @VisibleForTesting
    val virtualViews: MutableList<VirtualView> = ArrayList()
    /**
     * Zero-based index of the shot that is currently being changed.
     * If no shot is selected it is set to EndRenderer#NO_SELECTION.
     */
    protected var currentShotIndex: Int = 0
        set(currentArrow) {
            field = currentArrow
            if (target.model.dependsOnArrowIndex()) {
                updateSelectableZones()
            }
        }
    protected var endRenderer = EndRenderer()
    protected lateinit var shots: List<Shot>
    protected var round: RoundTemplate? = null
    protected var setListener: OnEndFinishedListener? = null
    protected open var inputMethod = EInputMethod.KEYBOARD
    protected var density: Float = 0.toFloat()
    protected lateinit var selectableZones: List<SelectableZone>
    protected lateinit var target: Target
    protected var targetDrawable: TargetImpactAggregationDrawable? = null
    protected var animator: AnimatorSet? = null

    protected var backspaceSymbol: Drawable
    private var backspaceButtonBounds: Rect? = null

    /**
     * The screen area reserved to show the already entered shots.
     */
    private var endRect: RectF? = null

    protected abstract val selectedShotCircleRadius: Int

    @JvmOverloads
    constructor(
            context: Context,
            attrs: AttributeSet? = null,
            defStyleAttr: Int = 0)
            : super(context, attrs, defStyleAttr)

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(
            context: Context,
            attrs: AttributeSet?,
            defStyleAttr: Int,
            defStyleRes: Int)
            : super(context, attrs, defStyleAttr, defStyleRes)

    init {
        setOnTouchListener(this)
        density = resources.displayMetrics.density
        ViewCompat.setAccessibilityDelegate(this, touchHelper)
        ViewCompat.setImportantForAccessibility(this, ViewCompat.IMPORTANT_FOR_ACCESSIBILITY_YES)
        backspaceSymbol = ContextCompat.getDrawable(context, R.drawable.ic_backspace_grey600_24dp)!!
        initForDesigner()
    }

    protected val isCurrentlySelecting: Boolean
        get() = currentShotIndex != EndRenderer.NO_SELECTION && shots[currentShotIndex].scoringRing != Shot.NOTHING_SELECTED

    protected val circleAnimation: Animator?
        get() {
            var pos: PointF? = null
            if (isCurrentlySelecting) {
                pos = getShotCoordinates(shots[currentShotIndex])
            }
            val initialSize = selectedShotCircleRadius
            return endRenderer
                    .getAnimationToSelection(currentShotIndex, pos, initialSize, endRect)
        }

    private fun initForDesigner() {
        if (isInEditMode) {
            shots = listOf(Shot(i = 0), Shot(i = 1), Shot(i = 2))
            shots[0].scoringRing = 0
            shots[0].x = 0.01f
            shots[0].y = 0.05f
            target = Target(WAFull.ID, 0)
            targetDrawable = target.impactAggregationDrawable
            endRenderer.init(this, density, target)
            endRenderer.setShots(shots)
            currentShotIndex = 1
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        targetDrawable?.callback = null
        targetDrawable?.cleanup()
    }

    open fun initWithTarget(target: Target) {
        this.target = target
        targetDrawable = this.target.impactAggregationDrawable
        targetDrawable?.callback = this
        endRenderer.init(this, density, this.target)
        updateSelectableZones()
    }

    open fun replaceWithEnd(end: End) {
        shots = end.loadShots()
        currentShotIndex = getNextShotIndex(-1)
        endRenderer.setShots(shots)
        endRenderer.setSelection(currentShotIndex, null, EndRenderer
                .MAX_CIRCLE_SIZE)
        animateToNewState()
        notifyTargetShotsChanged()
    }

    override fun invalidateDrawable(drawable: Drawable) {
        super.invalidateDrawable(drawable)
        invalidate()
    }

    public override fun dispatchHoverEvent(event: MotionEvent): Boolean {
        return touchHelper.dispatchHoverEvent(event) || super.dispatchHoverEvent(event)
    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        return touchHelper.dispatchKeyEvent(event) || super.dispatchKeyEvent(event)
    }

    public override fun onFocusChanged(gainFocus: Boolean, direction: Int,
                                       previouslyFocusedRect: Rect?) {
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect)
        touchHelper.onFocusChanged(gainFocus, direction, previouslyFocusedRect)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        updateLayout()
        animateToNewState()
        updateVirtualViews()
        invalidate()
    }

    protected fun updateLayout() {
        updateLayoutBounds(width, height)
        backspaceButtonBounds = getBackspaceButtonBounds()
        endRect = getEndRect()
        applyBoundsToBackspaceSymbol()
    }

    private fun applyBoundsToBackspaceSymbol() {
        val innerButtonBounds = Rect(backspaceButtonBounds)
        innerButtonBounds.inset((8 * density).toInt(), (8 * density).toInt())
        val bounds = Rect(0, 0, backspaceSymbol.intrinsicWidth, backspaceSymbol
                .intrinsicHeight)
        val backspaceSymbolBounds = RectUtils.fitRectWithin(bounds, innerButtonBounds)
        backspaceSymbol.bounds = backspaceSymbolBounds
    }

    protected fun drawBackspaceButton(canvas: Canvas) {
        backspaceSymbol.draw(canvas)
    }

    protected abstract fun getBackspaceButtonBounds(): Rect

    protected abstract fun updateLayoutBounds(width: Int, height: Int)

    protected abstract fun getEndRect(): RectF

    protected fun getSelectableZoneIndexFromShot(shot: Shot): Int {
        return selectableZones.indexOfFirst { shot.scoringRing == it.index }
    }

    fun setOnTargetSetListener(listener: OnEndFinishedListener) {
        setListener = listener
    }

    protected abstract fun getSelectableZonePosition(i: Int): Rect

    override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {
        val x = motionEvent.x
        val y = motionEvent.y

        if (!isCurrentlySelecting && (selectPreviousShots(motionEvent, x, y) || pressBackspace(motionEvent, x, y))) {
            return true
        }

        if (currentShotIndex == EndRenderer.NO_SELECTION) {
            return true
        }

        val shot = shots[currentShotIndex]
        if (updateShotToPosition(shot, x, y)) {
            endRenderer.setSelection(
                    currentShotIndex, getShotCoordinates(shot),
                    selectedShotCircleRadius)
            invalidate()

            // If finger is released go to next shoot
            if (motionEvent.action == MotionEvent.ACTION_UP) {
                onShotSelectionFinished()
            }
        }
        return true
    }

    protected open fun onShotSelectionFinished() {
        currentShotIndex = getNextShotIndex(this.currentShotIndex)
        animateToNewState()
        notifyTargetShotsChanged()
        notifyEndFinished()
    }

    /**
     * Returns the index of the next shot, after the given one, which does not have a zone set yet.
     *
     * @param currentShotIndex Index of the current shot.
     * Can also be set to -1, to start the search from the first shot.
     * @return Returns a valid index or EndRenderer.NO_SELECTION
     */
    protected fun getNextShotIndex(currentShotIndex: Int): Int {
        var nextShotIndex = currentShotIndex + 1
        while (nextShotIndex < shots.size && shots[nextShotIndex].scoringRing != Shot.NOTHING_SELECTED) {
            nextShotIndex++
        }
        return if (nextShotIndex == shots.size) {
            EndRenderer.NO_SELECTION
        } else nextShotIndex
    }

    protected open fun notifyTargetShotsChanged() {
        invalidate()
    }

    protected fun notifyEndFinished() {
        if (this.currentShotIndex == EndRenderer.NO_SELECTION) {
            setListener?.onEndFinished(shots)
        }
    }

    protected abstract fun getShotCoordinates(shot: Shot): PointF

    protected fun animateToNewState() {
        if (endRect == null) {
            return
        }
        // Extension point for sub-classes making use of spots
        val animations = ArrayList<Animator>()
        collectAnimations(animations)
        playAnimations(animations)
    }

    protected open fun collectAnimations(animations: MutableList<Animator>) {
        val animation = circleAnimation
        if (animation != null) {
            animations.add(animation)
        }
    }

    protected fun playAnimations(setList: List<Animator>) {
        cancelPendingAnimations()
        animator = AnimatorSet()
        animator!!.playTogether(setList)
        animator!!.interpolator = AccelerateDecelerateInterpolator()
        animator!!.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationCancel(animation: Animator) {
                onAnimationEnd(animation)
            }

            override fun onAnimationEnd(animation: Animator) {
                animator = null
                updateVirtualViews()
                invalidate()
            }
        })
        animator!!.duration = 300
        animator!!.start()
    }

    protected fun cancelPendingAnimations() {
        if (animator != null) {
            val tmp = animator!!
            animator = null
            tmp.cancel()
        }
    }

    /**
     * Updates the given Shot to the given position.
     *
     * @param shot Shot to update
     * @param x    X-Coordinate
     * @param y    Y-Coordinate
     * @return Returns true if the update was successful and false if the position is invalid
     */
    protected abstract fun updateShotToPosition(shot: Shot, x: Float, y: Float): Boolean

    protected abstract fun selectPreviousShots(motionEvent: MotionEvent, x: Float, y: Float): Boolean

    private fun pressBackspace(motionEvent: MotionEvent, x: Float, y: Float): Boolean {
        if (backspaceButtonBounds!!.contains(x.toInt(), y.toInt())) {
            if (motionEvent.action == MotionEvent.ACTION_UP) {
                if (currentShotIndex != 0) {
                    if (currentShotIndex == EndRenderer.NO_SELECTION) {
                        currentShotIndex = shots.size
                    }
                    currentShotIndex -= 1
                    val shot = shots[currentShotIndex]
                    shot.scoringRing = Shot.NOTHING_SELECTED
                    notifyTargetShotsChanged()
                    animateToNewState()
                }
            }
            return true
        }
        return false
    }

    protected fun updateSelectableZones() {
        if (currentShotIndex != EndRenderer.NO_SELECTION) {
            selectableZones = target.getSelectableZoneList(currentShotIndex)
            if (virtualViews.size > 0) {
                updateVirtualViews()
            }
        }
    }

    enum class EInputMethod {
        KEYBOARD, PLOTTING
    }

    interface OnEndFinishedListener {
        fun onEndFinished(shotList: List<Shot>)
    }

    private fun updateVirtualViews() {
        virtualViews.clear()
        var vv = VirtualView()
        vv.description = resources.getString(R.string.backspace)
        vv.rect = backspaceButtonBounds
        vv.id = 0
        vv.shot = false
        virtualViews.add(vv)
        if (inputMethod == EInputMethod.KEYBOARD) {
            for (i in selectableZones.indices) {
                vv = VirtualView()
                vv.id = i + 1
                vv.shot = false
                vv.description = selectableZones[i].text
                if ("M" == vv.description) {
                    vv.description = resources.getString(R.string.miss)
                }
                vv.rect = getSelectableZonePosition(i)
                virtualViews.add(vv)
            }
        }
        val firstId = virtualViews.size
        for ((_, index, _, _, _, scoringRing) in shots) {
            if (scoringRing == Shot.NOTHING_SELECTED) {
                continue
            }
            vv = VirtualView()
            vv.id = firstId + index
            vv.shot = true
            var score = target.zoneToString(scoringRing, index)
            if ("M" == score) {
                score = resources.getString(R.string.miss)
            }
            vv.description = resources
                    .getString(R.string.accessibility_description_shot_n_score, index + 1, score)
            vv.rect = endRenderer.getBoundsForShot(index)
            virtualViews.add(vv)
        }
    }

    private class TargetAccessibilityTouchHelper internal constructor(private val targetView: TargetViewBase) : ExploreByTouchHelper(targetView) {

        override fun getVirtualViewAt(x: Float, y: Float): Int {
            val vw = findVirtualViewByPosition(x, y) ?: return ExploreByTouchHelper.INVALID_ID
            return vw.id
        }

        private fun findVirtualViewByPosition(x: Float, y: Float): VirtualView? {
            for (virtualView in targetView.virtualViews) {
                if (virtualView.rect!!.contains(x.toInt(), y.toInt())) {
                    return virtualView
                }
            }
            return null
        }

        override fun getVisibleVirtualViews(virtualViewIds: MutableList<Int>) {
            for (i in targetView.virtualViews.indices) {
                virtualViewIds.add(targetView.virtualViews[i].id)
            }
        }

        override fun onPopulateEventForVirtualView(virtualViewId: Int, event: AccessibilityEvent) {
            val vw = findVirtualViewById(virtualViewId) ?: return
            event.text.add(vw.description)
        }

        private fun findVirtualViewById(virtualViewId: Int): VirtualView? {
            for (virtualView in targetView.virtualViews) {
                if (virtualView.id == virtualViewId) {
                    return virtualView
                }
            }
            return null
        }

        override fun onPopulateNodeForVirtualView(virtualViewId: Int, node: AccessibilityNodeInfoCompat) {
            val vw = findVirtualViewById(virtualViewId) ?: return

            node.text = vw.description
            node.contentDescription = vw.description
            node.className = targetView.javaClass.name
            node.setBoundsInParent(vw.rect)
        }

        override fun onPerformActionForVirtualView(virtualViewId: Int, action: Int, arguments: Bundle?): Boolean {
            return false
        }
    }

    inner class VirtualView {
        var id: Int = 0
        var shot: Boolean = false
        var rect: Rect? = null
        var description: String? = null
    }
}
