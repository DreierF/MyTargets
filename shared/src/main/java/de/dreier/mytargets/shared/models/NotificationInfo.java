/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */
package de.dreier.mytargets.shared.models;

import org.parceler.ParcelConstructor;

import java.io.Serializable;

public class NotificationInfo implements Serializable {
    public final String title;
    public final String text;
    public final Round round;

    @ParcelConstructor
    public NotificationInfo(Round round, String title, String text) {
        this.round = round;
        this.title = title;
        this.text = text;
    }
}
