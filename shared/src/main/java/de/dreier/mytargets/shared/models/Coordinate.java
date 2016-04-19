package de.dreier.mytargets.shared.models;

import org.parceler.Parcel;

@Parcel
public class Coordinate {
    public float x;
    public float y;

    public Coordinate() {
    }

    public Coordinate(float x, float y) {
        this.x = x;
        this.y = y;
    }
}
