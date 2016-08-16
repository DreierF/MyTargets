package de.dreier.mytargets.shared.targets.drawable;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;

import de.dreier.mytargets.shared.models.Coordinate;
import de.dreier.mytargets.shared.models.Target;
import de.dreier.mytargets.shared.targets.models.TargetModelBase;
import de.dreier.mytargets.shared.targets.zone.ZoneBase;

public class TargetDrawable extends Drawable {

    protected final Target target;
    final TargetModelBase model;
    private Matrix matrix = new Matrix();

    public TargetDrawable(Target target) {
        this.model = target.getModel();
        this.target = target;
    }

    public void setMatrix(Matrix matrix) {
        this.matrix = matrix;
    }

    @Override
    public void setBounds(int left, int top, int right, int bottom) {
        super.setBounds(left, top, right, bottom);
        matrix.setRectToRect(new RectF(-1, -1, 1, 1),
                new RectF(left, top, right, bottom),
                Matrix.ScaleToFit.CENTER);
    }

    @Override
    public void setBounds(Rect bounds) {
        super.setBounds(bounds);
        matrix.setRectToRect(new RectF(-1, -1, 1, 1),
                new RectF(bounds),
                Matrix.ScaleToFit.CENTER);
    }

    public Target getTarget() {
        return target;
    }

    public TargetModelBase getModel() {
        return model;
    }

    private int getZones() {
        return model.getZoneCount();
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.save();
        for (int faceIndex = 0; faceIndex < model.facePositions.length; faceIndex++) {
            Matrix targetMatrix = getTargetMatrix(faceIndex);
            canvas.setMatrix(targetMatrix);
            for (int i = getZones() - 1; i >= 0; i--) {
                if (!model.shouldDrawZone(i, target.scoringStyle)) {
                    continue;
                }
                ZoneBase zone = model.getZone(i);
                zone.draw(canvas);
            }
            onPostDraw(canvas);
        }
        canvas.restore();
    }

    protected Matrix getTargetMatrix(int index) {
        Coordinate pos = model.facePositions[index % model.facePositions.length];
        RectF fullRect = new RectF(-1f, -1f, 1f, 1f);
        final RectF spotRectIn11 = new RectF(pos.x - model.faceRadius, pos.y - model.faceRadius,
                pos.x + model.faceRadius, pos.y + model.faceRadius);
        Matrix matrix = new Matrix();
        matrix.setRectToRect(fullRect, new RectF(spotRectIn11), Matrix.ScaleToFit.CENTER);
        Matrix m = new Matrix(this.matrix);
        m.preConcat(matrix);
        return m;
    }

    protected void onPostDraw(Canvas canvas) {
        if (model.getDecorator() != null) {
            model.getDecorator().drawDecoration(canvas);
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
    public float getWidth() {
        return getDiff(0);
    }

    public float getHeight() {
        return getDiff(1);
    }

    private float getDiff(int coordinate) {
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
        bounds.left = rect.left + (500f + pos.x * 500f - model.faceRadius * 500f) * 0.5f;
        bounds.top = rect.top + (500f + pos.y * 500f - model.faceRadius * 500f) * 0.5f;
        bounds.right = rect.left + (500f + pos.x * 500f + model.faceRadius * 500f) * 0.5f;
        bounds.bottom = rect.top + (500f + pos.y * 500f + model.faceRadius * 500f) * 0.5f;
        return bounds;
    }
}
