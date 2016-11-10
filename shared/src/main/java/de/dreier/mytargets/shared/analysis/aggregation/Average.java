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

package de.dreier.mytargets.shared.analysis.aggregation;

import android.graphics.PointF;
import android.graphics.RectF;

import java.util.ArrayList;

public abstract class Average {

    protected final PointF average = new PointF(0.0F, 0.0F);
    protected final ArrayList<PointF> data = new ArrayList<>();
    protected final RectF nonUniformStdDev = new RectF(-1.0F, -1.0F, -1.0F, -1.0F);
    protected double centerStdDev = -1.0D;
    protected double dirVariance = -1.0D;
    protected double stdDevX = -1.0D;
    protected double stdDevY = -1.0D;
    private boolean dirty = false;

    private void compute() {
        if (dirty) {
            if (data.size() == 0) {
                average.set(0.0F, 0.0F);
                nonUniformStdDev.set(-1.0F, -1.0F, -1.0F, -1.0F);
                centerStdDev = -1.0D;
                stdDevX = -1.0D;
                stdDevY = -1.0D;
                dirVariance = -1.0D;
            } else {
                computeAverage();
                computeNonUniformStdDeviations();
                computeCenterStdDev();
                computeStdDevX();
                computeStdDevY();
                computeDirectionalVariance();
            }

            dirty = false;
        }
    }

    private void computeCenterStdDev() {
        double sumXSquare = 0.0D;
        double sumYSquare = 0.0D;
        for (PointF point : data) {
            sumXSquare += (double) (point.x * point.x);
            sumYSquare += (double) (point.y * point.y);
        }

        centerStdDev = (Math.sqrt(sumXSquare / (double) data.size()) + Math
                .sqrt(sumYSquare / (double) data.size())) / 2.0D;
    }

    private void computeDirectionalVariance() {
        double cosSum = 0.0D;
        double sinSum = 0.0D;

        for (PointF point : data) {
            double atan2 = Math.atan2((double) point.x, (double) point.y);
            cosSum += Math.cos(atan2);
            sinSum += Math.sin(atan2);
        }

        dirVariance = 1.0D - Math.sqrt(cosSum * cosSum + sinSum * sinSum) / (double) data.size();
    }

    private void computeNonUniformStdDeviations() {
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

    private void computeStdDevX() {
        double sumSquaredXError = 0.0D;

        for (PointF point : data) {
            double error = (double) (point.x - average.x);
            sumSquaredXError += error * error;
        }

        stdDevX = Math.sqrt(sumSquaredXError / (double) data.size());
    }

    private void computeStdDevY() {
        double sumSquaredYError = 0.0D;

        for (PointF point : data) {
            double error = (double) (point.y - average.y);
            sumSquaredYError += error * error;
        }

        stdDevY = Math.sqrt(sumSquaredYError / (double) data.size());
    }

    public void add(float var1, float var2) {
        data.add(new PointF(var1, var2));
        dirty = true;
    }

    protected void computeAverage() {
        double sumX = 0.0D;
        double sumY = 0.0D;

        for (PointF point : data) {
            sumX += (double) point.x;
            sumY += (double) point.y;
        }

        average.set((float) (sumX / (double) data.size()), (float) (sumY / (double) data.size()));
    }

    public PointF getAverage() {
        compute();
        return average;
    }

    public double getDirectionalVariance() {
        compute();
        return dirVariance;
    }

    public double getISV() {
        compute();
        return getISV(getStdDev());
    }

    public double getISV(double var1) {
        compute();
        return (Math.log(var1) - 2.57D) / -0.027D;
    }

    public RectF getNonUniformStdDev() {
        compute();
        return nonUniformStdDev;
    }

    public double getStdDev() {
        compute();
        return (stdDevX + stdDevY) / 2.0D;
    }

    public double getStdDevX() {
        compute();
        return stdDevX;
    }

    public double getStdDevY() {
        compute();
        return stdDevY;
    }

    public void reset() {
        data.clear();
        dirty = true;
        compute();
    }

    public int size() {
        return data.size();
    }
}
