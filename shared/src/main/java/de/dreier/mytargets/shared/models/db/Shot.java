package de.dreier.mytargets.shared.models.db;

import android.support.annotation.NonNull;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import de.dreier.mytargets.shared.AppDatabase;
import de.dreier.mytargets.shared.models.IIdSettable;

@Table(database = AppDatabase.class)
public class Shot extends BaseModel implements IIdSettable, Comparable<Shot> {
    public static final int NOTHING_SELECTED = -2;
    public static final int MISS = -1;

    @PrimaryKey(autoincrement = true)
    Long id;
    @ForeignKey(tableClass = Passe.class)
    public Long passe;
    @Column
    public float x;
    @Column
    public float y;
    @Column
    public int zone = NOTHING_SELECTED;
    @Column
    public String comment = "";

    // Is the actual number of the arrow not its index, arrow id or something else
    @Column
    public String arrow = null;

    // The index of the shot in the containing passe
    @Column
    public int index;

    public Shot() {}

    public Shot(int i) {
        index = i;
    }

    @Override
    public int compareTo(@NonNull Shot another) {
        if (another.zone == zone) {
            return 0;
        }
        return ((zone > another.zone && another.zone != MISS) || zone == MISS) ? 1 : -1;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object another) {
        return another instanceof Shot &&
                getClass().equals(another.getClass()) &&
                id == ((Shot) another).id;
    }
}
