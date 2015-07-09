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
import android.support.annotation.StringRes;

public class SpotBase extends Target {
    protected int faceRadius;
    protected Target face;
    protected int[][] facePositions;

    protected SpotBase(Context c, long id, @StringRes int nameRes) {
        super(c, id, nameRes);
    }

    @Override
    protected void draw(Canvas canvas, Rect rect) {
        for (int[] pos : facePositions) {
            Rect bounds = new Rect();
            bounds.left = (int) (rect.left + recalc(rect,pos[0] - faceRadius));
            bounds.top = (int) (rect.top + recalc(rect,pos[1] - faceRadius));
            bounds.right = (int) (rect.left + recalc(rect,pos[0] + faceRadius));
            bounds.bottom = (int) (rect.top + recalc(rect,pos[1] + faceRadius));
            face.draw(canvas, bounds);
        }
        onPostDraw(canvas, rect);
    }

    @Override
    public String zoneToString(int zone) {
        face.scoringStyle = scoringStyle;
        return face.zoneToString(zone);
    }

    public int getPointsByZone(int zone) {
        face.scoringStyle = scoringStyle;
        return face.getPointsByZone(zone);
    }

    public int getMaxPoints() {
        face.scoringStyle = scoringStyle;
        return face.getMaxPoints();
    }

    public float zoneToX(int zone) {
        face.scoringStyle = scoringStyle;
        return face.zoneToX(zone);
    }

    public int getZoneColor(int zone) {
        face.scoringStyle = scoringStyle;
        return face.getZoneColor(zone);
    }

    public int getStrokeColor(int zone) {
        face.scoringStyle = scoringStyle;
        return face.getStrokeColor(zone);
    }

    public int getTextColor(int zone) {
        face.scoringStyle = scoringStyle;
        return face.getTextColor(zone);
    }

    public int getZones() {
        return face.zones;
    }
}
