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

package de.dreier.mytargets.features.training.input

import android.animation.Animator
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.app.AlertDialog
import android.content.Context
import android.graphics.*
import android.text.TextPaint
import android.util.AttributeSet
import android.util.Property
import android.view.MotionEvent
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ArrayAdapter
import android.widget.GridView
import de.dreier.mytargets.R
import de.dreier.mytargets.features.settings.SettingsManager
import de.dreier.mytargets.features.training.input.TargetView.EKeyboardType.LEFT
import de.dreier.mytargets.shared.analysis.aggregation.EAggregationStrategy
import de.dreier.mytargets.shared.models.Dimension
import de.dreier.mytargets.shared.models.Target
import de.dreier.mytargets.shared.models.db.Shot
import de.dreier.mytargets.shared.targets.drawable.TargetDrawable
import de.dreier.mytargets.shared.utils.EndRenderer
import de.dreier.mytargets.shared.utils.MatrixEvaluator
import de.dreier.mytargets.shared.views.TargetViewBase
import de.dreier.mytargets.shared.views.TargetViewBase.EInputMethod.KEYBOARD
import de.dreier.mytargets.shared.views.TargetViewBase.EInputMethod.PLOTTING

class TargetView : TargetViewBase {
    private var spotMatrices: Array<Matrix>? = null
    private var arrowNumbering: Boolean = false
    private var arrowDiameter: Dimension? = null
    private var maxArrowNumber: Int = 0
    private var targetZoomFactor: Float = 0.toFloat()
    private var updateListener: OnEndUpdatedListener? = null
    /**
     * Matrix to translate the target face with -1..1 coordinate system
     * to the correct area on the screen.
     */
    private var fullMatrix: Matrix? = null
    /**
     * Matrix to translate the target face with -1..1 coordinate system
     * to the correct area on the screen when selecting a shot position.
     * This area is inset by 30dp to allow easier placement outside of the target face.
     */
    private var fullExtendedMatrix: Matrix? = null
    /**
     * Inverse of [.fullExtendedMatrix].
     * Allows to map screen coordinates to -1..1 coordinate system.
     */
    private var fullExtendedMatrixInverse: Matrix? = null
    /**
     * Temporary point vector used to translate between different coordinate systems.
     */
    private val pt = FloatArray(2)
    private var aggregationStrategy = EAggregationStrategy.NONE
    /**
     * Left-handed or right-handed mode.
     */
    private var keyboardType: EKeyboardType? = null
    /**
     * Used to draw the keyboard buttons.
     */
    private var fillPaint = Paint()
    /**
     * Used to draw the keyboard button borders.
     */
    private var borderPaint = Paint()
    /**
     * Used to draw the keyboard button texts.
     */
    private var textPaint = TextPaint()

    /**
     * Percentage of the keyboard that is currently supposed to be shown. (0..1).
     */
    private var keyboardVisibility = 0f
    private var keyboardRect: RectF? = null
    private var targetRect: RectF? = null

    private val slipDetector = FingerSlipDetector()

    public override var inputMethod: TargetViewBase.EInputMethod
        get() = super.inputMethod
        set(mode) {
            if (mode !== inputMethod) {
                super.inputMethod = mode
                targetDrawable!!.drawArrowsEnabled(inputMethod === PLOTTING)
                targetDrawable!!.setAggregationStrategy(if (inputMethod === PLOTTING)
                    aggregationStrategy
                else
                    EAggregationStrategy.NONE)
            }
        }

    private val spotEndMatrix: Matrix
        get() {
            return if (currentShotIndex == EndRenderer.NO_SELECTION || inputMethod === KEYBOARD) {
                Matrix()
            } else {
                spotMatrices!![currentShotIndex % target.model.faceCount]
            }
        }

    val inputMode: TargetViewBase.EInputMethod
        get() = inputMethod

