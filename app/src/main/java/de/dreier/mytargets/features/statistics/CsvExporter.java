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

package de.dreier.mytargets.features.statistics;

import android.content.Context;
import android.support.annotation.NonNull;

import org.threeten.bp.format.DateTimeFormatter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.Stack;

import de.dreier.mytargets.R;
import de.dreier.mytargets.shared.models.Target;
import de.dreier.mytargets.shared.models.db.End;
import de.dreier.mytargets.shared.models.db.Round;
import de.dreier.mytargets.shared.models.db.Shot;
import de.dreier.mytargets.shared.models.db.Training;

public class CsvExporter {

    @NonNull
    private final Context context;

    public CsvExporter(@NonNull Context context) {
        this.context = context;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void exportAll(@NonNull File file, @NonNull List<Long> roundIds) throws IOException {
        FileWriter writer = new FileWriter(file);
        writeExportData(writer, roundIds);
    }

    public void writeExportData(@NonNull Writer writer, @NonNull List<Long> roundIds) throws IOException {
        CsvBuilder csv = new CsvBuilder(writer);
        csv.enterScope();
        csv.add(context.getString(R.string.title));
        csv.add(context.getString(R.string.date));
        csv.add(context.getString(R.string.standard_round));
        csv.add(context.getString(R.string.indoor));
        csv.add(context.getString(R.string.bow));
        csv.add(context.getString(R.string.arrow));
        csv.add(context.getString(R.string.round));
        csv.add(context.getString(R.string.distance));
        csv.add(context.getString(R.string.target));
        csv.add(context.getString(R.string.passe));
        csv.add(context.getString(R.string.timestamp));
        csv.add(context.getString(R.string.points));
        csv.add("x");
        csv.add("y");
        csv.newLine();
        csv.exitScope();
        for (Training t : Training.Companion.getAll()) {
            addTraining(csv, t, roundIds);
        }

        writer.flush();
        writer.close();
    }

    private void addTraining(@NonNull CsvBuilder csv, @NonNull Training t, @NonNull List<Long> roundIds) throws IOException {
        csv.enterScope();
        // Title
        csv.add(t.getTitle());
        // Date
        csv.add(t.getDate().format(DateTimeFormatter.ISO_LOCAL_DATE));
        // StandardRound
        csv.add(t.getStandardRoundId() == null ? context.getString(R.string.practice) : t.getStandardRound()
                .getName());
        // Indoor
        csv.add(t.getIndoor() ? context.getString(R.string.indoor) : context.getString(R.string.outdoor));
        // Bow
        csv.add(t.getBow() == null ? "" : t.getBow().getName());
        // Arrow
        csv.add(t.getArrow() == null ? "" : t.getArrow().getName());
        for (Round r : t.loadRounds()) {
            if (!roundIds.contains(r.getId())) {
                continue;
            }
            addRound(csv, r);
        }
        csv.exitScope();
    }

    private static void addRound(@NonNull CsvBuilder csv, @NonNull Round r) throws IOException {
        csv.enterScope();
        // Round
        csv.add(String.valueOf(r.getIndex() + 1));
        // Distance
        csv.add(r.getDistance().toString());
        // Target
        final Target target = r.getTarget();
        csv.add(target.getModel().toString() + " (" + target.diameter
                .toString() + ")");
        for (End e : r.loadEnds()) {
            csv.enterScope();
            // End
            csv.add(String.valueOf(e.getIndex() + 1));
            // Timestamp
            csv.add(e.getSaveTime().format(DateTimeFormatter.ISO_LOCAL_TIME));
            for (Shot s : e.loadShots()) {
                csv.enterScope();
                // Score
                csv.add(target.zoneToString(s.getScoringRing(), s.getIndex()));

                // Coordinates (X, Y)
                csv.add(String.valueOf(s.getX()));
                csv.add(String.valueOf(s.getY()));

                csv.newLine();
                csv.exitScope();
            }
            csv.exitScope();
        }
        csv.exitScope();
    }

    private static class CsvBuilder {
        private final Writer writer;
        private final Stack<String> scopeStack = new Stack<>();

        public CsvBuilder(Writer writer) {
            this.writer = writer;
            scopeStack.push("");
        }

        public void add(String text) {
            String line = scopeStack.pop();
            if (!line.isEmpty()) {
                line += ";";
            }
            line += "\"" + text + "\"";
            scopeStack.push(line);
        }

        public void newLine() throws IOException {
            writer.append(scopeStack.peek());
            writer.append("\n");
        }

        public void enterScope() {
            scopeStack.push(scopeStack.peek());
        }

        public void exitScope() {
            scopeStack.pop();
        }
    }
}
