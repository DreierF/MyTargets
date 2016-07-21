package de.dreier.mytargets.shared.models;

import org.joda.time.LocalDate;

import java.text.DateFormat;
import java.util.List;
import java.util.Locale;

public class Training implements IIdSettable {
    public String title = "";
    public LocalDate date = new LocalDate();
    public Environment environment;
    public long standardRoundId;
    public long bow;
    public long arrow;
    public boolean arrowNumbering;
    public int timePerPasse;
    long id;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object another) {
        return another instanceof Training &&
                getClass().equals(another.getClass()) &&
                id == ((Training) another).id;
    }

    public String getFormattedDate() {
        return DateFormat.getDateInstance().format(date.toDate());
    }

    public String getReachedPoints(List<Round> rounds) {
        int maxPoints = 0;
        int reachedPoints = 0;
        for (Round r : rounds) {
            maxPoints += r.info.getMaxPoints();
            reachedPoints += r.reachedPoints;
        }
        return String.format(Locale.ENGLISH, "%d/%d", reachedPoints, maxPoints);
    }
}
