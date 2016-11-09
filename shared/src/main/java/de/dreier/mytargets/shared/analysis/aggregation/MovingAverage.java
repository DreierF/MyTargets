package de.dreier.mytargets.shared.analysis.aggregation;

import android.graphics.PointF;

import junit.framework.Assert;

import java.util.ArrayList;

public abstract class MovingAverage extends Average {

    protected final ArrayList<PointF> allData;
    private int nWindow;

    public MovingAverage(int windowSize) {
        Assert.assertTrue(windowSize > 0);
        allData = new ArrayList<>();
        nWindow = windowSize;
    }

    private void prune() {
        while(data.size() > nWindow) {
            data.remove(0);
        }

    }

    public void add(float var1, float var2) {
        super.add(var1, var2);
        allData.add(new PointF(var1, var2));
        prune();
    }

    public int getNWindow() {
        return nWindow;
    }

    public void reset() {
        super.reset();
        allData.clear();
    }

    public void setNWindow(int var1) {
        this.nWindow = var1;
        super.reset();

        for (PointF point : allData) {
            super.add(point.x, point.y);
            prune();
        }
    }
}