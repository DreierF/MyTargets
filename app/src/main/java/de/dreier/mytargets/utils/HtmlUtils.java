/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */

package de.dreier.mytargets.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import de.dreier.mytargets.ApplicationInstance;
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
import de.dreier.mytargets.shared.models.Dimension;
import de.dreier.mytargets.shared.models.Passe;
import de.dreier.mytargets.shared.models.Round;
import de.dreier.mytargets.shared.models.Shot;
import de.dreier.mytargets.shared.models.StandardRound;
import de.dreier.mytargets.shared.models.Target;
import de.dreier.mytargets.shared.models.Training;
import de.dreier.mytargets.shared.targets.ScoringStyle;
import de.dreier.mytargets.shared.utils.StandardRoundFactory;

public class HtmlUtils {

    private static final String CSS = "<style type=\"text/css\">\n" +
            "body{font-family: Roboto, Sans-serif;}\n" +
            ".myTable { border-collapse:collapse; width:100%; }\n" +
            ".myTable td, .myTable th { padding:5px; border:1px solid #000; }\n" +
            ".align_left td {text-align: left; }\n" +
            ".align_center td {text-align: center; }" +
            ".circle {border-radius: 50%; width: 20px; height: 20px; padding: 5px; text-align: center; margin: auto;}" +
            "</style>";

    public static String getScoreboard(Context context, long trainingId, ScoreboardConfiguration configuration) {
        // Query information from database
        RoundDataSource roundDataSource = new RoundDataSource();
        Training training = new TrainingDataSource().get(trainingId);
        ArrayList<Round> rounds = roundDataSource.getAll(trainingId);

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
            html += "<table class=\"myTable\">";
            html += getTableHeader(context, rounds.get(0).info.arrowsPerPasse);
            int carry = 0;
            for (int r = 0; r < rounds.size(); r++) {
                Round round = rounds.get(r);
                html += "<tr class=\"align_left\"><td colspan=\"" +
                        (round.info.arrowsPerPasse + 2) +
                        "\">";
                html += "<b>" + context.getString(R.string.round) + " " + (round.info.index + 1) +
                        "</b>";
                if (configuration.showProperties) {
                    html += "<br />" + getRoundInfo(round, equals);
                }
                html += "</td></tr>";
                ArrayList<Passe> passes = new PasseDataSource().getAllByRound(round.getId());
                for (Passe passe : passes) {
                    html += "<tr class=\"align_center\">";
                    int sum = 0;
                    for (int i = 0; i < passe.shot.length; i++) {
                        Shot shot = passe.shot[i];
                        html += "<td>";
                        final Target target = round.info.target;
                        if (configuration.showPointsColored) {
                            int fillColor = target.getModel().getFillColor(shot.zone);
                            int color = target.getModel().getTextColor(shot.zone);
                            html += String
                                    .format("<div class=\"circle\" style='background: #%06X; color: #%06X'>",
                                            fillColor & 0xFFFFFF, color & 0xFFFFFF);
                        }
                        html += target.zoneToString(shot.zone, i);
                        if (configuration.showPointsColored) {
                            html += "</div>";
                        }
                        html += "</td>";
                        int points = target.getPointsByZone(shot.zone, i);
                        sum += points;
                        carry += points;
                    }
                    html += "<td>" + sum + "</td>";
                    html += "<td>" + carry + "</td>";
                    html += "</tr>";
                }
            }
            html += "</table>";
        }

        if (configuration.showComments) {
            html += getComments(context, rounds);
        }

        if (configuration.showDispersionPattern) {
            html += getTarget(trainingId);
        }

        if (configuration.showSignature) {
            html += getSignature();
        }

