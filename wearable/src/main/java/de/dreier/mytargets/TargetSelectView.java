package de.dreier.mytargets;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.MotionEvent;

import de.dreier.mytargets.shared.models.Coordinate;
import de.dreier.mytargets.shared.models.RoundTemplate;
import de.dreier.mytargets.shared.models.Shot;
import de.dreier.mytargets.shared.utils.Circle;
import de.dreier.mytargets.shared.views.TargetViewBase;

import static android.graphics.Color.WHITE;


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

    void setChinHeight(int chinHeight) {
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
        // Erase background
        drawColorPaint.setColor(WHITE);
        canvas.drawRect(0, 0, contentWidth, contentHeight, drawColorPaint);

        // Draw all possible points in a circular
        int curZone = getCurrentlySelectedZone();
        for (int i = 0; i < selectableZones.size(); i++) {
            Coordinate coordinate = getCircularCoordinates(i);
            circle.draw(canvas, coordinate.x, coordinate.y, selectableZones.get(i).index,
                    i == curZone ? 23 : 17, false, currentArrow, null);
        }

        // Draw all points of this end in the center
        endRenderer.draw(canvas);
    }

    private int getCurrentlySelectedZone() {
        if (end != null && currentArrow < round.arrowsPerEnd) {
            return end.shot[currentArrow].zone;
        } else {
            return Shot.NOTHING_SELECTED;
        }
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
        return getCircularCoordinates(getSelectableZoneIndexFromShot(end.shot[i]));
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
        endRenderer.animateToRect(rect);
    }

    @NonNull
    @Override
    protected Rect getSelectableZonePosition(int i) {
        Coordinate coordinate = getCircularCoordinates(i);
        final int rad = i == getCurrentlySelectedZone() ? 23 : 17;
        final Rect rect = new Rect();
        rect.left = (int) (coordinate.x - rad);
        rect.top = (int) (coordinate.y - rad);
        rect.right = (int) (coordinate.x + rad);
        rect.bottom = (int) (coordinate.y + rad);
        return rect;
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
            s.zone = selectableZones.get(index).index;
        }

        if (s.zone == Shot.NOTHING_SELECTED) {
            // When nothing is selected do nothing
            return null;
        }
        return s;
    }

    @Override
    protected boolean selectPreviousShots(MotionEvent motionEvent, float x, float y) {
        return false;
    }
}
