/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */

package de.dreier.mytargets.utils;

import android.content.Context;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.util.ArrayList;

import de.dreier.mytargets.R;
import de.dreier.mytargets.managers.DatabaseManager;
import de.dreier.mytargets.shared.models.Arrow;
import de.dreier.mytargets.shared.models.Bow;
import de.dreier.mytargets.shared.models.Passe;
import de.dreier.mytargets.shared.models.Round;
import de.dreier.mytargets.shared.models.Shot;
import de.dreier.mytargets.shared.models.StandardRound;
import de.dreier.mytargets.shared.models.Training;
import de.dreier.mytargets.shared.models.target.Target;

public class ScoreboardUtils {

    private static final String CSS = "<style type=\"text/css\">\n" +
            "body{font-family: Roboto, Sans-serif;}\n" +
            ".myTable { border-collapse:collapse; width:100%; }\n" +
            ".myTable td, .myTable th { padding:5px; border:1px solid #000; }\n" +
            ".align_left td {text-align: left; }\n" +
            ".align_center td {text-align: center; }" +
            "</style>";

    public static String getHTMLString(Context context, long trainingId, boolean scoreboard, boolean withTarget, boolean showComments) {
        // Query information from database
        DatabaseManager db = DatabaseManager.getInstance(context);
        Training training = db.getTraining(trainingId);
        ArrayList<Round> rounds = db.getRounds(trainingId);

        // Initialize html Strings
        String html = "<html>" + CSS;

        if (scoreboard) {
            String formattedDate = DateFormat.getDateInstance().format(training.date);
            html += "<h3>" + training.title + " (" + formattedDate + ")</h3>";
            boolean[] equals = new boolean[2];
            html += getTrainingInfoHTML(context, db, training, rounds, equals);

            html += "<table class=\"myTable\">";
            html += getTableHeader(context, rounds.get(0).info.arrowsPerPasse);
            int carry = 0, count = 0;
            for (int r = 0; r < rounds.size(); r++) {
                Round round = rounds.get(r);
                html += "<tr class=\"align_left\"><td colspan=\"" +
                        (round.info.arrowsPerPasse + 2) +
                        "\">";
                html += "<b>" + context.getString(R.string.round) + " " + (round.info.index + 1) +
                        "</b><br />" +
                        getRoundInfoHTML(context, round, equals) +
                        "</td></tr>";
                ArrayList<Passe> passes = db.getPassesOfRound(round.getId());
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
                        count++;
                    }
                    html += "<td>" + sum + "</td>";
                    html += "<td>" + carry + "</td>";
                    html += "</tr>";
                }
            }
            float avg = 0;
            if (count > 0) {
                avg = ((carry * 100) / count) / 100.0f;
            }
            html += "</table>";
            if (rounds.size() > 0) {
                ArrayList<Pair<String, Integer>> scoreCount = db
                        .getTrainingTopScoreDistribution(trainingId);
                html += "<table class=\"myTable\" style=\"margin-top:5px;\">" +
                        "<tr class=\"align_center\"><th>" + scoreCount.get(2).getFirst() + "</th>" +
                        "<th>" + scoreCount.get(1).getFirst() + "</th>" +
                        "<th>" + scoreCount.get(0).getFirst() + "</th>" +
                        "<th>" + context.getString(R.string.average) + "</th></tr>" +
                        "<tr class=\"align_center\"><td>" + scoreCount.get(2).getSecond() +
                        "</td>" +
                        "<td>" + scoreCount.get(1).getSecond() + "</td>" +
                        "<td>" + scoreCount.get(0).getSecond() + "</td>" +
                        "<td>" + avg + "</td>" +
                        "</tr></table>";
            }
        }

        if (showComments) {
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
                ArrayList<Passe> passes = db.getPasses(round.getId());
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
        }

        if (withTarget) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            new TargetImage().generateBitmap(context, 800, trainingId, byteArrayOutputStream);

            // Convert bitmap to Base64 encoded image for web
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            String imageBase64 = Base64.encodeToString(byteArray, Base64.DEFAULT);
            String image = "data:image/png;base64," + imageBase64;
            html += "<div align='center' style=\"padding: 15px;\"><img src='" + image +
                    "' width='98%' /></div>";
        }

        html += "</html>";
        Log.d("html", html);
        return html;
    }

    public static String getRoundInfoHTML(Context context, Round round, boolean[] equals) {
        int maxPoints = round.info.getMaxPoints();
        int reachedPoints = round.reachedPoints;

        String percent = maxPoints == 0 ? "" : " (" + (reachedPoints * 100 / maxPoints) + "%)";
        String infoText = context.getString(R.string.points) + ": <b>" +
                reachedPoints + "/" + maxPoints + percent + "</b>";
        if (!equals[0]) {
            infoText += "<br>" + context.getString(R.string.distance) + ": <b>" +
                    round.info.distance.toString(context);
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

    public static String getTrainingInfoHTML(Context context, DatabaseManager db, Training training, ArrayList<Round> rounds, boolean[] equals) {
        StandardRound standardRound = db.getStandardRound(training.standardRoundId);
        boolean indoor = standardRound.indoor;

        int maxPoints = 0;
        int reachedPoints = 0;
        for (Round r : rounds) {
            maxPoints += r.info.getMaxPoints();
            reachedPoints += r.reachedPoints;
        }
        String percent = maxPoints == 0 ? "" :
                " (" + (reachedPoints * 100 / maxPoints) + "%)";
        String infoText = context.getString(R.string.points) + ": <b>" +
                reachedPoints + "/" +
                maxPoints + percent + "</b>";

        infoText += "<br>" + context.getString(R.string.standard_round) + ": <b>" + TextUtils
                .htmlEncode(standardRound.name) + "</b>";

        // Set round info
        Bow bow = db.getBow(training.bow, true);
        if (bow != null) {
            infoText += "<br>" + context.getString(R.string.bow) +
                    ": <b>" + TextUtils.htmlEncode(bow.name) + "</b>";
        }

        Arrow arrow = db.getArrow(training.arrow, true);
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
