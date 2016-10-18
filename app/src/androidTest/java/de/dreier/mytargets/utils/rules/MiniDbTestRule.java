package de.dreier.mytargets.utils.rules;

import java.util.Random;

import de.dreier.mytargets.managers.SettingsManager;
import de.dreier.mytargets.shared.models.Dimension;
import de.dreier.mytargets.shared.models.Target;
import de.dreier.mytargets.shared.models.db.Round;
import de.dreier.mytargets.shared.models.db.StandardRound;
import de.dreier.mytargets.shared.models.db.Training;
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
        addRandomTraining(578459341);
        addRandomTraining(454459456);
    }

    private void addRandomTraining(int seed) {
        Random generator = new Random(seed);
        StandardRound standardRound = StandardRound.get(32L);

        Training training = insertDefaultTraining(standardRound, generator);

        Round round1 = new Round();
        round1.trainingId = training.getId();
        round1.info = standardRound.rounds.get(0);
        round1.info.target = round1.info.getTargetTemplate();
        round1.comment = "";
        round1.update();

        Round round2 = new Round();
        round2.trainingId = training.getId();
        round2.info = standardRound.rounds.get(1);
        round2.info.target = round2.info.getTargetTemplate();
        round2.comment = "";
        round2.update();

        randomPasse(training, round1, 6, generator).update();
        randomPasse(training, round1, 6, generator).update();

        randomPasse(training, round2, 6, generator).update();
        randomPasse(training, round2, 6, generator).update();
    }

}