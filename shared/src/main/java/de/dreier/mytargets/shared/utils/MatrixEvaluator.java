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

package de.dreier.mytargets.shared.utils;

import android.animation.TypeEvaluator;
import android.graphics.Matrix;
import android.support.annotation.NonNull;

/**
 * This class is passed to ValueAnimator in order to animate matrix changes
 */
public class MatrixEvaluator implements TypeEvaluator<Matrix> {

    @NonNull
    private float[] tempStartValues = new float[9];

    @NonNull
    private float[] tempEndValues = new float[9];

    @NonNull
    private Matrix tempMatrix = new Matrix();

    @NonNull
    @Override
    public Matrix evaluate(float fraction, @NonNull Matrix startValue, @NonNull Matrix endValue) {
        startValue.getValues(tempStartValues);
        endValue.getValues(tempEndValues);
        for (int i = 0; i < 9; i++) {
            float diff = tempEndValues[i] - tempStartValues[i];
            tempEndValues[i] = tempStartValues[i] + (fraction * diff);
        }
        tempMatrix.setValues(tempEndValues);

        return tempMatrix;
    }
}
