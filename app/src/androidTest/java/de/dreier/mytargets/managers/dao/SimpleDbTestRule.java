package de.dreier.mytargets.managers.dao;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.test.InstrumentationRegistry;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import de.dreier.mytargets.R;
import de.dreier.mytargets.managers.SettingsManager;
import de.dreier.mytargets.shared.models.Bow;
import de.dreier.mytargets.shared.models.Dimension;
import de.dreier.mytargets.shared.models.EBowType;
import de.dreier.mytargets.shared.models.EWeather;
import de.dreier.mytargets.shared.models.Environment;
import de.dreier.mytargets.shared.models.Passe;
import de.dreier.mytargets.shared.models.Round;
import de.dreier.mytargets.shared.models.StandardRound;
import de.dreier.mytargets.shared.models.Target;
import de.dreier.mytargets.shared.models.Training;
import de.dreier.mytargets.shared.targets.WAFull;
import de.dreier.mytargets.shared.utils.BitmapUtils;
import de.dreier.mytargets.utils.ThumbnailUtils;

public class SimpleDbTestRule implements TestRule {
    private final Context context;
    private final TrainingDataSource trainingDataSource;
    private final RoundDataSource roundDataSource;
    private final BowDataSource bowDataSource;
    private final ArrowDataSource arrowDataSource;
    private final StandardRoundDataSource standardRoundDataSource;
    private PasseDataSource passeDataSource;

    public SimpleDbTestRule() {
        context = InstrumentationRegistry.getTargetContext();
        trainingDataSource = new TrainingDataSource();
        roundDataSource = new RoundDataSource();
        bowDataSource = new BowDataSource();
        arrowDataSource = new ArrowDataSource();
        standardRoundDataSource = new StandardRoundDataSource();
        passeDataSource = new PasseDataSource();
    }

