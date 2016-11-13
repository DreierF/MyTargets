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
import de.dreier.mytargets.utils.rules.MiniDbTestRule;

@SmallTest
@RunWith(AndroidJUnit4.class)
public class ExportTest extends InstrumentedTestBase {

    private static final String EXPECTED = "\"Title\";\"Date\";\"Standard round\";\"Indoor\";\"Bow\";\"Arrow\";\"Round\";\"Distance\";\"Target\";\"End\";\"Timestamp\";\"Points\";\"x\";\"y\"\n" +
            "\"Training\";\"2016-07-03\";\"WA Standard\";\"Outdoor\";\"\";\"\";\"1\";\"50m\";\"WA Full (122cm)\";\"1\";\"14:11:30\";\"6\";\"-0.09704613\";\"0.48751888\"\n" +
            "\"Training\";\"2016-07-03\";\"WA Standard\";\"Outdoor\";\"\";\"\";\"1\";\"50m\";\"WA Full (122cm)\";\"1\";\"14:11:30\";\"8\";\"0.2544537\";\"0.18759277\"\n" +
            "\"Training\";\"2016-07-03\";\"WA Standard\";\"Outdoor\";\"\";\"\";\"1\";\"50m\";\"WA Full (122cm)\";\"1\";\"14:11:30\";\"8\";\"0.22305901\";\"0.24469331\"\n" +
            "\"Training\";\"2016-07-03\";\"WA Standard\";\"Outdoor\";\"\";\"\";\"1\";\"50m\";\"WA Full (122cm)\";\"1\";\"14:11:30\";\"4\";\"-0.7274932\";\"-0.101296484\"\n" +
            "\"Training\";\"2016-07-03\";\"WA Standard\";\"Outdoor\";\"\";\"\";\"1\";\"50m\";\"WA Full (122cm)\";\"1\";\"14:11:30\";\"6\";\"0.031262573\";\"0.4623869\"\n" +
            "\"Training\";\"2016-07-03\";\"WA Standard\";\"Outdoor\";\"\";\"\";\"1\";\"50m\";\"WA Full (122cm)\";\"1\";\"14:11:30\";\"8\";\"-0.08347331\";\"0.27355132\"\n" +
            "\"Training\";\"2016-07-03\";\"WA Standard\";\"Outdoor\";\"\";\"\";\"1\";\"50m\";\"WA Full (122cm)\";\"2\";\"14:23:38\";\"1\";\"-0.71199644\";\"-0.72292596\"\n" +
            "\"Training\";\"2016-07-03\";\"WA Standard\";\"Outdoor\";\"\";\"\";\"1\";\"50m\";\"WA Full (122cm)\";\"2\";\"14:23:38\";\"3\";\"0.7745199\";\"-0.24011837\"\n" +
            "\"Training\";\"2016-07-03\";\"WA Standard\";\"Outdoor\";\"\";\"\";\"1\";\"50m\";\"WA Full (122cm)\";\"2\";\"14:23:38\";\"9\";\"-0.1675149\";\"-0.095472515\"\n" +
            "\"Training\";\"2016-07-03\";\"WA Standard\";\"Outdoor\";\"\";\"\";\"1\";\"50m\";\"WA Full (122cm)\";\"2\";\"14:23:38\";\"3\";\"0.0035093275\";\"-0.83239406\"\n" +
            "\"Training\";\"2016-07-03\";\"WA Standard\";\"Outdoor\";\"\";\"\";\"1\";\"50m\";\"WA Full (122cm)\";\"2\";\"14:23:38\";\"7\";\"0.25423\";\"0.3326286\"\n" +
            "\"Training\";\"2016-07-03\";\"WA Standard\";\"Outdoor\";\"\";\"\";\"1\";\"50m\";\"WA Full (122cm)\";\"2\";\"14:23:38\";\"9\";\"0.08042156\";\"0.17108005\"\n" +
            "\"Training\";\"2016-07-03\";\"WA Standard\";\"Outdoor\";\"\";\"\";\"2\";\"30m\";\"WA Full (122cm)\";\"1\";\"14:18:42\";\"9\";\"-0.19679837\";\"-0.054444958\"\n" +
            "\"Training\";\"2016-07-03\";\"WA Standard\";\"Outdoor\";\"\";\"\";\"2\";\"30m\";\"WA Full (122cm)\";\"1\";\"14:18:42\";\"7\";\"-0.35070917\";\"0.21802907\"\n" +
            "\"Training\";\"2016-07-03\";\"WA Standard\";\"Outdoor\";\"\";\"\";\"2\";\"30m\";\"WA Full (122cm)\";\"1\";\"14:18:42\";\"5\";\"0.1591012\";\"-0.6074063\"\n" +
            "\"Training\";\"2016-07-03\";\"WA Standard\";\"Outdoor\";\"\";\"\";\"2\";\"30m\";\"WA Full (122cm)\";\"1\";\"14:18:42\";\"5\";\"-0.34579107\";\"-0.47265264\"\n" +
            "\"Training\";\"2016-07-03\";\"WA Standard\";\"Outdoor\";\"\";\"\";\"2\";\"30m\";\"WA Full (122cm)\";\"1\";\"14:18:42\";\"7\";\"0.20607667\";\"-0.37253922\"\n" +
            "\"Training\";\"2016-07-03\";\"WA Standard\";\"Outdoor\";\"\";\"\";\"2\";\"30m\";\"WA Full (122cm)\";\"1\";\"14:18:42\";\"6\";\"-0.43094382\";\"-0.17701732\"\n" +
            "\"Training\";\"2016-07-03\";\"WA Standard\";\"Outdoor\";\"\";\"\";\"2\";\"30m\";\"WA Full (122cm)\";\"2\";\"14:33:36\";\"1\";\"0.07179225\";\"0.99751586\"\n" +
            "\"Training\";\"2016-07-03\";\"WA Standard\";\"Outdoor\";\"\";\"\";\"2\";\"30m\";\"WA Full (122cm)\";\"2\";\"14:33:36\";\"9\";\"-0.19982919\";\"0.11988278\"\n" +
            "\"Training\";\"2016-07-03\";\"WA Standard\";\"Outdoor\";\"\";\"\";\"2\";\"30m\";\"WA Full (122cm)\";\"2\";\"14:33:36\";\"6\";\"0.49992648\";\"0.05285425\"\n" +
            "\"Training\";\"2016-07-03\";\"WA Standard\";\"Outdoor\";\"\";\"\";\"2\";\"30m\";\"WA Full (122cm)\";\"2\";\"14:33:36\";\"9\";\"0.113363184\";\"-0.13224195\"\n" +
            "\"Training\";\"2016-07-03\";\"WA Standard\";\"Outdoor\";\"\";\"\";\"2\";\"30m\";\"WA Full (122cm)\";\"2\";\"14:33:36\";\"10\";\"0.025195435\";\"-0.118830875\"\n" +
            "\"Training\";\"2016-07-03\";\"WA Standard\";\"Outdoor\";\"\";\"\";\"2\";\"30m\";\"WA Full (122cm)\";\"2\";\"14:33:36\";\"4\";\"-0.26668936\";\"0.61415976\"\n" +
            "\"Training\";\"2016-05-02\";\"WA Standard\";\"Outdoor\";\"\";\"\";\"1\";\"50m\";\"WA Full (122cm)\";\"1\";\"14:09:22\";\"1\";\"-0.5778972\";\"0.7959325\"\n" +
            "\"Training\";\"2016-05-02\";\"WA Standard\";\"Outdoor\";\"\";\"\";\"1\";\"50m\";\"WA Full (122cm)\";\"1\";\"14:09:22\";\"6\";\"-0.28520566\";\"-0.4321238\"\n" +
            "\"Training\";\"2016-05-02\";\"WA Standard\";\"Outdoor\";\"\";\"\";\"1\";\"50m\";\"WA Full (122cm)\";\"1\";\"14:09:22\";\"10\";\"-0.044608343\";\"-0.12625475\"\n" +
            "\"Training\";\"2016-05-02\";\"WA Standard\";\"Outdoor\";\"\";\"\";\"1\";\"50m\";\"WA Full (122cm)\";\"1\";\"14:09:22\";\"7\";\"-0.24878271\";\"-0.25027454\"\n" +
            "\"Training\";\"2016-05-02\";\"WA Standard\";\"Outdoor\";\"\";\"\";\"1\";\"50m\";\"WA Full (122cm)\";\"1\";\"14:09:22\";\"3\";\"-0.62481874\";\"-0.4338018\"\n" +
            "\"Training\";\"2016-05-02\";\"WA Standard\";\"Outdoor\";\"\";\"\";\"1\";\"50m\";\"WA Full (122cm)\";\"1\";\"14:09:22\";\"X\";\"-0.028194645\";\"0.07728534\"\n" +
            "\"Training\";\"2016-05-02\";\"WA Standard\";\"Outdoor\";\"\";\"\";\"1\";\"50m\";\"WA Full (122cm)\";\"2\";\"14:54:25\";\"5\";\"-0.08748839\";\"-0.62123376\"\n" +
            "\"Training\";\"2016-05-02\";\"WA Standard\";\"Outdoor\";\"\";\"\";\"1\";\"50m\";\"WA Full (122cm)\";\"2\";\"14:54:25\";\"7\";\"-0.2658152\";\"0.29600945\"\n" +
            "\"Training\";\"2016-05-02\";\"WA Standard\";\"Outdoor\";\"\";\"\";\"1\";\"50m\";\"WA Full (122cm)\";\"2\";\"14:54:25\";\"X\";\"-0.02324394\";\"-0.056518044\"\n" +
            "\"Training\";\"2016-05-02\";\"WA Standard\";\"Outdoor\";\"\";\"\";\"1\";\"50m\";\"WA Full (122cm)\";\"2\";\"14:54:25\";\"8\";\"-0.23567894\";\"-0.17782852\"\n" +
            "\"Training\";\"2016-05-02\";\"WA Standard\";\"Outdoor\";\"\";\"\";\"1\";\"50m\";\"WA Full (122cm)\";\"2\";\"14:54:25\";\"5\";\"0.20571919\";\"-0.5271199\"\n" +
            "\"Training\";\"2016-05-02\";\"WA Standard\";\"Outdoor\";\"\";\"\";\"1\";\"50m\";\"WA Full (122cm)\";\"2\";\"14:54:25\";\"9\";\"-0.21914628\";\"-0.057659067\"\n" +
            "\"Training\";\"2016-05-02\";\"WA Standard\";\"Outdoor\";\"\";\"\";\"2\";\"30m\";\"WA Full (122cm)\";\"1\";\"14:12:29\";\"5\";\"0.54042196\";\"0.27746376\"\n" +
            "\"Training\";\"2016-05-02\";\"WA Standard\";\"Outdoor\";\"\";\"\";\"2\";\"30m\";\"WA Full (122cm)\";\"1\";\"14:12:29\";\"2\";\"-0.86734706\";\"-0.32767305\"\n" +
            "\"Training\";\"2016-05-02\";\"WA Standard\";\"Outdoor\";\"\";\"\";\"2\";\"30m\";\"WA Full (122cm)\";\"1\";\"14:12:29\";\"6\";\"0.51952213\";\"0.014782413\"\n" +
            "\"Training\";\"2016-05-02\";\"WA Standard\";\"Outdoor\";\"\";\"\";\"2\";\"30m\";\"WA Full (122cm)\";\"1\";\"14:12:29\";\"9\";\"-0.23790026\";\"-0.036906872\"\n" +
            "\"Training\";\"2016-05-02\";\"WA Standard\";\"Outdoor\";\"\";\"\";\"2\";\"30m\";\"WA Full (122cm)\";\"1\";\"14:12:29\";\"10\";\"0.10485902\";\"-0.017559964\"\n" +
            "\"Training\";\"2016-05-02\";\"WA Standard\";\"Outdoor\";\"\";\"\";\"2\";\"30m\";\"WA Full (122cm)\";\"1\";\"14:12:29\";\"4\";\"0.24249218\";\"-0.7027249\"\n" +
            "\"Training\";\"2016-05-02\";\"WA Standard\";\"Outdoor\";\"\";\"\";\"2\";\"30m\";\"WA Full (122cm)\";\"2\";\"14:48:12\";\"9\";\"0.11511669\";\"-0.18904422\"\n" +
            "\"Training\";\"2016-05-02\";\"WA Standard\";\"Outdoor\";\"\";\"\";\"2\";\"30m\";\"WA Full (122cm)\";\"2\";\"14:48:12\";\"M\";\"0.8259577\";\"-0.7482542\"\n" +
            "\"Training\";\"2016-05-02\";\"WA Standard\";\"Outdoor\";\"\";\"\";\"2\";\"30m\";\"WA Full (122cm)\";\"2\";\"14:48:12\";\"8\";\"-0.15863813\";\"0.20996018\"\n" +
            "\"Training\";\"2016-05-02\";\"WA Standard\";\"Outdoor\";\"\";\"\";\"2\";\"30m\";\"WA Full (122cm)\";\"2\";\"14:48:12\";\"6\";\"0.0089092525\";\"0.5021496\"\n" +
            "\"Training\";\"2016-05-02\";\"WA Standard\";\"Outdoor\";\"\";\"\";\"2\";\"30m\";\"WA Full (122cm)\";\"2\";\"14:48:12\";\"10\";\"-0.029776974\";\"-0.13108598\"\n" +
            "\"Training\";\"2016-05-02\";\"WA Standard\";\"Outdoor\";\"\";\"\";\"2\";\"30m\";\"WA Full (122cm)\";\"2\";\"14:48:12\";\"6\";\"-0.30494335\";\"0.3902735\"\n";
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