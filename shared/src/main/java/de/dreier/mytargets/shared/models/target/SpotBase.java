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

    /*@Override
    protected float[] getArrowPosition(Rect rect, float x, float y, int arrow) {
        Log.d("", x + "," + y);
        Rect spotRect = getBounds(arrow, rect);
        Log.d("", rect.toShortString() + "," + spotRect.toShortString());
        float[] pos = new float[2];
        pos[0] = spotRect.left + (1 + x) * spotRect.width() * 0.5f;
        pos[1] = spotRect.top + (1 + y) * spotRect.width() * 0.5f;
        Log.d("", pos[0] + "," + pos[1]);
        return pos;
    }*/

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

    public int getMaxPoints() {
        face.scoringStyle = scoringStyle;
        return face.getMaxPoints();
    }

    public float zoneToX(int zone) {
        face.scoringStyle = scoringStyle;
        return face.zoneToX(zone);
    }

    public int getFillColor(int zone) {
        face.scoringStyle = scoringStyle;
        return face.getFillColor(zone);
    }

    public int getStrokeColor(int zone) {
        face.scoringStyle = scoringStyle;
        return face.getStrokeColor(zone);
    }

    public int getContrastColor(int zone) {
        face.scoringStyle = scoringStyle;
        return face.getContrastColor(zone);
    }

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
        int pos[] = facePositions[index];
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

    public int getFaceCount() {
        return facePositions.length;
    }
}