    @Override
    public Statement apply(Statement base, Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                trainingDataSource.deleteAll();
                roundDataSource.deleteAll();
                bowDataSource.deleteAll();
                passeDataSource.deleteAll();
                SettingsManager.setTarget(new Target(WAFull.ID, 0, new Dimension(122, Dimension.Unit.CENTIMETER)));
                SettingsManager.setDistance(new Dimension(50, Dimension.Unit.METER));
                SettingsManager.setIndoor(false);
                SettingsManager.setInputMode(false);
                SettingsManager.setTimerEnabled(true);
                SettingsManager.setArrowsPerPasse(6);
                Bow bow = addBow();
                addRandomTraining(578459341);
                addRandomTraining(454459456);
                addRandomTraining(763478984);
                addRandomTraining(453891238);
                addRandomTraining(719789367);
                addRandomTraining(658795439);
                addFullTraining(bow);
                base.evaluate();
            }
        };
    }

    private void addRandomTraining(int seed) {
        Random generator = new Random(seed);
        StandardRound standardRound = standardRoundDataSource.get(32);

        Training training = new Training();
        training.title = InstrumentationRegistry.getTargetContext().getString(R.string.training);
        training.date = new Date(116, 4 + generator.nextInt(5), generator.nextInt(29));
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

        Round round1 = new Round();
        round1.trainingId = training.getId();
        round1.info = standardRound.getRounds().get(0);
        round1.info.target = round1.info.targetTemplate;
        round1.comment = "";
        roundDataSource.update(round1);

        Round round2 = new Round();
        round2.trainingId = training.getId();
        round2.info = standardRound.getRounds().get(1);
        round2.info.target = round2.info.targetTemplate;
        round2.comment = "";
        roundDataSource.update(round2);

        passeDataSource.update(randomPasse(round1, generator));
        passeDataSource.update(randomPasse(round1, generator));
        passeDataSource.update(randomPasse(round1, generator));
        passeDataSource.update(randomPasse(round1, generator));
        passeDataSource.update(randomPasse(round1, generator));
        passeDataSource.update(randomPasse(round1, generator));

        passeDataSource.update(randomPasse(round2, generator));
        passeDataSource.update(randomPasse(round2, generator));
        passeDataSource.update(randomPasse(round2, generator));
        passeDataSource.update(randomPasse(round2, generator));
        passeDataSource.update(randomPasse(round2, generator));
        passeDataSource.update(randomPasse(round2, generator));
    }

    @NonNull
    private Bow addBow() {
        Bow bow = new Bow();
        bow.name = "PSE Fever";
        bow.brand = "PSE";
        bow.size = "64\"";
        bow.height = "6 3/8\"";
        bow.type = EBowType.COMPOUND_BOW;
        bow.imageFile = null;
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.recurve_bow);
        Bitmap thumbnail = ThumbnailUtils.extractThumbnail(bitmap,
                ThumbnailUtils.TARGET_SIZE_MICRO_THUMBNAIL,
                ThumbnailUtils.TARGET_SIZE_MICRO_THUMBNAIL, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        bow.thumb = BitmapUtils.getBitmapAsByteArray(thumbnail);
        bow.sightSettings = new ArrayList<>();
        bowDataSource.update(bow);
        return bow;
    }

    private void addFullTraining(Bow bow) {
        StandardRound standardRound = standardRoundDataSource.get(32);

        Training training = new Training();
        training.title = InstrumentationRegistry.getTargetContext().getString(R.string.training);
        training.date = new Date(116, 7, 15);
        training.environment = new Environment();
        training.environment.location = "";
        training.environment.weather = EWeather.SUNNY;
        training.environment.windSpeed = 1;
        training.environment.windDirection = 0;
        training.standardRoundId = standardRound.id;
        training.bow = bow.id;
        training.arrow = 0;
        training.arrowNumbering = false;
        training.timePerPasse = 0;
        trainingDataSource.update(training);

        Round round1 = new Round();
        round1.trainingId = training.getId();
        round1.info = standardRound.getRounds().get(0);
        round1.info.target = round1.info.targetTemplate;
        round1.comment = "";
        roundDataSource.update(round1);

        Round round2 = new Round();
        round2.trainingId = training.getId();
        round2.info = standardRound.getRounds().get(1);
        round2.info.target = round2.info.targetTemplate;
        round2.comment = "";
        roundDataSource.update(round2);

        passeDataSource.update(passe(round1, 1, 1, 2, 3, 3, 4));
        passeDataSource.update(passe(round1, 0, 0, 1, 2, 2, 3));
        passeDataSource.update(passe(round1, 1, 1, 1, 3, 4, 4));
        passeDataSource.update(passe(round1, 0, 1, 1, 1, 2, 3));
        passeDataSource.update(passe(round1, 1, 2, 3, 3, 4, 5));
        passeDataSource.update(passe(round1, 1, 2, 2, 3, 3, 3));

        passeDataSource.update(passe(round2, 1, 2, 2, 3, 4, 5));
        passeDataSource.update(passe(round2, 0, 0, 1, 2, 2, 3));
        passeDataSource.update(passe(round2, 0, 1, 2, 2, 2, 3));
        passeDataSource.update(passe(round2, 1, 1, 2, 3, 4, 4));
        passeDataSource.update(passe(round2, 1, 2, 2, 3, 3, 3));
        passeDataSource.update(passe(round2, 1, 2, 2, 3, 3, 4));
    }

    private Passe passe(Round round, int... shots) {
        Passe p = new Passe(shots.length);
        p.roundId = round.getId();
        for (int i = 0; i < shots.length; i++) {
            p.shot[i].index = i;
            p.shot[i].zone = shots[i];
        }
        return p;
    }

    private Passe randomPasse(Round round, Random gen) {
        Passe p = new Passe(6);
        p.roundId = round.getId();
        for (int i = 0; i < 6; i++) {
            p.shot[i].index = i;
            p.shot[i].zone = gen.nextInt(5);
        }
        p.sort();
        return p;
    }
}