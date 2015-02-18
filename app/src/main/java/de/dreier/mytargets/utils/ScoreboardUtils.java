package de.dreier.mytargets.utils;

import android.content.Context;
import android.database.Cursor;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import de.dreier.mytargets.R;
import de.dreier.mytargets.managers.DatabaseManager;
import de.dreier.mytargets.models.Round;
import de.dreier.mytargets.models.Shot;
import de.dreier.mytargets.models.Target;

public class ScoreboardUtils {

    private static final String CSS = "<style type=\"text/css\">\n" +
            ".myTable { border-collapse:collapse; width:100%; }\n" +
            ".myTable td, .myTable th { padding:5px; border:1px solid #000; text-align: center; font-family: Roboto, Sans-serif; }\n" +
            "</style>";

    public static String getHTMLString(Context context, long round, boolean withTarget) {
        DatabaseManager db = new DatabaseManager(context);
        Round info = db.getRound(round);
        Cursor cur = db.getPasses(round);
        String html = "<html>" + CSS +
                "<table class=\"myTable\"><tr>";
        for (int i = 1; i <= info.ppp; i++) {
            html += "<th>" + i + "</th>";
        }
        html += context.getString(R.string.html_header_fields);
        html += "</tr>";
        int sum = 0, carry = 0, count = 0;
        int i = 0;
        String tmp_html = "";
        if (cur.moveToFirst()) {
            do {
                int passe_id = cur.getInt(0);
                int[] passe = db.getPasse(passe_id);
                int arrows = 0;
                if (i % 2 == 1) {
                    tmp_html += "<tr>";
                    for (int aPasse : passe) {
                        tmp_html += "<td>";
                        tmp_html += Target.getStringByZone(info.target, aPasse, info.compound);
                        tmp_html += "</td>";
                        int points = Target.getPointsByZone(info.target, aPasse, info.compound);
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
                    for (int aPasse : passe) {
                        html += "<td>";
                        html += Target.getStringByZone(info.target, aPasse, info.compound);
                        html += "</td>";
                        int points = Target.getPointsByZone(info.target, aPasse, info.compound);
                        arrows += points;
                        sum += points;
                        carry += points;
                        count++;
                    }
                    html += "<td>" + arrows + "</td>";
                }
                i++;
                tmp_html += "</tr>";
            } while (cur.moveToNext());
        }
        if (i % 2 == 1) {
            html += "<td rowspan=\"2\">" + sum + "</td>";
            html += "<td rowspan=\"2\">" + carry + "</td>";
        }
        html += tmp_html;
        db.close();
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

        //TODO hide comments in share image
        ArrayList<Shot> shots = db.getCommentedShots(round);
        if (shots != null && shots.size() > 0) {
            html += "<table class=\"myTable\" style=\"margin-top:5px;\">" +
                    "<tr><th colspan=\"3\">"+context.getString(R.string.comments)+"</th></tr>"+
                    "<th>" + context.getString(R.string.passe) + "</th>" + //TODO right passe e.g. 2 instead of 38
                    "<th>" + context.getString(R.string.points) + "</th>" +
                    "<th>" + context.getString(R.string.comment) + "</th></tr>";
            for (Shot s : shots) {
                html += "<tr><td>" + s.passe + "</td>" +
                        "<td>" + Target.getStringByZone(info.target, s.zone, info.compound) + "</td>" +
                        "<td>" + s.comment + "</td></tr>";
            }
            html += "</table>";
        }

        if (withTarget) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            new TargetImage().generateBitmap(context, 800, info, round, byteArrayOutputStream);

            // Convert bitmap to Base64 encoded image for web
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            String imageBase64 = Base64.encodeToString(byteArray, Base64.DEFAULT);
            String image = "data:image/png;base64," + imageBase64;
            html += "<div align='center' style=\"padding: 20px;\"><img src='" + image + "' width='60%' /></div>";
        }

        html += "</html>";
        return html;
    }
}
