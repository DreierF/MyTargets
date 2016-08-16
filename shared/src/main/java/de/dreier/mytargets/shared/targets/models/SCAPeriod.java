package de.dreier.mytargets.shared.targets.models;

import de.dreier.mytargets.shared.R;
import de.dreier.mytargets.shared.models.Dimension;
import de.dreier.mytargets.shared.targets.decoration.CenterMarkDecorator;
import de.dreier.mytargets.shared.targets.scoringstyle.ScoringStyle;
import de.dreier.mytargets.shared.targets.zone.CircularZone;
import de.dreier.mytargets.shared.targets.zone.ZoneBase;

import static de.dreier.mytargets.shared.utils.Color.DARK_GRAY;
import static de.dreier.mytargets.shared.utils.Color.GREEN;
import static de.dreier.mytargets.shared.utils.Color.LEMON_YELLOW;
import static de.dreier.mytargets.shared.utils.Color.WHITE;

public class SCAPeriod extends TargetModelBase {
    private static final int ID = 24;

    public SCAPeriod() {
        super(ID, R.string.sca_period);
        zones = new ZoneBase[]{
                new CircularZone(0.1f, LEMON_YELLOW, DARK_GRAY, 2),
                new CircularZone(0.4f, GREEN, DARK_GRAY, 2),
                new CircularZone(1.0f, WHITE, DARK_GRAY, 2)
        };
        scoringStyles = new ScoringStyle[]{
                new ScoringStyle(false, 8, 4, 2)
        };
        diameters = new Dimension[]{
                new Dimension(60, Dimension.Unit.CENTIMETER)
        };
        decorator = new CenterMarkDecorator(DARK_GRAY, 5, 4, false);
    }
}
