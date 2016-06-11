package de.dreier.mytargets.shared.targets;

import de.dreier.mytargets.shared.R;
import de.dreier.mytargets.shared.models.Dimension;

import static de.dreier.mytargets.shared.utils.Color.DARK_GRAY;
import static de.dreier.mytargets.shared.utils.Color.GREEN;
import static de.dreier.mytargets.shared.utils.Color.LEMON_YELLOW;
import static de.dreier.mytargets.shared.utils.Color.WHITE;

class SCAPeriod extends TargetModelBase {
    private static final int ID = 24;

    public SCAPeriod() {
        super(ID, R.string.sca_period);
        zones = new Zone[]{
                new Zone(50, LEMON_YELLOW, DARK_GRAY, 2),
                new Zone(200, GREEN, DARK_GRAY, 2),
                new Zone(500, WHITE, DARK_GRAY, 2)
        };
        scoringStyles = new ScoringStyle[]{
                new ScoringStyle(false, 8, 4, 2)
        };
        diameters = new Dimension[]{
                new Dimension(60, Dimension.Unit.CENTIMETER)
        };
        centerMark = new CenterMark(DARK_GRAY, 5, 4, false);
    }
}
