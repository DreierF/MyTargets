/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */
package de.dreier.mytargets.shared.models;

import org.parceler.Parcel;

@Parcel
public class NotificationInfo {
    public String title;
    public String text;
    public Round round;

    public NotificationInfo() {}

    public NotificationInfo(Round round, String title, String text) {
        this.round = round;
        this.title = title;
        this.text = text;
    }
}
