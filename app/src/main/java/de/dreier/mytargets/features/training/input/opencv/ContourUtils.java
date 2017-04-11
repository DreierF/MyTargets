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
import android.util.Log;

import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

import de.dreier.mytargets.features.training.input.opencv.detection.arrow.Line;
import de.dreier.mytargets.features.training.input.opencv.detection.target.TargetZone;

public class ContourUtils {
    @NonNull
    private static List<MatOfPoint> getContour(Mat bin1) {
        List<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();
        Imgproc.cvtColor(bin1, bin1, Imgproc.COLOR_BGR2GRAY);
        Imgproc.GaussianBlur(bin1, bin1, new Size(9, 9), 2, 2);
        Imgproc.Canny(bin1, bin1, 80, 100);
        Imgproc.findContours(bin1, contours, hierarchy, Imgproc.RETR_TREE,
                Imgproc.CHAIN_APPROX_SIMPLE, new Point(0, 0));
        return contours;
    }

    @NonNull
    private static List<Line> getLinesFromContour(Mat bin1, List<MatOfPoint> contours) {
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

    public static void filterCircleContours(List<TargetZone> target, List<Line> potentialArrows, float width) {
        float rad = width * 0.5f;
        for (TargetZone radius : target) {
            float radius2 = radius.model.zone.radius * width;
            Log.d("radius", String.valueOf(radius2));
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
}
