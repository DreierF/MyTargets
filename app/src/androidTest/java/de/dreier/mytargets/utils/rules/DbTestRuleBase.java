/*
 * Copyright (C) 2016 Florian Dreier
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

package de.dreier.mytargets.utils.rules;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.test.InstrumentationRegistry;

import com.raizlabs.android.dbflow.config.FlowManager;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import java.util.Random;

import de.dreier.mytargets.ApplicationInstance;
import de.dreier.mytargets.R;
import de.dreier.mytargets.shared.AppDatabase;
import de.dreier.mytargets.shared.models.EBowType;
import de.dreier.mytargets.shared.models.EWeather;
import de.dreier.mytargets.shared.models.Thumbnail;
import de.dreier.mytargets.shared.models.db.Bow;
import de.dreier.mytargets.shared.models.db.End;
import de.dreier.mytargets.shared.models.db.Round;
import de.dreier.mytargets.shared.models.db.Training;

public abstract class DbTestRuleBase implements TestRule {
    private final Context context;

    public DbTestRuleBase() {
        context = InstrumentationRegistry.getTargetContext();
    }

    @Override
    public Statement apply(Statement base, Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                FlowManager.getDatabase(AppDatabase.class).reset(context);
                ApplicationInstance.initFlowManager(context);
                addDatabaseContent();
                base.evaluate();
            }
        };
    }

    protected End buildEnd(Round round, int... shots) {
        End end = round.addEnd();
        end.roundId = round.getId();
        for (int i = 0; i < shots.length; i++) {
            end.getShots().get(i).index = i;
            end.getShots().get(i).scoringRing = shots[i];
        }
        return end;
    }

    protected End randomEnd(Training training, Round round, int arrowsPerEnd, Random gen, int index) {
        End end = new End(arrowsPerEnd, index);
        end.roundId = round.getId();
        end.exact = true;
        for (int i = 0; i < arrowsPerEnd; i++) {
            end.getShots().get(i).index = i;
            end.getShots().get(i).x = gaussianRand(gen);
            end.getShots().get(i).y = gaussianRand(gen);
            end.getShots().get(i).scoringRing = round.getTarget().getModel()
                    .getZoneFromPoint(end.getShots().get(i).x,
                            end.getShots().get(i).y, 0.05f);
        }
        end.saveTime = new DateTime().withDate(training.date)
                .withTime(14, gen.nextInt(59), gen.nextInt(59), 0);
        return end;
    }

    private float gaussianRand(Random gen) {
        final float rand1 = gen.nextFloat();
        final float rand2 = gen.nextFloat();
        return (float) (Math.sqrt(-2 * Math.log(rand1) / Math.log(Math.E)) *
                Math.cos(2 * Math.PI * rand2)) * 0.4f;
    }

    protected abstract void addDatabaseContent();

    @NonNull
    protected Bow addBow() {
        Bow bow = new Bow();
        bow.name = "PSE Fever";
        bow.brand = "PSE";
        bow.size = "64\"";
        bow.braceHeight = "6 3/8\"";
        bow.type = EBowType.COMPOUND_BOW;
        bow.imageFile = null;
        Bitmap bitmap = BitmapFactory
                .decodeResource(context.getResources(), R.drawable.recurve_bow);
        bow.thumbnail = new Thumbnail(bitmap);
        bow.save();
        return bow;
    }

    @NonNull
    protected Training saveDefaultTraining(Long standardRoundId, Random generator) {
        Training training = new Training();
        training.title = InstrumentationRegistry.getTargetContext().getString(R.string.training);
        training.date = new LocalDate(2016, 4 + generator.nextInt(5), generator.nextInt(29));
        training.location = "";
        training.weather = EWeather.SUNNY;
        training.windSpeed = 1;
        training.windDirection = 0;
        training.standardRoundId = standardRoundId;
        training.bowId = null;
        training.arrowId = null;
        training.arrowNumbering = false;
        training.timePerEnd = 0;
        training.save();
        return training;
    }
}
