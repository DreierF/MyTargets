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

package de.dreier.mytargets.test.utils.rules;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.test.InstrumentationRegistry;

import com.raizlabs.android.dbflow.sql.language.SQLite;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalTime;

import java.util.Collections;
import java.util.Random;

import de.dreier.mytargets.R;
import de.dreier.mytargets.app.ApplicationInstance;
import de.dreier.mytargets.shared.models.Dimension;
import de.dreier.mytargets.shared.models.EBowType;
import de.dreier.mytargets.shared.models.EWeather;
import de.dreier.mytargets.shared.models.Thumbnail;
import de.dreier.mytargets.shared.models.db.Arrow;
import de.dreier.mytargets.shared.models.db.Bow;
import de.dreier.mytargets.shared.models.db.End;
import de.dreier.mytargets.shared.models.db.Round;
import de.dreier.mytargets.shared.models.db.Training;

public abstract class DbTestRuleBase implements TestRule {
    private final Context context;

    public DbTestRuleBase() {
        context = InstrumentationRegistry.getTargetContext();
    }

    @NonNull
    @Override
    public Statement apply(@NonNull Statement base, Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                ApplicationInstance.initFlowManager(context);
                SQLite.delete(Arrow.class).execute();
                SQLite.delete(Bow.class).execute();
                SQLite.delete(Training.class).execute();
                addDatabaseContent();
                base.evaluate();
            }
        };
    }

    @NonNull
    protected End buildEnd(@NonNull Round round, @NonNull int... shots) {
        End end = round.addEnd();
        end.roundId = round.getId();
        for (int i = 0; i < shots.length; i++) {
            end.getShots().get(i).index = i;
            end.getShots().get(i).scoringRing = shots[i];
        }
        return end;
    }

    @NonNull
    protected End randomEnd(@NonNull Round round, int arrowsPerEnd, @NonNull Random gen, int index) {
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
        end.saveTime = LocalTime.of(14, gen.nextInt(59), gen.nextInt(59), 0);
        return end;
    }

    private float gaussianRand(@NonNull Random gen) {
        final float rand1 = gen.nextFloat();
        final float rand2 = gen.nextFloat();
        return (float) (Math.sqrt(-2 * Math.log(rand1) / Math.log(Math.E)) *
                Math.cos(2 * Math.PI * rand2)) * 0.4f;
    }

    protected abstract void addDatabaseContent();

    @NonNull
    protected Bow addBow(String name) {
        Bow bow = new Bow();
        bow.name = name;
        bow.brand = "PSE";
        bow.size = "64\"";
        bow.braceHeight = "6 3/8\"";
        bow.type = EBowType.COMPOUND_BOW;
        bow.images = Collections.emptyList();
        bow.thumbnail = new Thumbnail(context, R.drawable.recurve_bow);
        bow.save();
        return bow;
    }

    @NonNull
    protected Arrow addArrow(String name) {
        Arrow arrow = new Arrow();
        arrow.name = name;
        arrow.length = "30inch";
        arrow.comment = "some comment";
        arrow.diameter = new Dimension(4, Dimension.Unit.MILLIMETER);
        arrow.nock = "Awesome nock";
        arrow.images = Collections.emptyList();
        arrow.thumbnail = new Thumbnail(context, R.drawable.arrows);
        arrow.save();
        return arrow;
    }

    @NonNull
    protected Training saveDefaultTraining(Long standardRoundId, @NonNull Random generator) {
        Training training = new Training();
        training.title = InstrumentationRegistry.getTargetContext().getString(R.string.training);
        training.date = LocalDate.of(2016, 4 + generator.nextInt(5), generator.nextInt(29));
        training.location = "";
        training.weather = EWeather.SUNNY;
        training.windSpeed = 1;
        training.windDirection = 0;
        training.standardRoundId = standardRoundId;
        training.bowId = null;
        training.arrowId = null;
        training.arrowNumbering = false;
        training.save();
        return training;
    }
}
