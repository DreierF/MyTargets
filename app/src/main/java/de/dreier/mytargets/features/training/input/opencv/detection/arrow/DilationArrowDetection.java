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
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

import de.dreier.mytargets.features.training.input.opencv.ColorUtils;
import de.dreier.mytargets.features.training.input.opencv.LineUtils;
import de.dreier.mytargets.shared.models.Target;

import static de.dreier.mytargets.shared.utils.Color.CERULEAN_BLUE;
import static de.dreier.mytargets.shared.utils.Color.FLAMINGO_RED;
import static de.dreier.mytargets.shared.utils.Color.LEMON_YELLOW;

public class DilationArrowDetection implements IArrowDetectionStrategy {

    @Override
    public List<Point> detectArrows(Mat image, Target target, int arrows, boolean fromLeftViewpoint) {
//        Mat image = new Mat();
//        Imgproc.bilateralFilter(org, image, 5, 30, 10);

        Mat mask1 = ColorUtils.getColorMask(image, CERULEAN_BLUE, false);
        Mat mask2 = ColorUtils.getColorMask(image, FLAMINGO_RED, false);
        Mat mask3 = ColorUtils.getColorMask(image, LEMON_YELLOW, false);
        Mat mask4 = new Mat(image.size(), CvType.CV_8U, new Scalar(255, 255, 255));
        Imgproc.circle(mask4, new Point(image.width() / 2, image.height() / 2), image.width() / 2,
                new Scalar(0, 0, 0), Core.FILLED, 8, 0);
        Core.bitwise_or(mask1, mask4, mask1);

        Core.bitwise_or(mask1, mask2, mask1);
        Core.bitwise_or(mask1, mask3, mask1);

        // noise removal
//        Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(8, 8));
//        Imgproc.morphologyEx(mask1, mask1, Imgproc.MORPH_DILATE, kernel);

        Mat inv = new Mat(image.size(), CvType.CV_8U, new Scalar(255,255,255));
        Core.bitwise_xor(mask1, inv, mask1);

        List<Line> potentialArrows = LineUtils.getLines(mask1, 70, 50, 10);

//        Imgproc.Canny(mask1, mask1, 80, 100);
//        Imgproc.cvtColor(mask1, image, Imgproc.COLOR_GRAY2RGB, 4);
//        LineUtils.draw(image, potentialArrows);

        List<List<Line>> buckets = groupLinesToBuckets(potentialArrows);
//        Imgproc.cvtColor(mask1, image, Imgproc.COLOR_GRAY2RGB, 4);
//        drawBuckets(image, buckets);
        return extractArrowPositions(buckets, arrows, fromLeftViewpoint);
//        return Collections.emptyList();
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
