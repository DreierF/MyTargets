package de.dreier.mytargets.shared.targets;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;

import java.util.List;

import de.dreier.mytargets.shared.models.Coordinate;
import de.dreier.mytargets.shared.models.Target;
import de.dreier.mytargets.shared.models.db.Passe;
import de.dreier.mytargets.shared.models.db.Shot;
import de.dreier.mytargets.shared.utils.Color;

public class TargetDrawable extends Drawable {

    static final float ARROW_RADIUS = 8;
    private static final Path heart = new Path();
    public static final Region HEART_REGION;
    private static final Path ellipse = new Path();
    public static final Region ELLIPSE_REGION;

    static {
        heart.moveTo(296.724f, 162.855f);
        heart.cubicTo(335.067f, 163.204f, 373.244f, 166.065f, 411.339f, 170.284f);
        heart.cubicTo(456.474f, 175.282f, 501.268f, 182.376f, 545.813f, 191.214f);
        heart.cubicTo(603.462f, 202.651f, 659.337f, 219.971f, 714.041f, 241.221f);
        heart.cubicTo(758.396f, 258.451f, 801.187f, 278.853f, 840.953f, 305.206f);
        heart.cubicTo(878.816f, 330.298f, 912.56f, 359.766f, 937.666f, 398.081f);
        heart.cubicTo(963.709f, 437.826f, 977.903f, 481.424f, 977.803f, 529.132f);
        heart.cubicTo(977.729f, 564.625f, 967.457f, 597.725f, 952.022f, 629.375f);
        heart.cubicTo(932.967f, 668.448f, 907.063f, 702.686f, 877.3f, 734.084f);
        heart.cubicTo(819.307f, 795.264f, 751.654f, 843.58f, 677.4f, 882.921f);
        heart.cubicTo(636.841f, 904.41f, 594.784f, 922.353f, 550.413f, 934.514f);
        heart.cubicTo(528.327f, 940.568f, 505.912f, 944.696f, 482.973f, 945.587f);
        heart.cubicTo(457.304f, 946.582f, 433.869f, 938.534f, 411.189f, 927.642f);
        heart.cubicTo(370.663f, 908.181f, 337.503f, 879.354f, 308.084f, 845.893f);
        heart.cubicTo(273.747f, 806.837f, 246.171f, 763.221f, 222.543f, 717.076f);
        heart.cubicTo(176.966f, 628.063f, 145.785f, 534.017f, 125.222f, 436.309f);
        heart.cubicTo(117.673f, 400.437f, 111.934f, 364.285f, 108.819f, 327.757f);
        heart.cubicTo(106.461f, 300.103f, 105.353f, 272.386f, 108.692f, 244.734f);
        heart.cubicTo(109.772f, 235.791f, 111.818f, 226.933f, 113.908f, 218.152f);
        heart.cubicTo(116.691f, 206.465f, 123.898f, 197.894f, 133.807f, 191.308f);
        heart.cubicTo(149.769f, 180.7f, 167.684f, 175.263f, 186.146f, 171.335f);
        heart.cubicTo(214.046f, 165.399f, 242.359f, 163.326f, 270.816f, 162.875f);
        heart.cubicTo(279.451f, 162.739f, 288.089f, 162.855f, 296.724f, 162.855f);
        heart.close();
        RectF rectF = new RectF();
        heart.computeBounds(rectF, true);
        HEART_REGION = new Region();
        HEART_REGION.setPath(heart, new Region((int) rectF.left, (int) rectF.top, (int) rectF.right,
                (int) rectF.bottom));
    }

    static {
        ellipse.moveTo(670, 252.5f);
        ellipse.arcTo(new RectF(94.5f, 252.5f, 332 + (332 - 94.5f), 749.5f), -90, -180, false);
        ellipse.arcTo(new RectF(670 - (332 - 94.5f), 252.5f, 670 + (332 - 94.5f), 749.5f), 90, -180,
                false);
        ellipse.close();
        RectF rectF = new RectF();
        ellipse.computeBounds(rectF, true);
        ELLIPSE_REGION = new Region();
        ELLIPSE_REGION.setPath(ellipse, new Region((int) rectF.left, (int) rectF.top, (int) rectF.right,
                (int) rectF.bottom));
    }

    private final Target target;
    private final TargetModelBase model;
    private Paint paintFill;
    private Paint paintStroke;
    private TextPaint paintText;

    public TargetDrawable(Target target) {
        this.target = target;
        this.model = target.getModel();
        initPaint();
    }

    public Target getTarget() {
        return target;
    }

    public TargetModelBase getModel() {
        return model;
    }

