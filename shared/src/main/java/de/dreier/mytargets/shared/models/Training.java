package de.dreier.mytargets.shared.models;

import java.util.Calendar;
import java.util.Date;

public class Training implements IIdSettable {
    public static final String ID = "_id";
    static final long serialVersionUID = 58L;

    public String title = "";
    public Date date = new Date();
    public Environment environment;
    public long standardRoundId;
    public long bow;
    public long arrow;
    public boolean arrowNumbering;
    public int timePerPasse;
    protected long id;

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