    override val selectedShotCircleRadius: Int
        get() = if (inputMethod === KEYBOARD) EndRenderer.MAX_CIRCLE_SIZE else 0

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        init()
    }

    private fun init() {
        // Set up a default TextPaint object
        textPaint.flags = Paint.ANTI_ALIAS_FLAG
        textPaint.textAlign = Paint.Align.LEFT
        textPaint.color = Color.BLACK
        textPaint.textSize = 22 * density
        textPaint.textAlign = Paint.Align.CENTER

        fillPaint.isAntiAlias = true

        borderPaint.color = -0xe3e3e5
        borderPaint.isAntiAlias = true
        borderPaint.style = Paint.Style.STROKE
    }

    override fun replaceWithEnd(shots: List<Shot>, exact: Boolean) {
        inputMethod = if (shots.all { it.scoringRing == Shot.NOTHING_SELECTED }) {
            if (exact) PLOTTING else KEYBOARD
        } else {
            SettingsManager.inputMethod
        }
        super.replaceWithEnd(shots, exact)
    }

    fun setArrow(diameter: Dimension, numbers: Boolean, maxArrowNumber: Int) {
        this.arrowNumbering = numbers
        this.arrowDiameter = diameter
        this.maxArrowNumber = maxArrowNumber
        targetDrawable!!.setArrowDiameter(diameter, SettingsManager.inputArrowDiameterScale)
    }

    fun setAggregationStrategy(aggregationStrategy: EAggregationStrategy) {
        this.aggregationStrategy = aggregationStrategy
    }

    override fun onDraw(canvas: Canvas) {
        // Draw target
        if (inputMethod === PLOTTING && isCurrentlySelecting) {
            drawZoomedInTarget(canvas)
        } else {
            drawTarget(canvas)
        }

        drawBackspaceButton(canvas)

        // Draw right indicator
        if (keyboardVisibility > 0) {
            drawKeyboard(canvas)
        }

        // Draw all points of this end at the top
        endRenderer.draw(canvas)
    }

    private fun drawZoomedInTarget(canvas: Canvas) {
        val shot = shots[currentShotIndex]

        targetDrawable!!.setMatrix(fullExtendedMatrix!!)
        targetDrawable!!.spotMatrix = spotMatrices!![currentShotIndex % target.model.faceCount]
        targetDrawable!!.setZoom(targetZoomFactor)
        targetDrawable!!.setFocusedArrow(shot)
        targetDrawable!!.setOffset(0f, POINTER_OFFSET_Y_DP * density)

        targetDrawable!!.draw(canvas)
    }

    // Draw actual target face
    private fun drawTarget(canvas: Canvas) {
        targetDrawable!!.setOffset(0f, 0f)
        targetDrawable!!.setZoom(1f)
        targetDrawable!!.setFocusedArrow(null)
        if (animator == null) {
            targetDrawable!!.setMatrix(fullMatrix!!)
            if (currentShotIndex == EndRenderer.NO_SELECTION || inputMethod === KEYBOARD) {
                targetDrawable!!.spotMatrix = Matrix()
            } else {
                targetDrawable!!.spotMatrix = spotMatrices!![currentShotIndex % target.model.faceCount]
            }
        }
        targetDrawable!!.draw(canvas)
    }

    override fun notifyTargetShotsChanged() {
        val displayedShots = shots.filter { it.scoringRing != Shot.NOTHING_SELECTED && it.index != currentShotIndex }
        targetDrawable!!.replaceShotsWith(displayedShots)
        super.notifyTargetShotsChanged()
        updateListener?.onEndUpdated(shots)
    }

    override fun getShotCoordinates(shot: Shot): PointF {
        val coordinate = PointF()
        if (inputMethod === KEYBOARD) {
            coordinate.x = keyboardRect!!.left
            if (keyboardType == LEFT) {
                coordinate.x += (KEYBOARD_WIDTH_DP + KEYBOARD_INNER_PADDING_DP) * density
            } else {
                coordinate.x -= KEYBOARD_INNER_PADDING_DP * density
            }
            val indicatorHeight = keyboardRect!!.height() / selectableZones.size
            val index = getSelectableZoneIndexFromShot(shot)
            coordinate.y = keyboardRect!!.top + indicatorHeight * index + indicatorHeight / 2.0f
        } else {
            pt[0] = shot.x
            pt[1] = shot.y
            fullMatrix!!.mapPoints(pt)
            coordinate.x = pt[0]
            coordinate.y = pt[1]
        }
        return coordinate
    }

    override fun initWithTarget(target: Target) {
        super.initWithTarget(target)
        spotMatrices = Array(target.model.faceCount) { Matrix() }
        for (i in 0 until target.model.faceCount) {
            targetDrawable!!.getPreCalculatedFaceMatrix(i).invert(spotMatrices!![i])
        }
    }

    override fun updateLayoutBounds(width: Int, height: Int) {
        targetRect = RectF(0f, MIN_END_RECT_HEIGHT_DP * density, width.toFloat(), height.toFloat())
        targetRect!!.inset(TARGET_PADDING_DP * density, TARGET_PADDING_DP * density)
        if (inputMethod === KEYBOARD) {
            if (keyboardType == LEFT) {
                targetRect!!.left += KEYBOARD_TOTAL_WIDTH_DP * density
            } else {
                targetRect!!.right -= KEYBOARD_TOTAL_WIDTH_DP * density
            }
        }
        if (targetRect!!.height() > targetRect!!.width()) {
            targetRect!!.top = targetRect!!.bottom - targetRect!!.width()
        }

        fullMatrix = Matrix()
        fullMatrix!!.setRectToRect(TargetDrawable.SRC_RECT, targetRect, Matrix.ScaleToFit.CENTER)

        val targetRectExt = RectF(targetRect)
        targetRectExt.inset(30 * density, 30 * density)
        fullExtendedMatrix = Matrix()
        fullExtendedMatrix!!
                .setRectToRect(TargetDrawable.SRC_RECT, targetRectExt, Matrix.ScaleToFit.CENTER)
        fullExtendedMatrixInverse = Matrix()
        fullExtendedMatrix!!.invert(fullExtendedMatrixInverse)

        keyboardRect = RectF()
        if (keyboardType == LEFT) {
            keyboardRect!!.left = KEYBOARD_OUTER_PADDING_DP * density
        } else {
            keyboardRect!!.left = width - KEYBOARD_TOTAL_WIDTH_DP * density
        }
        keyboardRect!!.right = keyboardRect!!.left + KEYBOARD_WIDTH_DP * density
        keyboardRect!!.top = (height / (selectableZones.size + 1)).toFloat()
        keyboardRect!!.bottom = height.toFloat()
    }

    override fun getBackspaceButtonBounds(): Rect {
        val backspaceButtonBounds = Rect()
        backspaceButtonBounds.top = 0
        backspaceButtonBounds.bottom = (keyboardRect!!.top - density).toInt()
        backspaceButtonBounds.left = keyboardRect!!.left.toInt()
        backspaceButtonBounds.right = keyboardRect!!.right.toInt()
        return backspaceButtonBounds
    }

    override fun getEndRect(): RectF {
        val endRect = RectF(targetRect)
        endRect.top = 0f
        endRect.bottom = targetRect!!.top
        if (keyboardType == LEFT) {
            endRect.left = keyboardRect!!.right
        } else {
            endRect.right = keyboardRect!!.left
        }
        endRect.inset(20 * density, TARGET_PADDING_DP * density)
        return endRect
    }

    /**
     * {@inheritDoc}
     */
    override fun updateShotToPosition(shot: Shot, x: Float, y: Float): Boolean {
        if (inputMethod === KEYBOARD) {
            if (keyboardRect!!.contains(x, y)) {
                var index = ((y - keyboardRect!!.top) * selectableZones.size / keyboardRect!!.height()).toInt()
                index = Math.min(Math.max(0, index), selectableZones.size - 1)
                shot.scoringRing = selectableZones[index].index
            } else {
                return false
            }
        } else {
            pt[0] = x
            pt[1] = y
            fullExtendedMatrixInverse!!.mapPoints(pt)
            shot.x = pt[0]
            shot.y = pt[1]
            shot.scoringRing = targetDrawable!!.getZoneFromPoint(shot.x, shot.y)
            slipDetector.addShot(shot.x, shot.y)
        }
        return true
    }

    override fun selectPreviousShots(motionEvent: MotionEvent, x: Float, y: Float): Boolean {
        // Handle selection of already saved shoots
        val shotIndex = endRenderer.getPressedPosition(x, y)
        endRenderer.setPressed(shotIndex)
        if (shotIndex != EndRenderer.NO_SELECTION) {
            if (motionEvent.action == MotionEvent.ACTION_UP) {
                currentShotIndex = shotIndex
                animateToNewState()
            }
            return true
        }
        return false
    }

    override fun collectAnimations(animations: MutableList<Animator>) {
        val initFullMatrix = Matrix(fullMatrix)
        updateLayout()
        val endMatrix = spotEndMatrix

        val newVisibility = (if (inputMethod === KEYBOARD) 1 else 0).toFloat()
        val inputAnimator = ValueAnimator.ofFloat(keyboardVisibility, newVisibility)
        inputAnimator.interpolator = AccelerateDecelerateInterpolator()
        inputAnimator.addUpdateListener { valueAnimator ->
            keyboardVisibility = valueAnimator.animatedValue as Float
            invalidate()
        }
        animations.add(inputAnimator)

        animations.add(ObjectAnimator.ofObject(this, ANIMATED_FULL_TRANSFORM_PROPERTY,
                MatrixEvaluator(), initFullMatrix, fullMatrix))
        animations.add(ObjectAnimator.ofObject(this, ANIMATED_SPOT_TRANSFORM_PROPERTY,
                MatrixEvaluator(), endMatrix))
        super.collectAnimations(animations)
    }

    override fun onShotSelectionFinished() {
        // Replace shot position with final position from slip detector
        val position = slipDetector.finalPosition
        val shot = shots[currentShotIndex]
        if (position != null) {
            shot.x = position.x
            shot.y = position.y
            shot.scoringRing = targetDrawable!!.getZoneFromPoint(shot.x, shot.y)
            slipDetector.reset()
        }

        if (!arrowNumbering) {
            super.onShotSelectionFinished()
        } else {

            // Prepare grid view
            val gridView = GridView(context)

            // Set grid view to alertDialog
            val dialog = AlertDialog.Builder(context)
                    .setView(gridView)
                    .setCancelable(false)
                    .setTitle(R.string.arrow_numbers)
                    .create()

            val numbers = (1..maxArrowNumber).map { it.toString() }
            gridView.adapter = ArrayAdapter(context, android.R.layout.simple_list_item_1, numbers)
            gridView.numColumns = 4
            gridView.setOnItemClickListener { _, _, pos, _ ->
                if (currentShotIndex < shots.size) {
                    shot.arrowNumber = numbers[pos]
                }
                dialog.dismiss()
                setOnTouchListener(this)
                super.onShotSelectionFinished()
            }
            // Disable touch input while dialog is visible
            setOnTouchListener(null)
            dialog.show()
        }
    }

    /**
     * Draws a rect on the right that shows all possible points.
     *
     * @param canvas Canvas to draw on
     */
    private fun drawKeyboard(canvas: Canvas) {
        for (i in 0 until selectableZones.size) {
            val zone = selectableZones[i]

            val rect = getSelectableZonePosition(i)

            fillPaint.color = zone.zone.fillColor
            canvas.drawRect(rect, fillPaint)

            borderPaint.color = zone.zone.strokeColor
            canvas.drawRect(rect, borderPaint)

            // For yellow and white background use black font color
            textPaint.color = zone.zone.textColor
            canvas.drawText(zone.text, rect.centerX().toFloat(), rect.centerY() + 10 * density,
                    textPaint)
        }
    }

    override fun getSelectableZonePosition(i: Int): Rect {
        val rect = Rect()
        val singleZoneHeight = keyboardRect!!.height() / selectableZones.size
        rect.top = (singleZoneHeight * i + density + keyboardRect!!.top).toInt()
        rect.bottom = (singleZoneHeight * (i + 1) - density + keyboardRect!!.top).toInt()
        rect.left = keyboardRect!!.left.toInt()
        rect.right = keyboardRect!!.right.toInt()
        val visibilityXOffset = (KEYBOARD_TOTAL_WIDTH_DP.toFloat() * (1 - keyboardVisibility) *
                density).toInt()
        if (keyboardType == LEFT) {
            rect.offset(-visibilityXOffset, 0)
        } else {
            rect.offset(visibilityXOffset, 0)
        }
        return rect
    }

    override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {
        // Cancel animation
        if (animator != null) {
            cancelPendingAnimations()
            return true
        }

        return super.onTouch(view, motionEvent)
    }

    fun setUpdateListener(updateListener: OnEndUpdatedListener) {
        this.updateListener = updateListener
    }

    fun reloadSettings() {
        this.targetZoomFactor = SettingsManager.inputTargetZoom
        this.keyboardType = SettingsManager.inputKeyboardType
        targetDrawable!!
                .setArrowDiameter(arrowDiameter!!, SettingsManager.inputArrowDiameterScale)
    }

    fun setTransparentShots(shotStream: List<Shot>) {
        targetDrawable!!.replacedTransparentShots(shotStream)
    }

    enum class EKeyboardType {
        LEFT, RIGHT
    }

    interface OnEndUpdatedListener {
        fun onEndUpdated(shots: List<Shot>)
    }

    companion object {

        /**
         * This property is passed to ObjectAnimator when animating the spot matrix of TargetView
         */
        private val ANIMATED_SPOT_TRANSFORM_PROPERTY = object : Property<TargetView, Matrix>(
                Matrix::class.java, "animatedSpotTransform") {

            override fun set(targetView: TargetView, matrix: Matrix) {
                targetView.targetDrawable!!.spotMatrix = matrix
                targetView.invalidate()
            }

            override fun get(targetView: TargetView): Matrix {
                return targetView.targetDrawable!!.spotMatrix
            }
        }

        /**
         * This property is passed to ObjectAnimator when animating the full matrix of TargetView
         */
        private val ANIMATED_FULL_TRANSFORM_PROPERTY = object : Property<TargetView, Matrix>(
                Matrix::class.java, "animatedFullTransform") {

            override fun set(targetView: TargetView, matrix: Matrix) {
                targetView.targetDrawable!!.setMatrix(matrix)
                targetView.invalidate()
            }

            override fun get(targetView: TargetView): Matrix? {
                return null
            }
        }

        private const val TARGET_PADDING_DP = 10
        private const val KEYBOARD_OUTER_PADDING_DP = 20
        private const val KEYBOARD_WIDTH_DP = 40
        private const val KEYBOARD_TOTAL_WIDTH_DP = KEYBOARD_WIDTH_DP + KEYBOARD_OUTER_PADDING_DP
        private const val POINTER_OFFSET_Y_DP = -60
        private const val MIN_END_RECT_HEIGHT_DP = 80
        private const val KEYBOARD_INNER_PADDING_DP = 40
    }
}
