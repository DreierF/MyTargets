package de.dreier.mytargets.shared.models;

import org.joda.time.LocalDate;

import java.text.DateFormat;

public class Training implements IIdSettable {
    long id;
    public String title = "";
    public LocalDate date = new LocalDate();
    public Environment environment;
    public long standardRoundId;
    public long bow;
    public long arrow;
    public boolean arrowNumbering;
    public int timePerPasse;

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
}
