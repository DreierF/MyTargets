package de.dreier.mytargets.managers;

import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.io.StringWriter;

import de.dreier.mytargets.InstrumentedTestBase;
import de.dreier.mytargets.managers.dao.MiniDbTestRule;

@SmallTest
@RunWith(AndroidJUnit4.class)
public class ExportTest extends InstrumentedTestBase {

    public static final String EXPECTED = "\"Title\";\"Date\";\"Standard round\";\"Indoor\";\"Bow\";\"Arrow\";\"Round\";\"Distance\";\"Target\";\"End\";\"Points\";\"x\";\"y\"\n" +
            "\"Training\";\"2016-07-03\";\"WA Standard\";\"Outdoor\";\"\";\"\";\"1\";\"50m\";\"WA Full (122cm)\";\"1\";\"8\";\"0.0\";\"0.0\"\n" +
            "\"Training\";\"2016-07-03\";\"WA Standard\";\"Outdoor\";\"\";\"\";\"1\";\"50m\";\"WA Full (122cm)\";\"1\";\"8\";\"0.0\";\"0.0\"\n" +
            "\"Training\";\"2016-07-03\";\"WA Standard\";\"Outdoor\";\"\";\"\";\"1\";\"50m\";\"WA Full (122cm)\";\"1\";\"8\";\"0.0\";\"0.0\"\n" +
            "\"Training\";\"2016-07-03\";\"WA Standard\";\"Outdoor\";\"\";\"\";\"1\";\"50m\";\"WA Full (122cm)\";\"1\";\"7\";\"0.0\";\"0.0\"\n" +
            "\"Training\";\"2016-07-03\";\"WA Standard\";\"Outdoor\";\"\";\"\";\"1\";\"50m\";\"WA Full (122cm)\";\"1\";\"8\";\"0.0\";\"0.0\"\n" +
            "\"Training\";\"2016-07-03\";\"WA Standard\";\"Outdoor\";\"\";\"\";\"1\";\"50m\";\"WA Full (122cm)\";\"1\";\"X\";\"0.0\";\"0.0\"\n" +
            "\"Training\";\"2016-07-03\";\"WA Standard\";\"Outdoor\";\"\";\"\";\"1\";\"50m\";\"WA Full (122cm)\";\"2\";\"7\";\"0.0\";\"0.0\"\n" +
            "\"Training\";\"2016-07-03\";\"WA Standard\";\"Outdoor\";\"\";\"\";\"1\";\"50m\";\"WA Full (122cm)\";\"2\";\"10\";\"0.0\";\"0.0\"\n" +
            "\"Training\";\"2016-07-03\";\"WA Standard\";\"Outdoor\";\"\";\"\";\"1\";\"50m\";\"WA Full (122cm)\";\"2\";\"8\";\"0.0\";\"0.0\"\n" +
            "\"Training\";\"2016-07-03\";\"WA Standard\";\"Outdoor\";\"\";\"\";\"1\";\"50m\";\"WA Full (122cm)\";\"2\";\"9\";\"0.0\";\"0.0\"\n" +
            "\"Training\";\"2016-07-03\";\"WA Standard\";\"Outdoor\";\"\";\"\";\"1\";\"50m\";\"WA Full (122cm)\";\"2\";\"7\";\"0.0\";\"0.0\"\n" +
            "\"Training\";\"2016-07-03\";\"WA Standard\";\"Outdoor\";\"\";\"\";\"1\";\"50m\";\"WA Full (122cm)\";\"2\";\"10\";\"0.0\";\"0.0\"\n" +
            "\"Training\";\"2016-07-03\";\"WA Standard\";\"Outdoor\";\"\";\"\";\"2\";\"30m\";\"WA Full (122cm)\";\"1\";\"X\";\"0.0\";\"0.0\"\n" +
            "\"Training\";\"2016-07-03\";\"WA Standard\";\"Outdoor\";\"\";\"\";\"2\";\"30m\";\"WA Full (122cm)\";\"1\";\"8\";\"0.0\";\"0.0\"\n" +
            "\"Training\";\"2016-07-03\";\"WA Standard\";\"Outdoor\";\"\";\"\";\"2\";\"30m\";\"WA Full (122cm)\";\"1\";\"X\";\"0.0\";\"0.0\"\n" +
            "\"Training\";\"2016-07-03\";\"WA Standard\";\"Outdoor\";\"\";\"\";\"2\";\"30m\";\"WA Full (122cm)\";\"1\";\"8\";\"0.0\";\"0.0\"\n" +
            "\"Training\";\"2016-07-03\";\"WA Standard\";\"Outdoor\";\"\";\"\";\"2\";\"30m\";\"WA Full (122cm)\";\"1\";\"7\";\"0.0\";\"0.0\"\n" +
            "\"Training\";\"2016-07-03\";\"WA Standard\";\"Outdoor\";\"\";\"\";\"2\";\"30m\";\"WA Full (122cm)\";\"1\";\"7\";\"0.0\";\"0.0\"\n" +
            "\"Training\";\"2016-07-03\";\"WA Standard\";\"Outdoor\";\"\";\"\";\"2\";\"30m\";\"WA Full (122cm)\";\"2\";\"10\";\"0.0\";\"0.0\"\n" +
            "\"Training\";\"2016-07-03\";\"WA Standard\";\"Outdoor\";\"\";\"\";\"2\";\"30m\";\"WA Full (122cm)\";\"2\";\"X\";\"0.0\";\"0.0\"\n" +
            "\"Training\";\"2016-07-03\";\"WA Standard\";\"Outdoor\";\"\";\"\";\"2\";\"30m\";\"WA Full (122cm)\";\"2\";\"9\";\"0.0\";\"0.0\"\n" +
            "\"Training\";\"2016-07-03\";\"WA Standard\";\"Outdoor\";\"\";\"\";\"2\";\"30m\";\"WA Full (122cm)\";\"2\";\"7\";\"0.0\";\"0.0\"\n" +
            "\"Training\";\"2016-07-03\";\"WA Standard\";\"Outdoor\";\"\";\"\";\"2\";\"30m\";\"WA Full (122cm)\";\"2\";\"X\";\"0.0\";\"0.0\"\n" +
            "\"Training\";\"2016-07-03\";\"WA Standard\";\"Outdoor\";\"\";\"\";\"2\";\"30m\";\"WA Full (122cm)\";\"2\";\"10\";\"0.0\";\"0.0\"\n" +
            "\"Training\";\"2016-05-02\";\"WA Standard\";\"Outdoor\";\"\";\"\";\"1\";\"50m\";\"WA Full (122cm)\";\"1\";\"9\";\"0.0\";\"0.0\"\n" +
            "\"Training\";\"2016-05-02\";\"WA Standard\";\"Outdoor\";\"\";\"\";\"1\";\"50m\";\"WA Full (122cm)\";\"1\";\"9\";\"0.0\";\"0.0\"\n" +
            "\"Training\";\"2016-05-02\";\"WA Standard\";\"Outdoor\";\"\";\"\";\"1\";\"50m\";\"WA Full (122cm)\";\"1\";\"8\";\"0.0\";\"0.0\"\n" +
            "\"Training\";\"2016-05-02\";\"WA Standard\";\"Outdoor\";\"\";\"\";\"1\";\"50m\";\"WA Full (122cm)\";\"1\";\"X\";\"0.0\";\"0.0\"\n" +
            "\"Training\";\"2016-05-02\";\"WA Standard\";\"Outdoor\";\"\";\"\";\"1\";\"50m\";\"WA Full (122cm)\";\"1\";\"7\";\"0.0\";\"0.0\"\n" +
            "\"Training\";\"2016-05-02\";\"WA Standard\";\"Outdoor\";\"\";\"\";\"1\";\"50m\";\"WA Full (122cm)\";\"1\";\"7\";\"0.0\";\"0.0\"\n" +
            "\"Training\";\"2016-05-02\";\"WA Standard\";\"Outdoor\";\"\";\"\";\"1\";\"50m\";\"WA Full (122cm)\";\"2\";\"8\";\"0.0\";\"0.0\"\n" +
            "\"Training\";\"2016-05-02\";\"WA Standard\";\"Outdoor\";\"\";\"\";\"1\";\"50m\";\"WA Full (122cm)\";\"2\";\"X\";\"0.0\";\"0.0\"\n" +
            "\"Training\";\"2016-05-02\";\"WA Standard\";\"Outdoor\";\"\";\"\";\"1\";\"50m\";\"WA Full (122cm)\";\"2\";\"7\";\"0.0\";\"0.0\"\n" +
            "\"Training\";\"2016-05-02\";\"WA Standard\";\"Outdoor\";\"\";\"\";\"1\";\"50m\";\"WA Full (122cm)\";\"2\";\"8\";\"0.0\";\"0.0\"\n" +
            "\"Training\";\"2016-05-02\";\"WA Standard\";\"Outdoor\";\"\";\"\";\"1\";\"50m\";\"WA Full (122cm)\";\"2\";\"10\";\"0.0\";\"0.0\"\n" +
            "\"Training\";\"2016-05-02\";\"WA Standard\";\"Outdoor\";\"\";\"\";\"1\";\"50m\";\"WA Full (122cm)\";\"2\";\"10\";\"0.0\";\"0.0\"\n" +
            "\"Training\";\"2016-05-02\";\"WA Standard\";\"Outdoor\";\"\";\"\";\"2\";\"30m\";\"WA Full (122cm)\";\"1\";\"7\";\"0.0\";\"0.0\"\n" +
            "\"Training\";\"2016-05-02\";\"WA Standard\";\"Outdoor\";\"\";\"\";\"2\";\"30m\";\"WA Full (122cm)\";\"1\";\"8\";\"0.0\";\"0.0\"\n" +
            "\"Training\";\"2016-05-02\";\"WA Standard\";\"Outdoor\";\"\";\"\";\"2\";\"30m\";\"WA Full (122cm)\";\"1\";\"9\";\"0.0\";\"0.0\"\n" +
            "\"Training\";\"2016-05-02\";\"WA Standard\";\"Outdoor\";\"\";\"\";\"2\";\"30m\";\"WA Full (122cm)\";\"1\";\"7\";\"0.0\";\"0.0\"\n" +
            "\"Training\";\"2016-05-02\";\"WA Standard\";\"Outdoor\";\"\";\"\";\"2\";\"30m\";\"WA Full (122cm)\";\"1\";\"X\";\"0.0\";\"0.0\"\n" +
            "\"Training\";\"2016-05-02\";\"WA Standard\";\"Outdoor\";\"\";\"\";\"2\";\"30m\";\"WA Full (122cm)\";\"1\";\"7\";\"0.0\";\"0.0\"\n" +
            "\"Training\";\"2016-05-02\";\"WA Standard\";\"Outdoor\";\"\";\"\";\"2\";\"30m\";\"WA Full (122cm)\";\"2\";\"8\";\"0.0\";\"0.0\"\n" +
            "\"Training\";\"2016-05-02\";\"WA Standard\";\"Outdoor\";\"\";\"\";\"2\";\"30m\";\"WA Full (122cm)\";\"2\";\"7\";\"0.0\";\"0.0\"\n" +
            "\"Training\";\"2016-05-02\";\"WA Standard\";\"Outdoor\";\"\";\"\";\"2\";\"30m\";\"WA Full (122cm)\";\"2\";\"9\";\"0.0\";\"0.0\"\n" +
            "\"Training\";\"2016-05-02\";\"WA Standard\";\"Outdoor\";\"\";\"\";\"2\";\"30m\";\"WA Full (122cm)\";\"2\";\"7\";\"0.0\";\"0.0\"\n" +
            "\"Training\";\"2016-05-02\";\"WA Standard\";\"Outdoor\";\"\";\"\";\"2\";\"30m\";\"WA Full (122cm)\";\"2\";\"8\";\"0.0\";\"0.0\"\n" +
            "\"Training\";\"2016-05-02\";\"WA Standard\";\"Outdoor\";\"\";\"\";\"2\";\"30m\";\"WA Full (122cm)\";\"2\";\"9\";\"0.0\";\"0.0\"\n";
    @Rule
    public final MiniDbTestRule dbTestRule = new MiniDbTestRule();

    @Test
    public void testDataExport() throws IOException {
        setLocale("en", "EN");
        final StringWriter writer = new StringWriter();
        DatabaseManager.writeExportData(writer);
        Assert.assertEquals(EXPECTED, writer.toString());
    }
}