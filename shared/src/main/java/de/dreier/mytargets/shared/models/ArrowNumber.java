/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */
package de.dreier.mytargets.shared.models;

public class ArrowNumber extends IdProvider {
    public int number;

    @Override
    public String toString() {
        return String.valueOf(number);
    }
}
