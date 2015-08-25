/*
 * Copyright (c) 2010, Jeffrey F. Cole
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 * 
 * 	Redistributions of source code must retain the above copyright notice, this
 * 	list of conditions and the following disclaimer.
 * 
 * 	Redistributions in binary form must reproduce the above copyright notice, 
 * 	this list of conditions and the following disclaimer in the documentation 
 * 	and/or other materials provided with the distribution.
 * 
 * 	Neither the name of the technologichron.net nor the names of its contributors 
 * 	may be used to endorse or promote products derived from this software 
 * 	without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE 
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE 
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR 
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS 
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN 
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE 
 * POSSIBILITY OF SUCH DAMAGE.
 */

/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */

package de.dreier.mytargets.views;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.PluralsRes;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import de.dreier.mytargets.R;

/**
 * A simple layout group that provides a numeric text area with two buttons to
 * increment or decrement the value in the text area. Holding either button
 * will auto increment the value up or down appropriately.
 *
 * @author Jeffrey F. Cole
 */
public class NumberPicker extends LinearLayout {

    private static final long REPEAT_DELAY = 50;

    public interface OnValueChangedListener {
        void onValueChanged(int val);
    }

    private int mMinimum = 1;
    private int mMaximum = 30;

    private Integer value;

    private TextView valueText;

    private final Handler repeatUpdateHandler = new Handler();

    private boolean autoIncrement = false;
    private boolean autoDecrement = false;
    private OnValueChangedListener changeListener;

    @PluralsRes
    private int mTextPattern;

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
        inflater.inflate(R.layout.layout_number_picker, this, true);

        // init the individual elements
        initDecrementButton();
        initValueEditText();
        initIncrementButton();
    }

    private void initIncrementButton() {
        Button increment = (Button) findViewById(R.id.number_increment);

        // Increment once for a click
        increment.setOnClickListener(v -> increment());

        // Auto increment for a long click
        increment.setOnLongClickListener(
                arg0 -> {
                    autoIncrement = true;
                    repeatUpdateHandler.post(new RepetitiveUpdater());
                    return false;
                }
        );

        // When the button is released, if we're auto incrementing, stop
        increment.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP && autoIncrement) {
                autoIncrement = false;
            }
            return false;
        });
    }

    private void initValueEditText() {
        valueText = (TextView) findViewById(R.id.number_value);
        setValue(mMinimum);
    }

    private void initDecrementButton() {
        Button decrement = (Button) findViewById(R.id.number_decrement);


        // Decrement once for a click
        decrement.setOnClickListener(v -> decrement());


        // Auto Decrement for a long click
        decrement.setOnLongClickListener(
                arg0 -> {
                    autoDecrement = true;
                    repeatUpdateHandler.post(new RepetitiveUpdater());
                    return false;
                }
        );

        // When the button is released, if we're auto decrementing, stop
        decrement.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP && autoDecrement) {
                autoDecrement = false;
            }
            return false;
        });
    }

    public void setTextPattern(@PluralsRes int textPattern) {
        mTextPattern = textPattern;
    }

    void increment() {
        setValue(value + 1);
    }

    void decrement() {
        setValue(value - 1);
    }

    public void setMinimum(int minimum) {
        mMinimum = minimum;
    }

    public void setMaximum(int maximum) {
        mMaximum = maximum;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int val) {
        if (val > mMaximum) {
            val = mMaximum;
            autoIncrement = false;
        }
        if (val < mMinimum) {
            val = mMinimum;
            autoDecrement = false;
        }
        value = val;
        if (mTextPattern == 0) {
            valueText.setText(value.toString());
        } else {
            valueText.setText(getResources().getQuantityString(mTextPattern, value, value));
        }
        if (changeListener != null) {
            changeListener.onValueChanged(val);
        }
    }
}
