/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */
package de.dreier.mytargets.models;

import java.io.Serializable;

public class NotificationInfo implements Serializable {
    static final long serialVersionUID = 43L;
    public String title;
    public String text;
    public Round round;
    public NotificationInfo(Round round, String title, String text) {
        this.round = round;
        this.title = title;
        this.text = text;
    }
}