    private void initPaint() {
        paintFill = new Paint();
        paintFill.setAntiAlias(true);
        paintStroke = new Paint();
        paintStroke.setStyle(Paint.Style.STROKE);
        paintStroke.setAntiAlias(true);
        paintText = new TextPaint();
        paintText.setAntiAlias(true);
        paintText.setColor(Color.BLACK);
    }

    private int getZones() {
        return model.getZoneCount();
    }

    @Override
    public void draw(Canvas canvas) {
        draw(canvas, getBounds());
    }

    void draw(Canvas canvas, Rect rect) {
        if (paintFill == null) {
            initPaint();
        }
        for (int faceIndex = 0; faceIndex < model.facePositions.length; faceIndex++) {
            Rect targetRect = getTargetBounds(rect, faceIndex);
            for (int i = getZones() - 1; i >= 0; i--) {
                if(!model.shouldDrawZone(i, target.scoringStyle))
                    continue;
                Zone zone = model.getZone(i);
                paintFill.setColor(zone.fillColor);
                paintStroke.setColor(zone.strokeColor);
                paintStroke.setStrokeWidth(zone.strokeWidth * targetRect.width() / 1000.0f);
                drawZone(canvas, targetRect, zone);
            }
            onPostDraw(canvas, targetRect);
        }
    }

    public void drawArrows(Canvas canvas, List<Passe> passes, boolean transparent) {
        for (Passe p : passes) {
            drawArrows(canvas, p, transparent);
        }
    }

    public void drawArrows(Canvas canvas, Passe passe, boolean transparent) {
        drawArrows(canvas, passe, getBounds(), transparent);
    }

    private void drawArrows(Canvas canvas, Passe passe, Rect rect, boolean transparent) {
        if(!passe.exact) {
            return;
        }
        for (Shot s : passe.getShots()) {
            drawArrow(canvas, s, rect, transparent);
        }
    }

    public void drawArrow(Canvas canvas, Shot shot, boolean transparent) {
        drawArrow(canvas, shot, getBounds(), transparent);
    }

    private void drawArrow(Canvas canvas, Shot shot, Rect rect, boolean transparent) {
        int color = model.getContrastColor(shot.zone);
        if(transparent) {
            color = 0x55000000 | color & 0xFFFFFF;
        }
        paintFill.setColor(color);
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
        float lineLen = reCalc(targetRect, 20);
        canvas.drawLine(pos[0] - lineLen, pos[1], pos[0] + lineLen, pos[1], paintFill);
        canvas.drawLine(pos[0], pos[1] - lineLen, pos[0], pos[1] + lineLen, paintFill);

        // Draw zone points
        String zoneString = target.zoneToString(shot.zone, shot.index);
        Rect tr = new Rect();
        paintText.getTextBounds(zoneString, 0, zoneString.length(), tr);
        float width = tr.width() / 2.0f;
        float height = tr.height() / 2.0f;
        paintText.setTextSize(reCalc(targetRect, 12));
        paintText.setColor(0xFFFFFFFF);
        canvas.drawText(zoneString, pos[0] - width, pos[1] + height, paintText);
    }

    private float getArrowSize(Rect rect, int arrow) {
        return reCalc(getTargetBounds(rect, arrow), ARROW_RADIUS);
    }

    private Rect getTargetBounds(Rect rect, int index) {
        Coordinate pos = model.facePositions[index % model.facePositions.length];
        Rect bounds = new Rect();
        bounds.left = (int) (rect.left + reCalc(rect, pos.x - model.faceRadius));
        bounds.top = (int) (rect.top + reCalc(rect, pos.y - model.faceRadius));
        bounds.right = (int) (rect.left + reCalc(rect, pos.x + model.faceRadius));
        bounds.bottom = (int) (rect.top + reCalc(rect, pos.y + model.faceRadius));
        return bounds;
    }

    public void drawArrowAvg(Canvas canvas, float x, float y, int arrow) {
        Rect rect = getBounds();
        int zone = getZoneFromPoint(x, y);
        int color = model.getContrastColor(zone);
        paintStroke.setColor(color);
        paintStroke.setStrokeWidth(reCalc(rect, 1.5f));
        Rect targetRect = getTargetBounds(rect, arrow);
        float[] pos = new float[2];
        pos[0] = targetRect.left + (1 + x) * targetRect.width() * 0.5f;
        pos[1] = targetRect.top + (1 + y) * targetRect.width() * 0.5f;
        float radius = getArrowSize(rect, arrow);
        canvas.drawCircle(pos[0], pos[1], radius, paintStroke);
        canvas.drawLine(pos[0], pos[1] + radius, pos[0], pos[1] - radius, paintStroke);
        canvas.drawLine(pos[0] - radius, pos[1], pos[0] + radius, pos[1], paintStroke);
    }

