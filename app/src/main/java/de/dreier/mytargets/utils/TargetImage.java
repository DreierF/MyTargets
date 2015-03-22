/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */

package de.dreier.mytargets.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.support.v7.app.ActionBarActivity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

import de.dreier.mytargets.managers.DatabaseManager;
import de.dreier.mytargets.models.Passe;
import de.dreier.mytargets.models.Round;

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
        DatabaseManager db = DatabaseManager.getInstance(context);
        ArrayList<Passe> oldOnes = db.getPasses(round);
        mZoneCount = Target.target_rounds[roundInfo.target].length;
        init();

        // Draw target
        target = Target.target_rounds[roundInfo.target];
        for (int i = mZoneCount; i > 0; i--) {
            // Select colors to draw with
            drawColorP.setColor(Target.highlightColor[target[i - 1]]);

            // Draw a ring mit separator line
            if (i != 2 || roundInfo.target != 3 || !roundInfo.compound) {
                float rad = (radius * i) / (float) mZoneCount;
                canvas.drawCircle(radius, radius, rad, drawColorP);
                canvas.drawCircle(radius, radius, rad,
                        Target.target_rounds[roundInfo.target][i - 1] == 3 ?
                                thinWhiteBorder : thinBlackBorder);
            }
        }

        // Draw cross in the middle
        Paint midColor =
                Target.target_rounds[roundInfo.target][0] == 3 ? thinWhiteBorder : thinBlackBorder;
        if (roundInfo.target < 5) {
            float lineLength = radius / (float) (mZoneCount * 6);
            canvas.drawLine(radius - lineLength, radius, radius + lineLength, radius, midColor);
            canvas.drawLine(radius, radius - lineLength, radius, radius + lineLength, midColor);
        } else {
            float lineLength = radius / (float) (mZoneCount * 4);
            canvas.drawLine(radius - lineLength, radius - lineLength, radius + lineLength,
                    radius + lineLength, midColor);
            canvas.drawLine(radius - lineLength, radius + lineLength, radius + lineLength,
                    radius - lineLength, midColor);
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
            for (int i = 0; i < p.shot.length; i++) {
                // For yellow and white background use black font color
                int colorInd = i == mZoneCount || p.shot[i].zone < 0 ? 0 : target[p.shot[i].zone];
                drawColorP.setColor(colorInd == 0 || colorInd == 4 ? Color.BLACK : Color.WHITE);
                float selX = p.shot[i].x;
                float selY = p.shot[i].y;
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
            canvas.drawCircle(radius + (sumX / count) * radius, radius + (sumY / count) * radius,
                    3 * density, drawColorP);
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

    public void generateBitmap(ActionBarActivity context, int size, Round mRoundInfo, long mRound, File f)
            throws FileNotFoundException {
        final FileOutputStream fOut = new FileOutputStream(f);
        generateBitmap(context, size, mRoundInfo, mRound, fOut);
    }
}
