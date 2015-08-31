/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */
package de.dreier.mytargets.models;

import java.io.Serializable;

import de.dreier.mytargets.shared.models.Dimension;
import de.dreier.mytargets.shared.models.Distance;

public class SightSetting implements Serializable {
    public Distance distance = new Distance(18, Dimension.METER);
    public String value = "";
}
