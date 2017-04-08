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

import android.support.annotation.NonNull;
import android.support.v4.util.Pair;
import android.util.Log;

import com.annimon.stream.Stream;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

import de.dreier.mytargets.shared.models.SelectableZone;
import de.dreier.mytargets.shared.models.Target;

public class ContourHoughLineBasedArrowDetectionStrategy implements IArrowDetectionStrategy {

    @Override
    public List<Point> detectArrows(Mat image, Target target, int arrows, boolean fromLeftViewpoint) {
        // Convert to gray scale and get contours
        List<MatOfPoint> contours = getContour(image);

        // For all found contours ...
        Mat out = new Mat(image.size(), CvType.CV_8UC3, new Scalar(0, 0, 0));
        List<Line> potentialArrows = getLinesFromContour(image, contours);

        // Remove all lines that lie outside the target face
        float rad = image.width() / 2.0f;
        Point center = new Point(rad, rad);
        for (int j = 0; j < potentialArrows.size(); j++) {
            Line line = potentialArrows.get(j);
            if (!line.isInCircle(center, rad)) {
                line.isInBucket = true;
            }
        }
        // Mark lines that are likely to represent parts of the circle
        filterCircleContours(target, out, potentialArrows, rad, center, image.width());

        List<List<Line>> buckets = groupLinesToBuckets(potentialArrows);
        Imgproc.cvtColor(image, image, Imgproc.COLOR_GRAY2RGB, 4);
        drawBuckets(image, buckets);
        return extractArrowPositions(buckets, arrows, fromLeftViewpoint);
    }

    private List<Point> extractArrowPositions(List<List<Line>> buckets, int arrows, boolean fromLeftViewpoint) {
        return Stream.of(buckets)
                .filterNot(List::isEmpty)
                .map(b -> Stream.of(b)
                        .flatMap(l -> Stream.of(l.point1, l.point2))
                        .reduce(new Pair<Point, Point>(null, null), (oldPair, value) -> {
                            if (oldPair.first == null) {
                                return new Pair<>(value, value);
                            } else if (value.x < oldPair.first.x) {
                                return new Pair<>(value, oldPair.second);
                            } else if (value.x > oldPair.second.x) {
                                return new Pair<>(oldPair.first, value);
                            } else {
                                return oldPair;
                            }
                        }))
                .sortBy(pair -> pair.first.x - pair.second.x)
                .limit(arrows)
                .map(p -> fromLeftViewpoint ? p.first : p.second)
                .toList();
    }

    @NonNull
    private List<MatOfPoint> getContour(Mat bin1) {
        List<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();
        Imgproc.cvtColor(bin1, bin1, Imgproc.COLOR_BGR2GRAY);
        Imgproc.Canny(bin1, bin1, 80, 100);
        Imgproc.findContours(bin1, contours, hierarchy, Imgproc.RETR_TREE,
                Imgproc.CHAIN_APPROX_SIMPLE, new Point(0, 0));
        return contours;
    }

    @NonNull
    private List<Line> getLinesFromContour(Mat bin1, List<MatOfPoint> contours) {
        List<Line> potentialArrows = new ArrayList<>();
        MatOfPoint2f approx2f = new MatOfPoint2f();
        for (int i = 0; i < contours.size(); i++) {
            MatOfPoint contour = contours.get(i);
            // Sort out contours that are too small

            // Test if contour is big enough to be relevant
            Rect rect = Imgproc.boundingRect(contour);
            if (rect.width * rect.width + rect.height * rect.height < 200) {
                continue;
            }
            //Imgproc.drawContours(out, contours, i,
            //        new Scalar(Math.random() * 255, Math.random() * 255, Math.random() * 255), 1);

            // Approximate contour with accuracy proportional to the contour perimeter
            MatOfPoint2f contour2f = new MatOfPoint2f();
            contour2f.fromList(contour.toList());
            double epsilon = Imgproc.arcLength(contour2f, true) * 0.001;
            Imgproc.approxPolyDP(contour2f, approx2f, epsilon, true);

            // Convert to point array
            MatOfPoint approx = new MatOfPoint();
            Point[] lp = approx2f.toArray();
            approx.fromArray(lp);

            //ArrayList<MatOfPoint> list = new ArrayList<>();
            //list.add(approx);
            //Imgproc.polylines(out, list, false, new Scalar(255, 255, 255));

            // Get contour lines that are long enough and add them to the list
            for (int j = 1; j < lp.length; j++) {
                double dx = lp[j - 1].x - lp[j].x;
                double dy = lp[j - 1].y - lp[j].y;
                if (dx * dx + dy * dy > bin1.width() * 0.15) {
                    potentialArrows.add(new Line(lp[j - 1], lp[j]));
                }
            }
        }
        return potentialArrows;
    }

