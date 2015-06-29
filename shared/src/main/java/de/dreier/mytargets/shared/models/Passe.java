package de.dreier.mytargets.shared.models;

import android.content.ContentValues;
import android.database.Cursor;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Created by Florian on 13.03.2015.
 */
public class Passe extends IdProvider implements Serializable, DatabaseSerializable {
    static final long serialVersionUID = 55L;
    public static final String TABLE = "PASSE";
    public static final String ROUND = "round";
    public static final String CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS " + TABLE + " (" +
                    ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    ROUND + " INTEGER REFERENCES " + Round.TABLE + " ON DELETE CASCADE);";

    public Shot[] shot;
    public long roundId;
    public int index;

    public Passe(int ppp) {
        shot = new Shot[ppp];
        for (int i = 0; i < ppp; i++) {
            shot[i] = new Shot();
        }
    }

    public Passe(Passe p) {
        super.setId(p.getId());
        shot = p.shot.clone();
    }

    @Override
    public void setId(long id) {
        super.setId(id);
        for (Shot s : shot) {
            s.passe = id;
        }
    }

    @Override
    public long getParentId() {
        return roundId;
    }

    public void sort() {
        Arrays.sort(shot);
    }

    @Override
    public String getTableName() {
        return TABLE;
    }

    @Override
    public ContentValues getContentValues() {
        if (shot.length == 0) {
            return null;
        }
        ContentValues values = new ContentValues();
        values.put(ROUND, roundId);
        return values;
    }

    @Override
    public void fromCursor(Cursor cursor, int startColumnIndex) {
        throw new IllegalArgumentException("Not implemented!");
    }
}
