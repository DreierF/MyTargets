package de.dreier.mytargets.shared.targets.drawable;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;

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
    public void setBounds(@NonNull Rect bounds) {
        super.setBounds(bounds);
        matrix.setRectToRect(new RectF(-1, -1, 1, 1),
                new RectF(bounds),
                Matrix.ScaleToFit.CENTER);
    }

    public Target getTarget() {
        return target;
    }

    private int getZones() {
        return model.getZoneCount();
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
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
}
