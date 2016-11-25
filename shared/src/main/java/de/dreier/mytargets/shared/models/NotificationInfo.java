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