        html += "</html>";
        return html;
    }

    private static String getTableHeader(Context context, int ppp) {
        String html = "<tr class=\"align_center\">" +
                "<th colspan=\"" + ppp + "\">" + context.getString(R.string.arrows) +
                "</th>" +
                "<th rowspan=\"2\">" + context.getString(R.string.sum) + "</th>" +
                "<th rowspan=\"2\">" + context.getString(R.string.carry) + "</th>" +
                "</tr><tr class=\"align_center\">";
        for (int i = 1; i <= ppp; i++) {
            html += "<th>" + i + "</th>";
        }
        html += "</tr>";
        return html;
    }

    private static String getComments(Context context, ArrayList<Round> rounds) {
        String comments =
                "<table class=\"myTable\" style=\"margin-top:5px;\"><tr class=\"align_center\">" +
                        "<th>" + context.getString(R.string.round) + "</th>" +
                        "<th>" + context.getString(R.string.passe) + "</th>" +
                        "<th>" + context.getString(R.string.points) + "</th>" +
                        "<th>" + context.getString(R.string.comment) + "</th></tr>";
        int commentsCount = 0;

        int j = 0;
        for (Round round : rounds) {
            int i = 1;
            ArrayList<Passe> passes = new PasseDataSource().getAllByRound(round.getId());
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

    @NonNull
    private static String getTarget(long trainingId) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        new TargetImage().generateTrainingBitmap(800, trainingId, byteArrayOutputStream);

        // Convert bitmap to Base64 encoded image for web
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        String imageBase64 = Base64.encodeToString(byteArray, Base64.DEFAULT);
        String image = "data:image/png;base64," + imageBase64;
        return "<div align='center' style=\"padding: 15px;\"><img src='" + image +
                "' width='98%' /></div>";
    }

    public static String getRoundInfo(Round round, boolean[] equals) {
        int maxPoints = round.info.getMaxPoints();
        int reachedPoints = round.reachedPoints;

        String percent = maxPoints == 0 ? "" : " (" + (reachedPoints * 100 / maxPoints) + "%)";
        HTMLInfoBuilder info = new HTMLInfoBuilder();
        info.addLine(R.string.points, reachedPoints + "/" + maxPoints + percent);
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
    public static String getTrainingTopScoreDistribution(long training) {
        int maxPoints = 0;
        int reachedPoints = 0;
        ArrayList<Round> rounds = new RoundDataSource().getAll(training);
        for (Round r : rounds) {
            maxPoints += r.info.getMaxPoints();
            reachedPoints += r.reachedPoints;
        }
        String percent = maxPoints == 0 ? "" :
                " (" + (reachedPoints * 100 / maxPoints) + "%)";
        String value = reachedPoints + "/" + maxPoints + percent;

        HTMLInfoBuilder info = new HTMLInfoBuilder();
        info.addLine(R.string.points, value);

        PasseDataSource passeDataSource = new PasseDataSource();

        List<Pair<String, Integer>> scoreCount = passeDataSource.getTopScoreDistribution(training);
        int misses = 0;
        int hits = 0;
        for (Pair<String, Integer> score : scoreCount) {
            if (score.getFirst().equals(ScoringStyle.MISS_SYMBOL)) {
                misses += score.getSecond();
            } else {
                hits += score.getSecond();
            }
        }
        info.addLine(R.string.hits, hits);
        info.addLine(R.string.misses, misses);
        for (Pair<String, Integer> score : scoreCount
                .subList(0, Math.min(scoreCount.size(), 3))) {
            info.addLine(score.getFirst(), score.getSecond());
        }

        info.addLine(R.string.average, passeDataSource.getAverageScore(training));
        return info.toString();
    }

    public static String getTrainingInfoHTML(Training training, ArrayList<Round> rounds, boolean[] equals, boolean scoreboard) {
        HTMLInfoBuilder info = new HTMLInfoBuilder();
        if (scoreboard) {
            final String fullName = SettingsManager.getProfileFullName();
            if (!fullName.isEmpty()) {
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
            // Aggregate round information
            Round round = rounds.get(0);
            Dimension distance = round.info.distance;
            Target target = round.info.target;
            equals[0] = true;
            equals[1] = true;
            for (Round r : rounds) {
                equals[0] = r.info.distance.equals(distance) && equals[0];
                equals[1] = r.info.target.equals(target) && equals[1];
            }

            if (equals[0]) {
                final int envStringResId = standardRound.indoor ? R.string.indoor : R.string.outdoor;
                final String distanceValue = String.format("%s - %s", distance,
                        ApplicationInstance.getContext().getString(envStringResId));
                info.addLine(R.string.distance, distanceValue);
            }
            if (equals[1]) {
                info.addLine(R.string.target_face, target);
            }
        }
        return info.toString();
    }

    private static String getSignature() {
        return "<div style=\"border-top: 2px solid black; width: 30%;margin-right: 5%;margin-top: 100px;float:left;\">" +
                ApplicationInstance.getContext().getString(R.string.witness)
                + "</div>" +
                "<div style=\"border-top: 2px solid black; width: 30%;float:left; margin-top: 100px;\">" +
                ApplicationInstance.getContext().getString(R.string.archer)
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
