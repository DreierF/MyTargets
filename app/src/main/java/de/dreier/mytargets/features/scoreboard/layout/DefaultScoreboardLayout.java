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

package de.dreier.mytargets.features.scoreboard.layout;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.util.Pair;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import de.dreier.mytargets.R;
import de.dreier.mytargets.features.scoreboard.ScoreboardBuilder;
import de.dreier.mytargets.features.scoreboard.ScoreboardConfiguration;
import de.dreier.mytargets.features.scoreboard.builder.model.Table;
import de.dreier.mytargets.features.settings.SettingsManager;
import de.dreier.mytargets.shared.models.SelectableZone;
import de.dreier.mytargets.shared.models.Target;
import de.dreier.mytargets.shared.models.db.Arrow;
import de.dreier.mytargets.shared.models.db.Bow;
import de.dreier.mytargets.shared.models.db.End;
import de.dreier.mytargets.shared.models.db.Round;
import de.dreier.mytargets.shared.models.db.Shot;
import de.dreier.mytargets.shared.models.db.StandardRound;
import de.dreier.mytargets.shared.models.db.Training;
import de.dreier.mytargets.shared.utils.SharedUtils;

import static de.dreier.mytargets.shared.models.db.End.getSortedScoreDistribution;
import static de.dreier.mytargets.shared.targets.scoringstyle.ScoringStyle.MISS_SYMBOL;

public class DefaultScoreboardLayout {

    @NonNull
    private final Context context;
    @NonNull
    private final Locale locale;
    @NonNull
    private final ScoreboardConfiguration configuration;
    private ScoreboardBuilder builder;

    public DefaultScoreboardLayout(@NonNull Context context, @NonNull Locale locale, @NonNull ScoreboardConfiguration configuration) {
        this.context = context;
        this.locale = locale;
        this.configuration = configuration;
    }

    public void generateWithBuilder(ScoreboardBuilder builder, Training training, List<Round> rounds) {
        this.builder = builder;

        if (configuration.showTitle) {
            builder.title(training.title);
        }

        boolean[] equals = new boolean[2];
        if (configuration.showProperties) {
            builder.table(getTrainingInfoTable(training, rounds, equals));
        }

        if (configuration.showTable) {
            for (Round round : rounds) {
                builder.openSection();
                builder.subtitle(context.getResources().getQuantityString(R.plurals.rounds, round.index + 1, round.index + 1));
                if (configuration.showProperties) {
                    builder.table(getRoundInfo(round, equals));
                }
                builder.table(getRoundTable(round));
                builder.closeSection();
            }
        }

        if (configuration.showStatistics) {
            appendStatistics(rounds);
        }

        if (configuration.showComments) {
            appendComments(rounds);
        }

        if (configuration.showSignature) {
            appendSignature(training);
        }
    }

    private Table getTrainingInfoTable(@NonNull Training training, @NonNull List<Round> rounds, boolean[] equals) {
        InfoTableBuilder info = new InfoTableBuilder();
        addStaticTrainingHeaderInfo(info, training, rounds);
        addDynamicTrainingHeaderInfo(rounds, equals, info);
        return info.info;
    }

    private void addStaticTrainingHeaderInfo(@NonNull InfoTableBuilder info, @NonNull Training training, @NonNull List<Round> rounds) {
        getScoreboardOnlyHeaderInfo(info, training, rounds);

        if (training.indoor) {
            info.addLine(R.string.environment, context.getString(R.string.indoor));
        } else {
            info.addLine(R.string.weather, training.getEnvironment().getWeather().getName());
            info.addLine(R.string.wind,
                    training.getEnvironment().getWindSpeed(context));
            if (!TextUtils.isEmpty(training.getEnvironment().getLocation())) {
                info.addLine(R.string.location, training.getEnvironment().getLocation());
            }
        }

        Bow bow = Bow.Companion.get(training.bowId);
        if (bow != null) {
            info.addLine(R.string.bow, bow.getName());
            info.addLine(R.string.bow_type, bow.getType());
        }

        Arrow arrow = Arrow.Companion.get(training.arrowId);
        if (arrow != null) {
            info.addLine(R.string.arrow, arrow.getName());
        }

        if (training.standardRoundId != null) {
            StandardRound standardRound = StandardRound.get(training.standardRoundId);
            info.addLine(R.string.standard_round, standardRound.name);
        }
        if (!training.comment.isEmpty() && configuration.showComments) {
            info.addLine(R.string.comment, training.comment);
        }
    }

