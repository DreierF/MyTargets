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
import android.graphics.PorterDuff;
import android.support.v7.app.AppCompatActivity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

import de.dreier.mytargets.managers.DatabaseManager;
import de.dreier.mytargets.shared.models.Passe;
import de.dreier.mytargets.shared.models.Round;

public class TargetImage {

    public void generateBitmap(Context context, int size, long roundId, OutputStream fOut) {
        // Create bitmap to draw on
        Bitmap b = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(b);
        canvas.drawColor(0, PorterDuff.Mode.CLEAR);

        DatabaseManager db = DatabaseManager.getInstance(context);
        Round round = db.getRound(roundId);
        ArrayList<Passe> oldOnes = db.getPasses(roundId);
        round.info.target.setBounds(0, 0, size, size);
        round.info.target.draw(canvas);
        round.info.target.drawArrows(canvas, oldOnes);

        try {
            b.compress(Bitmap.CompressFormat.PNG, 100, fOut);
            fOut.flush();
            fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void generateBitmap(AppCompatActivity context, int size, long mRound, File f)
            throws FileNotFoundException {
        final FileOutputStream fOut = new FileOutputStream(f);
        generateBitmap(context, size, mRound, fOut);
    }
}
