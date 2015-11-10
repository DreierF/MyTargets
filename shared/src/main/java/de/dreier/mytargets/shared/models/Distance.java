/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */
package de.dreier.mytargets.shared.models;

public class Distance extends Dimension {

    public Distance(int distance, String unit) {
        super(distance, unit);
        this.setId((distance << 1) | (METER.equals(unit) ? 1 : 0));
    }

    public static Distance fromId(long id) {
        return new Distance((int) id >> 1, (id & 1) == 1 ? METER : YARDS);
    }
}
