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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.dreier.mytargets.managers.dao.PasseDataSource;
import de.dreier.mytargets.managers.dao.RoundDataSource;
import de.dreier.mytargets.shared.models.Passe;
import de.dreier.mytargets.shared.models.Round;

public class TargetImage {

    public void generateTrainingBitmap(Context context, int size, long trainingId, OutputStream fOut) {
        List<Round> rounds = new RoundDataSource(context).getAll(trainingId);

        // Create bitmap to draw on
        Bitmap b = Bitmap.createBitmap(size, size * rounds.size(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(b);
        canvas.drawColor(0, PorterDuff.Mode.CLEAR);

        for (int i = 0; i < rounds.size(); i++) {
            ArrayList<Passe> oldOnes = new PasseDataSource(context).getAll(rounds.get(i).getId());
            rounds.get(i).info.target.setBounds(0, i * size, size, (i + 1) * size);
            rounds.get(i).info.target.draw(canvas);
            rounds.get(i).info.target.drawArrows(canvas, oldOnes);
        }

        try {
            b.compress(Bitmap.CompressFormat.PNG, 100, fOut);
            fOut.flush();
            fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void generateTrainingBitmap(Context context, int size, long trainingId, File f)
            throws FileNotFoundException {
        final FileOutputStream fOut = new FileOutputStream(f);
        generateTrainingBitmap(context, size, trainingId, fOut);
    }
}
