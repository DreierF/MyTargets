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
import android.net.Uri;
import android.support.annotation.NonNull;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Stack;

import de.dreier.mytargets.R;
import de.dreier.mytargets.shared.models.Target;
import de.dreier.mytargets.shared.models.db.End;
import de.dreier.mytargets.shared.models.db.Round;
import de.dreier.mytargets.shared.models.db.Shot;
import de.dreier.mytargets.shared.models.db.Training;

import static android.support.v4.content.FileProvider.getUriForFile;
import static de.dreier.mytargets.shared.SharedApplicationInstance.get;

public class CsvExporter {
    private static final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
    private static DateFormat timeInstance = new SimpleDateFormat("HH:mm:ss", Locale.US);

    public static Uri export(Context context, List<Long> roundIds) throws IOException {
        String packageName = context.getPackageName();
        String authority = packageName + ".easyphotopicker.fileprovider";

        final File f = new File(context.getCacheDir(), getExportFileName());
        exportAll(f, roundIds);
        return getUriForFile(context, authority, f);
    }

    @NonNull
    private static String getExportFileName() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.US);
        return "MyTargets_exported_data_" + format.format(new Date()) + ".csv";
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private static void exportAll(File file, List<Long> roundIds) throws IOException {
        file.getParentFile().mkdirs();
        file.createNewFile();
        FileWriter writer = new FileWriter(file);
        writeExportData(writer, roundIds);
    }

    public static void writeExportData(Writer writer, List<Long> roundIds) throws IOException {
        CsvBuilder csv = new CsvBuilder(writer);
        csv.enterScope();
        csv.add(get(R.string.title));
        csv.add(get(R.string.date));
        csv.add(get(R.string.standard_round));
        csv.add(get(R.string.indoor));
        csv.add(get(R.string.bow));
        csv.add(get(R.string.arrow));
        csv.add(get(R.string.round));
        csv.add(get(R.string.distance));
        csv.add(get(R.string.target));
        csv.add(get(R.string.passe));
        csv.add(get(R.string.timestamp));
        csv.add(get(R.string.points));
        csv.add("x");
        csv.add("y");
        csv.newLine();
        csv.exitScope();
        for (Training t : Training.getAll()) {
            addTraining(csv, t, roundIds);
        }

        writer.flush();
        writer.close();
    }

    private static void addTraining(CsvBuilder csv, Training t, List<Long> roundIds) throws IOException {
        csv.enterScope();
        // Title
        csv.add(t.title);
        // Date
        csv.add(dateFormat.format(t.date.toDate()));
        // StandardRoundOld
        csv.add(t.standardRoundId == null ? get(R.string.practice) : t.getStandardRound()
                .getName());
        // Indoor
        csv.add(t.indoor ? get(R.string.indoor) : get(R.string.outdoor));
        // Bow
        csv.add(t.getBow() == null ? "" : t.getBow().getName());
        // Arrow
        csv.add(t.getArrow() == null ? "" : t.getArrow().getName());
        for (Round r : t.getRounds()) {
            if (!roundIds.contains(r.getId())) {
                continue;
            }
            addRound(csv, r);
        }
        csv.exitScope();
    }

    private static void addRound(CsvBuilder csv, Round r) throws IOException {
        csv.enterScope();
        // Round
        csv.add(String.valueOf(r.index + 1));
        // Distance
        csv.add(r.distance.toString());
        // Target
        final Target target = r.getTarget();
        csv.add(target.getModel().toString() + " (" + target.size
                .toString() + ")");
        for (End e : r.getEnds()) {
            csv.enterScope();
            // End
            csv.add(String.valueOf(e.index + 1));
            // Timestamp
            csv.add(timeInstance.format(e.saveTime.toDate()));
            for (Shot s : e.getShots()) {
                csv.enterScope();
                // Score
                csv.add(target.zoneToString(s.scoringRing, s.index));

                // Coordinates (X, Y)
                csv.add(String.valueOf(s.x));
                csv.add(String.valueOf(s.y));

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
