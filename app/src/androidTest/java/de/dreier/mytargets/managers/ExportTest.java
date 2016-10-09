package de.dreier.mytargets.managers;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
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

    public static final String EXPECTED = "\"Title\";\"Date\";\"Standard round\";\"Indoor\";\"Bow\";\"Arrow\";\"Round\";\"Distance\";\"Target\";\"End\";\"Timestamp\";\"Points\";\"x\";\"y\"\n" +
            "\"Training\";\"2016-07-03\";\"WA Standard\";\"Outdoor\";\"\";\"\";\"1\";\"50m\";\"WA Full (122cm)\";\"1\";\"14:09:48\";\"8\";\"0.0\";\"0.0\"\n" +
            "\"Training\";\"2016-07-03\";\"WA Standard\";\"Outdoor\";\"\";\"\";\"1\";\"50m\";\"WA Full (122cm)\";\"1\";\"14:09:48\";\"8\";\"0.0\";\"0.0\"\n" +
            "\"Training\";\"2016-07-03\";\"WA Standard\";\"Outdoor\";\"\";\"\";\"1\";\"50m\";\"WA Full (122cm)\";\"1\";\"14:09:48\";\"8\";\"0.0\";\"0.0\"\n" +
            "\"Training\";\"2016-07-03\";\"WA Standard\";\"Outdoor\";\"\";\"\";\"1\";\"50m\";\"WA Full (122cm)\";\"1\";\"14:09:48\";\"7\";\"0.0\";\"0.0\"\n" +
            "\"Training\";\"2016-07-03\";\"WA Standard\";\"Outdoor\";\"\";\"\";\"1\";\"50m\";\"WA Full (122cm)\";\"1\";\"14:09:48\";\"8\";\"0.0\";\"0.0\"\n" +
            "\"Training\";\"2016-07-03\";\"WA Standard\";\"Outdoor\";\"\";\"\";\"1\";\"50m\";\"WA Full (122cm)\";\"1\";\"14:09:48\";\"X\";\"0.0\";\"0.0\"\n" +
            "\"Training\";\"2016-07-03\";\"WA Standard\";\"Outdoor\";\"\";\"\";\"1\";\"50m\";\"WA Full (122cm)\";\"2\";\"14:47:31\";\"8\";\"0.0\";\"0.0\"\n" +
            "\"Training\";\"2016-07-03\";\"WA Standard\";\"Outdoor\";\"\";\"\";\"1\";\"50m\";\"WA Full (122cm)\";\"2\";\"14:47:31\";\"9\";\"0.0\";\"0.0\"\n" +
            "\"Training\";\"2016-07-03\";\"WA Standard\";\"Outdoor\";\"\";\"\";\"1\";\"50m\";\"WA Full (122cm)\";\"2\";\"14:47:31\";\"7\";\"0.0\";\"0.0\"\n" +
            "\"Training\";\"2016-07-03\";\"WA Standard\";\"Outdoor\";\"\";\"\";\"1\";\"50m\";\"WA Full (122cm)\";\"2\";\"14:47:31\";\"10\";\"0.0\";\"0.0\"\n" +
            "\"Training\";\"2016-07-03\";\"WA Standard\";\"Outdoor\";\"\";\"\";\"1\";\"50m\";\"WA Full (122cm)\";\"2\";\"14:47:31\";\"X\";\"0.0\";\"0.0\"\n" +
            "\"Training\";\"2016-07-03\";\"WA Standard\";\"Outdoor\";\"\";\"\";\"1\";\"50m\";\"WA Full (122cm)\";\"2\";\"14:47:31\";\"8\";\"0.0\";\"0.0\"\n" +
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
        Context context = InstrumentationRegistry.getTargetContext();
        DatabaseManager database = DatabaseManager.getInstance(context);
        final StringWriter writer = new StringWriter();
        database.writeExportData(writer);
        Assert.assertEquals(EXPECTED, writer.toString());
    }
}