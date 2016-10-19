package de.dreier.mytargets.utils.rules;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.test.InstrumentationRegistry;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import java.util.Random;

import de.dreier.mytargets.R;
import de.dreier.mytargets.shared.models.EBowType;
import de.dreier.mytargets.shared.models.EWeather;
import de.dreier.mytargets.shared.models.Thumbnail;
import de.dreier.mytargets.shared.models.db.Arrow;
import de.dreier.mytargets.shared.models.db.Bow;
import de.dreier.mytargets.shared.models.db.Passe;
import de.dreier.mytargets.shared.models.db.Round;
import de.dreier.mytargets.shared.models.db.StandardRound;
import de.dreier.mytargets.shared.models.db.Training;

public abstract class DbTestRuleBase implements TestRule {
    private final Context context;

    public DbTestRuleBase() {
        context = InstrumentationRegistry.getTargetContext();
    }

    @Override
    public Statement apply(Statement base, Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                deleteAll();
                addDatabaseContent();
                base.evaluate();
            }
        };
    }

    protected Passe passe(Round round, int... shots) {
        Passe p = new Passe(shots.length);
        p.roundId = round.getId();
        for (int i = 0; i < shots.length; i++) {
            p.shots.get(i).index = i;
            p.shots.get(i).zone = shots[i];
        }
        return p;
    }

    protected Passe randomPasse(Training training, Round round, int arrowsPerEnd, Random gen) {
        Passe p = new Passe(arrowsPerEnd);
        p.roundId = round.getId();
        for (int i = 0; i < arrowsPerEnd; i++) {
            p.shots.get(i).index = i;
            p.shots.get(i).zone = gen.nextInt(5);
        }
        p.saveDate = new DateTime().withDate(training.date).withTime(14, gen.nextInt(59), gen.nextInt(59), 0);
        return p;
    }

    protected abstract void addDatabaseContent();

    private void deleteAll() {
        Training.deleteAll();
        Round.deleteAll();
        Bow.deleteAll();
        Passe.deleteAll();
        Arrow.deleteAll();
    }

    @NonNull
    protected Bow addBow() {
        Bow bow = new Bow();
        bow.name = "PSE Fever";
        bow.brand = "PSE";
        bow.size = "64\"";
        bow.braceHeight = "6 3/8\"";
        bow.type = EBowType.COMPOUND_BOW;
        bow.imageFile = null;
        Bitmap bitmap = BitmapFactory
                .decodeResource(context.getResources(), R.drawable.recurve_bow);
        bow.thumbnail = new Thumbnail(bitmap);
        bow.insert();
        return bow;
    }

    @NonNull
    protected Training insertDefaultTraining(StandardRound standardRound, Random generator) {
        Training training = new Training();
        training.title = InstrumentationRegistry.getTargetContext().getString(R.string.training);
        training.date = new LocalDate(2016, 4 + generator.nextInt(5), generator.nextInt(29));
        training.location = "";
        training.weather = EWeather.SUNNY;
        training.windSpeed = 1;
        training.windDirection = 0;
        training.standardRoundId = standardRound.getId();
        training.bow = null;
        training.arrow = null;
        training.arrowNumbering = false;
        training.timePerPasse = 0;
        training.insert();
        return training;
    }
}
