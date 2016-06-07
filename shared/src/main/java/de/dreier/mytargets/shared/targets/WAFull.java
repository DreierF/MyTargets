/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */
package de.dreier.mytargets.shared.targets;

import de.dreier.mytargets.shared.R;
import de.dreier.mytargets.shared.models.Dimension;

import static de.dreier.mytargets.shared.models.Dimension.Unit.CENTIMETER;
import static de.dreier.mytargets.shared.utils.Color.BLACK;
import static de.dreier.mytargets.shared.utils.Color.CERULEAN_BLUE;
import static de.dreier.mytargets.shared.utils.Color.DARK_GRAY;
import static de.dreier.mytargets.shared.utils.Color.FLAMINGO_RED;
import static de.dreier.mytargets.shared.utils.Color.LEMON_YELLOW;
import static de.dreier.mytargets.shared.utils.Color.WHITE;

public class WAFull extends TargetModelBase {

    public static final int ID = 0;

    public WAFull() {
        super(ID, R.string.wa_full);
        zones = new Zone[]{
                new Zone(25, LEMON_YELLOW, DARK_GRAY, 2),
                new Zone(50, LEMON_YELLOW, DARK_GRAY, 2),
                new Zone(100, LEMON_YELLOW, DARK_GRAY, 2),
                new Zone(150, FLAMINGO_RED, DARK_GRAY, 2),
                new Zone(200, FLAMINGO_RED, DARK_GRAY, 2),
                new Zone(250, CERULEAN_BLUE, DARK_GRAY, 2),
                new Zone(300, CERULEAN_BLUE, DARK_GRAY, 2),
                new Zone(350, BLACK, DARK_GRAY, 2),
                new Zone(400, BLACK, DARK_GRAY, 2),
                new Zone(450, WHITE, DARK_GRAY, 2),
                new Zone(500, WHITE, DARK_GRAY, 2)
        };
        scoringStyles = new ScoringStyle[]{
                new ScoringStyle(true, 10, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1),
                new ScoringStyle(false, 10, 9, 9, 8, 7, 6, 5, 4, 3, 2, 1),
                new ScoringStyle(false, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1),
                new ScoringStyle(true, 5, 5, 5, 4, 4, 3, 3, 2, 2, 1, 1),
                new ScoringStyle(false, 9, 9, 9, 7, 7, 5, 5, 3, 3, 1, 1),
                new ColorScoringStyle("FCFS Color (Reversed 10-1)", 31, 1, 1, 2, 4, 4, 6, 6, 8, 8, 10, 10)
        };
        diameters = new Dimension[]{
                new Dimension(40, CENTIMETER),
                new Dimension(60, CENTIMETER),
                new Dimension(80, CENTIMETER),
                new Dimension(92, CENTIMETER),
                new Dimension(122, CENTIMETER)
        };
        centerMark = new CenterMark(DARK_GRAY, 5f, 4, false);
    }
}
