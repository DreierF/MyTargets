package de.dreier.mytargets.shared.targets;

public class CenterMark {
    public final int color;
    public final float size;
    public final int stroke;
    public final boolean tilted;

    public CenterMark(int color, float size, int stroke, boolean tilted) {
        this.color = color;
        this.size = size;
        this.stroke = stroke;
        this.tilted = tilted;
    }
}
