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

package de.dreier.mytargets.features.statistics;

import android.support.annotation.NonNull;

import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

import de.dreier.mytargets.R;
import de.dreier.mytargets.shared.analysis.aggregation.average.Average;
import de.dreier.mytargets.shared.models.Dimension;
import de.dreier.mytargets.shared.models.Score;
import de.dreier.mytargets.shared.models.Target;
import de.dreier.mytargets.shared.models.db.Arrow;
import de.dreier.mytargets.shared.models.db.Round;
import de.dreier.mytargets.shared.models.db.Shot;
import de.dreier.mytargets.shared.streamwrapper.Stream;

import static de.dreier.mytargets.shared.SharedApplicationInstance.Companion;
import static java.lang.Math.ceil;

@Parcel
public class ArrowStatistic implements Comparable<ArrowStatistic> {

    private static final int[] BG_COLORS = {0xFFF44336, 0xFFFF5722, 0xFFFF9800, 0xFFFFC107, 0xFFFFEB3B, 0xFFCDDC39, 0xFF8BC34A, 0xFF4CAF50};
    private static final int[] TEXT_COLORS = {0xFFFFFFFF, 0xFFFFFFFF, 0xFF000002, 0xFF000002, 0xFF000002, 0xFF000002, 0xFF000002, 0xFF000002};
    public String arrowName;
    public String arrowNumber;
    public Average average = new Average();
    public Target target;
    public List<Shot> shots = new ArrayList<>();
    public Score totalScore;
    public Dimension arrowDiameter = new Dimension(5, Dimension.Unit.MILLIMETER);

    public ArrowStatistic() {
    }

    public ArrowStatistic(@NonNull Target target, @NonNull List<Shot> shots) {
        this(null, null, target, shots);
    }

    private ArrowStatistic(String arrowName, String arrowNumber, @NonNull Target target, @NonNull List<Shot> shots) {
        this.arrowName = arrowName;
        this.arrowNumber = arrowNumber;
        this.target = target;
        this.average.computeAll(shots);
        this.shots.addAll(shots);
        this.totalScore = Stream.of(shots)
                .map(shot -> target.getScoringStyle().getReachedScore(shot)).scoreSum();
    }

    public static List<ArrowStatistic> getAll(@NonNull Target target, @NonNull List<Round> rounds) {
        return Stream.of(rounds)
                .withoutNulls()
                .groupBy(r -> r.getTraining().getArrowId() == null ? 0 :
                        r.getTraining().getArrowId())
                .flatMap(t -> {
                    Arrow arrow = Arrow.Companion.get(t.getKey());
                    String name = arrow == null ? Companion.getStr(R.string.unknown) : arrow.getName();
                    return Stream.of(t.getValue())
                            .flatMap(r -> Stream.of(r.loadEnds())
                                    .flatMap(e -> Stream.of(e.loadShots())))
                            .filter(s -> s.getArrowNumber() != null)
                            .groupBy(shot -> shot.getArrowNumber())
                            .filter(entry -> entry.getValue().size() > 1)
                            .map(stringListEntry -> new ArrowStatistic(name,
                                    stringListEntry.getKey(), target, stringListEntry.getValue()));
                }).toList();
    }

    public int getAppropriateBgColor() {
        return BG_COLORS[((int) ceil((BG_COLORS.length - 1) * totalScore.getPercent()))];
    }

    public int getAppropriateTextColor() {
        return TEXT_COLORS[((int) ceil((TEXT_COLORS.length - 1) * totalScore.getPercent()))];
    }

    @Override
    public int compareTo(@NonNull ArrowStatistic another) {
        return Float.compare(another.totalScore.getShotAverage(), totalScore.getShotAverage());
    }
}
