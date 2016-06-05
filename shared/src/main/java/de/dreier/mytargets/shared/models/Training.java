package de.dreier.mytargets.shared.models;

import java.util.Date;

public class Training implements IIdSettable {
    protected long id;
    public String title = "";
    public Date date = new Date();
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
}
