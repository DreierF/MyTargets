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

package de.dreier.mytargets.views;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Handler;
import android.support.annotation.PluralsRes;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.widget.LinearLayout;

import de.dreier.mytargets.R;
import de.dreier.mytargets.databinding.LayoutNumberPickerBinding;

/**
 * A simple layout group that provides a numeric text area with two buttons to
 * increment or decrement the value in the text area. Holding either button
 * will auto increment the value up or down appropriately.
 *
 * @author Jeffrey F. Cole
 */
public class NumberPicker extends LinearLayout {

    private static final long REPEAT_DELAY = 50;

    private int minimum = 1;
    private int maximum = 30;

    private Integer value;

    private final Handler repeatUpdateHandler = new Handler();

    private boolean autoIncrement = false;
    private boolean autoDecrement = false;
    private OnValueChangedListener changeListener;

    @PluralsRes
    private int textPattern;
    private final LayoutNumberPickerBinding binding;

    public interface OnValueChangedListener {
        void onValueChanged(int val);
    }

    public void setOnValueChangedListener(OnValueChangedListener listener) {
        changeListener = listener;
    }

    /**
     * This little guy handles the auto part of the auto incrementing feature.
     * In doing so it instantiates itself. There has to be a pattern name for
     * that...
     *
     * @author Jeffrey F. Cole
     */
    private class RepetitiveUpdater implements Runnable {
        public void run() {
            if (autoIncrement) {
                increment();
                repeatUpdateHandler.postDelayed(new RepetitiveUpdater(), REPEAT_DELAY);
            } else if (autoDecrement) {
                decrement();
                repeatUpdateHandler.postDelayed(new RepetitiveUpdater(), REPEAT_DELAY);
            }
        }
    }

    public NumberPicker(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        binding = DataBindingUtil.inflate(inflater, R.layout.layout_number_picker, this, true);

        // init the individual elements
        initDecrementButton();
        setValue(minimum);
        initIncrementButton();
    }

    private void initIncrementButton() {
        // Increment once for a click
        binding.numberIncrement.setOnClickListener(v -> increment());

        // Auto increment for a long click
        binding.numberIncrement.setOnLongClickListener(
                arg0 -> {
                    autoIncrement = true;
                    repeatUpdateHandler.post(new RepetitiveUpdater());
                    return false;
                }
        );

        // When the button is released, if we're auto incrementing, stop
        binding.numberIncrement.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP && autoIncrement) {
                autoIncrement = false;
            }
            return false;
        });
    }

    private void initDecrementButton() {
        // Decrement once for a click
        binding.numberDecrement.setOnClickListener(v -> decrement());

        // Auto Decrement for a long click
        binding.numberDecrement.setOnLongClickListener(
                arg0 -> {
                    autoDecrement = true;
                    repeatUpdateHandler.post(new RepetitiveUpdater());
                    return false;
                }
        );

        // When the button is released, if we're auto decrementing, stop
        binding.numberDecrement.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP && autoDecrement) {
                autoDecrement = false;
            }
            return false;
        });
    }

    public void setTextPattern(@PluralsRes int textPattern) {
        this.textPattern = textPattern;
    }

    private void increment() {
        setValue(value + 1);
    }

    private void decrement() {
        setValue(value - 1);
    }

    public void setMinimum(int minimum) {
        this.minimum = minimum;
    }

    public void setMaximum(int maximum) {
        this.maximum = maximum;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int val) {
        if (val > maximum) {
            val = maximum;
            autoIncrement = false;
        }
        if (val < minimum) {
            val = minimum;
            autoDecrement = false;
        }
        value = val;
        if (textPattern == 0) {
            binding.numberValue.setText(value.toString());
        } else {
            binding.numberValue.setText(getResources().getQuantityString(textPattern, value, value));
        }
        if (changeListener != null) {
            changeListener.onValueChanged(val);
        }
    }
}
