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

import java.util.ArrayList;
import java.util.Random;

import de.dreier.mytargets.R;
import de.dreier.mytargets.managers.dao.ArrowDataSource;
import de.dreier.mytargets.managers.dao.BowDataSource;
import de.dreier.mytargets.managers.dao.PasseDataSource;
import de.dreier.mytargets.managers.dao.RoundDataSource;
import de.dreier.mytargets.managers.dao.StandardRoundDataSource;
import de.dreier.mytargets.managers.dao.TrainingDataSource;
import de.dreier.mytargets.shared.models.Bow;
import de.dreier.mytargets.shared.models.EBowType;
import de.dreier.mytargets.shared.models.EWeather;
import de.dreier.mytargets.shared.models.Environment;
import de.dreier.mytargets.shared.models.Passe;
import de.dreier.mytargets.shared.models.Round;
import de.dreier.mytargets.shared.models.StandardRound;
import de.dreier.mytargets.shared.models.Training;
import de.dreier.mytargets.shared.utils.BitmapUtils;
import de.dreier.mytargets.utils.ThumbnailUtils;

public abstract class DbTestRuleBase implements TestRule {
    protected final Context context;
    protected final TrainingDataSource trainingDataSource;
    protected final RoundDataSource roundDataSource;
    protected final BowDataSource bowDataSource;
    protected final StandardRoundDataSource standardRoundDataSource;
    protected final PasseDataSource passeDataSource;
    protected final ArrowDataSource arrowDataSource;

    public DbTestRuleBase() {
        context = InstrumentationRegistry.getTargetContext();
        trainingDataSource = new TrainingDataSource();
        roundDataSource = new RoundDataSource();
        bowDataSource = new BowDataSource();
        standardRoundDataSource = new StandardRoundDataSource();
        passeDataSource = new PasseDataSource();
        arrowDataSource = new ArrowDataSource();
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
            p.shot[i].index = i;
            p.shot[i].zone = shots[i];
        }
        return p;
    }

    protected Passe randomPasse(Training training, Round round, int arrowsPerEnd, Random gen) {
        Passe p = new Passe(arrowsPerEnd);
        p.roundId = round.getId();
        for (int i = 0; i < arrowsPerEnd; i++) {
            p.shot[i].index = i;
            p.shot[i].zone = gen.nextInt(5);
        }
        p.saveDate = new DateTime().withDate(training.date).withTime(14, gen.nextInt(59), gen.nextInt(59), 0);
        return p;
    }

    protected abstract void addDatabaseContent();

    private void deleteAll() {
        trainingDataSource.deleteAll();
        roundDataSource.deleteAll();
        bowDataSource.deleteAll();
        passeDataSource.deleteAll();
        arrowDataSource.deleteAll();
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
        Bitmap thumbnail = ThumbnailUtils.extractThumbnail(bitmap,
                ThumbnailUtils.TARGET_SIZE_MICRO_THUMBNAIL,
                ThumbnailUtils.TARGET_SIZE_MICRO_THUMBNAIL, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        bow.thumb = BitmapUtils.getBitmapAsByteArray(thumbnail);
        bow.sightSettings = new ArrayList<>();
        bowDataSource.update(bow);
        return bow;
    }

    @NonNull
    protected Training insertDefaultTraining(StandardRound standardRound, Random generator) {
        Training training = new Training();
        training.title = InstrumentationRegistry.getTargetContext().getString(R.string.training);
        training.date = new LocalDate(2016, 4 + generator.nextInt(5), generator.nextInt(29));
        training.environment = new Environment();
        training.environment.location = "";
        training.environment.weather = EWeather.SUNNY;
        training.environment.windSpeed = 1;
        training.environment.windDirection = 0;
        training.standardRoundId = standardRound.id;
        training.bow = 0;
        training.arrow = 0;
        training.arrowNumbering = false;
        training.timePerPasse = 0;
        trainingDataSource.update(training);
        return training;
    }
}
