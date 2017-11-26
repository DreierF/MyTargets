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

import java.util.List;

public class Table {

    public class Row {
        public List<Row.Cell> cells;

        public class Cell {
            public int columnSpan = 1;
            public int rowSpan = 1;
            public final String content;
            public final boolean bold;

            public Cell(String content, boolean bold) {
                this.content = content;
                this.bold = bold;
            }

            public Cell(String content, boolean bold, int columnSpan, int rowSpan) {
                this.content = content;
                this.bold = bold;
                this.columnSpan = columnSpan;
                this.rowSpan = rowSpan;
            }
        }

        public Row addCell(String content) {
            cells.add(new Row.Cell(content, false));
            return this;
        }

        public Row addBoldCell(String content) {
            return addCell(content, true, 1,1);
        }

        public Row addCell(int number) {
            cells.add(new Row.Cell(String.valueOf(number), false));
            return this;
        }

        public Row addCell(String content, boolean bold, int columnSpan, int rowSpan) {
            cells.add(new Row.Cell(content, bold, columnSpan, rowSpan));
            return this;
        }

        public Row nextRow() {
            return Table.this.startRow();
        }
    }

    public List<Row> rows;

    public Row startRow() {
        Row row = new Row();
        rows.add(row);
        return row;
    }
}
