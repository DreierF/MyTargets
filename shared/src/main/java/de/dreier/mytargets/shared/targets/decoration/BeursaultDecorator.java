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

package de.dreier.mytargets.shared.targets.decoration;

import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.text.TextPaint;

import de.dreier.mytargets.shared.targets.drawable.CanvasWrapper;
import de.dreier.mytargets.shared.targets.models.Beursault;
import de.dreier.mytargets.shared.targets.zone.ZoneBase;
import de.dreier.mytargets.shared.utils.Color;

import static de.dreier.mytargets.shared.utils.Color.DARK_GRAY;

public class BeursaultDecorator extends CenterMarkDecorator {
    private static final Path one = new Path();
    private static final Path two = new Path();
    private static final Path three = new Path();
    private static final RectF oneBounds;
    private static final RectF twoBounds;
    private static final RectF threeBounds;

    static {
        one.moveTo(-9.3f, 30.7f);
        one.lineTo(-11.9f, 30.7f);
        one.lineTo(-11.9f, 20.900002f);
        one.cubicTo(-12.9f, 21.800001f, -14.0f, 22.400002f, -15.299999f, 22.900002f);
        one.lineTo(-15.299999f, 20.500002f);
        one.cubicTo(-14.599999f, 20.300001f, -13.9f, 19.900002f, -13.099999f, 19.200003f);
        one.cubicTo(-12.299999f, 18.600002f, -11.799999f, 17.900003f, -11.499999f, 17.000002f);
        one.lineTo(-9.4f, 17.000002f);
        one.lineTo(-9.3f, 30.7f);
        one.lineTo(-9.3f, 30.7f);
        one.close();
        oneBounds = new RectF();
        one.computeBounds(oneBounds, true);
    }

    static {
        two.moveTo(-5.2f, 28.4f);
        two.lineTo(-5.2f, 30.8f);
        two.lineTo(-14.3f, 30.8f);
        two.cubicTo(-14.2f, 29.9f, -13.900001f, 29.0f, -13.400001f, 28.199999f);
        two.cubicTo(-12.900001f, 27.4f, -11.900001f, 26.3f, -10.5f, 24.9f);
        two.cubicTo(-9.3f, 23.8f, -8.6f, 23.1f, -8.3f, 22.699999f);
        two.cubicTo(-7.9f, 22.199999f, -7.8f, 21.599998f, -7.8f, 21.099998f);
        two.cubicTo(-7.8f, 20.499998f, -8.0f, 20.099998f, -8.3f, 19.8f);
        two.cubicTo(-8.6f, 19.5f, -9.0f, 19.3f, -9.6f, 19.3f);
        two.cubicTo(-10.1f, 19.3f, -10.6f, 19.5f, -10.900001f, 19.8f);
        two.cubicTo(-11.200001f, 20.099998f, -11.400001f, 20.699999f, -11.500001f, 21.4f);
        two.lineTo(-14.000001f, 21.199999f);
        two.cubicTo(-13.800001f, 19.8f, -13.400001f, 18.699999f, -12.500001f, 18.099998f);
        two.cubicTo(-11.700001f, 17.499998f, -10.700001f, 17.199999f, -9.400002f, 17.199999f);
        two.cubicTo(-8.100001f, 17.199999f, -7.0000014f, 17.599998f, -6.2000017f, 18.3f);
        two.cubicTo(-5.4f, 19.0f, -5.0f, 19.9f, -5.0f, 21.0f);
        two.cubicTo(-5.0f, 21.6f, -5.1f, 22.2f, -5.3f, 22.7f);
        two.cubicTo(-5.5f, 23.300001f, -5.9f, 23.800001f, -6.3f, 24.400002f);
        two.cubicTo(-6.6000004f, 24.800001f, -7.2000003f, 25.400002f, -8.0f, 26.100002f);
        two.cubicTo(-8.8f, 26.900002f, -9.3f, 27.400002f, -9.6f, 27.600002f);
        two.cubicTo(-9.8f, 27.800003f, -10.0f, 28.100002f, -10.1f, 28.300003f);
        two.lineTo(-5.2f, 28.4f);
        two.lineTo(-5.2f, 28.4f);
        two.close();
        twoBounds = new RectF();
        two.computeBounds(twoBounds, true);
    }

