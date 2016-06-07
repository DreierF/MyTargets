package de.dreier.mytargets.shared.targets;

public class SelectableZone {
    public final int zone;
    public final String text;

    public SelectableZone(int zone, String text) {
        this.zone = zone;
        this.text = text;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof SelectableZone && zone == ((SelectableZone) o).zone;
    }
}
