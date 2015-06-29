/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */
package de.dreier.mytargets.shared.models;

public class Distance extends Dimension {
    public static final Integer[] valuesMetric = {10, 15, 18, 20, 25, 30, 40, 50, 60, 70,
            90};
    public static final Integer[] valuesImperial = {20, 25, 30, 40, 50, 60, 80, 100};

    public Distance(int distance, String unit) {
        super(distance, unit);
        this.setId((distance << 1) | (unit.equals(METER) ? 1 : 0));
    }

    public static Distance fromId(long id) {
        return new Distance((int) id >> 1, (id & 1) == 1 ? METER : YARDS);
    }
}
