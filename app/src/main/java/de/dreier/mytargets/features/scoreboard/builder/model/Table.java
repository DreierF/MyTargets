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

package de.dreier.mytargets.features.scoreboard.builder.model;

import java.util.ArrayList;
import java.util.List;

public class Table extends Cell {

    public List<Row> rows = new ArrayList<>();

    public class Row {

        public List<Cell> cells = new ArrayList<>();

        public class TextCell extends Cell {
            public final String content;
            public boolean bold;

            public TextCell(String content, boolean bold) {
                this.content = content;
                this.bold = bold;
            }
        }

        public class EndCell extends Cell {
            public final String score;
            public final int fillColor;
            public final int textColor;
            public final String arrowNumber;

            public EndCell(String score, int fillColor, int textColor, String arrowNumber) {
                this.score = score;
                this.fillColor = fillColor;
                this.textColor = textColor;
                this.arrowNumber = arrowNumber;
            }
        }

        public void addCell(Cell cell) {
            cells.add(cell);
        }

        public Row addCell(String content) {
            cells.add(new TextCell(content, false));
            return this;
        }

        public Row addCell(int number) {
            return addCell(String.valueOf(number));
        }

        public Row addBoldCell(String content) {
            cells.add(new TextCell(content, true));
            return this;
        }

        public Row addBoldCell(String content, int columnSpan) {
            TextCell textCell = new TextCell(content, true);
            textCell.columnSpan = columnSpan;
            cells.add(textCell);
            return this;
        }

        public void addEndCell(String content, int fillColor, int textColor, String arrowNumber) {
            cells.add(new Row.EndCell(content, fillColor, textColor, arrowNumber));
        }
    }

    public Row startRow() {
        Row row = new Row();
        rows.add(row);
        return row;
    }
}
