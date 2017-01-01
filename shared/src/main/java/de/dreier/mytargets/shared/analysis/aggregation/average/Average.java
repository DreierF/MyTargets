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

package de.dreier.mytargets.shared.analysis.aggregation.average;

import android.graphics.PointF;
import android.graphics.RectF;

import java.util.List;

import de.dreier.mytargets.shared.models.db.Shot;

public class Average {
    int dataPointCount;
    PointF average = new PointF(0.0F, 0.0F);
    PointF weightedAverage = new PointF(0.0F, 0.0F);
    RectF nonUniformStdDev = new RectF(-1.0F, -1.0F, -1.0F, -1.0F);
    double stdDevX = -1.0D;
    double stdDevY = -1.0D;

    public void computeAll(List<Shot> shots) {
        dataPointCount = shots.size();
        computeAverage(shots);
        computeNonUniformStdDeviations(shots);
        computeStdDevX(shots);
        computeStdDevY(shots);
        computeWeightedAverage(shots);
    }

    private void computeWeightedAverage(List<Shot> data) {
        double sumX = 0.0D;
        double sumY = 0.0D;
        int i = 0;

        for (Shot point : data) {
            ++i;
            sumX += (double) ((float) i * point.x);
            sumY += (double) ((float) i * point.y);
        }

        i = (i + 1) * i / 2;
        weightedAverage.set((float) (sumX / (double) i), (float) (sumY / (double) i));
    }

    private void computeNonUniformStdDeviations(List<Shot> data) {
        int negCountX = 0;
        int posCountX = 0;
        int posCountY = 0;
        int negCountY = 0;
        double negSquaredXError = 0.0D;
        double posSquaredXError = 0.0D;
        double posSquaredYError = 0.0D;
        double negSquaredYError = 0.0D;

        for (Shot point : data) {
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
                (float) Math.sqrt(posSquaredYError / (double) posCountY),
                (float) Math.sqrt(posSquaredXError / (double) posCountX),
                (float) Math.sqrt(negSquaredYError / (double) negCountY));
    }

    public void computeStdDevX(List<Shot> data) {
        double sumSquaredXError = 0.0D;

        for (Shot point : data) {
            double error = (double) (point.x - average.x);
            sumSquaredXError += error * error;
        }

        stdDevX = Math.sqrt(sumSquaredXError / (double) data.size());
    }

    public void computeStdDevY(List<Shot> data) {
        double sumSquaredYError = 0.0D;

        for (Shot point : data) {
            double error = (double) (point.y - average.y);
            sumSquaredYError += error * error;
        }

        stdDevY = Math.sqrt(sumSquaredYError / (double) data.size());
    }

    public void computeAverage(List<Shot> data) {
        double sumX = 0.0D;
        double sumY = 0.0D;

        for (Shot point : data) {
            sumX += (double) point.x;
            sumY += (double) point.y;
        }

        average.set((float) (sumX / (double) data.size()), (float) (sumY / (double) data.size()));
    }

    public PointF getAverage() {
        return average;
    }

    public RectF getNonUniformStdDev() {
        return nonUniformStdDev;
    }

    public double getStdDev() {
        return (stdDevX + stdDevY) / 2.0D;
    }

    public int getDataPointCount() {
        return dataPointCount;
    }
}
