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

import android.graphics.PointF;
import android.graphics.RectF;

import java.util.ArrayList;
import java.util.List;

public abstract class Average {

    protected final PointF average = new PointF(0.0F, 0.0F);
    protected final PointF weightedAverage = new PointF(0.0F, 0.0F);
    private final RectF nonUniformStdDev = new RectF(-1.0F, -1.0F, -1.0F, -1.0F);
    private double centerStdDev = -1.0D;
    private double dirVariance = -1.0D;
    private double stdDevX = -1.0D;
    private double stdDevY = -1.0D;
    int dataPointCount;

    void computeWeightedAverage(ArrayList<PointF> data) {
        double sumX = 0.0D;
        double sumY = 0.0D;
        int i = 0;

        for (PointF point : data) {
            ++i;
            sumX += (double) ((float) i * point.x);
            sumY += (double) ((float) i * point.y);
        }

        i = (i + 1) * i / 2;
        weightedAverage.set((float) (sumX / (double) i), (float) (sumY / (double) i));
    }

    void computeCenterStdDev(ArrayList<PointF> data) {
        double sumXSquare = 0.0D;
        double sumYSquare = 0.0D;
        for (PointF point : data) {
            sumXSquare += (double) (point.x * point.x);
            sumYSquare += (double) (point.y * point.y);
        }

        centerStdDev = (Math.sqrt(sumXSquare / (double) data.size()) + Math
                .sqrt(sumYSquare / (double) data.size())) / 2.0D;
    }

    void computeDirectionalVariance(ArrayList<PointF> data) {
        double cosSum = 0.0D;
        double sinSum = 0.0D;

        for (PointF point : data) {
            double atan2 = Math.atan2((double) point.x, (double) point.y);
            cosSum += Math.cos(atan2);
            sinSum += Math.sin(atan2);
        }

        dirVariance = 1.0D - Math.sqrt(cosSum * cosSum + sinSum * sinSum) / (double) data.size();
    }

    void computeNonUniformStdDeviations(ArrayList<PointF> data) {
        int negCountX = 0;
        int posCountX = 0;
        int posCountY = 0;
        int negCountY = 0;
        double negSquaredXError = 0.0D;
        double posSquaredXError = 0.0D;
        double posSquaredYError = 0.0D;
        double negSquaredYError = 0.0D;

        for (PointF point : data) {
            double error = (double) (point.x - average.x);
            if (error < 0.0D) {
                negSquaredXError += error * error;
                ++negCountX;
            } else {
                posSquaredXError += error * error;
                ++posCountX;
            }

            error = (double) (point.y - average.y);
            if (error >= 0.0D) {
                posSquaredYError += error * error;
                ++posCountY;
            } else {
                negSquaredYError += error * error;
                ++negCountY;
            }
        }

        nonUniformStdDev.set((float) Math.sqrt(negSquaredXError / (double) negCountX),
                (float) Math.sqrt(posSquaredYError / (double) posCountY), (float) Math.sqrt(posSquaredXError / (double) posCountX),
                (float) Math.sqrt(negSquaredYError / (double) negCountY));
    }

    public void computeStdDevX(ArrayList<PointF> data) {
        double sumSquaredXError = 0.0D;

        for (PointF point : data) {
            double error = (double) (point.x - average.x);
            sumSquaredXError += error * error;
        }

        stdDevX = Math.sqrt(sumSquaredXError / (double) data.size());
    }

    public void computeStdDevY(ArrayList<PointF> data) {
        double sumSquaredYError = 0.0D;

        for (PointF point : data) {
            double error = (double) (point.y - average.y);
            sumSquaredYError += error * error;
        }

        stdDevY = Math.sqrt(sumSquaredYError / (double) data.size());
    }

    public void computeAverage(List<PointF> data) {
        double sumX = 0.0D;
        double sumY = 0.0D;

        for (PointF point : data) {
            sumX += (double) point.x;
            sumY += (double) point.y;
        }

        average.set((float) (sumX / (double) data.size()), (float) (sumY / (double) data.size()));
    }

    public PointF getAverage() {
        return average;
    }

    public double getDirectionalVariance() {
        return dirVariance;
    }

    public double getISV() {
        return getISV(getStdDev());
    }

    private double getISV(double var1) {
        return (Math.log(var1) - 2.57D) / -0.027D;
    }

    public RectF getNonUniformStdDev() {
        return nonUniformStdDev;
    }

    public double getStdDev() {
        return (stdDevX + stdDevY) / 2.0D;
    }

    public double getStdDevX() {
        return stdDevX;
    }

    public double getStdDevY() {
        return stdDevY;
    }

    public int getDataPointCount() {
        return dataPointCount;
    }
}
