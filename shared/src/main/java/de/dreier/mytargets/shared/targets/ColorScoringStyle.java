package de.dreier.mytargets.shared.targets;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;

import de.dreier.mytargets.shared.models.db.Passe;

public class ColorScoringStyle extends ScoringStyle {

    private final String title;
    private final int maxEndPoints;

    public ColorScoringStyle(String title, int maxEndPoints, int... points) {
        super(false, points);
        this.title = title;
        this.maxEndPoints = maxEndPoints;
    }

    @Override
    public int getEndMaxPoints(int arrowsPerPasse) {
        return maxEndPoints;
    }

    @Override
    public int getReachedPoints(Passe passe) {
        return Stream.of(passe.shotList())
                .map(s -> getPoints(s.zone, s.index))
                .distinct()
                .collect(Collectors.reducing(0, (a, b) -> a + b));
    }

    @Override
    public String toString() {
        return title;
    }
}
