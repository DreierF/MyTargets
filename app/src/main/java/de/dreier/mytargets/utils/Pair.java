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
package de.dreier.mytargets.utils;

public class Pair<S, T> {

    /**
     * The first element.
     */
    private S first;

    /**
     * The second element.
     */
    private T second;

    /**
     * Constructor.
     */
    public Pair(S first, T second) {
        this.first = first;
        this.second = second;
    }

    /**
     * Returns the first element of the pair.
     */
    public S getFirst() {
        return first;
    }

    /**
     * Returns the second element of the pair.
     */
    public T getSecond() {
        return second;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Pair<?, ?>)) {
            return false;
        }
        Pair<?, ?> p = (Pair<?, ?>) obj;
        return areEqual(first, p.first) && areEqual(second, p.second);
    }

    /**
     * Returns true if either both are <code>null</code> or they are equal.
     */
    private boolean areEqual(Object o1, Object o2) {
        if (o1 == null) {
            return o2 == null;
        }
        return o1.equals(o2);
    }

    /**
     * {@inheritDoc}
     * <p>
     * The hash code is based on the hash code of the first and second members.
     */
    @Override
    public int hashCode() {
        int firstCode = 1;
        if (first != null) {
            firstCode = first.hashCode();
        }

        int secondCode = 1;
        if (second != null) {
            secondCode = second.hashCode();
        }

        return firstCode + 1013 * secondCode;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "(" + first + "," + second + ")";
    }

    /**
     * Set the first value.
     */
    public void setFirst(S first) {
        this.first = first;
    }

    /**
     * Set the second value.
     */
    public void setSecond(T second) {
        this.second = second;
    }
}