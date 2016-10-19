package de.dreier.mytargets.utils.rules;

import android.support.annotation.NonNull;
import android.support.test.InstrumentationRegistry;

import org.joda.time.LocalDate;

import java.util.Arrays;
import java.util.Random;

import de.dreier.mytargets.R;
import de.dreier.mytargets.managers.SettingsManager;
import de.dreier.mytargets.shared.models.Dimension;
import de.dreier.mytargets.shared.models.EWeather;
import de.dreier.mytargets.shared.models.Target;
import de.dreier.mytargets.shared.models.db.Bow;
import de.dreier.mytargets.shared.models.db.Round;
import de.dreier.mytargets.shared.models.db.RoundTemplate;
import de.dreier.mytargets.shared.models.db.StandardRound;
import de.dreier.mytargets.shared.models.db.Training;
import de.dreier.mytargets.shared.targets.WAFull;
import de.dreier.mytargets.shared.utils.StandardRoundFactory;

public class SimpleDbTestRule extends DbTestRuleBase {

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
        round1.info = standardRound.getRounds().get(0);
        round1.setTarget(round1.info.getTargetTemplate());
        round1.comment = "";
        round1.insert();

        Round round2 = new Round();
        round2.trainingId = training.getId();
        round2.info = standardRound.getRounds().get(1);
        round2.setTarget(round2.info.getTargetTemplate());
        round2.comment = "";
        round2.insert();

        randomPasse(training, round1, 6, generator).insert();
        randomPasse(training, round1, 6, generator).insert();
        randomPasse(training, round1, 6, generator).insert();
        randomPasse(training, round1, 6, generator).insert();
        randomPasse(training, round1, 6, generator).insert();
        randomPasse(training, round1, 6, generator).insert();

        randomPasse(training, round2, 6, generator).insert();
        randomPasse(training, round2, 6, generator).insert();
        randomPasse(training, round2, 6, generator).insert();
        randomPasse(training, round2, 6, generator).insert();
        randomPasse(training, round2, 6, generator).insert();
        randomPasse(training, round2, 6, generator).insert();
    }

    @NonNull
    private StandardRound getCustomRound() {
        StandardRound standardRound;
        standardRound = new StandardRound();
        standardRound.club = StandardRoundFactory.CUSTOM_PRACTICE;
        standardRound.name = "Practice";
        standardRound.indoor = true;
        standardRound.setRounds(Arrays.asList(getRoundTemplate(0, 50), getRoundTemplate(1, 30)));
        standardRound.insert();
        return standardRound;
    }

    @NonNull
    private RoundTemplate getRoundTemplate(int index, int distance) {
        RoundTemplate roundTemplate = new RoundTemplate();
        roundTemplate.index = index;
        roundTemplate.setTargetTemplate(new Target(WAFull.ID, 0, new Dimension(60, Dimension.Unit.CENTIMETER)));
        roundTemplate.arrowsPerEnd = 6;
        roundTemplate.endCount = 6;
        roundTemplate.distance = new Dimension(distance, Dimension.Unit.METER);
        return roundTemplate;
    }

    private void addRandomTraining(int seed) {
        Random generator = new Random(seed);
        StandardRound standardRound = StandardRound.get(32L);

        Training training = insertDefaultTraining(standardRound, generator);

        Round round1 = new Round();
        round1.trainingId = training.getId();
        round1.info = standardRound.getRounds().get(0);
        round1.setTarget(round1.info.getTargetTemplate());
        round1.comment = "";
        round1.insert();

        Round round2 = new Round();
        round2.trainingId = training.getId();
        round2.info = standardRound.getRounds().get(1);
        round2.setTarget(round2.info.getTargetTemplate());
        round2.comment = "";
        round2.insert();

        randomPasse(training, round1, 6, generator).insert();
        randomPasse(training, round1, 6, generator).insert();
        randomPasse(training, round1, 6, generator).insert();
        randomPasse(training, round1, 6, generator).insert();
        randomPasse(training, round1, 6, generator).insert();
        randomPasse(training, round1, 6, generator).insert();

        randomPasse(training, round2, 6, generator).insert();
        randomPasse(training, round2, 6, generator).insert();
        randomPasse(training, round2, 6, generator).insert();
        randomPasse(training, round2, 6, generator).insert();
        randomPasse(training, round2, 6, generator).insert();
        randomPasse(training, round2, 6, generator).insert();
    }

    private void addFullTraining(Bow bow) {
        StandardRound standardRound = StandardRound.get(32L);

        Training training = new Training();
        training.title = InstrumentationRegistry.getTargetContext().getString(R.string.training);
        training.date = new LocalDate(2016, 7, 15);
        training.location = "";
        training.weather = EWeather.SUNNY;
        training.windSpeed = 1;
        training.windDirection = 0;
        training.standardRoundId = standardRound.getId();
        training.bow = bow.id;
        training.arrow = null;
        training.arrowNumbering = false;
        training.timePerPasse = 0;
        training.insert();

        Round round1 = new Round();
        round1.trainingId = training.getId();
        round1.info = standardRound.getRounds().get(0);
        round1.setTarget(round1.info.getTargetTemplate());
        round1.comment = "";
        round1.insert();

        Round round2 = new Round();
        round2.trainingId = training.getId();
        round2.info = standardRound.getRounds().get(1);
        round2.setTarget(round2.info.getTargetTemplate());
        round2.comment = "";
        round2.insert();

        passe(round1, 1, 1, 2, 3, 3, 4).insert();
        passe(round1, 0, 0, 1, 2, 2, 3).insert();
        passe(round1, 1, 1, 1, 3, 4, 4).insert();
        passe(round1, 0, 1, 1, 1, 2, 3).insert();
        passe(round1, 1, 2, 3, 3, 4, 5).insert();
        passe(round1, 1, 2, 2, 3, 3, 3).insert();

        passe(round2, 1, 2, 2, 3, 4, 5).insert();
        passe(round2, 0, 0, 1, 2, 2, 3).insert();
        passe(round2, 0, 1, 2, 2, 2, 3).insert();
        passe(round2, 1, 1, 2, 3, 4, 4).insert();
        passe(round2, 1, 2, 2, 3, 3, 3).insert();
        passe(round2, 1, 2, 2, 3, 3, 4).insert();
    }
}
