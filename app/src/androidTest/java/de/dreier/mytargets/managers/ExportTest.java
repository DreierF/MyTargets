package de.dreier.mytargets.managers;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Locale;

import de.dreier.mytargets.managers.dao.MiniDbTestRule;

@SmallTest
@RunWith(AndroidJUnit4.class)
public class ExportTest {

    public static final String EXPECTED = "\"Title\";\"Date\";\"Standard round\";\"Indoor\";\"Bow\";\"Arrow\";\"Round\";\"Distance\";\"Target\";\"End\";\"Points\";\"x\";\"y\"\n" +
            "\"Training\";\"2016-07-02\";\"WA Standard\";\"Outdoor\";\"\";\"\";\"1\";\"50m\";\"WA Full (122cm)\";\"1\";\"X\";\"0.0\";\"0.0\"\n" +
            "\"Training\";\"2016-07-02\";\"WA Standard\";\"Outdoor\";\"\";\"\";\"1\";\"50m\";\"WA Full (122cm)\";\"1\";\"8\";\"0.0\";\"0.0\"\n" +
            "\"Training\";\"2016-07-02\";\"WA Standard\";\"Outdoor\";\"\";\"\";\"1\";\"50m\";\"WA Full (122cm)\";\"1\";\"8\";\"0.0\";\"0.0\"\n" +
            "\"Training\";\"2016-07-02\";\"WA Standard\";\"Outdoor\";\"\";\"\";\"1\";\"50m\";\"WA Full (122cm)\";\"1\";\"8\";\"0.0\";\"0.0\"\n" +
            "\"Training\";\"2016-07-02\";\"WA Standard\";\"Outdoor\";\"\";\"\";\"1\";\"50m\";\"WA Full (122cm)\";\"1\";\"8\";\"0.0\";\"0.0\"\n" +
            "\"Training\";\"2016-07-02\";\"WA Standard\";\"Outdoor\";\"\";\"\";\"1\";\"50m\";\"WA Full (122cm)\";\"1\";\"7\";\"0.0\";\"0.0\"\n" +
            "\"Training\";\"2016-07-02\";\"WA Standard\";\"Outdoor\";\"\";\"\";\"1\";\"50m\";\"WA Full (122cm)\";\"2\";\"10\";\"0.0\";\"0.0\"\n" +
            "\"Training\";\"2016-07-02\";\"WA Standard\";\"Outdoor\";\"\";\"\";\"1\";\"50m\";\"WA Full (122cm)\";\"2\";\"10\";\"0.0\";\"0.0\"\n" +
            "\"Training\";\"2016-07-02\";\"WA Standard\";\"Outdoor\";\"\";\"\";\"1\";\"50m\";\"WA Full (122cm)\";\"2\";\"9\";\"0.0\";\"0.0\"\n" +
            "\"Training\";\"2016-07-02\";\"WA Standard\";\"Outdoor\";\"\";\"\";\"1\";\"50m\";\"WA Full (122cm)\";\"2\";\"8\";\"0.0\";\"0.0\"\n" +
            "\"Training\";\"2016-07-02\";\"WA Standard\";\"Outdoor\";\"\";\"\";\"1\";\"50m\";\"WA Full (122cm)\";\"2\";\"7\";\"0.0\";\"0.0\"\n" +
            "\"Training\";\"2016-07-02\";\"WA Standard\";\"Outdoor\";\"\";\"\";\"1\";\"50m\";\"WA Full (122cm)\";\"2\";\"7\";\"0.0\";\"0.0\"\n" +
            "\"Training\";\"2016-07-02\";\"WA Standard\";\"Outdoor\";\"\";\"\";\"2\";\"30m\";\"WA Full (122cm)\";\"1\";\"X\";\"0.0\";\"0.0\"\n" +
            "\"Training\";\"2016-07-02\";\"WA Standard\";\"Outdoor\";\"\";\"\";\"2\";\"30m\";\"WA Full (122cm)\";\"1\";\"X\";\"0.0\";\"0.0\"\n" +
            "\"Training\";\"2016-07-02\";\"WA Standard\";\"Outdoor\";\"\";\"\";\"2\";\"30m\";\"WA Full (122cm)\";\"1\";\"8\";\"0.0\";\"0.0\"\n" +
            "\"Training\";\"2016-07-02\";\"WA Standard\";\"Outdoor\";\"\";\"\";\"2\";\"30m\";\"WA Full (122cm)\";\"1\";\"8\";\"0.0\";\"0.0\"\n" +
            "\"Training\";\"2016-07-02\";\"WA Standard\";\"Outdoor\";\"\";\"\";\"2\";\"30m\";\"WA Full (122cm)\";\"1\";\"7\";\"0.0\";\"0.0\"\n" +
            "\"Training\";\"2016-07-02\";\"WA Standard\";\"Outdoor\";\"\";\"\";\"2\";\"30m\";\"WA Full (122cm)\";\"1\";\"7\";\"0.0\";\"0.0\"\n" +
            "\"Training\";\"2016-07-02\";\"WA Standard\";\"Outdoor\";\"\";\"\";\"2\";\"30m\";\"WA Full (122cm)\";\"2\";\"X\";\"0.0\";\"0.0\"\n" +
            "\"Training\";\"2016-07-02\";\"WA Standard\";\"Outdoor\";\"\";\"\";\"2\";\"30m\";\"WA Full (122cm)\";\"2\";\"X\";\"0.0\";\"0.0\"\n" +
            "\"Training\";\"2016-07-02\";\"WA Standard\";\"Outdoor\";\"\";\"\";\"2\";\"30m\";\"WA Full (122cm)\";\"2\";\"10\";\"0.0\";\"0.0\"\n" +
            "\"Training\";\"2016-07-02\";\"WA Standard\";\"Outdoor\";\"\";\"\";\"2\";\"30m\";\"WA Full (122cm)\";\"2\";\"10\";\"0.0\";\"0.0\"\n" +
            "\"Training\";\"2016-07-02\";\"WA Standard\";\"Outdoor\";\"\";\"\";\"2\";\"30m\";\"WA Full (122cm)\";\"2\";\"9\";\"0.0\";\"0.0\"\n" +
            "\"Training\";\"2016-07-02\";\"WA Standard\";\"Outdoor\";\"\";\"\";\"2\";\"30m\";\"WA Full (122cm)\";\"2\";\"7\";\"0.0\";\"0.0\"\n" +
            "\"Training\";\"2016-05-01\";\"WA Standard\";\"Outdoor\";\"\";\"\";\"1\";\"50m\";\"WA Full (122cm)\";\"1\";\"X\";\"0.0\";\"0.0\"\n" +
            "\"Training\";\"2016-05-01\";\"WA Standard\";\"Outdoor\";\"\";\"\";\"1\";\"50m\";\"WA Full (122cm)\";\"1\";\"9\";\"0.0\";\"0.0\"\n" +
            "\"Training\";\"2016-05-01\";\"WA Standard\";\"Outdoor\";\"\";\"\";\"1\";\"50m\";\"WA Full (122cm)\";\"1\";\"9\";\"0.0\";\"0.0\"\n" +
            "\"Training\";\"2016-05-01\";\"WA Standard\";\"Outdoor\";\"\";\"\";\"1\";\"50m\";\"WA Full (122cm)\";\"1\";\"8\";\"0.0\";\"0.0\"\n" +
            "\"Training\";\"2016-05-01\";\"WA Standard\";\"Outdoor\";\"\";\"\";\"1\";\"50m\";\"WA Full (122cm)\";\"1\";\"7\";\"0.0\";\"0.0\"\n" +
            "\"Training\";\"2016-05-01\";\"WA Standard\";\"Outdoor\";\"\";\"\";\"1\";\"50m\";\"WA Full (122cm)\";\"1\";\"7\";\"0.0\";\"0.0\"\n" +
            "\"Training\";\"2016-05-01\";\"WA Standard\";\"Outdoor\";\"\";\"\";\"1\";\"50m\";\"WA Full (122cm)\";\"2\";\"X\";\"0.0\";\"0.0\"\n" +
            "\"Training\";\"2016-05-01\";\"WA Standard\";\"Outdoor\";\"\";\"\";\"1\";\"50m\";\"WA Full (122cm)\";\"2\";\"10\";\"0.0\";\"0.0\"\n" +
            "\"Training\";\"2016-05-01\";\"WA Standard\";\"Outdoor\";\"\";\"\";\"1\";\"50m\";\"WA Full (122cm)\";\"2\";\"10\";\"0.0\";\"0.0\"\n" +
            "\"Training\";\"2016-05-01\";\"WA Standard\";\"Outdoor\";\"\";\"\";\"1\";\"50m\";\"WA Full (122cm)\";\"2\";\"8\";\"0.0\";\"0.0\"\n" +
            "\"Training\";\"2016-05-01\";\"WA Standard\";\"Outdoor\";\"\";\"\";\"1\";\"50m\";\"WA Full (122cm)\";\"2\";\"8\";\"0.0\";\"0.0\"\n" +
            "\"Training\";\"2016-05-01\";\"WA Standard\";\"Outdoor\";\"\";\"\";\"1\";\"50m\";\"WA Full (122cm)\";\"2\";\"7\";\"0.0\";\"0.0\"\n" +
            "\"Training\";\"2016-05-01\";\"WA Standard\";\"Outdoor\";\"\";\"\";\"2\";\"30m\";\"WA Full (122cm)\";\"1\";\"X\";\"0.0\";\"0.0\"\n" +
            "\"Training\";\"2016-05-01\";\"WA Standard\";\"Outdoor\";\"\";\"\";\"2\";\"30m\";\"WA Full (122cm)\";\"1\";\"9\";\"0.0\";\"0.0\"\n" +
            "\"Training\";\"2016-05-01\";\"WA Standard\";\"Outdoor\";\"\";\"\";\"2\";\"30m\";\"WA Full (122cm)\";\"1\";\"8\";\"0.0\";\"0.0\"\n" +
            "\"Training\";\"2016-05-01\";\"WA Standard\";\"Outdoor\";\"\";\"\";\"2\";\"30m\";\"WA Full (122cm)\";\"1\";\"7\";\"0.0\";\"0.0\"\n" +
            "\"Training\";\"2016-05-01\";\"WA Standard\";\"Outdoor\";\"\";\"\";\"2\";\"30m\";\"WA Full (122cm)\";\"1\";\"7\";\"0.0\";\"0.0\"\n" +
            "\"Training\";\"2016-05-01\";\"WA Standard\";\"Outdoor\";\"\";\"\";\"2\";\"30m\";\"WA Full (122cm)\";\"1\";\"7\";\"0.0\";\"0.0\"\n" +
            "\"Training\";\"2016-05-01\";\"WA Standard\";\"Outdoor\";\"\";\"\";\"2\";\"30m\";\"WA Full (122cm)\";\"2\";\"9\";\"0.0\";\"0.0\"\n" +
            "\"Training\";\"2016-05-01\";\"WA Standard\";\"Outdoor\";\"\";\"\";\"2\";\"30m\";\"WA Full (122cm)\";\"2\";\"9\";\"0.0\";\"0.0\"\n" +
            "\"Training\";\"2016-05-01\";\"WA Standard\";\"Outdoor\";\"\";\"\";\"2\";\"30m\";\"WA Full (122cm)\";\"2\";\"8\";\"0.0\";\"0.0\"\n" +
            "\"Training\";\"2016-05-01\";\"WA Standard\";\"Outdoor\";\"\";\"\";\"2\";\"30m\";\"WA Full (122cm)\";\"2\";\"8\";\"0.0\";\"0.0\"\n" +
            "\"Training\";\"2016-05-01\";\"WA Standard\";\"Outdoor\";\"\";\"\";\"2\";\"30m\";\"WA Full (122cm)\";\"2\";\"7\";\"0.0\";\"0.0\"\n" +
            "\"Training\";\"2016-05-01\";\"WA Standard\";\"Outdoor\";\"\";\"\";\"2\";\"30m\";\"WA Full (122cm)\";\"2\";\"7\";\"0.0\";\"0.0\"\n";
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
    private void setLocale(String language, String country) {
        Locale locale = new Locale(language, country);
        // here we update locale for date formatters
        Locale.setDefault(locale);
        // here we update locale for app resources
        Resources res = InstrumentationRegistry.getTargetContext().getResources();
        Configuration config = res.getConfiguration();
        config.locale = locale;
        res.updateConfiguration(config, res.getDisplayMetrics());
    }
}