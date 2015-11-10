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
import de.dreier.mytargets.shared.utils.Circle;
import de.dreier.mytargets.shared.views.TargetViewBase;


public class TargetSelectView extends TargetViewBase {

    private int radius;
    private Paint drawColorP;
    private int chinHeight;
    private double circRadius;
    private Circle mCircle;

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
        drawColorP = new Paint();
        drawColorP.setAntiAlias(true);
        setOnTouchListener(this);
    }

    @Override
    public void setRoundTemplate(RoundTemplate r) {
        super.setRoundTemplate(r);
        mCircle = new Circle(density, r.target);
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
        drawColorP.setColor(0xffffffff);
        canvas.drawRect(0, 0, contentWidth, contentHeight, drawColorP);

        // Draw all possible points in a circular
        for (int i = -1; i < mZoneCount; i++) {
            Coordinate coord = getCircularCoords(i);
            mCircle.draw(canvas, coord.x, coord.y, i, i == curZone ? 23 : 17, false, currentArrow,
                    -1);
        }

        // Draw all points of this passe in the center
        passeDrawer.draw(canvas);
    }

    private Coordinate getCircularCoords(int zone) {
        double degree = Math.toRadians(zone * 360.0 / (double) (mZoneCount + 1));
        Coordinate coord = new Coordinate();
        coord.x = (float) (radius + (Math.cos(degree) * circRadius));
        coord.y = (float) (radius + (Math.sin(degree) * circRadius));
        float bound = contentHeight - (chinHeight + 15) * density;
        if (coord.y > bound) {
            coord.y = bound;
        }
        return coord;
    }

    @Override
    protected Coordinate initAnimationPositions(int i) {
        return getCircularCoords(passe.shot[currentArrow].zone);
    }

    @Override
    protected void calcSizes() {
        radius = (int) (contentWidth / 2.0);
        circRadius = radius - 25 * density;
        RectF rect = new RectF();
        rect.left = radius - 35 * density;
        rect.right = radius + 35 * density;
        rect.top = radius / 2;
        rect.bottom = radius;
        passeDrawer.animateToRect(rect);
    }

    @Override
    protected Shot getShotFromPos(float x, float y) {
        int rings = round.target.getZones();
        Shot s = new Shot(currentArrow);

        double xDiff = x - radius;
        double yDiff = y - radius;

        float perception_rad = radius - 50 * density;
        // Select current arrow
        if (xDiff * xDiff + yDiff * yDiff > perception_rad * perception_rad) {
            double degree1 = Math.toDegrees(Math.atan2(-yDiff, xDiff)) - (180.0 / (double) rings);
            if (degree1 < 0) {
                degree1 += 360.0;
            }
            s.zone = (int) ((rings + 1) * ((360.0 - degree1) / 360.0));
        }

        if (s.zone == Shot.NOTHING_SELECTED) {
            // When nothing is selected do nothing
            return null;
        } else if (s.zone >= rings) {
            // Correct points_zone
            s.zone = Shot.MISS;
        }
        s.x = round.target.getXFromZone(s.zone);
        s.y = 0f;
        return s;
    }

    @Override
    protected boolean selectPreviousShots(MotionEvent motionEvent, float x, float y) {
        return false;
    }
}
