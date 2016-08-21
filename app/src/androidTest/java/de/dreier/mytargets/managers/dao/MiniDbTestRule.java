package de.dreier.mytargets.managers.dao;

import java.util.Random;

import de.dreier.mytargets.managers.SettingsManager;
import de.dreier.mytargets.shared.models.Bow;
import de.dreier.mytargets.shared.models.Dimension;
import de.dreier.mytargets.shared.models.Round;
import de.dreier.mytargets.shared.models.StandardRound;
import de.dreier.mytargets.shared.models.Target;
import de.dreier.mytargets.shared.models.Training;
import de.dreier.mytargets.shared.targets.WAFull;

public class MiniDbTestRule extends DbTestRuleBase {

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

        passeDataSource.update(randomPasse(round2, 6, generator));
        passeDataSource.update(randomPasse(round2, 6, generator));
    }

}