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

package de.dreier.mytargets.features.training.input.opencv.detection.target;


import org.opencv.core.Mat;

import de.dreier.mytargets.features.training.input.opencv.transformation.ITransformation;
import de.dreier.mytargets.shared.models.Target;

public class WatershedTargetDetection implements ITargetDetectionStrategy {

    @Override
    public ITransformation detectTargetFace(Mat mRgba, Target target) {
        return null;
    }

    /* TODO's:
    * - Try watershed to get a better image segmentation and restore the original bounds */

    /////////////////////// WATERSHED /////////////////////////
//
//    public class WatershedSegmenter {
//        public Mat markers;
//
//        public void setMarkers(Mat markerImage) {
//            markerImage.convertTo(markers, CvType.CV_32S);
//        }
//
//        public Mat process(Mat image) {
//            Imgproc.watershed(image, markers);
//            markers.convertTo(markers, CvType.CV_8U);
//            return markers;
//        }
//    }

    //http://docs.opencv.org/master/d3/db4/tutorial_py_watershed.html
       /*Mat bg = new Mat(mRgba.size(),CvType.CV_8U);
        Imgproc.dilate(mRgba,bg,new Mat(),new Point(-1,-1),3);
        Imgproc.threshold(bg, bg, 1, 128, Imgproc.THRESH_BINARY_INV);

        Mat markers = new Mat(mRgba.size(),CvType.CV_8U, new Scalar(0));
        Core.add(fg, bg, markers);
        markers = Imgproc.connectedComponents(sure_bg, );

        WatershedSegmenter segmenter = new WatershedSegmenter();
        segmenter.setMarkers(markers);
        Mat result = segmenter.process(mRgba);
        result.copyTo(result);*/
    //Imgproc.Canny(bin, bin, 80, 100);


    // sure background area
    //Mat sure_bg = new Mat();

    // Finding sure foreground area
        /*Mat dist_transform = new Mat();
        Imgproc.distanceTransform(opening, dist_transform, Imgproc.DIST_L2, 5);
        Mat sure_fg = new Mat();
        Imgproc.threshold(dist_transform, sure_fg, 50, 255, 0);

        // Finding unknown region
        Core.subtract(sure_bg, sure_fg, mRgba, new Mat(), CvType.CV_8UC1);*/
    //Mat fg = new Mat(mRgba.size(),CvType.CV_8U);


    ///////////////////////// DETECT with feature detection ///////////////
    /*
    private TargetDetectionFilter mTargetDetector;
    private Mat getCornersWithFeatureDetection(Mat mRgba, Mat template) {
        FeatureDetector mFeatureDetector = FeatureDetector.create(FeatureDetector.FAST);
        DescriptorExtractor mDescriptorExtractor = DescriptorExtractor.create(
                DescriptorExtractor.ORB);
        DescriptorMatcher mDescriptorMatcher = DescriptorMatcher
                .create(DescriptorMatcher.BRUTEFORCE_HAMMINGLUT);


        long s0 = System.currentTimeMillis();

        // Get features
        MatOfKeyPoint mTemplateKeypoints = new MatOfKeyPoint();
        mFeatureDetector.detect(template, mTemplateKeypoints);

        // Get template descriptors
        Mat mTemplateDescriptors = new Mat();
        mDescriptorExtractor.compute(template, mTemplateKeypoints, mTemplateDescriptors);
        long s1 = System.currentTimeMillis();
        Log.d("apply", "preprocess template " + (s1 - s0));

        long t0 = System.currentTimeMillis();
        Mat thresholdImage;
        thresholdImage = mRgba.clone();
        long t1 = System.currentTimeMillis();
        Log.d("apply", "loading " + (t1 - t0));

        long t2 = System.currentTimeMillis();
        Log.d("apply", "canny " + (t2 - t1));

        long t5 = System.currentTimeMillis();

        // Get scene key points
        MatOfKeyPoint mSceneKeypoints = new MatOfKeyPoint();
        mFeatureDetector.detect(thresholdImage, mSceneKeypoints);
        long t6 = System.currentTimeMillis();
        Log.d("apply", "extracted " + mSceneKeypoints.size() + " in " + (t6 - t5) + "ms");

        // Extract descriptors
        long t7 = System.currentTimeMillis();
        Mat mSceneDescriptors = new Mat();
        mDescriptorExtractor.compute(thresholdImage, mSceneKeypoints, mSceneDescriptors);
        long t8 = System.currentTimeMillis();
        Log.d("apply", "descriptor " + mSceneDescriptors.size() + " in " + (t8 - t7) + "ms");

        // Matching
        long t9 = System.currentTimeMillis();
        MatOfDMatch mMatches = new MatOfDMatch();
        mDescriptorMatcher.match(mSceneDescriptors, mTemplateDescriptors, mMatches);
        long t10 = System.currentTimeMillis();

        Log.d("apply", "matches " + mMatches.toList().size() + " in " + (t10 - t9) + "ms");

        //Features2d.drawMatches(thresholdImage, mSceneKeypoints, template, mTemplateKeypoints,
        //        mMatches, mRgba);

        Mat corners = findSceneCorners(mSceneKeypoints, mTemplateKeypoints, mMatches, template);
        long t11 = System.currentTimeMillis();
        Log.d("apply", "find corners " + (corners == null ? "null" : corners.size()) + " in " +
                (t11 - t10) + "ms");
        return corners;
    }*/
    /*private Mat findSceneCorners(MatOfKeyPoint mSceneKeypoints, MatOfKeyPoint mReferenceKeypoints, MatOfDMatch mMatches, Mat template) {
        List<DMatch> matchesList = mMatches.toList();
        if (matchesList.size() < 4) {
// There are too few matches to find the homography.
            return null;
        }
        List<KeyPoint> referenceKeypointsList =
                mReferenceKeypoints.toList();
        List<KeyPoint> sceneKeypointsList =
                mSceneKeypoints.toList();
// Calculate the max and min distances between keypoints.
        double maxDist = 0.0;
        double minDist = Double.MAX_VALUE;
        double sumDist = 0.0;
        for (DMatch match : matchesList) {
            double dist = match.distance;
            if (dist < minDist) {
                minDist = dist;
            }
            if (dist > maxDist) {
                maxDist = dist;
            }
            sumDist += dist;
        }
        // The thresholds for minDist are chosen subjectively
        // based on testing. The unit is not related to pixel
        // distances; it is related to the number of failed tests
        // for similarity between the matched descriptors.
            *//*if (minDist > 50.0) {
                // The target is completely lost.
                // Discard any previously found corners.
                mSceneCorners.create(0, 0, mSceneCorners.type());
                return;
            } else if (minDist > 25.0) {
                // The target is lost but maybe it is still close.
                // Keep any previously found corners.
                return;
            }*//*

        // Identify "good" keypoints based on match distance.
        ArrayList<Point> goodReferencePointsList =
                new ArrayList<>();
        ArrayList<Point> goodScenePointsList =
                new ArrayList<>();
        double maxGoodMatchDist = ((sumDist / matchesList.size()) + minDist + minDist) / 3.0;
        for (DMatch match : matchesList) {
            if (match.distance <= maxGoodMatchDist) {
                goodReferencePointsList.add(
                        referenceKeypointsList.get(match.trainIdx).pt);
                goodScenePointsList.add(
                        sceneKeypointsList.get(match.queryIdx).pt);
            }
        }
        if (goodReferencePointsList.size() < 4 ||
                goodScenePointsList.size() < 4) {
            // There are too few good points to find the homography.
            return null;
        }
        MatOfPoint2f goodReferencePoints = new MatOfPoint2f();
        goodReferencePoints.fromList(goodReferencePointsList);

        MatOfPoint2f goodScenePoints = new MatOfPoint2f();
        goodScenePoints.fromList(goodScenePointsList);

        Mat homography = Calib3d.findHomography(
                goodReferencePoints, goodScenePoints, Calib3d.RANSAC, 5);

        return homographyToCorners(homography, template.size());
    }*/
    /*    private Mat homographyToCorners(Mat homography, Size size) {
        Mat mCandidateSceneCorners = new Mat(4, 1, CvType.CV_32FC2); //scene_corners

        List<Point> dest = new ArrayList<>();
        dest.add(new Point(0, 0));
        dest.add(new Point(size.width, 0));
        dest.add(new Point(size.width, size.height));
        dest.add(new Point(0, size.height));
        Mat mReferenceCorners = Converters.vector_Point2f_to_Mat(dest);

        Core.perspectiveTransform(mReferenceCorners,
                mCandidateSceneCorners, homography);

        MatOfPoint mIntSceneCorners = new MatOfPoint();
        mCandidateSceneCorners.convertTo(mIntSceneCorners, CvType.CV_32S);
        Mat mSceneCorners = new Mat(4, 1,
                CvType.CV_32FC2);
        //if (Imgproc.isContourConvex(mIntSceneCorners)) {
        mCandidateSceneCorners.copyTo(mSceneCorners);
        //}*//*
        return mSceneCorners;
    }*/


