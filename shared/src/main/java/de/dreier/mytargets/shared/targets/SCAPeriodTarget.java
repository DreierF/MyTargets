package de.dreier.mytargets.shared.targets;

import android.graphics.Canvas;
import android.graphics.Rect;

import de.dreier.mytargets.shared.R;
import de.dreier.mytargets.shared.models.Diameter;
import de.dreier.mytargets.shared.models.Dimension;
import de.dreier.mytargets.shared.utils.Color;

import static android.graphics.Color.WHITE;

public class SCAPeriodTarget extends TargetModelBase {

    public static final int ID = 24;

    public SCAPeriodTarget() {
        super((long) ID, R.string.sca_period);
        zones = 3;
        zone.radius = new float[]{50, 200, 500};
        zone.fillColor = new int[]{Color.LEMON_YELLOW, Color.GREEN, WHITE};
        zone.strokeColor = new int[]{Color.DARK_GRAY, Color.DARK_GRAY, Color.DARK_GRAY};
        zone.strokeWidth = new int[]{2, 2, 2};
        zone.zonePoints = new int[][]{{8, 4, 2}};
        zone.showAsX = new boolean[]{false};
        diameters = new Diameter[]{new Diameter(60, Dimension.CENTIMETER)};
    }

    @Override
    protected void onPostDraw(Canvas canvas, Rect rect) {
        paintStroke.setColor(Color.DARK_GRAY);
        final float size = reCalc(rect, 5);
        paintStroke.setStrokeWidth(4 * rect.width() / 1000f);
        canvas.drawLine(rect.exactCenterX() - size, rect.exactCenterY(),
                rect.exactCenterX() + size, rect.exactCenterY(), paintStroke);
        canvas.drawLine(rect.exactCenterX(), rect.exactCenterY() - size,
                rect.exactCenterX(), rect.exactCenterY() + size, paintStroke);
    }
}
