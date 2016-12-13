/*
 * Copyright (C) 2016 Florian Dreier
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

package de.dreier.mytargets.models;

import android.support.annotation.NonNull;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;

import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

import de.dreier.mytargets.shared.analysis.aggregation.average.Average;
import de.dreier.mytargets.shared.models.Target;
import de.dreier.mytargets.shared.models.db.Arrow;
import de.dreier.mytargets.shared.models.db.Round;
import de.dreier.mytargets.shared.models.db.Shot;

@Parcel
public class ArrowStatistic implements Comparable<ArrowStatistic> {

    private static final int[] BG_COLORS = {0xFFF44336, 0xFFFF5722, 0xFFFF9800, 0xFFFFC107, 0xFFFFEB3B, 0xFFCDDC39, 0xFF8BC34A, 0xFF4CAF50};
    private static final int[] TEXT_COLORS = {0xFFFFFFFF, 0xFFFFFFFF, 0xFF000002, 0xFF000002, 0xFF000002, 0xFF000002, 0xFF000002, 0xFF000002};
    public String arrowName;
    public String arrowNumber;
    public Average average = new Average();
    public Target target;
    public ArrayList<Shot> shots = new ArrayList<>();
    public Integer reachedScore;

    public ArrowStatistic() {
    }

    public ArrowStatistic(Target target, List<Shot> shots) {
        this(null, null, target, shots);
    }

    private ArrowStatistic(String arrow, String arrowNumber, Target target, List<Shot> shots) {
        this.arrowName = arrow;
        this.arrowNumber = arrowNumber;
        this.target = target;
        this.average.computeAll(shots);
        this.shots.addAll(shots);
        this.reachedScore = Stream.of(shots).reduce(0,
                (sum, shot) -> sum + target.getScoreByZone(shot.scoringRing, shot.index));
    }

    public static List<ArrowStatistic> getAll(Target target, List<Round> rounds) {
        return Stream.of(rounds)
                .groupBy(r -> r.getTraining().arrowId)
                .flatMap(t -> {
                    Arrow arrow = Arrow.get(t.getKey());
                    return Stream.of(t.getValue())
                            .flatMap(r -> Stream.of(r.getEnds())
                                    .flatMap(e -> Stream.of(e.getShots())))
                            .groupBy(shot -> shot.arrowNumber)
                            .filter(entry -> entry.getValue().size() > 1)
                            .map(stringListEntry -> new ArrowStatistic(arrow.getName(),
                                    stringListEntry.getKey(), target, stringListEntry.getValue()));
                }).collect(Collectors.toList());
    }

    public int getAppropriateBgColor() {
        return BG_COLORS[0/*((int) Math
                .ceil(reachedPointsSum * (BG_COLORS.length - 1) / maxPointsSum))*/];
    }

    public int getAppropriateTextColor() {
        return TEXT_COLORS[0/*((int) Math
                .ceil(reachedPointsSum * (TEXT_COLORS.length - 1) / maxPointsSum))*/];
    }

    @Override
    public int compareTo(@NonNull ArrowStatistic another) {
        return Double.compare(average.getStdDev(), another.average.getStdDev());
    }
}
