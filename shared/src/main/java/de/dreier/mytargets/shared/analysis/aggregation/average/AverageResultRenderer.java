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
import android.graphics.Rect;
import android.graphics.RectF;

import com.annimon.stream.Stream;

import de.dreier.mytargets.shared.analysis.aggregation.IAggregationResultRenderer;
import de.dreier.mytargets.shared.utils.PathUtils;

public class AverageResultRenderer implements IAggregationResultRenderer {

    private final Average average;
    private final Path innerDrawPath = new Path();
    private final Paint innerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Path innerPath = new Path();
    private final Path outerDrawPath = new Path();
    private final Paint outerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Path outerPath = new Path();
    private final Path stdDevDrawPath = new Path();
    private final Paint stdDevPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Path stdDevPath = new Path();
    private final Paint subTitlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Path symbolDrawPath = new Path();
    private final Paint symbolPaintI = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint symbolPaintO = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Path symbolPath = new Path();
    private final Paint titlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private boolean drawInner = false;
    private boolean drawOuter = false;
    private boolean drawStdDev = true;
    private boolean drawSubTitle = false;
    private boolean drawSymbol = true;
    private boolean drawTitle = false;
    private String subTitle = null;
    private float[] subTitlePos = new float[2];
    private float subTitleVOffset;
    private String title = null;
    private float[] titlePos = new float[2];
    private float titleVOffset;

    public AverageResultRenderer(Average average) {
        this.average = average;
        setDefaults();
        clear();
    }

    private void setDefaults() {
        titlePaint.setARGB(255, 255, 255, 255);
        titlePaint.setTextAlign(Paint.Align.CENTER);
        subTitlePaint.setARGB(255, 255, 255, 255);
        subTitlePaint.setTextAlign(Paint.Align.CENTER);
        setTitleSize(123, 123);
        symbolPaintO.setARGB(255, 0, 255, 0);
        symbolPaintO.setStyle(Paint.Style.STROKE);
        symbolPaintO.setStrokeWidth(0.005f);
        symbolPaintI.setARGB(255, 0, 0, 0);
        symbolPaintI.setStyle(Paint.Style.STROKE);
        symbolPaintI.setStrokeWidth(0.003f);
        innerPaint.setARGB(120, 0, 0, 255);
        innerPaint.setStyle(Paint.Style.FILL);
        stdDevPaint.setARGB(150, 100, 0, 0);
        stdDevPaint.setStyle(Paint.Style.FILL);
        stdDevPaint.setStrokeWidth(4.0f);
        outerPaint.setARGB(255, 0, 0, 0);
        outerPaint.setStyle(Paint.Style.FILL);
    }

    public void clear() {
        symbolPath.rewind();
        innerPath.rewind();
        stdDevPath.rewind();
        outerPath.rewind();
    }

    public void drawInner(boolean enabled) {
        drawInner = enabled;
    }

    public void drawOuter(boolean enabled) {
        drawOuter = enabled;
    }

    public void drawStdDev(boolean enabled) {
        drawStdDev = enabled;
    }

    public void drawSubTitle(boolean enabled) {
        drawSubTitle = enabled;
    }

    public void drawSymbol(boolean enabled) {
        drawSymbol = enabled;
    }

    public void drawTitle(boolean enabled) {
        drawTitle = enabled;
    }

    @Override
    public void setColor(int color) {
        innerPaint.setColor(color);
//        float[] hsl = new float[3];
//        int alpha = Color.alpha(color);
//        ColorUtils.colorToHSL(color, hsl);
//        hsl[2] = (1.0f - hsl[2]) * 0.7f + hsl[2];
//        innerPaint.setColor(ColorUtils
//                .setAlphaComponent(ColorUtils.HSLToColor(hsl), (int) ((float) alpha * 0.8f)));
//        symbolPaintI.setColor(ColorUtils.HSLToColor(hsl));
//        if (stdDevPaint.getStyle() == Paint.Style.STROKE) {
//            stdDevPaint.setColor(ColorUtils.HSLToColor(hsl));
//        } else {
//            stdDevPaint.setColor(color);
//        }
//
//        hsl[2] = hsl[2] * 0.7f;
//        outerPaint.setColor(ColorUtils
//                .setAlphaComponent(ColorUtils.HSLToColor(hsl), (int) ((float) alpha * 0.6f)));
//        hsl[2] = (1.0f - hsl[2]) * 0.9f + hsl[2];
//        titlePaint.setColor(ColorUtils.HSLToColor(hsl));
//        subTitlePaint.setColor(ColorUtils.HSLToColor(hsl));
    }

