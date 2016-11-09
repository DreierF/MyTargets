package de.dreier.mytargets.shared.targets.drawable;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.v4.util.Pair;
import android.text.TextPaint;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.dreier.mytargets.shared.models.Dimension;
import de.dreier.mytargets.shared.models.SelectableZone;
import de.dreier.mytargets.shared.models.Shot;
import de.dreier.mytargets.shared.models.Target;

import static de.dreier.mytargets.shared.utils.Color.WHITE;

public class TargetImpactDrawable extends TargetDrawable {
    private Paint paintFill;
    private Paint paintStroke;
    private Map<String, Bitmap> scoresTextCache = new HashMap<>();
    private RectF textRect;
    private float arrowRadius = 8;
    protected List<List<Shot>> shots = new ArrayList<>();
    protected List<List<Shot>> transparentShots = new ArrayList<>();

    public TargetImpactDrawable(Target target) {
        super(target);
        initPaint();
        initScoresBitmapCache();
        setArrowDiameter(new Dimension(5, Dimension.Unit.MILLIMETER), 1);
        for (int i = 0; i < model.getFaceCount(); i++) {
            shots.add(new ArrayList<>());
            transparentShots.add(new ArrayList<>());
        }
    }

    private void initPaint() {
        paintFill = new Paint();
        paintFill.setAntiAlias(true);
        paintStroke = new Paint();
        paintStroke.setStyle(Paint.Style.STROKE);
        paintStroke.setAntiAlias(true);
    }

    private void initScoresBitmapCache() {
        TextPaint paintText = new TextPaint();
        paintText.setAntiAlias(true);
        paintText.setColor(WHITE);
        Rect tr = new Rect();
        paintText.setTextSize(20);

        final Set<SelectableZone> selectableZones = target.getAllPossibleSelectableZones();
        List<Pair<SelectableZone, Rect>> rects = new ArrayList<>();
        int maxWidth = 0;
        int maxHeight = 0;
        for (SelectableZone zone : selectableZones) {
            paintText.getTextBounds(zone.text, 0, zone.text.length(), tr);
            rects.add(new Pair<>(zone, new Rect(tr)));
            if (tr.width() > maxWidth) {
                maxWidth = tr.width();
            }
            if (tr.height() > maxHeight) {
                maxHeight = tr.height();
            }
        }

        // Leave some space around the text for antialiasing
        maxWidth += 4;
        maxHeight += 4;
        textRect = new RectF(0, 0, maxWidth, maxHeight);
        for (Pair<SelectableZone, Rect> rect : rects) {
            Bitmap b = Bitmap.createBitmap(maxWidth, maxHeight, Bitmap.Config.ARGB_8888);
            Canvas c = new Canvas(b);
            final float x = (maxWidth - rect.second.width()) * 0.5f;
            final float y = (maxHeight + rect.second.height()) * 0.5f;
            c.drawText(rect.first.text, x, y, paintText);
            scoresTextCache.put(rect.first.text, b);
        }
    }

    public void setArrowDiameter(Dimension arrowDiameter, float scale) {
        Dimension targetSize = model.getRealSize(target.size).convertTo(arrowDiameter.unit);
        arrowRadius = arrowDiameter.value * scale / targetSize.value;
    }

    @Override
    protected void onPostDraw(Canvas canvas, int faceIndex) {
        super.onPostDraw(canvas, faceIndex);
        if (paintFill == null) {
            initPaint();
        }

        if (transparentShots.size() > faceIndex) {
            for (Shot s : transparentShots.get(faceIndex)) {
                drawArrow(canvas, s, true);
            }
        }
        if (shots.size() > faceIndex) {
            for (Shot s : shots.get(faceIndex)) {
                drawArrow(canvas, s, false);
            }
        }
    }

    public int getZoneFromPoint(float x, float y) {
        return model.getZoneFromPoint(x, y, arrowRadius);
    }

    private void drawArrow(Canvas canvas, Shot shot, boolean transparent) {
        int color = model.getContrastColor(shot.zone);
        if (transparent) {
            color = 0x55000000 | color & 0xFFFFFF;
        }
        paintFill.setColor(color);
        canvas.drawCircle(shot.x, shot.y, arrowRadius, paintFill);
    }

    public void drawFocusedArrow(Canvas canvas, Shot shot) {
        Matrix targetMatrix = getTargetFaceMatrix(shot.index);
        canvas.setMatrix(targetMatrix);

        paintFill.setColor(0xFF009900);
        canvas.drawCircle(shot.x, shot.y, arrowRadius, paintFill);

        // Draw cross
        float lineLen = 2f * arrowRadius;
        paintFill.setStrokeWidth(0.2f * arrowRadius);
        canvas.drawLine(shot.x - lineLen, shot.y, shot.x + lineLen, shot.y, paintFill);
        canvas.drawLine(shot.x, shot.y - lineLen, shot.x, shot.y + lineLen, paintFill);

        // Draw zone points
        String zoneString = target.zoneToString(shot.zone, shot.index);
        RectF srcRect = new RectF(shot.x - arrowRadius, shot.y - arrowRadius,
                shot.x + arrowRadius, shot.y + arrowRadius);
        Matrix m = new Matrix();
        m.setRectToRect(textRect, srcRect, Matrix.ScaleToFit.CENTER);
        m.postConcat(targetMatrix);
        canvas.setMatrix(m);
        canvas.drawBitmap(scoresTextCache.get(zoneString), 0, 0, null);
    }

    public void setShots(List<Shot> shots) {
        for (int i = 0; i < this.shots.size(); i++) {
            this.shots.get(i).clear();
        }
        Map<Integer, List<Shot>> map = Stream.of(shots)
                .collect(Collectors.groupingBy(shot -> shot.index % model.getFaceCount()));
        for (Map.Entry<Integer, List<Shot>> entry : map.entrySet()) {
            this.shots.set(entry.getKey(), entry.getValue());
        }
        notifyArrowSetChanged();
    }

    public void setTransparentShots(List<Shot> transparentShots) {
        for (int i = 0; i < this.transparentShots.size(); i++) {
            this.transparentShots.get(i).clear();
        }
        Map<Integer, List<Shot>> map = Stream.of(transparentShots)
                .collect(Collectors.groupingBy(shot -> shot.index % model.getFaceCount()));
        for (Map.Entry<Integer, List<Shot>> entry : map.entrySet()) {
            this.transparentShots.set(entry.getKey(), entry.getValue());
        }
        notifyArrowSetChanged();
    }

    public void notifyArrowSetChanged() {
        invalidateSelf();
    }
}
