package de.dreier.mytargets;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;

import de.dreier.mytargets.shared.models.Coordinate;
import de.dreier.mytargets.shared.models.RoundTemplate;
import de.dreier.mytargets.shared.models.Shot;
import de.dreier.mytargets.shared.targets.TargetModelBase.SelectableZone;
import de.dreier.mytargets.shared.utils.Circle;
import de.dreier.mytargets.shared.views.TargetViewBase;


public class TargetSelectView extends TargetViewBase {

    private int radius;
    private Paint drawColorPaint;
    private int chinHeight;
    private double circleRadius;
    private Circle circle;

    public TargetSelectView(Context context) {
        super(context);
        init();
    }

    public TargetSelectView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TargetSelectView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public void setChinHeight(int chinHeight) {
        this.chinHeight = chinHeight;
    }

    private void init() {
        density = getResources().getDisplayMetrics().density;
        drawColorPaint = new Paint();
        drawColorPaint.setAntiAlias(true);
        setOnTouchListener(this);
    }

    @Override
    public void setRoundTemplate(RoundTemplate r) {
        super.setRoundTemplate(r);
        circle = new Circle(density, r.target);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int curZone;
        if (currentArrow < round.arrowsPerPasse) {
            curZone = passe.shot[currentArrow].zone;
        } else {
            curZone = -2;
        }

        // Erase background
        drawColorPaint.setColor(0xffffffff);
        canvas.drawRect(0, 0, contentWidth, contentHeight, drawColorPaint);

        // Draw all possible points in a circular
        for (int i = 0; i < selectableZones.size(); i++) {
            Coordinate coordinate = getCircularCoordinates(i);
            circle.draw(canvas, coordinate.x, coordinate.y, selectableZones.get(i).zone, i == curZone ? 23 : 17, false, currentArrow, -1);
        }

        // Draw all points of this passe in the center
        passeDrawer.draw(canvas);
    }

    private Coordinate getCircularCoordinates(int zone) {
        double degree = Math.toRadians(zone * 360.0 / (double) selectableZones.size());
        Coordinate coordinate = new Coordinate();
        coordinate.x = (float) (radius + (Math.cos(degree) * circleRadius));
        coordinate.y = (float) (radius + (Math.sin(degree) * circleRadius));
        float bound = contentHeight - (chinHeight + 15) * density;
        if (coordinate.y > bound) {
            coordinate.y = bound;
        }
        return coordinate;
    }

    @Override
    protected Coordinate initAnimationPositions(int i) {
        final SelectableZone dummyZone = new SelectableZone(passe.shot[currentArrow].zone, "");
        return getCircularCoordinates(selectableZones.indexOf(dummyZone));
    }

    @Override
    protected void calcSizes() {
        radius = (int) (contentWidth / 2.0);
        circleRadius = radius - 25 * density;
        RectF rect = new RectF();
        rect.left = radius - 35 * density;
        rect.right = radius + 35 * density;
        rect.top = radius / 2;
        rect.bottom = radius;
        passeDrawer.animateToRect(rect);
    }

    @Override
    protected Shot getShotFromPos(float x, float y) {
        int zones = selectableZones.size();
        Shot s = new Shot(currentArrow);

        double xDiff = x - radius;
        double yDiff = y - radius;

        float perception_rad = radius - 50 * density;
        // Select current arrow
        if (xDiff * xDiff + yDiff * yDiff > perception_rad * perception_rad) {
            double degree = Math.toDegrees(Math.atan2(-yDiff, xDiff)) - (180.0 / (double) zones);
            if (degree < 0) {
                degree += 360.0;
            }
            int index = (int) (zones * ((360.0 - degree) / 360.0));
            s.zone = selectableZones.get(index).zone;
        }

        if (s.zone == Shot.NOTHING_SELECTED) {
            // When nothing is selected do nothing
            return null;
        }
        final Coordinate coordinate = targetModel.getCoordinateFromZone(s.zone);
        s.x = coordinate.x;
        s.y = coordinate.y;
        return s;
    }

    @Override
    protected boolean selectPreviousShots(MotionEvent motionEvent, float x, float y) {
        return false;
    }
}
