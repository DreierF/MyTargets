/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */

package de.dreier.mytargets.utils;

import android.support.annotation.NonNull;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;

import java.util.Collections;
import java.util.List;

import de.dreier.mytargets.R;
import de.dreier.mytargets.managers.SettingsManager;
import de.dreier.mytargets.managers.dao.ArrowDataSource;
import de.dreier.mytargets.managers.dao.BowDataSource;
import de.dreier.mytargets.managers.dao.PasseDataSource;
import de.dreier.mytargets.managers.dao.RoundDataSource;
import de.dreier.mytargets.managers.dao.StandardRoundDataSource;
import de.dreier.mytargets.managers.dao.TrainingDataSource;
import de.dreier.mytargets.shared.models.Arrow;
import de.dreier.mytargets.shared.models.Bow;
import de.dreier.mytargets.shared.models.Passe;
import de.dreier.mytargets.shared.models.Round;
import de.dreier.mytargets.shared.models.Shot;
import de.dreier.mytargets.shared.models.StandardRound;
import de.dreier.mytargets.shared.models.Target;
import de.dreier.mytargets.shared.models.Training;
import de.dreier.mytargets.shared.utils.StandardRoundFactory;

import static de.dreier.mytargets.shared.SharedApplicationInstance.get;
import static de.dreier.mytargets.shared.targets.ScoringStyle.MISS_SYMBOL;

public class HtmlUtils {

    private static final String CSS = "<style type=\"text/css\">\n" +
            "body{font-family: Roboto, Sans-serif;}\n" +
            ".myTable { border-collapse:collapse; width:100%; }\n" +
            ".myTable td, .myTable th { padding:5px; border:1px solid #000; }\n" +
            ".align_left td {text-align: left; }\n" +
            ".align_center td {text-align: center; }\n" +
            ".circle {border-radius: 50%; width: 20px; height: 20px; padding: 5px; text-align: center; margin: auto;}\n" +
            "</style>";

