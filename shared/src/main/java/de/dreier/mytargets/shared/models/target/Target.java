package de.dreier.mytargets.shared.models.target;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.StringRes;
import android.text.TextPaint;

import java.io.Serializable;
import java.util.ArrayList;

import de.dreier.mytargets.shared.models.Diameter;
import de.dreier.mytargets.shared.models.IIdProvider;
import de.dreier.mytargets.shared.models.Passe;
import de.dreier.mytargets.shared.models.Shot;

import static android.graphics.Color.BLACK;
import static android.graphics.Color.WHITE;

public abstract class Target extends Drawable implements IIdProvider, Serializable {
    public static final int SAPPHIRE_BLUE = 0xFF2E489F;
    protected static final int DARK_GRAY = 0xFF221F1F;
    protected static final int GRAY = 0xFF686868;
    protected static final int LIGHTER_GRAY = 0xFFB7B7B7;
    protected static final int LIGHT_GRAY = 0xFFDBDBDA;

    protected static final int ORANGE = 0xFFFFA663;
    protected static final int GREEN = 0xFF009F23;
    protected static final int BROWN = 0xFF9F7800;
    protected static final int CERULEAN_BLUE = 0xFF00ADEF;
    protected static final int FLAMINGO_RED = 0xFFEF4E4C;
    protected static final int RED = 0xFFFF000D;
    protected static final int TURBO_YELLOW = 0xFFFEEA00;
    protected static final int LEMON_YELLOW = 0xFFF6EB0F;
    protected static final int STROKE_GRAY = 0xFF221F1F;
    protected static final int RED_MISS = 0xFFEE3D36;
    protected static final int YELLOW = 0xFFFFEB52;
    protected static final float ARROW_RADIUS = 8;
    static final long serialVersionUID = 62L;
    public long id;
    public String name;
    public int scoringStyle;
    public Diameter size;
    protected int zones;
    protected float[] radius;
    protected int[] colorFill;
    protected int[] colorStroke;
    protected int[] strokeWidth;
    protected boolean[] showAsX;
    protected int[][] zonePoints;
    protected transient Paint paintFill, paintStroke;
    private boolean outsideIn = true;
    private transient TextPaint paintText;

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
        paintText = new TextPaint();
        paintText.setAntiAlias(true);
        paintText.setColor(Color.BLACK);
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

    public void drawArrows(Canvas canvas, ArrayList<Passe> passes) {
        for (Passe p : passes) {
            drawArrows(canvas, p);
        }
    }

    public void drawArrows(Canvas canvas, Passe passe) {
        drawArrows(canvas, passe, getBounds());
    }

    protected void drawArrows(Canvas canvas, Passe passe, Rect rect) {
        for (int arrow = 0; arrow < passe.shot.length; arrow++) {
            drawArrow(canvas, passe.shot[arrow], rect);
        }
    }

    public void drawArrow(Canvas canvas, Shot shot) {
        drawArrow(canvas, shot, getBounds());
    }

    protected void drawArrow(Canvas canvas, Shot shot, Rect rect) {
        paintFill.setColor(getContrastColor(shot.zone));
        Rect targetRect = getTargetBounds(rect, shot.index);
        float[] pos = new float[2];
        pos[0] = targetRect.left + (1 + shot.x) * targetRect.width() * 0.5f;
        pos[1] = targetRect.top + (1 + shot.y) * targetRect.width() * 0.5f;
        canvas.drawCircle(pos[0], pos[1], getArrowSize(rect, shot.index), paintFill);
    }

    public void drawFocusedArrow(Canvas canvas, Shot shot) {
        drawFocusedArrow(canvas, shot, getBounds());
    }

    private void drawFocusedArrow(Canvas canvas, Shot shot, Rect rect) {
        Rect targetRect = getTargetBounds(rect, shot.index);
        float[] pos = new float[2];
        pos[0] = targetRect.left + (1 + shot.x) * targetRect.width() * 0.5f;
        pos[1] = targetRect.top + (1 + shot.y) * targetRect.width() * 0.5f;
        paintFill.setColor(0xFF009900);
        canvas.drawCircle(pos[0], pos[1], getArrowSize(rect, shot.index), paintFill);

        // Draw cross
        float lineLen = recalc(targetRect, 20);
        canvas.drawLine(pos[0] - lineLen, pos[1], pos[0] + lineLen, pos[1], paintFill);
        canvas.drawLine(pos[0], pos[1] - lineLen, pos[0], pos[1] + lineLen, paintFill);

        // Draw zone points
        String zoneString = zoneToString(shot.zone, shot.index);
        Rect tr = new Rect();
        paintText.getTextBounds(zoneString, 0, zoneString.length(), tr);
        float width = tr.width() / 2.0f;
        float height = tr.height() / 2.0f;
        paintText.setTextSize(recalc(targetRect, 12));
        paintText.setColor(0xFFFFFFFF);
        canvas.drawText(zoneString, pos[0] - width, pos[1] + height, paintText);
    }

