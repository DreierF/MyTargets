package de.dreier.mytargets.shared.analysis.aggregation;

import android.graphics.PointF;

import java.util.ArrayList;

public class Cluster {
    public final ArrayList<PointF> points = new ArrayList<>();
    private final PointF centerOfGroup = new PointF();
    private final int totalNumber;
    private boolean isDirty;
    private double weight = 0.0;

    public Cluster(PointF paramPointF, int paramInt) {
        points.add(paramPointF);
        totalNumber = paramInt;
        isDirty = true;
    }

    private void compute() {
        if (!isDirty) {
            return;
        }
        double d2 = 0.0;
        double d1 = 0.0;
        for (PointF localPointF : points) {
            d2 += localPointF.x;
            d1 += localPointF.y;
        }
        d2 /= points.size();
        d1 /= points.size();
        centerOfGroup.set((float) d2, (float) d1);
        weight = (points.size() / totalNumber);
        isDirty = false;
    }

    public void add(PointF paramPointF) {
        points.add(paramPointF);
        isDirty = true;
    }

    public PointF getCenterOfGroup() {
        compute();
        return centerOfGroup;
    }

    public int getSize() {
        return points.size();
    }

    public double getWeight() {
        return weight;
    }

    @Override
    public String toString() {
        return "Cluster{" +
                "points=" + points +
                ", centerOfGroup=" + centerOfGroup +
                ", totalNumber=" + totalNumber +
                ", isDirty=" + isDirty +
                ", weight=" + weight +
                '}';
    }
}
