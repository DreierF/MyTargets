package de.dreier.mytargets.shared.targets.decoration;

import android.graphics.Canvas;
import android.graphics.Paint;

public class CenterMarkDecorator implements TargetDecorator {
    public final int color;
    public final float size;
    public final int stroke;
    private final boolean tilted;
    private Paint paintStroke;

    public CenterMarkDecorator(int color, float size, int stroke, boolean tilted) {
        this.color = color;
        this.size = size;
        this.stroke = stroke;
        this.tilted = tilted;
    }

    private void initPaint() {
        paintStroke = new Paint();
        paintStroke.setStyle(Paint.Style.STROKE);
        paintStroke.setAntiAlias(true);
    }

    @Override
    public void drawDecoration(Canvas canvas) {
        if (paintStroke == null) {
            initPaint();
        }
        paintStroke.setColor(color);
        paintStroke.setStrokeWidth(stroke / 1000f);
        if (tilted) {
            canvas.drawLine(-size * 0.001f, -size * 0.001f,
                    size * 0.001f, size * 0.001f, paintStroke);
            canvas.drawLine(-size * 0.001f, size * 0.001f,
                    size * 0.001f, -size * 0.001f, paintStroke);
        } else {
            canvas.drawLine(-size * 0.001f, 0, size * 0.001f, 0, paintStroke);
            canvas.drawLine(0, -size * 0.001f, 0, size * 0.001f, paintStroke);
        }
    }
}
