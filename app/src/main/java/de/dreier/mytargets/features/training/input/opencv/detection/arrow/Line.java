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

package de.dreier.mytargets.features.training.input.opencv.detection.arrow;

import org.opencv.core.Point;

public class Line {
    public Point point1, point2;
    public double[] n;
    public double d;
    public boolean isInBucket = false;

    public Line(Point point1, Point point2) {
        if (point1.x < point2.x) {
            this.point1 = point1;
            this.point2 = point2;
        } else {
            this.point1 = point2;
            this.point2 = point1;
        }
        double n1 = point1.y - point2.y;
        double n2 = point2.x - point1.x;
        double sc = 1 / Math.sqrt(n1 * n1 + n2 * n2);
        if (n2 < 0) {
            n = new double[]{-n1 * sc, -n2 * sc};
        } else {
            n = new double[]{n1 * sc, n2 * sc};
        }
        recalcDistance();
    }

    public boolean sameOrientation(Line line2) {
        return similar(n[0], line2.n[0], 0.08) &&
                similar(n[1], line2.n[1], 0.08) &&
                similar(d, line2.d, 5);
    }

    private boolean similar(double v1, double v2, double threshold) {
        return v1 + threshold > v2 && v1 - threshold < v2;
    }

    @Override
    public String toString() {
        return "line " + d + " (" + n[0] + "," + n[1] + ") : (" + point1.x + "," + point1.y +
                ")-(" + point2.x + "," + point2.y + ")";
    }


    void recalcDistance() {
        d = point2.x * n[0] + point2.y * n[1];
    }

    public boolean nearOf(Point p) {
        return minimumDistance(p) < 50;
    }

    public boolean isInCircle(Point center, double radius) {
        return distance(point1, center) < radius || distance(point2, center) < radius;
    }

    public double minimumDistance(Point p) {
        // Return minimum distance between line segment vw and point p
        double l2 = sqr(point1.x - point2.x) +
                sqr(point1.y - point2.y);  // i.e. |w-v|^2 -  avoid a sqrt
        if (l2 == 0.0) {
            return distance(p, point1);   // v == w case
        }
        // Consider the line extending the segment, parameterized as v + t (w - v).
        // We find projection of point p onto the line.
        // It falls where t = [(p-v) . (w-v)] / |w-v|^2
        double t = ((p.x - point1.x) * (point2.x - point1.x) +
                (p.y - point1.y) * (point2.y - point1.y)) / l2;
        if (t < 0.0) {
            return distance(p, point1);       // Beyond the 'v' end of the segment
        } else if (t > 1.0) {
            return distance(p, point2);  // Beyond the 'w' end of the segment
        }
        Point projection = new Point(point1.x + t * (point2.x - point1.x),
                point1.y + t * (point2.y - point1.y));
        return distance(p, projection);
    }

    private double sqr(double v) {
        return v * v;
    }

    private double distance(Point p1, Point p2) {
        double n1 = p1.y - p2.y;
        double n2 = p1.x - p2.x;
        return Math.sqrt(n1 * n1 + n2 * n2);
    }

    public double len() {
        return distance(point1, point2);
    }

    public Point pointCloserTo(Point center) {
        return null;
    }
}
