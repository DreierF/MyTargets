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

import android.support.annotation.NonNull;
import android.support.v4.util.Pair;
import android.text.TextUtils;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import de.dreier.mytargets.R;
import de.dreier.mytargets.app.ApplicationInstance;
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

import static de.dreier.mytargets.shared.SharedApplicationInstance.get;
import static de.dreier.mytargets.shared.models.db.End.getSortedScoreDistribution;
import static de.dreier.mytargets.shared.targets.scoringstyle.ScoringStyle.MISS_SYMBOL;

public class HtmlUtils {

    public static final String BR = "<br>";
    private static final String CSS = "<style type=\"text/css\">\n" +
            "body{font-family: Roboto, Sans-serif;}\n" +
            ".myTable { border-collapse:collapse; width:100%; }\n" +
            ".myTable td, .myTable th { padding:5px; border:1px solid #000; }\n" +
            ".align_left td {text-align: left; }\n" +
            ".align_center td {text-align: center; }\n" +
            ".circle {border-radius: 50%; width: 20px; height: 20px; padding: 5px; text-align: center; margin: auto; position: relative;}\n" +
            ".circle_arrow { border-radius: 50%; width: 13px; height: 13px; \n" +
            "  padding: 2px; font-size: 11px; text-align: center; \n" +
            "  vertical-align: center; background: black; color: white;\n" +
            "  position: absolute; bottom: -4px; right: -4px; }" +
            "</style>";

    public static String getScoreboard(long trainingId, long roundId, ScoreboardConfiguration configuration) {
        // Query information from database
        Training training = Training.get(trainingId);
        List<Round> rounds;
        if (roundId == -1) {
            rounds = training.getRounds();
        } else {
            rounds = Collections.singletonList(Round.get(roundId));
        }

        // Initialize html Strings
        String html = "<html>" + CSS;

        if (configuration.showTitle) {
            html += "<h3>" + training.title + "</h3>";
        }

        boolean[] equals = new boolean[2];
        if (configuration.showProperties) {
            html += getTrainingInfoHTML(training, rounds, equals, true) + BR;
        }

        if (configuration.showTable) {
            for (Round round : rounds) {
                html += BR + bold(get(R.string.round) + " " + (round.index + 1));
                if (configuration.showProperties) {
                    html += BR + getRoundInfo(round, equals);
                }
                html += getRoundTable(configuration, round);
            }
        }

        if (configuration.showStatistics) {
            html += getStatistics(rounds);
        }

        if (configuration.showComments) {
            html += getComments(rounds);
        }

        if (configuration.showSignature) {
            html += getSignature();
        }

        html += "</html>";
        return html;
    }

    private static String getRoundTable(ScoreboardConfiguration configuration, Round round) {
        String html = "<table class=\"myTable\">";
        html += getTableHeader(round.shotsPerEnd);
        int carry = 0;
        for (End end : round.getEnds()) {
            html += "<tr class=\"align_center\">";
            html += "<td>" + (end.index + 1) + "</td>";
            int sum = 0;
            for (Shot shot : end.getSortedShotList()) {
                html += "<td>";
                html += getPoints(configuration, shot, round.getTarget());
                html += "</td>";
                int points = round.getTarget().getScoreByZone(shot.scoringRing, shot.index);
                sum += points;
                carry += points;
            }
            html += "<td>" + sum + "</td>";
            html += "<td>" + carry + "</td>";
            html += "</tr>";
        }
        html += "</table>";
        return html;
    }

    @NonNull
    private static String getPoints(ScoreboardConfiguration configuration, Shot shot, Target target) {
        if (shot.scoringRing == Shot.NOTHING_SELECTED) {
            return "";
        }
        final String points = target.zoneToString(shot.scoringRing, shot.index);
        if (configuration.showPointsColored) {
            int fillColor = target.getModel().getZone(shot.scoringRing).getFillColor();
            int color = target.getModel().getZone(shot.scoringRing).getTextColor();
            final String pointsDiv = String.format(
                    "<div class=\"circle\" style='background: #%06X; color: #%06X'>%s",
                    fillColor & 0xFFFFFF, color & 0xFFFFFF, points);
            final String arrowDiv = shot.arrowNumber == null ? "" : String.format(
                    "<div class=\"circle_arrow\">%s</div>", shot.arrowNumber);
            return pointsDiv + arrowDiv + "</div>";
        } else {
            return points;
        }
    }

    private static String getTableHeader(int ppp) {
        String html = "<tr class=\"align_center\">" +
                "<th rowspan=\"2\">" + get(R.string.passe) + "</th>" +
                "<th colspan=\"" + ppp + "\">" + get(R.string.arrows) + "</th>" +
                "<th rowspan=\"2\">" + get(R.string.sum) + "</th>" +
                "<th rowspan=\"2\">" + get(R.string.carry) + "</th>" +
                "</tr><tr class=\"align_center\">";
        for (int i = 1; i <= ppp; i++) {
            html += "<th>" + i + "</th>";
        }
        html += "</tr>";
        return html;
    }

    private static String getComments(List<Round> rounds) {
        String comments =
                "<table class=\"myTable\" style=\"margin-top:5px;\"><tr class=\"align_center\">" +
                        "<th>" + get(R.string.round) + "</th>" +
                        "<th>" + get(R.string.passe) + "</th>" +
                        "<th>" + get(R.string.comment) + "</th></tr>";
        int commentsCount = 0;

        for (Round round : rounds) {
            List<End> ends = round.getEnds();
            for (End end : ends) {
                if (!TextUtils.isEmpty(end.comment)) {
                    comments += "<tr class=\"align_center\">" +
                            "<td>" + (round.index + 1) + "</td>" +
                            "<td>" + (end.index + 1) + "</td>" +
                            "<td>" +
                            TextUtils.htmlEncode(end.comment).replace("\n", "<br />") +
                            "</td></tr>";
                    commentsCount++;
                }
            }
        }

        // If a minimum of one comment is present show comments table
        String html = "";
        if (commentsCount > 0) {
            html = comments + "</table>";
        }
        return html;
    }

