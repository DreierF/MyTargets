/*
 * Copyright (C) 2017 Florian Dreier
 *
 * This file is part of MyTargets.
 *
 * MyTargets is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2
 * as published by the Free Software Foundation.
 *
 * MyTargets is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */

package de.dreier.mytargets.shared.targets.drawable;

import android.graphics.Paint;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextPaint;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.dreier.mytargets.shared.models.Dimension;
import de.dreier.mytargets.shared.models.Target;
import de.dreier.mytargets.shared.models.db.Shot;
import de.dreier.mytargets.shared.streamwrapper.Stream;

import static de.dreier.mytargets.shared.utils.Color.WHITE;

public class TargetImpactDrawable extends TargetDrawable {
    @NonNull
    protected List<List<Shot>> shots = new ArrayList<>();
    @NonNull
    protected List<List<Shot>> transparentShots = new ArrayList<>();
    private Paint paintFill;
    private float arrowRadius;
    private boolean shouldDrawArrows = true;
    private Shot focusedArrow;
    private TextPaint paintText;

    public TargetImpactDrawable(@NonNull Target target) {
        super(target);
        initPaint();
        setArrowDiameter(new Dimension(5, Dimension.Unit.MILLIMETER), 1);
        for (int i = 0; i < model.getFaceCount(); i++) {
            shots.add(new ArrayList<>());
            transparentShots.add(new ArrayList<>());
        }
    }

    private void initPaint() {
        paintFill = new Paint();
        paintFill.setAntiAlias(true);
        paintText = new TextPaint();
        paintText.setAntiAlias(true);
        paintText.setColor(WHITE);
    }

    public void setArrowDiameter(@NonNull Dimension arrowDiameter, float scale) {
        Dimension targetSize = model.getRealSize(target.diameter).convertTo(arrowDiameter.getUnit());
        arrowRadius = arrowDiameter.getValue() * scale / targetSize.getValue();
    }

    @Override
    protected void onPostDraw(@NonNull CanvasWrapper canvas, int faceIndex) {
        super.onPostDraw(canvas, faceIndex);
        if (!shouldDrawArrows) {
            return;
        }
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
        if (focusedArrow != null) {
            drawFocusedArrow(canvas, focusedArrow, faceIndex);
        }
    }

    public int getZoneFromPoint(float x, float y) {
        return model.getZoneFromPoint(x, y, arrowRadius);
    }

    private void drawArrow(@NonNull CanvasWrapper canvas, @NonNull Shot shot, boolean transparent) {
        int color = model.getContrastColor(shot.scoringRing);
        if (transparent) {
            color = 0x55000000 | color & 0xFFFFFF;
        }
        paintFill.setColor(color);
        canvas.drawCircle(shot.x, shot.y, arrowRadius, paintFill);
    }

    public void setFocusedArrow(@Nullable Shot shot) {
        focusedArrow = shot;
        if (focusedArrow == null) {
            setMid(0, 0);
        } else {
            setMid(shot.x, shot.y);
        }
    }

    private void drawFocusedArrow(@NonNull CanvasWrapper canvas, @NonNull Shot shot, int drawFaceIndex) {
        if (shot.index % model.getFaceCount() != drawFaceIndex) {
            return;
        }

        paintFill.setColor(0xFF009900);
        canvas.drawCircle(shot.x, shot.y, arrowRadius, paintFill);

        // Draw cross
        float lineLen = 2f * arrowRadius;
        paintFill.setStrokeWidth(0.2f * arrowRadius);
        canvas.drawLine(shot.x - lineLen, shot.y, shot.x + lineLen, shot.y, paintFill);
        canvas.drawLine(shot.x, shot.y - lineLen, shot.x, shot.y + lineLen, paintFill);

        // Draw zone points
        String zoneString = target.zoneToString(shot.scoringRing, shot.index);
        RectF srcRect = new RectF(shot.x - arrowRadius, shot.y - arrowRadius,
                shot.x + arrowRadius, shot.y + arrowRadius);
        canvas.drawText(zoneString, srcRect, paintText);
    }

    public void setShots(@NonNull List<Shot> shots) {
        for (int i = 0; i < this.shots.size(); i++) {
            this.shots.get(i).clear();
        }
        Map<Integer, List<Shot>> map = Stream.of(shots)
                .groupingBy(shot -> shot.index % model.getFaceCount());
        for (Map.Entry<Integer, List<Shot>> entry : map.entrySet()) {
            this.shots.set(entry.getKey(), entry.getValue());
        }
        notifyArrowSetChanged();
    }

    public void setTransparentShots(@NonNull Stream<Shot> shots) {
        new AsyncTask<Void, Void, Map<Integer, List<Shot>>>() {
            @Override
            protected Map<Integer, List<Shot>> doInBackground(Void... objects) {
                return shots
                        .groupingBy(shot -> shot.index % model.getFaceCount());
            }

            @Override
            protected void onPostExecute(@NonNull Map<Integer, List<Shot>> map) {
                super.onPostExecute(map);
                for (List<Shot> shotList : transparentShots) {
                    shotList.clear();
                }
                for (Map.Entry<Integer, List<Shot>> entry : map.entrySet()) {
                    transparentShots.set(entry.getKey(), entry.getValue());
                }
                notifyArrowSetChanged();
            }
        }.execute();
    }

    public void notifyArrowSetChanged() {
        invalidateSelf();
    }

    public void drawArrowsEnabled(boolean enabled) {
        shouldDrawArrows = enabled;
    }

    public void cleanup() {

    }
}