    protected Rect getTargetBounds(Rect rect, int index) {
        return rect;
    }

    protected float getArrowSize(Rect rect, int arrow) {
        return recalc(rect, ARROW_RADIUS);
    }

    public void drawArrowAvg(Canvas canvas, float x, float y, int arrow) {
        Rect rect = getBounds();
        int zone = getZoneFromPoint(x, y);
        int color = getContrastColor(zone);
        paintStroke.setColor(color);
        paintStroke.setStrokeWidth(recalc(rect, 2));
        Rect targetRect = getTargetBounds(rect, arrow);
        float[] pos = new float[2];
        pos[0] = targetRect.left + (1 + x) * targetRect.width() * 0.5f;
        pos[1] = targetRect.top + (1 + y) * targetRect.width() * 0.5f;
        float radius = getArrowSize(rect, arrow);
        canvas.drawCircle(pos[0], pos[1], radius, paintStroke);
        canvas.drawLine(pos[0], pos[1] + radius, pos[0], pos[1] - radius, paintStroke);
        canvas.drawLine(pos[0] - radius, pos[1], pos[0] + radius, pos[1], paintStroke);
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
            int value = getPointsByZone(zone, scoringStyle, arrow);
            if (value == 0) {
                return "M";
            }
            return String.valueOf(value);
        }
    }

    public int getPointsByZone(int zone, int arrow) {
        return getPointsByZone(zone, scoringStyle, arrow);
    }

    protected int getPointsByZone(int zone, int scoringStyle, int arrow) {
        if (zone == -1 || zone >= zones) {
            return 0;
        }
        return zonePoints[scoringStyle][zone];
    }

    public int getMaxPoints() {
        return zonePoints[scoringStyle][0];
    }

    public float getXFromZone(int zone) {
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

    public int getFillColor(int zone) {
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
            float adaptedRadius = radius[i] +
                    (scoresAsOutSideIn(i) ? ARROW_RADIUS + strokeWidth[i] / 2.0f : -ARROW_RADIUS);
            float ro = adaptedRadius * adaptedRadius;
            if (radius[i] == 0 && isInZone(500.0f + ax, 500.0f + ay, i, outsideIn) ||
                    ro > distance) {
                return i;
            }
        }
        return Shot.MISS;
    }

    private boolean scoresAsOutSideIn(int i) {
        return outsideIn;
    }

    protected boolean isInZone(float ax, float ay, int zone, boolean outsideIn) {
        return false;
    }

    public int getStrokeColor(int zone) {
        if (zone < 0 || zone >= zones) {
            return BLACK;
        }
        switch (colorFill[zone]) {
            case WHITE:
                return BLACK;
            case BLACK:
            case DARK_GRAY:
            case GRAY:
            case LIGHT_GRAY:
            case ORANGE:
            case GREEN:
            case BROWN:
            case CERULEAN_BLUE:
            case SAPPHIRE_BLUE:
            case FLAMINGO_RED:
            case RED:
            case TURBO_YELLOW:
            case LEMON_YELLOW:
                return colorFill[zone];
            default:
                return DARK_GRAY;
        }
    }

    public int getContrastColor(int zone) {
        if (zone < 0 || zone >= zones) {
            return BLACK;
        }
        switch (colorFill[zone]) {
            case WHITE:
            case LIGHTER_GRAY:
            case LIGHT_GRAY:
            case TURBO_YELLOW:
            case LEMON_YELLOW:
            case YELLOW:
                return BLACK;
            case ORANGE:
                return BLACK;
            case GREEN:
                return BLACK;
            case BROWN:
                return BLACK;
            default:
                return WHITE;
        }
    }

    public int getTextColor(int zone) {
        if (zone < 0 || zone >= zones) {
            return WHITE;
        }
        return getContrastColor(zone);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Target) {
            Target t = (Target) o;
            return t.id == id;
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
                if (i == 0 && zonePoints[scoring][0] < zonePoints[scoring][1]) {
                    continue;
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

    public int getFaceCount() {
        return 1;
    }

    public int getWidth() {
        return 500;
    }

    public int getHeight() {
        return 500;
    }
}
