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

package de.dreier.mytargets.features.training.details;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.util.List;

import de.dreier.mytargets.R;
import de.dreier.mytargets.shared.models.db.Arrow;
import de.dreier.mytargets.shared.models.db.Bow;
import de.dreier.mytargets.shared.models.db.Round;
import de.dreier.mytargets.shared.models.db.StandardRound;
import de.dreier.mytargets.shared.models.db.Training;
import de.dreier.mytargets.shared.utils.SharedUtils;

public class HtmlUtils {

    public static String getTrainingInfoHTML(Context context, @NonNull Training training, @NonNull List<Round> rounds, boolean[] equals) {
        HtmlInfoBuilder info = new HtmlInfoBuilder();
        addStaticTrainingHeaderInfo(context, info, training);
        addDynamicTrainingHeaderInfo(rounds, equals, info);
        return info.toString();
    }

    private static void addStaticTrainingHeaderInfo(Context context, @NonNull HtmlInfoBuilder info, @NonNull Training training) {
        if (training.getIndoor()) {
            info.addLine(R.string.environment, context.getString(R.string.indoor));
        } else {
            info.addLine(R.string.weather, training.getEnvironment().getWeather().getName());
            info.addLine(R.string.wind,
                    training.getEnvironment().getWindSpeed(context));
            if (!TextUtils.isEmpty(training.getEnvironment().getLocation())) {
                info.addLine(R.string.location, training.getEnvironment().getLocation());
            }
        }

        Bow bow = Bow.Companion.get(training.getBowId());
        if (bow != null) {
            info.addLine(R.string.bow, bow.getName());
        }

        Arrow arrow = Arrow.Companion.get(training.getArrowId());
        if (arrow != null) {
            info.addLine(R.string.arrow, arrow.getName());
        }

        if (training.getStandardRoundId() != null) {
            StandardRound standardRound = StandardRound.Companion.get(training.getStandardRoundId());
            info.addLine(R.string.standard_round, standardRound.getName());
        }
    }

    private static void addDynamicTrainingHeaderInfo(@NonNull List<Round> rounds, boolean[] equals, @NonNull HtmlInfoBuilder info) {
        if (rounds.size() > 0) {
            getEqualValues(rounds, equals);
            Round round = rounds.get(0);
            if (equals[0]) {
                info.addLine(R.string.distance, round.getDistance());
            }
            if (equals[1]) {
                info.addLine(R.string.target_face, round.getTarget().getName());
            }
        }
    }

    private static void getEqualValues(@NonNull List<Round> rounds, boolean[] equals) {
        // Aggregate round information
        equals[0] = true;
        equals[1] = true;
        Round round = rounds.get(0);
        for (Round r : rounds) {
            equals[0] = SharedUtils.INSTANCE.equals(r.getDistance(), round.getDistance()) && equals[0];
            equals[1] = SharedUtils.INSTANCE.equals(r.getTarget(), round.getTarget()) && equals[1];
        }
    }

    public static String getRoundInfo(@NonNull Round round, boolean[] equals) {
        HtmlInfoBuilder info = new HtmlInfoBuilder();
        if (!equals[0]) {
            info.addLine(R.string.distance, round.getDistance());
        }
        if (!equals[1]) {
            info.addLine(R.string.target_face, round.getTarget().getName());
        }
        if (!round.getComment().isEmpty()) {
            info.addLine(R.string.comment, round.getComment());
        }
        return info.toString();
    }
}
