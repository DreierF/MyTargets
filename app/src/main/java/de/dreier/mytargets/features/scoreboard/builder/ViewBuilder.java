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
import android.view.Gravity;
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

    @NonNull
    private final Context context;
    @NonNull
    private final LinearLayout root;
    @NonNull
    private LinearLayout container;
    private final float density;

    public ViewBuilder(@NonNull Context context) {
        this.context = context;
        this.root = new LinearLayout(context);
        this.container = root;
        this.density = context.getResources().getDisplayMetrics().density;
    }

    @Override
    public void title(String title) {
        TextView titleView = new TextView(context);
        titleView.setLayoutParams(new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT));
        titleView.setTextColor(0xFF000000);
        titleView.setPadding(0, dp(8), 0, dp(8));
        titleView.setTextSize(COMPLEX_UNIT_SP, 16);
        titleView.setTypeface(null, Typeface.BOLD);
        titleView.setText(title);
        container.addView(titleView);
    }

    @Override
    public void openSection() {
        container = new LinearLayout(context);
        container.setOrientation(LinearLayout.VERTICAL);
        root.addView(container);
    }

    @Override
    public void closeSection() {
        container = root;
    }

    @Override
    public void subtitle(String subtitle) {
        TextView subtitleView = new TextView(context);
        subtitleView.setLayoutParams(new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT));
        subtitleView.setTextColor(0xFF000000);
        subtitleView.setPadding(0, dp(8), 0, dp(8));
        subtitleView.setTextSize(COMPLEX_UNIT_SP, 14);
        subtitleView.setTypeface(null, Typeface.BOLD);
        subtitleView.setText(subtitle);
        container.addView(subtitleView);
    }

    @Override
    public void table(Table table) {
        TableLayout tableLayout = createTableLayout(table);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        if (table.wrapContent) {
            params.setMargins(0, 0, 0, dp(-2));
        }
        tableLayout.setLayoutParams(params);
        container.addView(tableLayout);
    }

    @NonNull
    private TableLayout createTableLayout(Table table) {
        TableLayout tableLayout = new TableLayout(context);
        tableLayout.setBackgroundResource(R.drawable.table_cell_border);
        if (!table.wrapContent) {
            tableLayout.setShrinkAllColumns(true);
            tableLayout.setStretchAllColumns(true);
        }

        for (Table.Row row : table.rows) {
            TableRow tableRow = createTableRow(row);
            tableLayout.addView(tableRow);
        }
        return tableLayout;
    }

    @NonNull
    private TableRow createTableRow(Table.Row row) {
        TableRow tableRow = new TableRow(context);
        tableRow.setLayoutParams(new TableLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT));
        for (Cell cell : row.cells) {
            View cellView;
            if (cell instanceof Table.Row.EndCell) {
                cellView = createEndCell((Table.Row.EndCell) cell);
            } else if (cell instanceof Table) {
                cellView = createTableLayout((Table) cell);
                // Avoid duplicated borders
                int padding = (int) -density;
                cellView.setPadding(padding, padding, padding, padding);
            } else {
                cellView = createTextCell((Table.Row.TextCell) cell);
            }
            TableRow.LayoutParams params = new TableRow.LayoutParams(MATCH_PARENT, MATCH_PARENT);
            if (cell.columnSpan > 1) {
                params.weight = 1;
                params.span = cell.columnSpan;
            }
            cellView.setLayoutParams(params);
            tableRow.addView(cellView);
        }
        return tableRow;
    }

    @NonNull
    private ImageView createEndCell(Table.Row.EndCell endCell) {
        ImageView imageView = new ImageView(context);
        imageView.setBackgroundResource(R.drawable.table_cell_border);
        imageView.setMinimumWidth(dp(20));
        imageView.setMinimumHeight(dp(20));
        imageView.setPadding(dp(4), dp(4), dp(4), dp(4));
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        imageView
                .setImageDrawable(new CircleDrawable(density, endCell.score, endCell.arrowNumber, endCell.fillColor, endCell.textColor));
        return imageView;
    }

    @NonNull
    private TextView createTextCell(Table.Row.TextCell textCell) {
        TextView textView = new TextView(context);
        textView.setBackgroundResource(R.drawable.table_cell_border);
        textView.setMinimumWidth(dp(20));
        textView.setMinimumHeight(dp(20));
        textView.setGravity(Gravity.CENTER);
        textView.setTextColor(0xFF000000);
        textView.setPadding(dp(4), dp(4), dp(4), dp(4));
        textView.setText(textCell.content);

        if (textCell.bold) {
            textView.setTypeface(null, Typeface.BOLD);
        }
        return textView;
    }

    @Override
    public void signature(String archer, String targetCaptain) {
        openSection();
        container.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
        container.setLayoutParams(params);
        container.setWeightSum(3);
        appendSignature(archer);
        appendSignature(targetCaptain);
        closeSection();
    }

    private void appendSignature(String name) {
        LinearLayout layout = new LinearLayout(context);
        layout.setPadding(dp(10), dp(60), dp(10), 0);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, WRAP_CONTENT);
        params.weight = 1;
        layout.setLayoutParams(params);
        layout.setOrientation(LinearLayout.VERTICAL);
        ImageView signature = new ImageView(context);
        signature.setBackgroundResource(R.drawable.signature_line);
        params = new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
        signature.setLayoutParams(params);
        signature.setMinimumHeight(dp(48));
        layout.addView(signature);
        TextView signer = new TextView(context);
        signer.setTextColor(0xFF000000);
        params = new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
        signer.setLayoutParams(params);
        signer.setText(name);
        layout.addView(signer);
        container.addView(layout);
    }

    private int dp(int dips) {
        return (int) (dips * density);
    }

    public LinearLayout build() {
        container.setOrientation(LinearLayout.VERTICAL);
        return container;
    }
}