    private void filterCircleContours(Target target, Mat out, List<Line> potentialArrows, float rad, Point center, float width) {
        for (SelectableZone radius : target.getSelectableZoneList(0)) {
            float radius2 = radius.zone.radius * width;
            Log.d("radius", String.valueOf(radius2));
            Imgproc.circle(out, center, (int) radius2,
                    new Scalar(255, 255, 255));
            Line circle = new Line(new Point(0, 0), new Point(1, 0));
            for (int i = 0; i < 360; i++) {
                double n1 = Math.sin(i * Math.PI / 180.0);
                double n2 = Math.cos(i * Math.PI / 180.0);
                circle.point2 = new Point(n2 * radius2 + rad, n1 * radius2 + rad); //TODO
                if (n2 < 0) {
                    circle.n = new double[]{-n1, -n2};
                } else {
                    circle.n = new double[]{n1, n2};
                }
                circle.recalcDistance();
                for (int j = 0; j < potentialArrows.size(); j++) {
                    Line line = potentialArrows.get(j);

                    if (!line.isInBucket && circle.sameOrientation(line)) {
                        //double value = line.minimumDistance(circle.point2);

                        if (line.nearOf(circle.point2)) {
                            //bucket.add(line);
                            line.isInBucket = true;
                        }/* else if (value < 140) {
                                Log.d("" + i,
                                        "circle " + circle.d + " (" + circle.n[0] + "," +
                                                circle.n[1] + ") : (" + circle.point2.x +
                                                "," + circle.point2.y + ") " + line.toString());
                            //    Log.d("" + j, String.valueOf(value)+ " "+radius+" ("+circle.point2.x+","+circle.point2.y+") "+line );
                        }*/
                    }
                }

            }
        }
    }

    /**
     * Group lines that lie on the same straight
     *
     * @param potentialArrows
     * @return
     */
    @NonNull
    private List<List<Line>> groupLinesToBuckets(List<Line> potentialArrows) {
        List<List<Line>> buckets = new ArrayList<>();
        ArrayList<Line> bucket = new ArrayList<>();
        buckets.add(bucket);
        for (int i = 0; i < potentialArrows.size(); i++) {
            Line line1 = potentialArrows.get(i);
            if (line1.isInBucket) {
                continue;
            }
            bucket = new ArrayList<>();
            buckets.add(bucket);
            bucket.add(line1);
            line1.isInBucket = true;
            for (int j = i + 1; j < potentialArrows.size(); j++) {
                Line line2 = potentialArrows.get(j);
                if (line1.sameOrientation(line2) && !line2.isInBucket) {
                    bucket.add(line2);
                    line2.isInBucket = true;
                }
            }
        }
        return buckets;
    }

    /**
     * Draw all lines whereby lines in the same buckets are drawn with the same color
     *
     * @param out
     * @param buckets
     */
    private void drawBuckets(Mat out, List<List<Line>> buckets) {
        int i = 0;
        for (List<Line> group : buckets) {
            Scalar color = new Scalar(Math.random() * 255, Math.random() * 255,
                    Math.random() * 255);
            for (Line line : group) {
                Log.d("bucket " + i, line.toString());
                Imgproc.line(out, line.point1, line.point2, color);
            }
            i++;
        }
    }

    private void detectLinesWithHough(Mat bin1) {
        Imgproc.Canny(bin1, bin1, 80, 100);
        int threshold = 80;
        int minLineSize = 30;
        int lineGap = 10;
        Mat lines = new Mat();
        Imgproc.HoughLinesP(bin1, lines, 1, Math.PI / 180, threshold, minLineSize, lineGap);
        Imgproc.cvtColor(bin1, bin1, Imgproc.COLOR_GRAY2RGB);

        for (int x = 0; x < lines.cols(); x++) {
            double[] vec = lines.get(0, x);
            double x1 = vec[0],
                    y1 = vec[1],
                    x2 = vec[2],
                    y2 = vec[3];
            Point start = new Point(x1, y1);
            Point end = new Point(x2, y2);

            Imgproc.line(bin1, start, end, new Scalar(255, 0, 0), 3);
            Log.d("apply", "   found line (" + x1 + "," + y1 + ")-(" + x2 + "," + y2 + ")");
        }
    }
}
