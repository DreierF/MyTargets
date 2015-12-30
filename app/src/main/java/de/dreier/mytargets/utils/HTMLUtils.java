/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */

package de.dreier.mytargets.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.util.ArrayList;

import de.dreier.mytargets.R;
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
import de.dreier.mytargets.shared.models.Training;
import de.dreier.mytargets.shared.models.target.Target;

public class HTMLUtils {

    private static final String CSS = "<style type=\"text/css\">\n" +
            "body{font-family: Roboto, Sans-serif;}\n" +
            ".myTable { border-collapse:collapse; width:100%; }\n" +
            ".myTable td, .myTable th { padding:5px; border:1px solid #000; }\n" +
            ".align_left td {text-align: left; }\n" +
            ".align_center td {text-align: center; }" +
            "</style>";

    public static String getScorebard(Context context, long trainingId, boolean withTarget) {
        // Query information from database
        RoundDataSource roundDataSource = new RoundDataSource(context);
        Training training = new TrainingDataSource(context).get(trainingId);
        ArrayList<Round> rounds = roundDataSource.getAll(trainingId);

        // Initialize html Strings
        String html = "<html>" + CSS;

        String formattedDate = DateFormat.getDateInstance().format(training.date);
        html += "<h3>" + training.title + " (" + formattedDate + ")</h3>";
        boolean[] equals = new boolean[2];
        html += "<table width=\"100%\" style=\"border:0px;\"><tr>" +
                "<td width=\"50%\">" + getTrainingInfoHTML(context, training, rounds, equals) + "</td>" +
                "<td width=\"50%\">" + getTrainingTopScoreDistribution(context, trainingId) + "</td>" +
                "</tr></table>";

        html += "<table class=\"myTable\">";
        html += getTableHeader(context, rounds.get(0).info.arrowsPerPasse);
        int carry = 0;
        for (int r = 0; r < rounds.size(); r++) {
            Round round = rounds.get(r);
            html += "<tr class=\"align_left\"><td colspan=\"" +
                    (round.info.arrowsPerPasse + 2) +
                    "\">";
            html += "<b>" + context.getString(R.string.round) + " " + (round.info.index + 1) +
                    "</b><br />" +
                    getRoundInfoHTML(context, round, equals) +
                    "</td></tr>";
            ArrayList<Passe> passes = new PasseDataSource(context).getAllByRound(round.getId());
            for (Passe passe : passes) {
                html += "<tr class=\"align_center\">";
                int sum = 0;
                for (int i = 0; i < passe.shot.length; i++) {
                    Shot shot = passe.shot[i];
                    html += "<td>";
                    html += round.info.target.zoneToString(shot.zone, i);
                    html += "</td>";
                    int points = round.info.target.getPointsByZone(shot.zone, i);
                    sum += points;
                    carry += points;
                }
                html += "<td>" + sum + "</td>";
                html += "<td>" + carry + "</td>";
                html += "</tr>";
            }
        }
        html += "</table>";

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
            ArrayList<Passe> passes = new PasseDataSource(context).getAllByRound(round.getId());
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
        if (commentsCount > 0) {
            html += comments + "</table>";
        }

        if (withTarget) {
            html += getTargetHTML(context, trainingId);
        }

        html += "</html>";
        return html;
    }

    @NonNull
    private static String getTargetHTML(Context context, long trainingId) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        new TargetImage().generateTrainingBitmap(context, 800, trainingId, byteArrayOutputStream);