    /////////////////// Crop RotatedRect //////////////
                /*List<Point> dest = new ArrayList<>();
            dest.add(new Point(brect.x, brect.y));
            dest.add(new Point(brect.x + brect.width, brect.y));
            dest.add(new Point(brect.x + brect.width, brect.y + brect.height));
            dest.add(new Point(brect.x, brect.y + brect.height));*/


            /*Mat M, rotated = new Mat(), cropped = new Mat();
            // get angle and size from the bounding box
            double angle = rect.angle;
            Size rect_size = rect.size;
            // thanks to http://felix.abecassis.me/2011/10/opencv-rotation-deskewing/
            if (rect.angle < -45.) {
                angle += 90.0;
                double tmp = rect_size.width;
                rect_size.width = rect_size.height;
                rect_size.height = tmp;
            }
            // get the rotation matrix
            M = Imgproc.getRotationMatrix2D(rect.center, angle, 1.0);
            // perform the affine transformation
            Imgproc.warpAffine(mRgba, rotated, M, mRgba.size(), Imgproc.INTER_CUBIC);
            // crop the resulting image
            Imgproc.getRectSubPix(rotated, rect_size, rect.center, cropped);
            cropped.copyTo(mRgba);*/


//        List<Point> src = new ArrayList<>();
//        src.add(center1);
//        src.add(new Point(center1.x - radius1[0], center1.y));
//        src.add(new Point(center1.x, center1.y + radius1[0]));
//        src.add(new Point(center3.x + radius3[0], center3.y));
//        //src.add(new Point(center3.x, center3.y + radius3[0]));
//        Mat mSourceCorners = Converters.vector_Point2f_to_Mat(src);
//
//        int rad = Math.min(mRgba.width(), mRgba.height()) / 2;
//        List<Point> dest = new ArrayList<>();
//        dest.add(new Point(0, rad));
//        dest.add(new Point(rad, 2 * rad));
//        dest.add(new Point(rad, 2 * rad));
//        dest.add(new Point(rad + 0.33 * rad, rad));
//        //dest.add(new Point(rad, rad + 0.33 * rad));
//        Mat mReferenceCorners = Converters.vector_Point2f_to_Mat(dest);

    //   Mat homography = Imgproc.getPerspectiveTransform(mSourceCorners, mReferenceCorners);
    //Imgproc.warpPerspective(mRgba, mRgba, homography, new Size(rad * 2, rad * 2));
}
