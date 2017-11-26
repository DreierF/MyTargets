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

import android.text.TextUtils;

import de.dreier.mytargets.features.scoreboard.builder.model.Table;

public class HTMLBuilder {

    public static final String BR = "<br>";
    private static final String CSS = "<style type=\"text/css\">\n" +
            "body{font-family: Roboto, Sans-serif;}\n" +
            ".myTable { border-collapse:collapse; width:100%; }\n" +
            ".myTable td { padding:5px; border:1px solid #000; }\n" +
            ".align_left td {text-align: left; }\n" +
            ".align_center td {text-align: center; }\n" +
            ".circle {border-radius: 50%; width: 20px; height: 20px; padding: 5px; text-align: center; margin: auto; position: relative;}\n" +
            ".circle_arrow { border-radius: 50%; width: 13px; height: 13px; \n" +
            "  padding: 2px; font-size: 11px; text-align: center; \n" +
            "  vertical-align: center; background: black; color: white;\n" +
            "  position: absolute; bottom: -4px; right: -4px; }" +
            "</style>";

    private StringBuilder html = new StringBuilder();

    public HTMLBuilder() {
        html.append("<html>");
        html.append(CSS);
    }

    public void title(String title) {
        html.append("<h3>");
        html.append(TextUtils.htmlEncode(title));
        html.append("</h3>");
    }

    public void subtitle(String subtitle) {
        html.append("<b>");
        html.append(TextUtils.htmlEncode(subtitle));
        html.append("</b>");
    }

    public String build() {
        html.append("</html>");
        return html.toString();
    }

    public void table(Table table) {
        html.append(BR);
        html.append("<table class=\"myTable\">");
        for (Table.Row row : table.rows) {
            html.append("<tr class=\"align_center\">");
            for (Table.Row.Cell cell : row.cells) {
                html.append("<td");
                if (cell.rowSpan > 1) {
                    html.append(" rowspan=\"").append(cell.rowSpan).append("\"");
                }
                if (cell.columnSpan > 1) {
                    html.append(" colspan=\"").append(cell.columnSpan).append("\"");
                }
                html.append(">");
                if (cell.bold) {
                    html.append("<b>");
                }
                html.append(TextUtils.htmlEncode(cell.content).replace("\n", BR));
                if (cell.bold) {
                    html.append("</b>");
                }
                html.append("</td>");
            }
            html.append("</tr>");
        }
        html.append("</table>");
    }

    public void space() {
        html.append(BR);
    }

    public void signature(String archer, String targetCaptain) {
        html.append("<div style=\"border-top: 2px solid black; width: 30%;margin-right: 5%;margin-top: 100px;float:left;\">");
        html.append(targetCaptain);
        html.append("</div>");
        html.append("<div style=\"border-top: 2px solid black; width: 30%;float:left; margin-top: 100px;\">");
        html.append(archer);
        html.append("</div>");
    }
}