    private void addDynamicTrainingHeaderInfo(@NonNull List<Round> rounds, boolean[] equals, @NonNull InfoTableBuilder info) {
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

    private void getEqualValues(@NonNull List<Round> rounds, boolean[] equals) {
        // Aggregate round information
        equals[0] = true;
        equals[1] = true;
        Round round = rounds.get(0);
        for (Round r : rounds) {
            equals[0] = SharedUtils.equals(r.distance, round.distance) && equals[0];
            equals[1] = SharedUtils.equals(r.getTarget(), round.getTarget()) && equals[1];
        }
    }

    private void appendStatistics(@NonNull List<Round> rounds) {
        if (rounds.size() == 1) {
            builder.table(getStatisticsForRound(rounds));
        } else if (rounds.size() > 1) {
            for (Round round : rounds) {
                builder.openSection();
                builder.subtitle(context.getResources().getQuantityString(R.plurals.rounds, round.index + 1, round.index + 1));
                builder.table(getStatisticsForRound(Collections.singletonList(round)));
                builder.closeSection();
            }
            builder.openSection();
            builder.subtitle(context.getString(R.string.training));
            builder.table(getStatisticsForRound(rounds));
            builder.closeSection();
        }
    }

    private Table getRoundInfo(@NonNull Round round, boolean[] equals) {
        InfoTableBuilder info = new InfoTableBuilder();
        if (!equals[0]) {
            info.addLine(R.string.distance, round.distance);
        }
        if (!equals[1]) {
            info.addLine(R.string.target_face, round.getTarget().getName());
        }
        if (!round.comment.isEmpty() && configuration.showComments) {
            info.addLine(R.string.comment, round.comment);
        }
        return info.info;
    }

    @NonNull
    private Table getStatisticsForRound(@NonNull List<Round> rounds) {
        List<Map.Entry<SelectableZone, Integer>> scoreDistribution = getSortedScoreDistribution(
                rounds);
        int hits = 0;
        int total = 0;
        for (Map.Entry<SelectableZone, Integer> score : scoreDistribution) {
            if (!score.getKey().text.equals(MISS_SYMBOL)) {
                hits += score.getValue();
            }
            total += score.getValue();
        }

        List<Pair<String, Integer>> topScores = End.getTopScoreDistribution(scoreDistribution);

        Table table = new Table(false);
        Table.Row row = table.startRow();
        for (Pair<String, Integer> topScore : topScores) {
            row.addBoldCell(topScore.first);
        }
        row.addBoldCell(context.getString(R.string.hits));
        row.addBoldCell(context.getString(R.string.average));
        row = table.startRow();

        for (Pair<String, Integer> topScore : topScores) {
            row.addCell(topScore.second);
        }
        row.addCell(hits + "/" + total);
        row.addCell(getAverageScore(scoreDistribution));
        return table;
    }

    private String getAverageScore(@NonNull List<Map.Entry<SelectableZone, Integer>> scoreDistribution) {
        int sum = 0;
        int count = 0;
        for (Map.Entry<SelectableZone, Integer> entry : scoreDistribution) {
            sum += entry.getValue() * entry.getKey().points;
            count += entry.getValue();
        }
        if (count == 0) {
            return "-";
        } else {
            return String.format(locale, "%.2f", sum * 1.0f / count);
        }
    }

    @NonNull
    private Table getRoundTable(@NonNull Round round) {
        Table table = new Table(false);
        appendTableHeader(table, round.shotsPerEnd);
        int carry = 0;
        for (End end : round.getEnds()) {
            Table.Row row = table.startRow();
            row.addCell(end.index + 1);
            int sum = 0;
            final List<Shot> shots = new ArrayList<>(end.getShots());
            if (SettingsManager.INSTANCE.shouldSortTarget(round.getTarget())) {
                Collections.sort(shots);
            }
            for (Shot shot : shots) {
                appendPointsCell(row, shot, round.getTarget());
                int points = round.getTarget().getScoreByZone(shot.scoringRing, shot.index);
                sum += points;
                carry += points;
            }
            row.addCell(sum);
            row.addCell(carry);
        }
        return table;
    }

    private void appendTableHeader(Table table, int arrowsPerEnd) {
        Table.Row row = table.startRow();
        row.addBoldCell(context.getString(R.string.passe));
        Table sectioned = new Table(false);
        sectioned.startRow().addBoldCell(context.getString(R.string.arrows), arrowsPerEnd);
        Table.Row sectionedRow = sectioned.startRow();
        for (int i = 1; i <= arrowsPerEnd; i++) {
            sectionedRow.addBoldCell(String.valueOf(i));
        }
        sectioned.columnSpan = arrowsPerEnd;
        row.addCell(sectioned);
        row.addBoldCell(context.getString(R.string.sum));
        row.addBoldCell(context.getString(R.string.carry));
    }

    private void appendPointsCell(Table.Row row, @NonNull Shot shot, @NonNull Target target) {
        if (shot.scoringRing == Shot.NOTHING_SELECTED) {
            row.addCell("");
            return;
        }
        final String points = target.zoneToString(shot.scoringRing, shot.index);
        if (configuration.showPointsColored) {
            int fillColor = target.getModel().getZone(shot.scoringRing).getFillColor();
            int color = target.getModel().getZone(shot.scoringRing).getTextColor();
            row.addEndCell(points, fillColor, color, shot.arrowNumber);
        } else {
            row.addCell(points);
        }
    }

    private void appendComments(@NonNull List<Round> rounds) {
        Table comments = new Table(false);
        comments.startRow().addBoldCell(context.getString(R.string.round))
                .addBoldCell(context.getString(R.string.passe))
                .addBoldCell(context.getString(R.string.comment));

        int commentsCount = 0;
        for (Round round : rounds) {
            List<End> ends = round.getEnds();
            for (End end : ends) {
                if (!TextUtils.isEmpty(end.comment)) {
                    comments.startRow()
                            .addCell(round.index + 1)
                            .addCell(end.index + 1)
                            .addCell(end.comment);
                    commentsCount++;
                }
            }
        }

        // If a minimum of one comment is present show comments table
        if (commentsCount > 0) {
            builder.table(comments);
        }
    }

    private void getScoreboardOnlyHeaderInfo(@NonNull InfoTableBuilder info, @NonNull Training training, @NonNull List<Round> rounds) {
        final String fullName = SettingsManager.INSTANCE.getProfileFullName();
        if (!fullName.trim().isEmpty()) {
            info.addLine(R.string.name, fullName);
        }
        final Integer age = SettingsManager.INSTANCE.getProfileAge();
        if (age != null && age < 18) {
            info.addLine(R.string.age, age);
        }
        final String club = SettingsManager.INSTANCE.getProfileClub();
        if (!TextUtils.isEmpty(club)) {
            info.addLine(R.string.club, club);
        }
        final String licenceNumber = SettingsManager.INSTANCE.getProfileLicenceNumber();
        if (!TextUtils.isEmpty(licenceNumber)) {
            info.addLine(R.string.licence_number, licenceNumber);
        }
        if (rounds.size() > 1) {
            info.addLine(R.string.points, training.getReachedScore()
                    .format(locale, SettingsManager.INSTANCE.getScoreConfiguration()));
        }
        info.addLine(R.string.date, training.getFormattedDate());
    }

    private void appendSignature(Training training) {
        builder.signature(training.getOrCreateArcherSignature(), training.getOrCreateWitnessSignature());
    }
}
