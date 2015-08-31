/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */
package de.dreier.mytargets.shared.models;

import java.io.Serializable;

public class SightSetting extends IdProvider implements Serializable {
    public long bowId;
    public Distance distance = new Distance(18, Dimension.METER);
    public String value = "";
}
