/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */

package de.dreier.mytargets.managers;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Stack;

import de.dreier.mytargets.R;
import de.dreier.mytargets.shared.models.Target;
import de.dreier.mytargets.shared.models.db.End;
import de.dreier.mytargets.shared.models.db.Round;
import de.dreier.mytargets.shared.models.db.Shot;
import de.dreier.mytargets.shared.models.db.Training;

import static de.dreier.mytargets.shared.SharedApplicationInstance.get;

public class CsvExporter {
    private static final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void exportAll(File file, List<Long> roundIds) throws IOException {
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
        // StandardRound
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
            csv.add(SimpleDateFormat.getTimeInstance(DateFormat.MEDIUM, Locale.GERMAN)
                    .format(e.saveTime.toDate()));
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