    static {
        three.moveTo(-14.1f, 26.8f);
        three.lineTo(-11.6f, 26.5f);
        three.cubicTo(-11.5f, 27.1f, -11.3f, 27.6f, -11.0f, 28.0f);
        three.cubicTo(-10.7f, 28.4f, -10.2f, 28.5f, -9.7f, 28.5f);
        three.cubicTo(-9.2f, 28.5f, -8.7f, 28.3f, -8.3f, 27.9f);
        three.cubicTo(-7.9f, 27.5f, -7.7000003f, 26.9f, -7.7000003f, 26.199999f);
        three.cubicTo(-7.7000003f, 25.499998f, -7.9f, 24.999998f, -8.200001f, 24.599998f);
        three.cubicTo(-8.6f, 24.2f, -9.0f, 24.0f, -9.5f, 24.0f);
        three.cubicTo(-9.8f, 24.0f, -10.2f, 24.1f, -10.7f, 24.2f);
        three.lineTo(-10.4f, 22.1f);
        three.cubicTo(-9.7f, 22.1f, -9.2f, 22.0f, -8.799999f, 21.6f);
        three.cubicTo(-8.4f, 21.300001f, -8.199999f, 20.800001f, -8.199999f, 20.300001f);
        three.cubicTo(-8.199999f, 19.800001f, -8.299999f, 19.500002f, -8.599998f, 19.2f);
        three.cubicTo(-8.899999f, 18.900002f, -9.199999f, 18.800001f, -9.699999f, 18.800001f);
        three.cubicTo(-10.099998f, 18.800001f, -10.499999f, 19.000002f, -10.799999f, 19.300001f);
        three.cubicTo(-11.099999f, 19.6f, -11.299999f, 20.1f, -11.4f, 20.7f);
        three.lineTo(-13.799999f, 20.300001f);
        three.cubicTo(-13.599999f, 19.500002f, -13.4f, 18.800001f, -12.999999f, 18.300001f);
        three.cubicTo(-12.699999f, 17.800001f, -12.199999f, 17.400002f, -11.599999f, 17.1f);
        three.cubicTo(-10.999999f, 16.800001f, -10.299999f, 16.7f, -9.599999f, 16.7f);
        three.cubicTo(-8.299999f, 16.7f, -7.299999f, 17.1f, -6.4999995f, 17.900002f);
        three.cubicTo(-5.8999996f, 18.600002f, -5.4999995f, 19.300001f, -5.4999995f, 20.2f);
        three.cubicTo(-5.4999995f, 21.400002f, -6.1999993f, 22.300001f, -7.4999995f, 23.1f);
        three.cubicTo(-6.6999993f, 23.300001f, -6.0999994f, 23.6f, -5.5999994f, 24.2f);
        three.cubicTo(-5.0999994f, 24.800001f, -4.8999996f, 25.5f, -4.8999996f, 26.300001f);
        three.cubicTo(-4.8999996f, 27.500002f, -5.2999997f, 28.500002f, -6.2f, 29.400002f);
        three.cubicTo(-7.1f, 30.300001f, -8.2f, 30.7f, -9.5f, 30.7f);
        three.cubicTo(-10.7f, 30.7f, -11.8f, 30.300001f, -12.6f, 29.6f);
        three.cubicTo(-13.5f, 28.9f, -13.9f, 28.0f, -14.1f, 26.8f);
        three.close();
        threeBounds = new RectF();
        three.computeBounds(threeBounds, true);
    }

    private Paint paintFill;
    private TextPaint paintText;
    private Beursault model;

    public BeursaultDecorator(Beursault beursault) {
        super(DARK_GRAY, 500f, 6, false);
        model = beursault;
    }

    private void initPaint() {
        paintFill = new Paint();
        paintFill.setAntiAlias(true);
        paintFill.setColor(Color.WHITE);
        paintText = new TextPaint();
        paintText.setAntiAlias(true);
        paintText.setColor(Color.BLACK);
    }

    @Override
    public void drawDecoration(CanvasWrapper canvas) {
        super.drawDecoration(canvas);
        if (paintText == null) {
            initPaint();
        }
        drawPathsForZone(canvas, 6, 6, 2.4f, one, oneBounds);
        drawPathsForZone(canvas, 4, 4, 2.4f, two, twoBounds);
        drawPathsForZone(canvas, 2, 3, 1.05f, three, threeBounds);
    }

    private void drawPathsForZone(CanvasWrapper canvas, int innerZoneIndex, int outerZoneIndex, float scale, Path path, RectF pathBounds) {
        final ZoneBase outerZone = model.getZone(outerZoneIndex);
        final ZoneBase innerZone = model.getZone(innerZoneIndex);
        final float outerRadius = outerZone.radius - outerZone.strokeWidth * 0.5f;
        final float innerRadius = innerZone.radius + innerZone.strokeWidth * 0.5f;
        final float rel = (outerRadius + innerRadius) * 0.5f;
        drawFilledPath(canvas, 0f, -rel, scale, path, pathBounds); // top
        drawFilledPath(canvas, -rel, 0f, scale, path, pathBounds); // left
        drawFilledPath(canvas, 0f, rel, scale, path, pathBounds); // bottom
        drawFilledPath(canvas, rel, 0f, scale, path, pathBounds); // right
    }

    private void drawFilledPath(CanvasWrapper canvas, float x, float y, float scaleFactor, Path path, RectF bounds) {
        float rectSize = 0.012f * 2 * scaleFactor;
        final RectF bgRect = new RectF(x - rectSize, y - rectSize, x + rectSize, y + rectSize);
        canvas.drawRect(bgRect, paintFill);
        rectSize = 0.007f * 2 * scaleFactor;
        final RectF numberRect = new RectF(x - rectSize, y - rectSize, x + rectSize, y + rectSize);
        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setRectToRect(bounds, numberRect, Matrix.ScaleToFit.CENTER);
        Path tmp = new Path(path);
        tmp.transform(scaleMatrix);
        canvas.drawPath(tmp, paintText);
    }
}
