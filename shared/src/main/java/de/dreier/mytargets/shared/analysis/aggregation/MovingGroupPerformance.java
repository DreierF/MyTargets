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

import junit.framework.Assert;

public class MovingGroupPerformance {
    private static final double DIRVAR_COEF = 0.2D;

    private static final double RELDEV_COEF = 1.7D;
    private static final double RELGRP_COEF = 2.0D;
    protected final int ident;
    protected final PointF lastPos;
    private final long id;
    protected int alertLevel;
    protected boolean dirty;
    protected SimpleMovingAverage myRelativePos;
    protected SimpleMovingAverage otherAverages;

    public MovingGroupPerformance(long var1, int var3, int var4, int var5) {
        this.dirty = true;
        this.alertLevel = 0;

        Assert.assertTrue(var1 != -1L);
        Assert.assertTrue(var4 > 0);
        Assert.assertTrue(var5 > 0);
        this.lastPos = new PointF();
        this.ident = var3;
        this.id = var1;
        this.otherAverages = new SimpleMovingAverage(var5 * var4);
        this.myRelativePos = new SimpleMovingAverage(var4);
        this.reset(var4);
    }

    public void add(long var1, float var3, float var4) {
        this.dirty = true;
        if (this.id == var1) {
            PointF var5 = this.otherAverages.getAverage();
            this.myRelativePos.add(var3 - var5.x, var4 - var5.y);
            this.lastPos.set(var3, var4);
            this.compute();
        } else {
            this.otherAverages.add(var3, var4);
        }
    }

    public int compare(MovingGroupPerformance var1) {
        double var2 = (double) this.getAlertLevel();
        double var4 = (double) var1.getAlertLevel();
        return var2 > 0.0D && var2 > var4 ? 1 : (var4 > 0.0D && var2 < var4 ? -1 : 0);
    }

    protected void compute() {
        if (this.dirty) {
            this.dirty = false;
            if (this.myRelativePos.size() < 3 || this.otherAverages.size() < 3) {
                this.alertLevel = 0;
                return;
            }

            double var3 = 0.0D;
            double var1 = var3;
            if (DIRVAR_COEF > 0.0D) {
                var1 = var3;
                if (this.myRelativePos.getDirectionalVariance() > 0.0D) {
                    var1 = DIRVAR_COEF / this.myRelativePos.getDirectionalVariance();
                }
            }

            double var5 = 0.0D;
            var3 = var5;
            if (this.otherAverages.getStdDevX() > 0.0D) {
                var3 = var5;
                if (RELDEV_COEF > 0.0D) {
                    var3 = Math.abs((double) this.lastPos.x / this.otherAverages
                            .getStdDevX()) / RELDEV_COEF;
                }
            }

            double var7 = 0.0D;
            var5 = var7;
            if (this.otherAverages.getStdDevY() > 0.0D) {
                var5 = var7;
                if (RELDEV_COEF > 0.0D) {
                    var5 = Math.abs((double) this.lastPos.y / this.otherAverages
                            .getStdDevY()) / RELDEV_COEF;
                }
            }

            double var9 = 0.0D;
            var7 = var9;
            if (this.myRelativePos.getStdDevX() > 0.0D) {
                var7 = var9;
                if (RELGRP_COEF > 0.0D) {
                    var7 = var9;
                    if (this.otherAverages.getStdDevX() > 0.0D) {
                        var7 = this.myRelativePos.getStdDevX() / this.otherAverages
                                .getStdDevX() / RELGRP_COEF;
                    }
                }
            }

            double var11 = 0.0D;
            var9 = var11;
            if (this.myRelativePos.getStdDevY() > 0.0D) {
                var9 = var11;
                if (RELGRP_COEF > 0.0D) {
                    var9 = var11;
                    if (this.otherAverages.getStdDevY() > 0.0D) {
                        var9 = this.myRelativePos.getStdDevY() / this.otherAverages
                                .getStdDevY() / RELGRP_COEF;
                    }
                }
            }

            if ((var1 <= 1.0D || var3 <= 0.68D && var5 <= 0.68D) && var3 <= 1.0D && var5 <= 1.0D && var7 <= 1.0D && var9 <= 1.0D) {
                if (this.alertLevel > 1) {
                    this.alertLevel = 1;
                    return;
                }

                this.alertLevel = 0;
                return;
            }

            ++this.alertLevel;
            if (alertLevel > 5) {
                alertLevel = 5;
                return;
            }
        }

    }

    public int getAlertLevel() {
        this.compute();
        return this.alertLevel;
    }

    public long getId() {
        return this.id;
    }

    public int getIdent() {
        return ident;
    }

    public void reset(int var1) {
        this.myRelativePos.reset();
        this.myRelativePos.setNWindow(var1);
        this.otherAverages.reset();
        this.otherAverages.setNWindow(var1);
        this.alertLevel = 0;
        this.dirty = true;
    }
}
