/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */

package de.dreier.mytargets.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.text.TextPaint;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import de.dreier.mytargets.ApplicationInstance;
import de.dreier.mytargets.R;
import de.dreier.mytargets.managers.dao.PasseDataSource;
import de.dreier.mytargets.managers.dao.RoundDataSource;
import de.dreier.mytargets.shared.models.Passe;
import de.dreier.mytargets.shared.models.Round;
import de.dreier.mytargets.shared.targets.TargetDrawable;

public class TargetImage {

    public void generateTrainingBitmap(int size, long trainingId, OutputStream fOut) {
        List<Round> rounds = new RoundDataSource().getAll(trainingId);
        if (rounds.size() == 0) {
            return;
        }
        List<Rect> bounds = getBoundsForTargets(rounds, size);

        // Create bitmap to draw on
        int height = bounds.get(bounds.size() - 1).bottom;
        Bitmap b = Bitmap.createBitmap(size, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(b);
        canvas.drawColor(0, PorterDuff.Mode.CLEAR);

        TextPaint textPaint = new TextPaint();
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(size / 20);

        for (int i = 0; i < rounds.size(); i++) {
            ArrayList<Passe> oldOnes = new PasseDataSource().getAllByRound(rounds.get(i).getId());
            TargetDrawable target = rounds.get(i).info.target.getDrawable();
            target.setBounds(bounds.get(i));
            target.draw(canvas);
            target.drawArrows(canvas, oldOnes, false);
            String roundTitle = ApplicationInstance.getContext().getResources().getQuantityString(R.plurals.rounds, i + 1, i + 1);
            Rect textBounds = new Rect();
            textPaint.getTextBounds(roundTitle, 0, roundTitle.length(), textBounds);
            int textX = bounds.get(i).centerX() - textBounds.width() / 2;
            int textY = bounds.get(i).top - size / 30;
            canvas.drawText(roundTitle, textX, textY, textPaint);
        }

        try {
            b.compress(Bitmap.CompressFormat.PNG, 100, fOut);
            fOut.flush();
            fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<Rect> getBoundsForTargets(List<Round> rounds, int size) {
        int headerHeight = size / 10;
        List<Rect> list = new ArrayList<>();
        int j = 0;
        boolean lastNarrow = false;
        boolean narrow;
        for (int i = 0; i < rounds.size(); i++) {
            TargetDrawable target = rounds.get(i).info.target.getDrawable();
            int width = target.getWidth();
            int height = target.getHeight();
            narrow = width / height < 0.5;
            if (lastNarrow && narrow) {
                narrow = false;
                j--;
                int top = headerHeight + j * (size + headerHeight);
                list.set(i - 1, new Rect(-size / 4, top, size * 3 / 4, top + size));
                list.add(new Rect(size / 4, top, size * 5 / 4, top + size));
            } else {
                int top = headerHeight + j * (size + headerHeight);
                list.add(new Rect(0, top, size, top + size));
            }
            j++;
            lastNarrow = narrow;
        }
        return list;
    }

    public void generateTrainingBitmap(int size, long trainingId, File f)
            throws FileNotFoundException {
        final FileOutputStream fOut = new FileOutputStream(f);
        generateTrainingBitmap(size, trainingId, fOut);
    }
}
