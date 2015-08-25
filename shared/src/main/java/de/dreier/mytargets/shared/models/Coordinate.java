package de.dreier.mytargets.shared.models;

import java.io.Serializable;

/**
* Created by Florian on 20.03.2015.
*/
public class Coordinate implements Serializable {
    public float x;
    public float y;

    public Coordinate() {
    }

    public Coordinate(float x, float y) {
        this.x = x;
        this.y = y;
    }
}
