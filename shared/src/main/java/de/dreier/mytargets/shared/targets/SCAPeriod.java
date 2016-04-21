package de.dreier.mytargets.shared.targets;

import de.dreier.mytargets.shared.R;
import de.dreier.mytargets.shared.models.Diameter;
import de.dreier.mytargets.shared.models.Dimension;

import static de.dreier.mytargets.shared.utils.Color.DARK_GRAY;
import static de.dreier.mytargets.shared.utils.Color.GREEN;
import static de.dreier.mytargets.shared.utils.Color.LEMON_YELLOW;
import static de.dreier.mytargets.shared.utils.Color.WHITE;

public class SCAPeriod extends TargetModelBase {
    public static final int ID = 24;

    public SCAPeriod() {
        super(ID, R.string.sca_period);
        zones = new Zone[]{
                new Zone(50, LEMON_YELLOW, DARK_GRAY, 2),
                new Zone(200, GREEN, DARK_GRAY, 2),
                new Zone(500, WHITE, DARK_GRAY, 2)
        };
        zonePoints = new int[][]{{8, 4, 2}};
        showAsX = new boolean[]{false};
        diameters = new Diameter[]{new Diameter(60, Dimension.CENTIMETER)};
        centerMark = new CenterMark(DARK_GRAY, 5, 4, false);
    }
}
