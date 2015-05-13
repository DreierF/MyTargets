package de.dreier.mytargets.shared.models;

import java.util.Calendar;
import java.util.Date;

public class Training extends IdProvider {
    static final long serialVersionUID = 58L;
    public String title = "";
    public Date date = new Date();
    public int reachedPoints;
    public int maxPoints;
    public int[] scoreCount = new int[3];

    @Override
    public long getParentId() {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(0);
        c.set(date.getYear()+1900, date.getMonth(),1);
        return c.getTimeInMillis();
    }
}
