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

import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

import de.dreier.mytargets.features.training.input.opencv.ContourUtils;
import de.dreier.mytargets.features.training.input.opencv.LineUtils;
import de.dreier.mytargets.features.training.input.opencv.detection.target.TargetZone;

public class ContourHoughLineBasedArrowDetectionStrategy implements IArrowDetectionStrategy {

    @Override
    public List<Point> detectArrows(Mat image, List<TargetZone> target, int arrows, boolean fromLeftViewpoint) {
        // Convert to gray scale and get contours
//        List<MatOfPoint> contours = getContour(image);
//
//        // For all found contours ...
//        List<Line> potentialArrows = getLinesFromContour(image, contours);

        List<Line> potentialArrows = LineUtils.getLines(image, 80, 30, 10);

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
        ContourUtils.filterCircleContours(target, potentialArrows, image.width());

        List<List<Line>> buckets = groupLinesToBuckets(potentialArrows);
      //  Imgproc.cvtColor(image, image, Imgproc.COLOR_GRAY2RGB, 4);
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

    /**
     * Group lines that lie on the same straight
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
}
