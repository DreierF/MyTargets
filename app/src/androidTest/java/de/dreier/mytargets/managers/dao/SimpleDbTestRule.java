package de.dreier.mytargets.managers.dao;

import android.support.annotation.NonNull;
import android.support.test.InstrumentationRegistry;

import org.joda.time.LocalDate;

import java.util.Arrays;
import java.util.Random;

import de.dreier.mytargets.R;
import de.dreier.mytargets.managers.SettingsManager;
import de.dreier.mytargets.shared.models.db.Bow;
import de.dreier.mytargets.shared.models.Dimension;
import de.dreier.mytargets.shared.models.EWeather;
import de.dreier.mytargets.shared.models.Environment;
import de.dreier.mytargets.shared.models.db.Passe;
import de.dreier.mytargets.shared.models.db.Round;
import de.dreier.mytargets.shared.models.db.StandardRound;
import de.dreier.mytargets.shared.models.Target;
import de.dreier.mytargets.shared.models.db.Training;
import de.dreier.mytargets.shared.targets.WAFull;
import de.dreier.mytargets.shared.utils.StandardRoundFactory;

public class SimpleDbTestRule extends DbTestRuleBase {

    public SimpleDbTestRule() {
    }

    @Override
    protected void addDatabaseContent() {
        SettingsManager.setTarget(
                new Target(WAFull.ID, 0, new Dimension(122, Dimension.Unit.CENTIMETER)));
        SettingsManager.setDistance(new Dimension(50, Dimension.Unit.METER));
        SettingsManager.setIndoor(false);
        SettingsManager.setInputMode(false);
        SettingsManager.setTimerEnabled(true);
        SettingsManager.setArrowsPerEnd(6);
        Bow bow = addBow();
        addRandomTraining(578459341);
        addRandomTraining(454459456);
        addRandomTraining(763478984);
        addRandomTraining(453891238);
        addRandomTraining(719789367);
        addRandomTraining(658795439);
        addFullTraining(bow);
        addPracticeTraining(438573454);
    }

    private void addPracticeTraining(int seed) {
        Random generator = new Random(seed);
        StandardRound standardRound = getCustomRound();

        Training training = insertDefaultTraining(standardRound, generator);

        Round round1 = new Round();
        round1.trainingId = training.getId();
        round1.info = standardRound.rounds.get(0);
        round1.info.target = round1.info.getTargetTemplate();
        round1.comment = "";
        roundDataSource.update(round1);

        Round round2 = new Round();
        round2.trainingId = training.getId();
        round2.info = standardRound.rounds.get(1);
        round2.info.target = round2.info.getTargetTemplate();
        round2.comment = "";
        roundDataSource.update(round2);

        passeDataSource.update(randomPasse(round1, 6, generator));
        passeDataSource.update(randomPasse(round1, 6, generator));
        passeDataSource.update(randomPasse(round1, 6, generator));
        passeDataSource.update(randomPasse(round1, 6, generator));
        passeDataSource.update(randomPasse(round1, 6, generator));
        passeDataSource.update(randomPasse(round1, 6, generator));

        passeDataSource.update(randomPasse(round2, 6, generator));
        passeDataSource.update(randomPasse(round2, 6, generator));
        passeDataSource.update(randomPasse(round2, 6, generator));
        passeDataSource.update(randomPasse(round2, 6, generator));
        passeDataSource.update(randomPasse(round2, 6, generator));
        passeDataSource.update(randomPasse(round2, 6, generator));
    }

    @NonNull
    private StandardRound getCustomRound() {
        StandardRound standardRound;
        standardRound = new StandardRound();
        standardRound.club = StandardRoundFactory.CUSTOM_PRACTICE;
        standardRound.name = "Practice";
        standardRound.indoor = true;
        standardRound.rounds = Arrays.asList(getRoundTemplate(0, 50), getRoundTemplate(1, 30));
        standardRoundDataSource.update(standardRound);
        return standardRound;
    }

    @NonNull
    private RoundTemplate getRoundTemplate(int index, int distance) {
        RoundTemplate roundTemplate = new RoundTemplate();
        roundTemplate.index = index;
        roundTemplate.target = new Target(WAFull.ID, 0, new Dimension(60, Dimension.Unit.CENTIMETER));
        roundTemplate.targetTemplate = roundTemplate.target;
        roundTemplate.arrowsPerEnd = 6;
        roundTemplate.endCount = 6;
        roundTemplate.distance = new Dimension(distance, Dimension.Unit.METER);
        return roundTemplate;
    }

    private void addRandomTraining(int seed) {
        Random generator = new Random(seed);
        StandardRound standardRound = standardRoundDataSource.get(32);

        Training training = insertDefaultTraining(standardRound, generator);

        Round round1 = new Round();
        round1.trainingId = training.getId();
        round1.info = standardRound.rounds.get(0);
        round1.info.target = round1.info.targetTemplate;
        round1.comment = "";
        roundDataSource.update(round1);

        Round round2 = new Round();
        round2.trainingId = training.getId();
        round2.info = standardRound.rounds.get(1);
        round2.info.target = round2.info.targetTemplate;
        round2.comment = "";
        roundDataSource.update(round2);

        passeDataSource.update(randomPasse(round1, 6, generator));
        passeDataSource.update(randomPasse(round1, 6, generator));
        passeDataSource.update(randomPasse(round1, 6, generator));
        passeDataSource.update(randomPasse(round1, 6, generator));
        passeDataSource.update(randomPasse(round1, 6, generator));
        passeDataSource.update(randomPasse(round1, 6, generator));

        passeDataSource.update(randomPasse(round2, 6, generator));
        passeDataSource.update(randomPasse(round2, 6, generator));
        passeDataSource.update(randomPasse(round2, 6, generator));
        passeDataSource.update(randomPasse(round2, 6, generator));
        passeDataSource.update(randomPasse(round2, 6, generator));
        passeDataSource.update(randomPasse(round2, 6, generator));
    }

    private void addFullTraining(Bow bow) {
        StandardRound standardRound = standardRoundDataSource.get(32);

        Training training = new Training();
        training.title = InstrumentationRegistry.getTargetContext().getString(R.string.training);
        training.date = new LocalDate(2016, 7, 15);
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
        round1.info = standardRound.rounds.get(0);
        round1.info.target = round1.info.getTargetTemplate();
        round1.comment = "";
        roundDataSource.update(round1);

        Round round2 = new Round();
        round2.trainingId = training.getId();
        round2.info = standardRound.rounds.get(1);
        round2.info.target = round2.info.getTargetTemplate();
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
            p.getShots().get(i).index = i;
            p.getShots().get(i).zone = shots[i];
        }
        return p;
    }

    private Passe randomPasse(Round round, Random gen) {
        Passe p = new Passe(6);
        p.roundId = round.getId();
        for (int i = 0; i < 6; i++) {
            p.getShots().get(i).index = i;
            p.getShots().get(i).zone = gen.nextInt(5);
        }
        p.sort();
        return p;
    }
}
