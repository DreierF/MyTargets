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

package de.dreier.mytargets.shared.targets.scoringstyle;

import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

import com.annimon.stream.Stream;

import de.dreier.mytargets.shared.models.Score;
import de.dreier.mytargets.shared.models.db.End;
import de.dreier.mytargets.shared.models.db.Shot;

import static de.dreier.mytargets.shared.SharedApplicationInstance.get;

public class ScoringStyle {

    public static final String MISS_SYMBOL = "M";
    private static final String X_SYMBOL = "X";
    private final String title;
    private final boolean showAsX;
    protected final int[][] points;
    private int maxScorePerShot;

    ScoringStyle(boolean showAsX, int[][] points) {
        this(null, showAsX, points);
    }

    public ScoringStyle(@StringRes int title, boolean showAsX, int... points) {
        this(get(title), showAsX, new int[][]{points});
    }

    private ScoringStyle(String title, boolean showAsX, int[][] points) {
        this.showAsX = showAsX;
        this.points = points;
        getMaxPoints();
        if (title == null) {
            this.title = getDescriptionString();
        } else {
            this.title = title;
        }
    }

    public ScoringStyle(boolean showAsX, int... points) {
        this(showAsX, new int[][]{points});
    }

    private int getMaxPoints() {
        maxScorePerShot = 0;
        for (int[] arrowPoints : points) {
            for (int point : arrowPoints) {
                if (point > maxScorePerShot) {
                    maxScorePerShot = point;
                }
            }
        }
        return maxScorePerShot;
    }

    @NonNull
    private String getDescriptionString() {
        String style = "";
        for (int i = 0; i < points[0].length; i++) {
            if (i + 1 < points[0].length && points[0][i] <= points[0][i + 1] && !(i == 0 && showAsX)) {
                continue;
            }
            if (!style.isEmpty()) {
                style += ", ";
            }
            style += zoneToString(i, 0);
            for (int a = 1; a < points.length; a++) {
                style += "/" + zoneToString(i, a);
            }
        }
        return style;
    }

    @Override
    public String toString() {
        return title;
    }

    public String zoneToString(int zone, int arrow) {
        if (isOutOfRange(zone)) {
            return MISS_SYMBOL;
        } else if (zone == 0 && showAsX) {
            return X_SYMBOL;
        } else {
            int value = getScoreByScoringRing(zone, arrow);
            if (value == 0) {
                return MISS_SYMBOL;
            }
            return String.valueOf(value);
        }
    }

    public int getScoreByScoringRing(int zone, int arrow) {
        if (isOutOfRange(zone)) {
            return 0;
        }
        return getPoints(zone, arrow);
    }

    protected int getPoints(int zone, int arrow) {
        return points[0][zone];
    }

    private boolean isOutOfRange(int zone) {
        return zone < 0 || zone >= points[0].length;
    }

    public Score getReachedScore(Shot shot) {
        Score score = new Score();
        score.reachedScore = getScoreByScoringRing(shot.scoringRing, shot.index);
        score.totalScore = maxScorePerShot;
        return score;
    }

    public Score getReachedScore(End end) {
        return Stream.of(end.getShots())
                .map(this::getReachedScore)
                .reduce(new Score(), Score::add);
    }
}
