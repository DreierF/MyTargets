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

package de.dreier.mytargets.features.training.input.opencv;

import android.support.annotation.NonNull;

import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

import de.dreier.mytargets.features.training.input.opencv.detection.arrow.Line;
import timber.log.Timber;

public class LineUtils {
    @NonNull
    public static List<Line> getLines(Mat image, int threshold, int minLineSize, int lineGap) {
        Mat temp = new Mat();
        Imgproc.Canny(image, temp, 80, 100);

        Mat lines = new Mat();
        Imgproc.HoughLinesP(temp, lines, 1, Math.PI / 180, threshold, minLineSize, lineGap);

        List<Line> list = new ArrayList<>();
        for (int x = 0; x < lines.rows(); x++) {
            double[] vec = lines.get(x, 0);
            double x1 = vec[0],
                    y1 = vec[1],
                    x2 = vec[2],
                    y2 = vec[3];
            Point start = new Point(x1, y1);
            Point end = new Point(x2, y2);

            list.add(new Line(start, end));
            Timber.d("getLines: (%f, %f) - (%f, %f)", x1, y1, x2, y2);
        }
        return list;
    }

    public static void draw(Mat image, List<Line> list) {
        for (Line line : list) {
            Imgproc.line(image, line.point1, line.point2, new Scalar(255, 0, 0), 3);
        }
    }
}
