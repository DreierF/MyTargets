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

package de.dreier.mytargets.views

import android.content.Context
import android.databinding.DataBindingUtil
import android.os.Handler
import android.support.annotation.PluralsRes
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.widget.LinearLayout

import de.dreier.mytargets.R
import de.dreier.mytargets.databinding.LayoutNumberPickerBinding

/**
 * A simple layout group that provides a numeric text area with two buttons to
 * increment or decrement the value in the text area. Holding either button
 * will auto increment the value up or down appropriately.
 *
 * @author Jeffrey F. Cole
 */
class NumberPicker(context: Context, attributeSet: AttributeSet) : LinearLayout(context, attributeSet) {

    var minimum = 1
    var maximum = 30

    private var value: Int = minimum
        set(value) {
            var adjValue = value
            if (adjValue > maximum) {
                adjValue = maximum
                autoIncrement = false
            }
            if (adjValue < minimum) {
                adjValue = minimum
                autoDecrement = false
            }
            field = adjValue
            if (textPattern == 0) {
                binding.numberValue.text = field.toString()
            } else {
                binding.numberValue.text = resources.getQuantityString(textPattern, field, field)
            }
            changeListener?.onValueChanged(field)
        }

    private val repeatUpdateHandler = Handler()

    private var autoIncrement = false
    private var autoDecrement = false
    private var changeListener: OnValueChangedListener? = null

    @PluralsRes
    private var textPattern: Int = 0
    private val binding: LayoutNumberPickerBinding

    interface OnValueChangedListener {
        fun onValueChanged(`val`: Int)
    }

    fun setOnValueChangedListener(listener: OnValueChangedListener) {
        changeListener = listener
    }

    /**
     * This little guy handles the auto part of the auto incrementing feature.
     * In doing so it instantiates itself. There has to be a pattern name for
     * that...
     *
     * @author Jeffrey F. Cole
     */
    private inner class RepetitiveUpdater : Runnable {
        override fun run() {
            if (autoIncrement) {
                increment()
                repeatUpdateHandler.postDelayed(RepetitiveUpdater(), REPEAT_DELAY)
            } else if (autoDecrement) {
                decrement()
                repeatUpdateHandler.postDelayed(RepetitiveUpdater(), REPEAT_DELAY)
            }
        }
    }

    init {
        val inflater = LayoutInflater.from(context)
        binding = DataBindingUtil.inflate(inflater, R.layout.layout_number_picker, this, true)
        initDecrementButton()
        initIncrementButton()
    }

    private fun initIncrementButton() {
        // Increment once for a click
        binding.numberIncrement.setOnClickListener { increment() }

        // Auto increment for a long click
        binding.numberIncrement.setOnLongClickListener {
            autoIncrement = true
            repeatUpdateHandler.post(RepetitiveUpdater())
            false
        }

        // When the button is released, if we're auto incrementing, stop
        binding.numberIncrement.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP && autoIncrement) {
                autoIncrement = false
            }
            false
        }
    }

    private fun initDecrementButton() {
        // Decrement once for a click
        binding.numberDecrement.setOnClickListener { v -> decrement() }

        // Auto Decrement for a long click
        binding.numberDecrement.setOnLongClickListener { arg0 ->
            autoDecrement = true
            repeatUpdateHandler.post(RepetitiveUpdater())
            false
        }

        // When the button is released, if we're auto decrementing, stop
        binding.numberDecrement.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP && autoDecrement) {
                autoDecrement = false
            }
            false
        }
    }

    fun setTextPattern(@PluralsRes textPattern: Int) {
        this.textPattern = textPattern
    }

    private fun increment() {
        value += 1
    }

    private fun decrement() {
        value -= 1
    }

    companion object {
        private val REPEAT_DELAY: Long = 50
    }
}