    public static String getRoundInfo(Round round, boolean[] equals) {
        HtmlInfoBuilder info = new HtmlInfoBuilder();
        if (!equals[0]) {
            info.addLine(R.string.distance, round.distance);
        }
        if (!equals[1]) {
            info.addLine(R.string.target_face, round.getTarget());
        }
        if (!round.comment.isEmpty()) {
            info.addLine(R.string.comment, round.comment);
        }
        return info.toString();
    }

    @NonNull
    private static String getStatistics(List<Round> rounds) {
        if (rounds.size() == 0) {
            return "";
        } else if (rounds.size() == 1) {
            return getStatisticsForRound(rounds);
        } else {
            String html = "";
            for (Round round : rounds) {
                html += BR + bold(get(R.string.round) + " " + (round.index + 1));
                html += getStatisticsForRound(Collections.singletonList(round));
            }
            html += BR + bold(get(R.string.training));
            return html + getStatisticsForRound(rounds);
        }
    }

    @NonNull
    private static String bold(String text) {
        return "<b>" + text + "</b>";
    }

    @NonNull
    private static String getStatisticsForRound(List<Round> rounds) {
        String html = BR + "<table class=\"myTable\" style=\"margin-top:5px;\"><tr>";
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
        for (Pair<String, Integer> topScore : topScores) {
            html += "<th>" + topScore.first + "</th>";
        }
        html += "<th>" + get(R.string.hits) + "</th>";
        html += "<th>" + get(R.string.average) + "</th>";
        html += "</tr><tr class=\"align_center\">";

        for (Pair<String, Integer> topScore : topScores) {
            html += "<td>" + topScore.second + "</td>";
        }
        html += "<td>" + hits + "/" + total + "</td>";
        html += "<td>" + getAverageScore(scoreDistribution) + "</td>";
        return html + "</tr></table>";
    }

    private static String getAverageScore(List<Map.Entry<SelectableZone, Integer>> scoreDistribution) {
        int sum = 0;
        int count = 0;
        for (Map.Entry<SelectableZone, Integer> entry : scoreDistribution) {
            sum += entry.getValue() * entry.getKey().points;
            count += entry.getValue();
        }
        if (count == 0) {
            return "-";
        } else {
            return String.format(Locale.getDefault(), "%.2f", sum * 1.0f / count);
        }
    }

    public static String getTrainingInfoHTML(Training training, List<Round> rounds, boolean[] equals, boolean scoreboard) {
        HtmlInfoBuilder info = new HtmlInfoBuilder();
        addStaticTrainingHeaderInfo(info, training, rounds, scoreboard);
        addDynamicTrainingHeaderInfo(rounds, equals, info);
        return info.toString();
    }

    private static void addStaticTrainingHeaderInfo(HtmlInfoBuilder info, Training training, List<Round> rounds, boolean scoreboard) {
        if (scoreboard) {
            getScoreboardOnlyHeaderInfo(info, training, rounds);
        }

        if (training.indoor) {
            info.addLine(R.string.environment, get(R.string.indoor));
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
            if (scoreboard) {
                info.addLine(R.string.bow_type, bow.type);
            }
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

    private static void addDynamicTrainingHeaderInfo(List<Round> rounds, boolean[] equals, HtmlInfoBuilder info) {
        if (rounds.size() > 0) {
            getEqualValues(rounds, equals);
            Round round = rounds.get(0);
            if (equals[0]) {
                info.addLine(R.string.distance, round.distance);
            }
            if (equals[1]) {
                info.addLine(R.string.target_face, round.getTarget());
            }
        }
    }

    private static void getEqualValues(List<Round> rounds, boolean[] equals) {
        // Aggregate round information
        equals[0] = true;
        equals[1] = true;
        Round round = rounds.get(0);
        for (Round r : rounds) {
            equals[0] = SharedUtils.equals(r.distance, round.distance) && equals[0];
            equals[1] = r.getTarget().equals(round.getTarget()) && equals[1];
        }
    }

    private static void getScoreboardOnlyHeaderInfo(HtmlInfoBuilder info, Training training, List<Round> rounds) {
        final String fullName = SettingsManager.getProfileFullName();
        if (!fullName.trim().isEmpty()) {
            info.addLine(R.string.name, fullName);
        }
        final int age = SettingsManager.getProfileAge();
        if (age > 0 && age < 18) {
            info.addLine(R.string.age, age);
        }
        final String club = SettingsManager.getProfileClub();
        if (!club.isEmpty()) {
            info.addLine(R.string.club, club);
        }
        if (rounds.size() > 1) {
            info.addLine(R.string.points, training.getReachedScore().format(true));
        }
        info.addLine(R.string.date, training.getFormattedDate());
    }

    private static String getSignature() {
        return "<div style=\"border-top: 2px solid black; width: 30%;margin-right: 5%;margin-top: 100px;float:left;\">" +
                get(R.string.witness)
                + "</div>" +
                "<div style=\"border-top: 2px solid black; width: 30%;float:left; margin-top: 100px;\">" +
                get(R.string.archer)
                + "</div>";
    }

}