        // Convert bitmap to Base64 encoded image for web
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        String imageBase64 = Base64.encodeToString(byteArray, Base64.DEFAULT);
        String image = "data:image/png;base64," + imageBase64;
        return "<div align='center' style=\"padding: 15px;\"><img src='" + image +
                "' width='98%' /></div>";
    }

    public static String getRoundInfoHTML(Context context, Round round, boolean[] equals) {
        int maxPoints = round.info.getMaxPoints();
        int reachedPoints = round.reachedPoints;

        String percent = maxPoints == 0 ? "" : " (" + (reachedPoints * 100 / maxPoints) + "%)";
        String infoText = context.getString(R.string.points) + ": <b>" +
                reachedPoints + "/" + maxPoints + percent + "</b>";
        if (!equals[0]) {
            infoText += "<br>" + context.getString(R.string.distance) + ": <b>" +
                    round.info.distance.toString(context) + "</b>";
        }
        if (!equals[1]) {
            infoText += "<br>" + context.getString(R.string.target_face) + ": <b>" +
                    round.info.target + "</b>";
        }
        if (!round.comment.isEmpty()) {
            infoText += "<br>" + context.getString(R.string.comment) +
                    ": <b>" + TextUtils.htmlEncode(round.comment) + "</b>";
        }
        return infoText;
    }

    @NonNull
    public static String getTrainingTopScoreDistribution(Context context, long training) {
        String infoText = "";
        int maxPoints = 0;
        int reachedPoints = 0;
        ArrayList<Round> rounds = new RoundDataSource(context).getAll(training);
        for (Round r : rounds) {
            maxPoints += r.info.getMaxPoints();
            reachedPoints += r.reachedPoints;
        }
        String percent = maxPoints == 0 ? "" :
                " (" + (reachedPoints * 100 / maxPoints) + "%)";
        String value = reachedPoints + "/" + maxPoints + percent;
        infoText += getKeyValueLine(context.getString(R.string.points), value);

        PasseDataSource passeDataSource = new PasseDataSource(context);

        ArrayList<Pair<String, Integer>> scoreCount = passeDataSource.getTopScoreDistribution(training);
        for (Pair<String, Integer> score : scoreCount) {
            infoText += getKeyValueLine(score.getFirst(), score.getSecond());
        }
        ;
        infoText += getKeyValueLine(context.getString(R.string.average),
                passeDataSource.getAverageScore(training));
        return infoText;
    }

    @NonNull
    private static String getKeyValueLine(String key, Object value) {
        return key + ": <b>" + value + "</b><br>";
    }

    public static String getTrainingInfoHTML(Context context, Training training, ArrayList<Round> rounds, boolean[] equals) {
        StandardRound standardRound = new StandardRoundDataSource(context).get(
                training.standardRoundId);
        boolean indoor = standardRound.indoor;

        String infoText = "";
        infoText += context.getString(R.string.standard_round) + ": <b>" + TextUtils
                .htmlEncode(standardRound.name) + "</b>";

        // Set round info
        Bow bow = new BowDataSource(context).get(training.bow);
        if (bow != null) {
            infoText += "<br>" + context.getString(R.string.bow) +
                    ": <b>" + TextUtils.htmlEncode(bow.name) + "</b>";
        }

        Arrow arrow = new ArrowDataSource(context).get(training.arrow);
        if (arrow != null) {
            infoText += "<br>" + context.getString(R.string.arrow) +
                    ": <b>" + TextUtils.htmlEncode(arrow.name) + "</b>";
        }

        if (rounds.size() > 0) {
            // Aggregate round information
            Round round = rounds.get(0);
            String distance = round.info.distance.toString(context);
            Target target = round.info.target;
            equals[0] = true;
            equals[1] = true;
            for (Round r : rounds) {
                equals[0] =
                        r.info.distance.toString(context).equals(distance) &&
                                equals[0];
                equals[1] = r.info.target.equals(target) && equals[1];
            }


            if (equals[0]) {
                infoText += "<br>" + context.getString(R.string.distance) + ": <b>" +
                        distance + " - " +
                        context.getString(indoor ? R.string.indoor : R.string.outdoor) +
                        "</b>";
            }
            if (equals[1]) {
                infoText += "<br>" + context.getString(R.string.target_face) + ": <b>" +
                        target + "</b>";
            }
        }
        return infoText;
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
}
