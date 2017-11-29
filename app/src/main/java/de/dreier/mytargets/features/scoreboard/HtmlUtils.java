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

package de.dreier.mytargets.features.scoreboard;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.widget.LinearLayout;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

import de.dreier.mytargets.R;
import de.dreier.mytargets.app.ApplicationInstance;
import de.dreier.mytargets.features.scoreboard.builder.ViewBuilder;
import de.dreier.mytargets.features.scoreboard.layout.DefaultScoreboardLayout;
import de.dreier.mytargets.shared.models.db.Arrow;
import de.dreier.mytargets.shared.models.db.Bow;
import de.dreier.mytargets.shared.models.db.Round;
import de.dreier.mytargets.shared.models.db.StandardRound;
import de.dreier.mytargets.shared.models.db.Training;
import de.dreier.mytargets.shared.utils.SharedUtils;

public class HtmlUtils {

    public static LinearLayout getScoreboardView(Context context, Locale locale, Training training, long roundId, @NonNull ScoreboardConfiguration configuration) {
        List<Round> rounds;
        if (roundId == -1) {
            rounds = training.getRounds();
        } else {
            rounds = Collections.singletonList(Round.get(roundId));
        }

        DefaultScoreboardLayout scoreboardLayout = new DefaultScoreboardLayout(context, locale, configuration);
        ViewBuilder html = new ViewBuilder(context);
        scoreboardLayout.generateWithBuilder(html, training, rounds);
        return html.build();
    }

    public static String getTrainingInfoHTML(Context context, @NonNull Training training, @NonNull List<Round> rounds, boolean[] equals) {
        HtmlInfoBuilder info = new HtmlInfoBuilder();
        addStaticTrainingHeaderInfo(context, info, training);
        addDynamicTrainingHeaderInfo(rounds, equals, info);
        return info.toString();
    }

    private static void addStaticTrainingHeaderInfo(Context context, @NonNull HtmlInfoBuilder info, @NonNull Training training) {
        if (training.indoor) {
            info.addLine(R.string.environment, context.getString(R.string.indoor));
        } else {
            info.addLine(R.string.weather, training.getEnvironment().weather.getName());
            info.addLine(R.string.wind,
                    training.getEnvironment().getWindSpeed(ApplicationInstance.getContext()));
            if (!TextUtils.isEmpty(training.getEnvironment().location)) {
                info.addLine(R.string.location, training.getEnvironment().location);
            }
        }

        Bow bow = Bow.get(training.bowId);
        if (bow != null) {
            info.addLine(R.string.bow, bow.name);
        }

        Arrow arrow = Arrow.get(training.arrowId);
        if (arrow != null) {
            info.addLine(R.string.arrow, arrow.name);
        }

        if (training.standardRoundId != null) {
            StandardRound standardRound = StandardRound.get(training.standardRoundId);
            info.addLine(R.string.standard_round, standardRound.name);
        }
    }

    private static void addDynamicTrainingHeaderInfo(@NonNull List<Round> rounds, boolean[] equals, @NonNull HtmlInfoBuilder info) {
        if (rounds.size() > 0) {
            getEqualValues(rounds, equals);
            Round round = rounds.get(0);
            if (equals[0]) {
                info.addLine(R.string.distance, round.distance);
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
            equals[0] = SharedUtils.equals(r.distance, round.distance) && equals[0];
            equals[1] = SharedUtils.equals(r.getTarget(), round.getTarget()) && equals[1];
        }
    }

    public static String getRoundInfo(@NonNull Round round, boolean[] equals) {
        HtmlInfoBuilder info = new HtmlInfoBuilder();
        if (!equals[0]) {
            info.addLine(R.string.distance, round.distance);
        }
        if (!equals[1]) {
            info.addLine(R.string.target_face, round.getTarget().getName());
        }
        if (!round.comment.isEmpty()) {
            info.addLine(R.string.comment, round.comment);
        }
        return info.toString();
    }
}
