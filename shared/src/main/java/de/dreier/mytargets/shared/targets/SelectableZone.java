package de.dreier.mytargets.shared.targets;

import android.support.annotation.NonNull;

public class SelectableZone implements Comparable<SelectableZone> {
    public final int index;
    public final Zone zone;
    public final int points;
    public final String text;

    public SelectableZone(int index, Zone zone, String text, int points) {
        this.index = index;
        this.zone = zone;
        this.points = points;
        this.text = text;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SelectableZone that = (SelectableZone) o;
        return index == that.index && points == that.points && text.equals(that.text);
    }

    @Override
    public int hashCode() {
        int result = index;
        result = 31 * result + points;
        result = 31 * result + text.hashCode();
        return result;
    }

    @Override
    public int compareTo(@NonNull SelectableZone another) {
        if (another.index == index) {
            return 0;
        } else if (another.index >= 0 && index >= 0) {
            return index - another.index;
        } else {
            return another.index - index;
        }
    }
}
