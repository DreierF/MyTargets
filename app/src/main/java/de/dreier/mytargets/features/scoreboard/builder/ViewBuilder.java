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

package de.dreier.mytargets.features.scoreboard.builder;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import de.dreier.mytargets.R;
import de.dreier.mytargets.features.scoreboard.ScoreboardBuilder;
import de.dreier.mytargets.features.scoreboard.builder.model.Cell;
import de.dreier.mytargets.features.scoreboard.builder.model.Table;
import de.dreier.mytargets.shared.utils.CircleDrawable;

import static android.util.TypedValue.COMPLEX_UNIT_SP;
import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class ViewBuilder implements ScoreboardBuilder {

    private final Context context;
    @NonNull
    private final LayoutInflater inflater;
    @NonNull
    private final LinearLayout container;
    private float density;

    public ViewBuilder(Context context, @NonNull LayoutInflater inflater, @NonNull LinearLayout container) {
        this.context = context;
        this.inflater = inflater;
        this.container = container;
        density = context.getResources().getDisplayMetrics().density;
    }

    @Override
    public void title(String title) {
        TextView titleView = (TextView) inflater
                .inflate(R.layout.partial_scoreboard_title, container, false);
        titleView.setTextSize(COMPLEX_UNIT_SP, 16);
        titleView.setText(title);
        container.addView(titleView);
    }

    @Override
    public void subtitle(String subtitle) {
        TextView subtitleView = (TextView) inflater
                .inflate(R.layout.partial_scoreboard_title, container, false);
        subtitleView.setTextSize(COMPLEX_UNIT_SP, 14);
        subtitleView.setText(subtitle);
        container.addView(subtitleView);
    }

    @Override
    public void table(Table table) {
        TableLayout tableLayout = createTableLayout(table);
        tableLayout.setLayoutParams(new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT));
        container.addView(tableLayout);
    }

    @NonNull
    private TableLayout createTableLayout(Table table) {
        TableLayout tableLayout = new TableLayout(context);
        tableLayout.setBackgroundResource(R.drawable.table_cell_border);

        for (Table.Row row : table.rows) {
            TableRow tableRow = new TableRow(context);
            tableRow.setLayoutParams(new TableLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT));
            tableLayout.addView(tableRow);
            for (Cell cell : row.cells) {
                View cellView;
                if (cell instanceof Table.Row.EndCell) {
                    Table.Row.EndCell endCell = (Table.Row.EndCell) cell;
                    cellView = inflater
                            .inflate(R.layout.partial_scoreboard_table_end_cell, tableRow, false);
                    ((ImageView) cellView)
                            .setImageDrawable(new CircleDrawable(density, endCell.score, endCell.arrowNumber, endCell.fillColor, endCell.textColor));
                } else if (cell instanceof Table) {
                    TableLayout tableLayout1 = createTableLayout((Table) cell);
                    tableLayout1
                            .setLayoutParams(new TableRow.LayoutParams(WRAP_CONTENT, WRAP_CONTENT));
                    cellView = tableLayout1;
                } else {
                    Table.Row.TextCell textCell = (Table.Row.TextCell) cell;
                    cellView = inflater
                            .inflate(R.layout.partial_scoreboard_table_cell, tableRow, false);
                    ((TextView) cellView).setText(textCell.content);

                    if (textCell.bold) {
                        ((TextView) cellView).setTypeface(null, Typeface.BOLD);
                    }
                }
                TableRow.LayoutParams params = (TableRow.LayoutParams) cellView.getLayoutParams();
                if (cell.columnSpan > 1) {
                    params.weight = 1;
                    params.span = cell.columnSpan;
                }
                cellView.setLayoutParams(params);
                tableRow.addView(cellView);
            }
        }
        return tableLayout;
    }

    @Override
    public void space() {

    }

    @Override
    public void signature(String archer, String targetCaptain) {

    }
}
