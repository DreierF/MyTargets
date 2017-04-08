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

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

import de.dreier.mytargets.shared.models.Target;

public class DilationArrowDetection implements IArrowDetectionStrategy {

    private static final String TAG = "DilationArrowDetection";

    @Override
    public List<Point> detectArrows(Mat image, Target target, int arrows, boolean fromLeftViewpoint) {
        Mat hsv = new Mat();
        Imgproc.cvtColor(image, hsv, Imgproc.COLOR_BGR2HSV);
        Mat mask1 = new Mat();
        Core.inRange(hsv, new Scalar(95, 140, 160), new Scalar(105, 255, 255), mask1);
        Mat mask2 = new Mat();
        Core.inRange(hsv, new Scalar(160, 115, 190), new Scalar(180, 255, 255), mask2);
        Mat mask3 = new Mat();
        Core.inRange(hsv, new Scalar(27, 102, 190), new Scalar(29, 255, 255), mask3);

        Mat mask4 = new Mat(image.size(), CvType.CV_8U, new Scalar(255, 255, 255));
        Imgproc.circle(mask4, new Point(image.width() / 2, image.height() / 2), image.width() / 2,
                new Scalar(0, 0, 0), Core.FILLED, 8, 0);

        Core.bitwise_or(mask1, mask4, mask1);
        Core.bitwise_or(mask1, mask2, mask1);
        Core.bitwise_or(mask1, mask3, mask1);

        // noise removal
        Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(6, 6));
        Imgproc.morphologyEx(mask1, mask1, Imgproc.MORPH_DILATE, kernel);

        Mat inv = new Mat(image.size(), CvType.CV_8U, new Scalar(255,255,255));
        Core.bitwise_xor(mask1, inv, mask1);

        Mat lines = new Mat();
        int threshold = 70;
        int minLineSize = 150;
        int lineGap = 25;

        Imgproc.HoughLinesP(mask1, lines, 1, Math.PI / 180, threshold, minLineSize, lineGap);

        Log.d(TAG, "detectArrows: " + lines.size());
        List<Line> potentialArrows = new ArrayList<>();
        for (int x = 0; x < lines.rows(); x++) {
            double[] vec = lines.get(x, 0);
            double x1 = vec[0],
                    y1 = vec[1],
                    x2 = vec[2],
                    y2 = vec[3];
            Point start = new Point(x1, y1);
            Point end = new Point(x2, y2);

            //Imgproc.line(image, start, end, new Scalar(255, 0, 0), 2);

            potentialArrows.add(new Line(start, end));
        }

        List<List<Line>> buckets = groupLinesToBuckets(potentialArrows);
        //Imgproc.cvtColor(image, image, Imgproc.COLOR_GRAY2RGB, 4);
        //drawBuckets(image, buckets);
        return extractArrowPositions(buckets, arrows, fromLeftViewpoint);
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
}