    private void drawZone(Canvas canvas, Rect rect, Zone zone) {
        switch (zone.type) {
            case CIRCLE:
                final Coordinate midpoint = zone.midpoint;
                drawStrokeCircle(canvas, rect, midpoint.x, midpoint.y, zone.radius);
                break;
            case HEART:
                drawStrokePath(canvas, rect, heart);
                break;
            case ELLIPSE:
                drawStrokePath(canvas, rect, ellipse);
                break;
        }
    }

    private void drawStrokePath(Canvas canvas, Rect rect, Path path) {
        Matrix scaleMatrix = new Matrix();
        float scale = rect.width() / 1000.0f;
        scaleMatrix.setScale(scale, scale);
        scaleMatrix.postTranslate(rect.left, rect.top);
        Path tmp = new Path(path);
        tmp.transform(scaleMatrix);
        canvas.drawPath(tmp, paintFill);
        canvas.drawPath(tmp, paintStroke);
    }

    private void drawStrokeCircle(Canvas canvas, Rect rect, float x, float y, float radius) {
        final float rad = reCalc(rect, radius);
        final float sx = reCalc(rect, x) + rect.left;
        final float sy = reCalc(rect, y) + rect.top;
        canvas.drawCircle(sx, sy, rad, paintFill);
        canvas.drawCircle(sx, sy, rad, paintStroke);
    }

    private float reCalc(Rect rect, float size) {
        return size * rect.width() / 1000.0f;
    }


    public int getZoneFromPoint(float x, float y) {
        return model.getZoneFromPoint(500.0f + x * 500, 500.0f + y * 500);
    }

    private void onPostDraw(Canvas canvas, Rect rect) {
        final CenterMark centerMark = model.getCenterMark();
        if (centerMark != null) {
            drawCenterMark(canvas, rect, centerMark);
        }

        if (model.getDecoration() != null) {
            switch (model.getDecoration()) {
                case BEURSAULT:
                    //TODO draw numbers
            }
        }
    }

    private void drawCenterMark(Canvas canvas, Rect rect, CenterMark centerMark) {
        paintStroke.setColor(centerMark.color);
        final float size = reCalc(rect, centerMark.size);
        paintStroke.setStrokeWidth(centerMark.stroke * rect.width() / 1000f);
        if (centerMark.tilted) {
            canvas.drawLine(rect.exactCenterX() - size, rect.exactCenterY() - size,
                    rect.exactCenterX() + size, rect.exactCenterY() + size, paintStroke);
            canvas.drawLine(rect.exactCenterX() - size, rect.exactCenterY() + size,
                    rect.exactCenterX() + size, rect.exactCenterY() - size, paintStroke);
        } else {
            canvas.drawLine(rect.exactCenterX() - size, rect.exactCenterY(),
                    rect.exactCenterX() + size, rect.exactCenterY(), paintStroke);
            canvas.drawLine(rect.exactCenterX(), rect.exactCenterY() - size,
                    rect.exactCenterX(), rect.exactCenterY() + size, paintStroke);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof TargetDrawable) {
            TargetDrawable t = (TargetDrawable) o;
            return t.target.id == target.id;
        }
        return false;
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

    /**
     * Used to determine if the target is vertical like 3 spot and
     * thus multiple targets fit next to each other onto one page.
     */
    public int getWidth() {
        return getDiff(0);
    }

    public int getHeight() {
        return getDiff(1);
    }

    private int getDiff(int coordinate) {
        int min = Integer.MAX_VALUE;
        int max = Integer.MIN_VALUE;
        for (Coordinate facePosition : model.facePositions) {
            final float v = coordinate == 0 ? facePosition.x : facePosition.y;
            if (v < min) {
                min = (int) v;
            }
            if (v > max) {
                max = (int) v;
            }
        }
        return max - min + model.faceRadius * 2;
    }

    public RectF getBoundsF(int index, Rect rect) {
        Coordinate pos = model.facePositions[index];
        RectF bounds = new RectF();
        bounds.left = rect.left + reCalc(rect, pos.x - model.faceRadius);
        bounds.top = rect.top + reCalc(rect, pos.y - model.faceRadius);
        bounds.right = rect.left + reCalc(rect, pos.x + model.faceRadius);
        bounds.bottom = rect.top + reCalc(rect, pos.y + model.faceRadius);
        return bounds;
    }
}
