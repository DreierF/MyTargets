/*
 * Copyright (C) 2016 Florian Dreier
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

package de.dreier.mytargets.managers;

import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

import de.dreier.mytargets.InstrumentedTestBase;
import de.dreier.mytargets.shared.models.db.Round;
import de.dreier.mytargets.shared.models.db.Training;
import de.dreier.mytargets.utils.rules.MiniDbTestRule;

@SmallTest
@RunWith(AndroidJUnit4.class)
public class ExportTest extends InstrumentedTestBase {

    private static final String EXPECTED = "\"Title\";\"Date\";\"Standard round\";\"Indoor\";\"Bow\";\"Arrow\";\"Round\";\"Distance\";\"Target\";\"End\";\"Timestamp\";\"Points\";\"x\";\"y\"\n" +
            "\"Training\";\"2016-07-03\";\"WA Standard\";\"Outdoor\";\"\";\"\";\"2\";\"30m\";\"WA Full (122cm)\";\"1\";\"14:07:13\";\"7\";\"0.0\";\"0.0\"\n" +
            "\"Training\";\"2016-07-03\";\"WA Standard\";\"Outdoor\";\"\";\"\";\"2\";\"30m\";\"WA Full (122cm)\";\"1\";\"14:07:13\";\"7\";\"0.0\";\"0.0\"\n" +
            "\"Training\";\"2016-07-03\";\"WA Standard\";\"Outdoor\";\"\";\"\";\"2\";\"30m\";\"WA Full (122cm)\";\"1\";\"14:07:13\";\"10\";\"0.0\";\"0.0\"\n" +
            "\"Training\";\"2016-07-03\";\"WA Standard\";\"Outdoor\";\"\";\"\";\"2\";\"30m\";\"WA Full (122cm)\";\"1\";\"14:07:13\";\"X\";\"0.0\";\"0.0\"\n" +
            "\"Training\";\"2016-07-03\";\"WA Standard\";\"Outdoor\";\"\";\"\";\"2\";\"30m\";\"WA Full (122cm)\";\"1\";\"14:07:13\";\"9\";\"0.0\";\"0.0\"\n" +
            "\"Training\";\"2016-07-03\";\"WA Standard\";\"Outdoor\";\"\";\"\";\"2\";\"30m\";\"WA Full (122cm)\";\"1\";\"14:07:13\";\"7\";\"0.0\";\"0.0\"\n" +
            "\"Training\";\"2016-07-03\";\"WA Standard\";\"Outdoor\";\"\";\"\";\"2\";\"30m\";\"WA Full (122cm)\";\"2\";\"14:55:49\";\"10\";\"0.0\";\"0.0\"\n" +
            "\"Training\";\"2016-07-03\";\"WA Standard\";\"Outdoor\";\"\";\"\";\"2\";\"30m\";\"WA Full (122cm)\";\"2\";\"14:55:49\";\"10\";\"0.0\";\"0.0\"\n" +
            "\"Training\";\"2016-07-03\";\"WA Standard\";\"Outdoor\";\"\";\"\";\"2\";\"30m\";\"WA Full (122cm)\";\"2\";\"14:55:49\";\"10\";\"0.0\";\"0.0\"\n" +
            "\"Training\";\"2016-07-03\";\"WA Standard\";\"Outdoor\";\"\";\"\";\"2\";\"30m\";\"WA Full (122cm)\";\"2\";\"14:55:49\";\"10\";\"0.0\";\"0.0\"\n" +
            "\"Training\";\"2016-07-03\";\"WA Standard\";\"Outdoor\";\"\";\"\";\"2\";\"30m\";\"WA Full (122cm)\";\"2\";\"14:55:49\";\"X\";\"0.0\";\"0.0\"\n" +
            "\"Training\";\"2016-07-03\";\"WA Standard\";\"Outdoor\";\"\";\"\";\"2\";\"30m\";\"WA Full (122cm)\";\"2\";\"14:55:49\";\"X\";\"0.0\";\"0.0\"\n" +
            "\"Training\";\"2016-05-02\";\"WA Standard\";\"Outdoor\";\"\";\"\";\"1\";\"50m\";\"WA Full (122cm)\";\"1\";\"14:43:45\";\"9\";\"0.0\";\"0.0\"\n" +
            "\"Training\";\"2016-05-02\";\"WA Standard\";\"Outdoor\";\"\";\"\";\"1\";\"50m\";\"WA Full (122cm)\";\"1\";\"14:43:45\";\"9\";\"0.0\";\"0.0\"\n" +
            "\"Training\";\"2016-05-02\";\"WA Standard\";\"Outdoor\";\"\";\"\";\"1\";\"50m\";\"WA Full (122cm)\";\"1\";\"14:43:45\";\"8\";\"0.0\";\"0.0\"\n" +
            "\"Training\";\"2016-05-02\";\"WA Standard\";\"Outdoor\";\"\";\"\";\"1\";\"50m\";\"WA Full (122cm)\";\"1\";\"14:43:45\";\"X\";\"0.0\";\"0.0\"\n" +
            "\"Training\";\"2016-05-02\";\"WA Standard\";\"Outdoor\";\"\";\"\";\"1\";\"50m\";\"WA Full (122cm)\";\"1\";\"14:43:45\";\"7\";\"0.0\";\"0.0\"\n" +
            "\"Training\";\"2016-05-02\";\"WA Standard\";\"Outdoor\";\"\";\"\";\"1\";\"50m\";\"WA Full (122cm)\";\"1\";\"14:43:45\";\"7\";\"0.0\";\"0.0\"\n" +
            "\"Training\";\"2016-05-02\";\"WA Standard\";\"Outdoor\";\"\";\"\";\"1\";\"50m\";\"WA Full (122cm)\";\"2\";\"14:56:09\";\"7\";\"0.0\";\"0.0\"\n" +
            "\"Training\";\"2016-05-02\";\"WA Standard\";\"Outdoor\";\"\";\"\";\"1\";\"50m\";\"WA Full (122cm)\";\"2\";\"14:56:09\";\"8\";\"0.0\";\"0.0\"\n" +
            "\"Training\";\"2016-05-02\";\"WA Standard\";\"Outdoor\";\"\";\"\";\"1\";\"50m\";\"WA Full (122cm)\";\"2\";\"14:56:09\";\"10\";\"0.0\";\"0.0\"\n" +
            "\"Training\";\"2016-05-02\";\"WA Standard\";\"Outdoor\";\"\";\"\";\"1\";\"50m\";\"WA Full (122cm)\";\"2\";\"14:56:09\";\"10\";\"0.0\";\"0.0\"\n" +
            "\"Training\";\"2016-05-02\";\"WA Standard\";\"Outdoor\";\"\";\"\";\"1\";\"50m\";\"WA Full (122cm)\";\"2\";\"14:56:09\";\"7\";\"0.0\";\"0.0\"\n" +
            "\"Training\";\"2016-05-02\";\"WA Standard\";\"Outdoor\";\"\";\"\";\"1\";\"50m\";\"WA Full (122cm)\";\"2\";\"14:56:09\";\"8\";\"0.0\";\"0.0\"\n" +
            "\"Training\";\"2016-05-02\";\"WA Standard\";\"Outdoor\";\"\";\"\";\"2\";\"30m\";\"WA Full (122cm)\";\"1\";\"14:14:58\";\"X\";\"0.0\";\"0.0\"\n" +
            "\"Training\";\"2016-05-02\";\"WA Standard\";\"Outdoor\";\"\";\"\";\"2\";\"30m\";\"WA Full (122cm)\";\"1\";\"14:14:58\";\"7\";\"0.0\";\"0.0\"\n" +
            "\"Training\";\"2016-05-02\";\"WA Standard\";\"Outdoor\";\"\";\"\";\"2\";\"30m\";\"WA Full (122cm)\";\"1\";\"14:14:58\";\"8\";\"0.0\";\"0.0\"\n" +
            "\"Training\";\"2016-05-02\";\"WA Standard\";\"Outdoor\";\"\";\"\";\"2\";\"30m\";\"WA Full (122cm)\";\"1\";\"14:14:58\";\"7\";\"0.0\";\"0.0\"\n" +
            "\"Training\";\"2016-05-02\";\"WA Standard\";\"Outdoor\";\"\";\"\";\"2\";\"30m\";\"WA Full (122cm)\";\"1\";\"14:14:58\";\"9\";\"0.0\";\"0.0\"\n" +
            "\"Training\";\"2016-05-02\";\"WA Standard\";\"Outdoor\";\"\";\"\";\"2\";\"30m\";\"WA Full (122cm)\";\"1\";\"14:14:58\";\"7\";\"0.0\";\"0.0\"\n" +
            "\"Training\";\"2016-05-02\";\"WA Standard\";\"Outdoor\";\"\";\"\";\"2\";\"30m\";\"WA Full (122cm)\";\"2\";\"14:37:20\";\"10\";\"0.0\";\"0.0\"\n" +
            "\"Training\";\"2016-05-02\";\"WA Standard\";\"Outdoor\";\"\";\"\";\"2\";\"30m\";\"WA Full (122cm)\";\"2\";\"14:37:20\";\"8\";\"0.0\";\"0.0\"\n" +
            "\"Training\";\"2016-05-02\";\"WA Standard\";\"Outdoor\";\"\";\"\";\"2\";\"30m\";\"WA Full (122cm)\";\"2\";\"14:37:20\";\"7\";\"0.0\";\"0.0\"\n" +
            "\"Training\";\"2016-05-02\";\"WA Standard\";\"Outdoor\";\"\";\"\";\"2\";\"30m\";\"WA Full (122cm)\";\"2\";\"14:37:20\";\"X\";\"0.0\";\"0.0\"\n" +
            "\"Training\";\"2016-05-02\";\"WA Standard\";\"Outdoor\";\"\";\"\";\"2\";\"30m\";\"WA Full (122cm)\";\"2\";\"14:37:20\";\"8\";\"0.0\";\"0.0\"\n" +
            "\"Training\";\"2016-05-02\";\"WA Standard\";\"Outdoor\";\"\";\"\";\"2\";\"30m\";\"WA Full (122cm)\";\"2\";\"14:37:20\";\"10\";\"0.0\";\"0.0\"\n";
    @Rule
    public final MiniDbTestRule dbTestRule = new MiniDbTestRule();

    @Test
    public void testDataExport() throws IOException {
        setLocale("en", "EN");
        final StringWriter writer = new StringWriter();
        List<Long> roundIds = Stream.of(Training.getAll())
                .flatMap(t -> Stream.of(t.getRounds()))
                .map(Round::getId)
                .sorted()
                .collect(Collectors.toList());
        roundIds.remove(0);
        CsvExporter.writeExportData(writer, roundIds);
        Assert.assertEquals(EXPECTED, writer.toString());
    }
}