package de.dreier.mytargets.shared.models.db;

import android.support.annotation.NonNull;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.ForeignKeyReference;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.parceler.Parcel;

import de.dreier.mytargets.shared.AppDatabase;
import de.dreier.mytargets.shared.models.IIdSettable;

@Parcel
@Table(database = AppDatabase.class, name = "SHOOT")
public class Shot extends BaseModel implements IIdSettable, Comparable<Shot> {
    public static final int NOTHING_SELECTED = -2;
    public static final int MISS = -1;

    @Column(name = "_id")
    @PrimaryKey(autoincrement = true)
    Long id;

    @ForeignKey(tableClass = End.class, references = {
            @ForeignKeyReference(columnName = "passe", columnType = Long.class, foreignKeyColumnName = "_id")})
    public Long endId;
    @Column(name = "x")
    public float x;
    @Column(name = "y")
    public float y;
    @Column(name = "points")
    public int zone = NOTHING_SELECTED;
    @Column(name = "comment")
    public String comment = "";
    // Is the actual number of the arrow not its index, arrow id or something else
    @Column(name = "arrow")
    public String arrowNumber = null;
    // The index of the shot in the containing end
    @Column(name = "arrow_index")
    public int index;

    public Shot() {
    }

    public Shot(int i) {
        index = i;
    }

    @Override
    public int compareTo(@NonNull Shot another) {
        if (another.zone == zone) {
            return 0;
        } else if (another.zone >= 0 && zone >= 0) {
            return zone - another.zone;
        } else {
            return another.zone - zone;
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object another) {
        return another instanceof Shot &&
                getClass().equals(another.getClass()) &&
                id.equals(((Shot) another).id);
    }
}
