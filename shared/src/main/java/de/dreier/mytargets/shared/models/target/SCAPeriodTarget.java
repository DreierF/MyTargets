package de.dreier.mytargets.shared.models.target;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;

import de.dreier.mytargets.shared.R;
import de.dreier.mytargets.shared.models.Diameter;
import de.dreier.mytargets.shared.models.Dimension;

import static android.graphics.Color.WHITE;

public class SCAPeriodTarget extends CircularTargetBase {

    public static final int ID = 24;

    public SCAPeriodTarget(Context context) {
        super(context, ID, R.string.sca_period);
        zones = 3;
        radius = new float[]{50, 200, 500};
        colorFill = new int[]{LEMON_YELLOW, GREEN, WHITE};
        colorStroke = new int[]{Target.DARK_GRAY, Target.DARK_GRAY, Target.DARK_GRAY};
        strokeWidth = new int[]{2, 2, 2};
        zonePoints = new int[][]{{8, 4, 2}};
        showAsX = new boolean[]{false};
        diameters = new Diameter[]{new Diameter(60, Dimension.CENTIMETER)};
    }

    @Override
    protected void onPostDraw(Canvas canvas, Rect rect) {
        paintStroke.setColor(Target.DARK_GRAY);
        final float size = recalc(rect, 5);
        paintStroke.setStrokeWidth(4 * rect.width() / 1000f);
        canvas.drawLine(rect.exactCenterX() - size, rect.exactCenterY(),
                rect.exactCenterX() + size, rect.exactCenterY(), paintStroke);
        canvas.drawLine(rect.exactCenterX(), rect.exactCenterY() - size,
                rect.exactCenterX(), rect.exactCenterY() + size, paintStroke);
    }
}
