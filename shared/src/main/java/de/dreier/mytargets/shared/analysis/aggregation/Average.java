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
import java.util.Iterator;

public abstract class Average {

    protected final PointF average = new PointF(0.0F, 0.0F);
    protected final ArrayList<PointF> data = new ArrayList<>();
    protected final RectF nonUniformStdDev = new RectF(-1.0F, -1.0F, -1.0F, -1.0F);
    protected double centerStdDev = -1.0D;
    protected double dirVariance = -1.0D;
    protected double stdDevX = -1.0D;
    protected double stdDevY = -1.0D;
    private boolean dirty = false;

    public Average() {
    }

    private void compute() {
        if (this.dirty) {
            if (this.data.size() == 0) {
                this.average.set(0.0F, 0.0F);
                this.nonUniformStdDev.set(-1.0F, -1.0F, -1.0F, -1.0F);
                this.centerStdDev = -1.0D;
                this.stdDevX = -1.0D;
                this.stdDevY = -1.0D;
                this.dirVariance = -1.0D;
            } else {
                this.computeAverage();
                this.computeNonUniformStdDeviations();
                this.computeCenterStdDev();
                this.computeStdDevX();
                this.computeStdDevY();
                this.computeDirectionalVariance();
            }

            this.dirty = false;
        }
    }

    private void computeCenterStdDev() {
        int var5 = this.data.size();
        double var3 = 0.0D;
        double var1 = 0.0D;

        PointF var7;
        for (Iterator var6 = this.data.iterator(); var6
                .hasNext(); var1 += (double) (var7.y * var7.y)) {
            var7 = (PointF) var6.next();
            var3 += (double) (var7.x * var7.x);
        }

        this.centerStdDev = (Math.sqrt(var3 / (double) var5) + Math
                .sqrt(var1 / (double) var5)) / 2.0D;
    }

    private void computeDirectionalVariance() {
        int var7 = this.data.size();
        double var3 = 0.0D;
        double var1 = 0.0D;

        double var5;
        for (Iterator var8 = this.data.iterator(); var8.hasNext(); var1 += Math.sin(var5)) {
            PointF var9 = (PointF) var8.next();
            var5 = Math.atan2((double) var9.x, (double) var9.y);
            var3 += Math.cos(var5);
        }

        this.dirVariance = 1.0D - Math.sqrt(var3 * var3 + var1 * var1) / (double) var7;
    }

    private void computeNonUniformStdDeviations() {
        int var13 = 0;
        int var14 = 0;
        int var11 = 0;
        int var12 = 0;
        double var5 = 0.0D;
        double var7 = 0.0D;
        double var1 = 0.0D;
        double var3 = 0.0D;

        for (PointF point : data) {
            double var9 = (double) (point.x - this.average.x);
            if (var9 < 0.0D) {
                var5 += var9 * var9;
                ++var13;
            } else {
                var7 += var9 * var9;
                ++var14;
            }

            var9 = (double) (point.y - this.average.y);
            if (var9 >= 0.0D) {
                var1 += var9 * var9;
                ++var11;
            } else {
                var3 += var9 * var9;
                ++var12;
            }
        }

        this.nonUniformStdDev.set((float) Math.sqrt(var5 / (double) var13),
                (float) Math.sqrt(var1 / (double) var11), (float) Math.sqrt(var7 / (double) var14),
                (float) Math.sqrt(var3 / (double) var12));
    }

    private void computeStdDevX() {
        int var5 = this.data.size();
        double var1 = 0.0D;

        double var3;
        for (Iterator var6 = this.data.iterator(); var6.hasNext(); var1 += var3 * var3) {
            var3 = (double) (((PointF) var6.next()).x - this.average.x);
        }

        this.stdDevX = Math.sqrt(var1 / (double) var5);
    }

    private void computeStdDevY() {
        int var5 = this.data.size();
        double var1 = 0.0D;

        double var3;
        for (Iterator var6 = this.data.iterator(); var6.hasNext(); var1 += var3 * var3) {
            var3 = (double) (((PointF) var6.next()).y - this.average.y);
        }

        this.stdDevY = Math.sqrt(var1 / (double) var5);
    }

    public void add(float var1, float var2) {
        this.data.add(new PointF(var1, var2));
        this.dirty = true;
    }

    protected void computeAverage() {
        int var5 = this.data.size();
        double var3 = 0.0D;
        double var1 = 0.0D;

        PointF var7;
        for (Iterator var6 = this.data.iterator(); var6.hasNext(); var1 += (double) var7.y) {
            var7 = (PointF) var6.next();
            var3 += (double) var7.x;
        }

        this.average.set((float) (var3 / (double) var5), (float) (var1 / (double) var5));
    }

    public PointF getAverage() {
        this.compute();
        return this.average;
    }

    public double getDirectionalVariance() {
        this.compute();
        return this.dirVariance;
    }

    public double getISV() {
        this.compute();
        return this.getISV(this.getStdDev());
    }

    public double getISV(double var1) {
        this.compute();
        return (Math.log(var1) - 2.57D) / -0.027D;
    }

    public RectF getNonUniformStdDev() {
        this.compute();
        return this.nonUniformStdDev;
    }

    public double getStdDev() {
        this.compute();
        return (this.stdDevX + this.stdDevY) / 2.0D;
    }

    public double getStdDevX() {
        this.compute();
        return this.stdDevX;
    }

    public double getStdDevY() {
        this.compute();
        return this.stdDevY;
    }

    public void reset() {
        this.data.clear();
        this.dirty = true;
        this.compute();
    }

    public int size() {
        return this.data.size();
    }
}
