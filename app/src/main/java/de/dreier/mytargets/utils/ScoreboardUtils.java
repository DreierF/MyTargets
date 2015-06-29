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

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import de.dreier.mytargets.R;
import de.dreier.mytargets.managers.DatabaseManager;
import de.dreier.mytargets.shared.models.Passe;
import de.dreier.mytargets.shared.models.Round;
import de.dreier.mytargets.shared.models.Shot;
import de.dreier.mytargets.shared.models.Target;
import de.dreier.mytargets.shared.models.Training;

public class ScoreboardUtils {

    private static final String CSS = "<style type=\"text/css\">\n" +
            ".myTable { border-collapse:collapse; width:100%; }\n" +
            ".myTable td, .myTable th { padding:5px; border:1px solid #000; text-align: center; font-family: Roboto, Sans-serif; }\n" +
            "</style>";

    public static String getHTMLString(Context context, long trainingId, boolean scoreboard, boolean withTarget, boolean showComments) {
        // Query information from database
        DatabaseManager db = DatabaseManager.getInstance(context);
        Training training = db.getTraining(trainingId);
        ArrayList<Round> rounds = db.getRounds(trainingId);

        // Initialize html Strings
        String html = "<html>" + CSS;

        if (scoreboard) {
            html += "<table class=\"myTable\">";
            html += getTableHeader(context, rounds.get(0).info.arrowsPerPasse);
            int carry = 0, count = 0;
            for (Round round : rounds) {
                ArrayList<Passe> passes = db.getPasses(round.getId());
                for (Passe passe : passes) {
                    html += "<tr>";
                    int sum = 0;
                    for (Shot shot : passe.shot) {
                        html += "<td>";
                        html += Target.getStringByZone(round.info.target, shot.zone);
                        html += "</td>";
                        int points = Target.getPointsByZone(round.info.target, shot.zone);
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
            html += "<table class=\"myTable\" style=\"margin-top:5px;\">" +
                    "<tr><th>" + context.getString(R.string.nine) + "</th>" +
                    "<th>" + context.getString(R.string.ten_x) + "</th>" +
                    "<th>X</th>" +
                    "<th>" + context.getString(R.string.average) + "</th></tr>" +
                    "<tr><td>" + training.scoreCount[2] + "</td>" +
                    "<td>" + (training.scoreCount[0] + training.scoreCount[1]) + "</td>" +
                    "<td>" + training.scoreCount[0] + "</td>" +
                    "<td>" + avg + "</td>" +
                    "</tr></table>";
        }

        if (showComments) {
            String comments = "<table class=\"myTable\" style=\"margin-top:5px;\">" +
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
                    for (Shot shot : passe.shot) {
                        if (!TextUtils.isEmpty(shot.comment)) {
                            comments += "<tr><td>" + j + "</td>" +
                                    "<td>" + i + "</td>" +
                                    "<td>" + Target.getStringByZone(round.info.target, shot.zone) +
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
        return html;
    }

    private static String getTableHeader(Context context, int ppp) {
        String html = "<tr>" +
                "<th colspan=\"" + ppp + "\">" + context.getString(R.string.arrows) +
                "</th>" +
                "<th rowspan=\"2\">" + context.getString(R.string.sum) + "</th>" +
                "<th rowspan=\"2\">" + context.getString(R.string.carry) + "</th>" +
                "</tr><tr>";
        for (int i = 1; i <= ppp; i++) {
            html += "<th>" + i + "</th>";
        }
        html += "</tr>";
        return html;
    }
}
