/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */
package de.dreier.mytargets.shared.models.target;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.StringRes;

import java.util.ArrayList;

import de.dreier.mytargets.shared.models.Diameter;
import de.dreier.mytargets.shared.models.Dimension;

public class SpotBase extends Target {
    protected int faceRadius;
    protected Target face;
    protected int[][] facePositions;

    protected SpotBase(Context c, long id, @StringRes int nameRes) {
        super(c, id, nameRes);
    }

    @Override
    protected void draw(Canvas canvas, Rect rect) {
        for (int i = 0; i < facePositions.length; i++) {
            face.draw(canvas, getTargetBounds(rect, i));
        }
        onPostDraw(canvas, rect);
    }

    @Override
    protected float getArrowSize(Rect rect, int arrow) {
        return recalc(getTargetBounds(rect, arrow), ARROW_RADIUS);
    }

    @Override
    public String zoneToString(int zone, int arrow) {
        face.scoringStyle = scoringStyle;
        return face.zoneToString(zone, arrow);
    }

    @Override
    protected int getPointsByZone(int zone, int scoringStyle, int arrow) {
        face.scoringStyle = scoringStyle;
        return face.getPointsByZone(zone, scoringStyle, arrow);
    }

    @Override
    public int getMaxPoints() {
        face.scoringStyle = scoringStyle;
        return face.getMaxPoints();
    }

    @Override
    public float getXFromZone(int zone) {
        face.scoringStyle = scoringStyle;
        return face.getXFromZone(zone);
    }

    @Override
    public int getFillColor(int zone) {
        face.scoringStyle = scoringStyle;
        return face.getFillColor(zone);
    }

    @Override
    public int getStrokeColor(int zone) {
        face.scoringStyle = scoringStyle;
        return face.getStrokeColor(zone);
    }

    @Override
    public int getContrastColor(int zone) {
        face.scoringStyle = scoringStyle;
        return face.getContrastColor(zone);
    }

    @Override
    public int getTextColor(int zone) {
        face.scoringStyle = scoringStyle;
        return face.getTextColor(zone);
    }

    @Override
    public int getZones() {
        return face.zones;
    }

    @Override
    public Diameter[] getDiameters() {
        return new Diameter[]{new Diameter(40, Dimension.CENTIMETER),
                new Diameter(60, Dimension.CENTIMETER)};
    }

    public RectF getBoundsF(int index, Rect rect) {
        int pos[] = facePositions[index];
        RectF bounds = new RectF();
        bounds.left = rect.left + recalc(rect, pos[0] - faceRadius);
        bounds.top = rect.top + recalc(rect, pos[1] - faceRadius);
        bounds.right = rect.left + recalc(rect, pos[0] + faceRadius);
        bounds.bottom = rect.top + recalc(rect, pos[1] + faceRadius);
        return bounds;
    }

    @Override
    public Rect getTargetBounds(Rect rect, int index) {
        int pos[] = facePositions[index % facePositions.length];
        Rect bounds = new Rect();
        bounds.left = (int) (rect.left + recalc(rect, pos[0] - faceRadius));
        bounds.top = (int) (rect.top + recalc(rect, pos[1] - faceRadius));
        bounds.right = (int) (rect.left + recalc(rect, pos[0] + faceRadius));
        bounds.bottom = (int) (rect.top + recalc(rect, pos[1] + faceRadius));
        return bounds;
    }

    @Override
    public int getZoneFromPoint(float x, float y) {
        return face.getZoneFromPoint(x, y);
    }

    @Override
    public ArrayList<String> getScoringStyles() {
        return face.getScoringStyles();
    }

    @Override
    public int getFaceCount() {
        return facePositions.length;
    }

    @Override
    public int getWidth() {
        return getDiff(0);
    }

    @Override
    public int getHeight() {
        return getDiff(1);
    }

    private int getDiff(int coordinate) {
        int min = Integer.MAX_VALUE, max = Integer.MIN_VALUE;
        for (int[] facePosition : facePositions) {
            if (facePosition[coordinate] < min) {
                min = facePosition[coordinate];
            }
            if (facePosition[coordinate] > max) {
                max = facePosition[coordinate];
            }
        }
        return max - min + faceRadius*2;
    }
}