    public static String getScoreboard(long trainingId, long roundId, ScoreboardConfiguration configuration) {
        // Query information from database
        RoundDataSource roundDataSource = new RoundDataSource();
        Training training = new TrainingDataSource().get(trainingId);
        List<Round> rounds;
        if (roundId == -1) {
            rounds = roundDataSource.getAll(trainingId);
        } else {
            rounds = Collections.singletonList(roundDataSource.get(roundId));
        }

        // Initialize html Strings
        String html = "<html>" + CSS;

        if (configuration.showTitle) {
            html += "<h3>" + training.title + " (" + training.getFormattedDate() + ")</h3>";
        }

        boolean[] equals = new boolean[2];
        if (configuration.showProperties) {
            html += "<table width=\"100%\" style=\"border:0px;\"><tr>" +
                    "<td width=\"50%\">" + getTrainingInfoHTML(training, rounds, equals,
                    true) + "</td>" +
                    "<td width=\"50%\">" + getTrainingTopScoreDistribution(trainingId) + "</td>" +
                    "</tr></table>";
        }

        if (configuration.showTable) {
            for (int r = 0; r < rounds.size(); r++) {
                Round round = rounds.get(r);
                html += "<b>" + get(R.string.round) + " " + (round.info.index + 1) + "</b>";
                if (configuration.showProperties) {
                    html += "<br>" + getRoundInfo(round, equals);
                }
                html += getRoundTable(configuration, round);
            }
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
        html += getTableHeader(round.info.arrowsPerPasse);
        int carry = 0;
        List<Passe> passes = new PasseDataSource().getAllByRound(round.getId());
        for (Passe passe : passes) {
            html += "<tr class=\"align_center\">";
            html += "<td>" + (passe.index + 1) + "</td>";
            int sum = 0;
            for (Shot shot : passe.shot) {
                html += "<td>";
                html += getPoints(configuration, shot, round.info.target);
                html += "</td>";
                int points = round.info.target.getPointsByZone(shot.zone, shot.index);
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
        final String points = target.zoneToString(shot.zone, shot.index);
        if (configuration.showPointsColored) {
            int fillColor = target.getModel().getZone(shot.zone).getFillColor();
            int color = target.getModel().getTextColor(shot.zone);
            return String
                    .format("<div class=\"circle\" style='background: #%06X; color: #%06X'>%s</div>",
                            fillColor & 0xFFFFFF, color & 0xFFFFFF, points);
        } else {
            return points;
        }
    }

    private static String getTableHeader(int ppp) {
        String html = "<tr class=\"align_center\">" +
                "<th rowspan=\"2\"><b>" + get(R.string.passe) + "</b></th>" +
                "<th colspan=\"" + ppp + "\"><b>" + get(R.string.arrows) +
                "</b></th>" +
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
                        "<th>" + get(R.string.points) + "</th>" +
                        "<th>" + get(R.string.comment) + "</th></tr>";
        int commentsCount = 0;

        int j = 0;
        for (Round round : rounds) {
            int i = 1;
            List<Passe> passes = new PasseDataSource().getAllByRound(round.getId());
            for (Passe passe : passes) {
                for (int s = 0; s < passe.shot.length; s++) {
                    Shot shot = passe.shot[s];
                    if (!TextUtils.isEmpty(shot.comment)) {
                        comments += "<tr class=\"align_center\"><td>" + j + "</td>" +
                                "<td>" + i + "</td>" +
                                "<td>" + round.info.target.zoneToString(shot.zone, s) +
                                "</td>" +
                                "<td>" +
                                TextUtils.htmlEncode(shot.comment).replace("\n", "<br />") +
                                "</td></tr>";
                        commentsCount++;
                    }
                }
                i++;
            }
            j++;
        }

        // If a minimum of one comment is present show comments table
        String html = "";
        if (commentsCount > 0) {
            html = comments + "</table>";
        }
        return html;
    }

    public static String getRoundInfo(Round round, boolean[] equals) {
        HTMLInfoBuilder info = new HTMLInfoBuilder();
        if (!equals[0]) {
            info.addLine(R.string.distance, round.info.distance);
        }
        if (!equals[1]) {
            info.addLine(R.string.target_face, round.info.target);
        }
        if (!round.comment.isEmpty()) {
            info.addLine(R.string.comment, round.comment);
        }
        return info.toString();
    }

    @NonNull
    private static String getTrainingTopScoreDistribution(long training) {
        HTMLInfoBuilder info = new HTMLInfoBuilder();
        PasseDataSource passeDataSource = new PasseDataSource();

        List<Pair<String, Integer>> scoreDistribution = passeDataSource.getTopScoreDistribution(new RoundDataSource().getAll(training));
        int misses = 0;
        int hits = 0;
        for (Pair<String, Integer> score : scoreDistribution) {
            if (score.getFirst().equals(MISS_SYMBOL)) {
                misses += score.getSecond();
            } else {
                hits += score.getSecond();
            }
        }
        info.addLine(R.string.hits, hits);
        info.addLine(R.string.misses, misses);

        for (Pair<String, Integer> score : scoreDistribution.subList(0, Math.min(scoreDistribution.size(), 3))) {
            info.addLine(score.getFirst(), score.getSecond());
        }

        info.addLine(R.string.average, passeDataSource.getAverageScore(training));
        return info.toString();
    }

    @NonNull
    private static String getReachedPointsFormatted(long training) {
        int maxPoints = 0;
        int reachedPoints = 0;
        List<Round> rounds = new RoundDataSource().getAll(training);
        for (Round r : rounds) {
            maxPoints += r.info.getMaxPoints();
            reachedPoints += r.reachedPoints;
        }
        String percent = maxPoints == 0 ? "" :
                " (" + (reachedPoints * 100 / maxPoints) + "%)";
        return reachedPoints + "/" + maxPoints + percent;
    }

    public static String getTrainingInfoHTML(Training training, List<Round> rounds, boolean[] equals, boolean scoreboard) {
        HTMLInfoBuilder info = new HTMLInfoBuilder();
        if (scoreboard) {
            getScoreboardHeaderInfo(info, training, rounds);
        }

        Bow bow = new BowDataSource().get(training.bow);
        if (bow != null) {
            info.addLine(R.string.bow, bow.name);
            if (scoreboard) {
                info.addLine(R.string.bow_type, bow.type);
            }
        }

        Arrow arrow = new ArrowDataSource().get(training.arrow);
        if (arrow != null) {
            info.addLine(R.string.arrow, arrow.name);
        }

        StandardRound standardRound = new StandardRoundDataSource().get(training.standardRoundId);
        if (standardRound.club != StandardRoundFactory.CUSTOM_PRACTICE) {
            info.addLine(R.string.standard_round, standardRound.name);
        }

        if (rounds.size() > 0) {
            getEqualValues(rounds, equals);
            Round round = rounds.get(0);
            if (equals[0]) {
                final int envStringResId = standardRound.indoor ? R.string.indoor : R.string.outdoor;
                info.addLine(R.string.distance, String.format("%s - %s", round.info.distance, get(envStringResId)));
            }
            if (equals[1]) {
                info.addLine(R.string.target_face, round.info.target);
            }
        }
        return info.toString();
    }

    private static void getEqualValues(List<Round> rounds, boolean[] equals) {
        // Aggregate round information
        equals[0] = true;
        equals[1] = true;
        Round round = rounds.get(0);
        for (Round r : rounds) {
            equals[0] = r.info.distance.equals(round.info.distance) && equals[0];
            equals[1] = r.info.target.equals(round.info.target) && equals[1];
        }
    }

    private static void getScoreboardHeaderInfo(HTMLInfoBuilder info, Training training, List<Round> rounds) {
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
            String value = getReachedPointsFormatted(training.getId());
            info.addLine(R.string.points, value);
        }
    }

    private static String getSignature() {
        return "<div style=\"border-top: 2px solid black; width: 30%;margin-right: 5%;margin-top: 100px;float:left;\">" +
                get(R.string.witness)
                + "</div>" +
                "<div style=\"border-top: 2px solid black; width: 30%;float:left; margin-top: 100px;\">" +
                get(R.string.archer)
                + "</div>";
    }

    @SuppressWarnings("deprecation")
    public static Spanned fromHtml(String html) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            return Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY);
        } else {
            return Html.fromHtml(html);
        }
    }
}
