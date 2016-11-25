/*
 * Copyright (C) 2016 Florian Dreier
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

/**
 * This class is passed to ValueAnimator in order to animate matrix changes
 */
public class MatrixEvaluator implements TypeEvaluator<Matrix> {

    private float[] tempStartValues = new float[9];

    private float[] tempEndValues = new float[9];

    private Matrix tempMatrix = new Matrix();

    @Override
    public Matrix evaluate(float fraction, Matrix startValue, Matrix endValue) {
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