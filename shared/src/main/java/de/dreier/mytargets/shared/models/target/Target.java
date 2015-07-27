package de.dreier.mytargets.shared.models.target;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.StringRes;

import java.io.Serializable;
import java.util.ArrayList;

import de.dreier.mytargets.shared.models.Diameter;
import de.dreier.mytargets.shared.models.IIdProvider;

import static android.graphics.Color.BLACK;
import static android.graphics.Color.WHITE;

public abstract class Target extends Drawable implements IIdProvider, Serializable {
    static final long serialVersionUID = 62L;

    protected static final int DARK_GRAY = 0xFF221F1F;
    protected static final int GRAY = 0xFF686868;
    protected static final int LIGHTER_GRAY = 0xFFB7B7B7;
    protected static final int LIGHT_GRAY = 0xFFDBDBDA;

    protected static final int ORANGE = 0xFFFFA663;
    protected static final int GREEN = 0xFF009F23;
    protected static final int BROWN = 0xFF9F7800;
    protected static final int CERULEAN_BLUE = 0xFF00ADEF;
    public static final int SAPPHIRE_BLUE = 0xFF2E489F;
    protected static final int FLAMINGO_RED = 0xFFEF4E4C;
    protected static final int RED = 0xFFFF000D;
    protected static final int TURBO_YELLOW = 0xFFFEEA00;
    protected static final int LEMON_YELLOW = 0xFFF6EB0F;

    public long id;
    public String name;
    protected int zones;
    protected float[] radius;
    protected int[] colorFill;
    protected int[] colorStroke;
    protected int[] strokeWidth;
    protected boolean[] showAsX;
    protected int[][] zonePoints;
    protected transient Paint paintFill, paintStroke;
    public int scoringStyle;
    public Diameter size;

    protected Target(Context c, long id, @StringRes int nameRes) {
        this.id = id;
        name = c.getString(nameRes);
        initPaint();
    }

    public void initPaint() {
        paintFill = new Paint();
        paintFill.setAntiAlias(true);
        paintStroke = new Paint();
        paintStroke.setStyle(Paint.Style.STROKE);
        paintStroke.setAntiAlias(true);
    }

    @Override
    public long getId() {
        return id;
    }

    public int getZones() {
        return zones;
    }

    @Override
    public void draw(Canvas canvas) {
        draw(canvas, getBounds());
    }

    protected void draw(Canvas canvas, Rect rect) {
        if (paintFill == null) {
            initPaint();
        }
        for (int zone = zones - 1; zone >= 0; zone--) {
            paintFill.setColor(colorFill[zone]);
            paintStroke.setColor(colorStroke[zone]);
            paintStroke.setStrokeWidth(strokeWidth[zone] * rect.width() / 1000.0f);
            drawZone(canvas, rect, zone);
        }
        onPostDraw(canvas, rect);
    }

    protected void drawZone(Canvas canvas, Rect rect, int zone) {
        drawStrokeCircle(canvas, rect, radius[zone]);
    }

    protected void onPostDraw(Canvas canvas, Rect rect) {
    }

    protected void drawStrokeCircle(Canvas canvas, Rect rect, float radius) {
        final float rad = recalc(rect, radius);
        float x = rect.exactCenterX();
        float y = rect.exactCenterY();
        canvas.drawCircle(x, y, rad, paintFill);
        canvas.drawCircle(x, y, rad, paintStroke);
    }

    protected void drawStrokePath(Canvas canvas, Rect rect, Path path) {
        Matrix scaleMatrix = new Matrix();
        float scale = rect.width() / 1000.0f;
        scaleMatrix.setScale(scale, scale);
        scaleMatrix.postTranslate(rect.left, rect.top);
        Path tmp = new Path(path);
        tmp.transform(scaleMatrix);
        canvas.drawPath(tmp, paintFill);
        canvas.drawPath(tmp, paintStroke);
    }

    protected void drawStrokeCircle(Canvas canvas, Rect rect, float x, float y, float radius) {
        final float rad = recalc(rect, radius);
        final float sx = recalc(rect, x) + rect.left;
        final float sy = recalc(rect, y) + rect.top;
        canvas.drawCircle(sx, sy, rad, paintFill);
        canvas.drawCircle(sx, sy, rad, paintStroke);
    }

    public String zoneToString(int zone, int arrow) {
        return zoneToString(zone, scoringStyle, arrow);
    }

    String zoneToString(int zone, int scoringStyle, int arrow) {
        if (zone <= -1 || zone >= zonePoints[scoringStyle].length) {
            return "M";
        } else if (zone == 0 && showAsX[scoringStyle]) {
            return "X";
        } else {
            int value = getPointsByZone(zone, arrow);
            if (value == 0) {
                return "M";
            }
            return String.valueOf(value);
        }
    }

    public int getPointsByZone(int zone, int arrow) {
        return zonePoints[scoringStyle][zone];
    }

    public int getMaxPoints() {
        return zonePoints[scoringStyle][0];
    }

    public float zoneToX(int zone) {
        int zones = zonePoints[scoringStyle].length;
        if (zone < 0) {
            return (zones * 2 + 1) / (float) (zones * 2);
        } else {
            float adjacentZone = zone == zones - 1 ? radius[zone - 1] : radius[zone + 1];
            float diff = Math
                    .abs(adjacentZone - radius[zone]);
            return (radius[zone] + (diff / 2.0f)) / 1000.0f;
            //TODO test for non circular targets
        }
    }

    protected float recalc(Rect rect, float size) {
        return size * rect.width() / 1000.0f;
    }

    public int getZoneColor(int zone) {
        if (zone == -1 || zone >= zones) {
            return BLACK;
        }
        return colorFill[zone];
    }

    public int getZoneFromPoint(float x, float y) {
        float ax = x * 500;
        float ay = y * 500;
        float distance = ax * ax + ay * ay;
        for (int i = 0; i < radius.length; i++) {
            float ro = radius[i] * radius[i];
            if (ro == 0 && isInZone(500.0f + ax, 500.0f + ay, i) || ro > distance) {
                return i;
            }
        }
        return -1;
    }

    protected boolean isInZone(float ax, float ay, int zone) {
        return false;
    }

    public int getStrokeColor(int zone) {
        if (zone == -1 || zone >= zones) {
            return WHITE;
        }
        if (colorFill[zone] == WHITE) {
            return BLACK;
        }
        return DARK_GRAY;
    }

    public int getTextColor(int zone) {
        if (zone == -1 || zone >= zones) {
            return WHITE;
        }
        if (colorFill[zone] == WHITE) {
            return BLACK;
        }
        return WHITE;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Target) {
            Target t = (Target) o;
            return t.id == id && t.scoringStyle == scoringStyle;
        }
        return false;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public int getOpacity() {
        return PixelFormat.OPAQUE;
    }

    @Override
    public void setAlpha(int arg0) {
    }

    @Override
    public void setColorFilter(ColorFilter arg0) {
    }

    public abstract Diameter[] getDiameters();

    public ArrayList<String> getScoringStyles() {
        ArrayList<String> styles = new ArrayList<>(zonePoints.length);
        for (int scoring = 0; scoring < zonePoints.length; scoring++) {
            String style = "";
            for (int i = 0; i < zones; i++) {
                if (!style.isEmpty()) {
                    style += ", ";
                }
                style += zoneToString(i, scoring, 0);
            }
            styles.add(style);
        }
        return styles;
    }

    public boolean dependsOnArrowIndex() {
        return false;
    }

    public boolean isFieldTarget() {
        return false;
    }

    public boolean is3DTarget() {
        return false;
    }
}
