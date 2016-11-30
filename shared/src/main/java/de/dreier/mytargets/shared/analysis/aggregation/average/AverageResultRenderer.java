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

package de.dreier.mytargets.shared.analysis.aggregation.average;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;

import com.annimon.stream.Stream;

import de.dreier.mytargets.shared.analysis.aggregation.IAggregationResultRenderer;
import de.dreier.mytargets.shared.utils.PathUtils;

public class AverageResultRenderer implements IAggregationResultRenderer {

    private final Average average;
    private final Paint stdDevPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Path stdDevPath = new Path();
    private final Paint symbolPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Path symbolPath = new Path();

    public AverageResultRenderer(Average average) {
        this.average = average;
        symbolPaint.setARGB(255, 37, 155, 36);
        symbolPaint.setStyle(Paint.Style.STROKE);
        symbolPaint.setStrokeWidth(0.005f);
        stdDevPaint.setARGB(150, 100, 0, 0);
        stdDevPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    public void setColor(int color) {
        stdDevPaint.setColor(color);
    }

    @Override
    public void onPrepareDraw() {
        PointF avg =  average.getAverage();
        RectF nUniStdDev = average.getNonUniformStdDev();
        stdDevPath.rewind();
        stdDevPath.arcTo(new RectF(
                average.weightedAverage.x - nUniStdDev.right,
                average.weightedAverage.y - nUniStdDev.bottom,
                average.weightedAverage.x + nUniStdDev.right,
                average.weightedAverage.y + nUniStdDev.bottom), 270.0f, 90.0f, true);
        stdDevPath.arcTo(new RectF(
                average.weightedAverage.x - nUniStdDev.right,
                average.weightedAverage.y - nUniStdDev.top,
                average.weightedAverage.x + nUniStdDev.right,
                average.weightedAverage.y + nUniStdDev.top), 0.0f, 90.0f);
        stdDevPath.arcTo(new RectF(
                average.weightedAverage.x - nUniStdDev.left,
                average.weightedAverage.y - nUniStdDev.top,
                average.weightedAverage.x + nUniStdDev.left,
                average.weightedAverage.y + nUniStdDev.top), 90.0f, 90.0f);
        stdDevPath.arcTo(new RectF(
                average.weightedAverage.x - nUniStdDev.left,
                average.weightedAverage.y - nUniStdDev.bottom,
                average.weightedAverage.x + nUniStdDev.left,
                average.weightedAverage.y + nUniStdDev.bottom), 180.0f, 90.0f);

        float smallestNonUniStdDev = Stream.of(nUniStdDev.top, nUniStdDev.bottom,
                nUniStdDev.left, nUniStdDev.right).min(Float::compare).get();

        float tmp = smallestNonUniStdDev / 4.0f;
        symbolPath.rewind();
        symbolPath.addCircle(avg.x, avg.y, smallestNonUniStdDev / 2.0f, Path.Direction.CW);
        symbolPath.moveTo(avg.x - nUniStdDev.left, avg.y - tmp);
        symbolPath.lineTo(avg.x - nUniStdDev.left, avg.y + tmp);
        symbolPath.moveTo(avg.x - nUniStdDev.left, avg.y);
        symbolPath.lineTo(avg.x + nUniStdDev.right, avg.y);
        symbolPath.moveTo(avg.x + nUniStdDev.right, avg.y - tmp);
        symbolPath.lineTo(avg.x + nUniStdDev.right, avg.y + tmp);
        symbolPath.moveTo(avg.x - tmp, avg.y + nUniStdDev.top);
        symbolPath.lineTo(avg.x + tmp, avg.y + nUniStdDev.top);
        symbolPath.moveTo(avg.x, avg.y + nUniStdDev.top);
        symbolPath.lineTo(avg.x, avg.y - nUniStdDev.bottom);
        symbolPath.moveTo(avg.x - tmp, avg.y - nUniStdDev.bottom);
        symbolPath.lineTo(avg.x + tmp, avg.y - nUniStdDev.bottom);
    }

    @Override
    public void onDraw(Canvas canvas) {
        if (average.getDataPointCount() >= 3) {
            PathUtils.drawPath(canvas, stdDevPath, stdDevPaint);
            PathUtils.drawPath(canvas, symbolPath, symbolPaint);
        }
    }
}
