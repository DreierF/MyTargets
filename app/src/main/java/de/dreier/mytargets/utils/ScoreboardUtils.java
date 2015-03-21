package de.dreier.mytargets.utils;

import android.content.Context;
import android.text.TextUtils;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import de.dreier.mytargets.R;
import de.dreier.mytargets.managers.DatabaseManager;
import de.dreier.mytargets.models.Passe;
import de.dreier.mytargets.models.Round;
import de.dreier.mytargets.models.Shot;

public class ScoreboardUtils {

    private static final String CSS = "<style type=\"text/css\">\n" +
            ".myTable { border-collapse:collapse; width:100%; }\n" +
            ".myTable td, .myTable th { padding:5px; border:1px solid #000; text-align: center; font-family: Roboto, Sans-serif; }\n" +
            "</style>";

    public static String getHTMLString(Context context, long round, boolean scoreboard, boolean withTarget, boolean showComments) {
        // Query information from database
        DatabaseManager db = DatabaseManager.getInstance(context);
        Round info = db.getRound(round);
        ArrayList<Passe> passes = db.getPasses(round);

        // Initialize html Strings
        String html = "<html>" + CSS;

        if (scoreboard) {
            html += "<table class=\"myTable\"><tr>" +
                    "<th colspan=\"" + info.ppp + "\">" + context.getString(R.string.arrows) +
                    "</th>" +
                    "<th rowspan=\"2\" colspan=\"2\">" + context.getString(R.string.sum) + "</th>" +
                    "<th rowspan=\"2\">" + context.getString(R.string.carry) + "</th>" +
                    "</tr><tr>";
            for (int i = 1; i <= info.ppp; i++) {
                html += "<th>" + i + "</th>";
            }
            html += "</tr>";
            int sum = 0, carry = 0, count = 0;
            int i = 0;
            String tmp_html = "";
            for (Passe passe : passes) {
                int arrows = 0;
                if (i % 2 == 1) {
                    tmp_html += "<tr>";
                    for (Shot shot : passe.shot) {
                        tmp_html += "<td>";
                        tmp_html += Target.getStringByZone(info.target, shot.zone);
                        tmp_html += "</td>";
                        int points = Target.getPointsByZone(info.target, shot.zone);
                        arrows += points;
                        sum += points;
                        carry += points;
                        count++;
                    }
                    tmp_html += "<td>" + arrows + "</td>";
                    html += "<td rowspan=\"2\">" + sum + "</td>";
                    html += "<td rowspan=\"2\">" + carry + "</td>";
                    html += tmp_html;
                    tmp_html = "";
                    sum = 0;
                } else {
                    html += "<tr>";
                    for (Shot shot : passe.shot) {
                        html += "<td>";
                        html += Target.getStringByZone(info.target, shot.zone);
                        html += "</td>";
                        int points = Target.getPointsByZone(info.target, shot.zone);
                        arrows += points;
                        sum += points;
                        carry += points;
                        count++;
                    }
                    html += "<td>" + arrows + "</td>";
                }
                i++;
                tmp_html += "</tr>";
            }
            if (i % 2 == 1) {
                html += "<td rowspan=\"2\">" + sum + "</td>";
                html += "<td rowspan=\"2\">" + carry + "</td>";
            }
            html += tmp_html;
            float avg = ((carry * 100) / count) / 100.0f;
            html += "</table>";
            html += "<table class=\"myTable\" style=\"margin-top:5px;\">" +
                    "<tr><th>" + context.getString(R.string.nine) + "</th>" +
                    "<th>" + context.getString(R.string.ten_x) + "</th>" +
                    "<th>X</th>" +
                    "<th>" + context.getString(R.string.average) + "</th></tr>" +
                    "<tr><td>" + info.scoreCount[2] + "</td>" +
                    "<td>" + (info.scoreCount[0] + info.scoreCount[1]) + "</td>" +
                    "<td>" + info.scoreCount[0] + "</td>" +
                    "<td>" + avg + "</td>" +
                    "</tr></table>";
        }

        if (showComments) {
            String comments = "<table class=\"myTable\" style=\"margin-top:5px;\">" +
                    "<th>" + context.getString(R.string.passe) + "</th>" +
                    "<th>" + context.getString(R.string.points) + "</th>" +
                    "<th>" + context.getString(R.string.comment) + "</th></tr>";
            int commentsCount = 0;

            int i = 1;
            for (Passe passe : passes) {
                for (Shot shot : passe.shot) {
                    if (!TextUtils.isEmpty(shot.comment)) {
                        comments += "<tr><td>" + i + "</td>" +
                                "<td>" + Target.getStringByZone(info.target, shot.zone) + "</td>" +
                                "<td>" +
                                TextUtils.htmlEncode(shot.comment).replace("\n", "<br />") +
                                "</td></tr>";
                        commentsCount++;
                    }
                }
                i++;
            }

            // If a minimum of one comment is present show comments table
            if (commentsCount > 0) {
                html += comments + "</table>";
            }
        }

        if (withTarget) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            new TargetImage().generateBitmap(context, 800, info, round, byteArrayOutputStream);

            // Convert bitmap to Base64 encoded image for web
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            String imageBase64 = Base64.encodeToString(byteArray, Base64.DEFAULT);
            String image = "data:image/png;base64," + imageBase64;
            html += "<div align='center' style=\"padding: 20px;\"><img src='" + image +
                    "' width='60%' /></div>";
        }

        html += "</html>";
        return html;
    }
}
