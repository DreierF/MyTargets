/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */
package de.dreier.mytargets.shared.models.target;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;

import de.dreier.mytargets.shared.R;
import de.dreier.mytargets.shared.models.Diameter;
import de.dreier.mytargets.shared.models.Dimension;

import static android.graphics.Color.WHITE;

public class Beursault extends CircularTargetBase {

    public Beursault(Context context) {
        super(context, 23, R.string.beursault);
        zones = 8;
        radius = new float[]{31.089f, 69.673f, 98.578f, 141.358f, 231.017f, 320.679f, 410.339f,
                500};
        colorFill = new int[]{WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE};
        colorStroke = new int[]{Target.DARK_GRAY, Target.DARK_GRAY, Target.DARK_GRAY,
                Target.DARK_GRAY, Target.DARK_GRAY, Target.DARK_GRAY, Target.DARK_GRAY,
                Target.DARK_GRAY,};
        strokeWidth = new int[]{27, 6, 6, 27, 6, 6, 6, 27};
        zonePoints = new int[][]{{4, 4, 3, 3, 2, 2, 1, 1}};
        //TODO inside out, see http://scores-sca.org/public/scores_rules.php?R=25&Shoot=108
        showAsX = new boolean[]{false};
        diameters = new Diameter[]{new Diameter(48, Dimension.CENTIMETER)};
    }

    @Override
    protected void onPostDraw(Canvas canvas, Rect rect) {
        paintStroke.setColor(Target.DARK_GRAY);
        paintStroke.setStrokeWidth(6 * rect.width() / 1000f);
        canvas.drawLine(rect.left, rect.exactCenterY(),
                rect.right, rect.exactCenterY(), paintStroke);
        canvas.drawLine(rect.exactCenterX(), rect.top,
                rect.exactCenterX(), rect.bottom, paintStroke);

        //TODO draw numbers
    }
}
