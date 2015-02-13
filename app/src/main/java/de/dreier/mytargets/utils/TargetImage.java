package de.dreier.mytargets.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

import de.dreier.mytargets.models.Round;
import de.dreier.mytargets.models.Target;
import de.dreier.mytargets.models.Passe;

public class TargetImage {

    private Paint thinBlackBorder;
    private Paint thinWhiteBorder;
    private Paint drawColorP;

    private int mZoneCount;
    private int[] target;
    private static final int density = 1;

    public void generateBitmap(Context context, int size, Round roundInfo, long round, OutputStream fOut) {
        // Create bitmap to draw on
        Bitmap b = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(b);
        canvas.drawColor(0, PorterDuff.Mode.CLEAR);

        // Initialize variables
        int radius = size / 2;
        TargetOpenHelper db = new TargetOpenHelper(context);
        ArrayList<Passe> oldOnes = db.getRoundPasses(round, -1);
        mZoneCount = Target.target_rounds[roundInfo.target].length;
        init();

        // Draw target
        target = Target.target_rounds[roundInfo.target];
        for (int i = mZoneCount; i > 0; i--) {
            // Select colors to draw with
            drawColorP.setColor(Target.highlightColor[target[i - 1]]);

            // Draw a ring mit separator line
            float rad = (radius * i) / (float) mZoneCount;
            canvas.drawCircle(radius, radius, rad, drawColorP);
            canvas.drawCircle(radius, radius, rad, Target.target_rounds[roundInfo.target][i - 1] == 3 ? thinWhiteBorder : thinBlackBorder);
        }

        // Draw exact arrow position
        drawArrows(canvas, radius, oldOnes);

        try {
            b.compress(Bitmap.CompressFormat.PNG, 100, fOut);
            fOut.flush();
            fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void drawArrows(Canvas canvas, int radius, ArrayList<Passe> oldOnes) {
        float count = 0;
        float sumX = 0;
        float sumY = 0;
        for (Passe p : oldOnes) {
            for (int i = 0; i < p.points.length; i++) {
                // For yellow and white background use black font color
                int colorInd = i == mZoneCount || p.zones[i] < 0 ? 0 : target[p.zones[i]];
                drawColorP.setColor(colorInd == 0 || colorInd == 4 ? Color.BLACK : Color.WHITE);
                float selX = p.points[i][0];
                float selY = p.points[i][1];
                sumX += selX;
                sumY += selY;
                count++;

                // Draw arrow position
                float xp = radius + selX * radius;
                float yp = radius + selY * radius;
                canvas.drawCircle(xp, yp, 3 * density, drawColorP);
            }
        }

        if (count >= 2) {
            drawColorP.setColor(Color.RED);
            canvas.drawCircle(radius + (sumX / count) * radius, radius + (sumY / count) * radius, 3 * density, drawColorP);
        }
    }

    private void init() {
        // Set up a default Paint objects
        thinBlackBorder = new Paint();
        thinBlackBorder.setColor(0xFF1C1C1B);
        thinBlackBorder.setAntiAlias(true);
        thinBlackBorder.setStyle(Paint.Style.STROKE);

        thinWhiteBorder = new Paint();
        thinWhiteBorder.setColor(0xFFEEEEEE);
        thinWhiteBorder.setAntiAlias(true);
        thinWhiteBorder.setStyle(Paint.Style.STROKE);

        drawColorP = new Paint();
        drawColorP.setAntiAlias(true);
    }

}