    public void setStdDevPaintStyle(Paint.Style style) {
        stdDevPaint.setStyle(style);
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    private void setTitleSize(float var1, float var2) {
        titlePaint.setTextSize(var1);
        Rect rect = new Rect();
        titlePaint.getTextBounds("XX", 0, 2, rect);
        titleVOffset = (float) rect.height() / 2.0f;
        subTitlePaint.setTextSize(var2);
        Rect var4 = new Rect();
        subTitlePaint.getTextBounds("XX", 0, 2, var4);
        subTitleVOffset = (float) rect.height() + (float) var4.height() / 1.9f;
    }

    @Override
    public void onPrepareDraw() {
        PointF avg = average.getAverage();
        RectF nUniStdDev = average.getNonUniformStdDev();

        float biggestNonUniStdDev = Stream.of(nUniStdDev.top, nUniStdDev.bottom,
                nUniStdDev.left, nUniStdDev.right).max(Float::compare).get();

        float smallestNonUniStdDev = Stream.of(nUniStdDev.top, nUniStdDev.bottom,
                nUniStdDev.left, nUniStdDev.right).min(Float::compare).get();

        if (drawOuter) {
            outerPath.rewind();
            outerPath.addCircle(avg.x, avg.y, biggestNonUniStdDev, Path.Direction.CW);
        }

        if (drawStdDev) {
            stdDevPath.rewind();
            stdDevPath.arcTo(new RectF(
                    average.weightedAverage.x - nUniStdDev.right, average.weightedAverage.y - nUniStdDev.bottom,
                    average.weightedAverage.x + nUniStdDev.right,
                    average.weightedAverage.y + nUniStdDev.bottom), 270.0f, 90.0f, true);
            stdDevPath.arcTo(new RectF(
                    average.weightedAverage.x - nUniStdDev.right, average.weightedAverage.y - nUniStdDev.top,
                    average.weightedAverage.x + nUniStdDev.right,
                    average.weightedAverage.y + nUniStdDev.top), 0.0f, 90.0f);
            stdDevPath.arcTo(new RectF(
                    average.weightedAverage.x - nUniStdDev.left, average.weightedAverage.y - nUniStdDev.top,
                    average.weightedAverage.x + nUniStdDev.left,
                    average.weightedAverage.y + nUniStdDev.top), 90.0f, 90.0f);
            stdDevPath.arcTo(new RectF(
                    average.weightedAverage.x - nUniStdDev.left, average.weightedAverage.y - nUniStdDev.bottom,
                    average.weightedAverage.x + nUniStdDev.left,
                    average.weightedAverage.y + nUniStdDev.bottom), 180.0f, 90.0f);
        }

        if (drawInner) {
            innerPath.rewind();
            innerPath.addCircle(avg.x, avg.y, smallestNonUniStdDev, Path.Direction.CW);
        }

        if (drawSymbol) {
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

        if (drawTitle && title != null && title.length() > 0) {
            avg = average.getAverage();
            titlePos = new float[]{avg.x, avg.y};
        }

        if (drawSubTitle && subTitle != null && subTitle.length() > 0) {
            avg = average.getAverage();
            subTitlePos = new float[]{avg.x, avg.y};
        }
    }

    @Override
    public void onDraw(Canvas canvas) {
        if (average.getDataPointCount() >= 3) {
            if (drawOuter) {
                outerDrawPath.set(outerPath);
                PathUtils.drawPath(canvas, outerDrawPath, outerPaint);
            }

            if (drawStdDev) {
                stdDevDrawPath.set(stdDevPath);
                PathUtils.drawPath(canvas, stdDevDrawPath, stdDevPaint);
            }

            if (drawInner) {
                innerDrawPath.set(innerPath);
                PathUtils.drawPath(canvas, innerDrawPath, innerPaint);
            }

            if (drawSymbol) {
                symbolDrawPath.set(symbolPath);
                PathUtils.drawPath(canvas, symbolDrawPath, symbolPaintO);
                PathUtils.drawPath(canvas, symbolDrawPath, symbolPaintI);
            }
        }

        if (average.getDataPointCount() >= 1 && drawTitle && title != null && title.length() > 0) {
            canvas.drawText(title, titlePos[0], titlePos[1] + titleVOffset,
                    titlePaint);
        }

        if (average.getDataPointCount() >= 1 && drawSubTitle && subTitle != null && subTitle.length() > 0) {
            canvas.drawText(subTitle, subTitlePos[0], subTitlePos[1] + subTitleVOffset,
                    subTitlePaint);
        }
    }
}
