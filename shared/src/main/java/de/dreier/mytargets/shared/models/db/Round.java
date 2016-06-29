package de.dreier.mytargets.shared.models.db;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import de.dreier.mytargets.shared.AppDatabase;
import de.dreier.mytargets.shared.models.IIdSettable;

@Table(database = AppDatabase.class)
public class Round extends BaseModel implements IIdSettable {

    @PrimaryKey(autoincrement = true)
    Long id;

    @ForeignKey(tableClass = Training.class)
    public Long trainingId;

    @ForeignKey(tableClass = RoundTemplate.class)
    public RoundTemplate info;

    @Column
    public String comment;

    @Column
    public int reachedPoints;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object another) {
        return another instanceof Round &&
                getClass().equals(another.getClass()) &&
                id == ((Round) another).id;
    }

    public String getReachedPointsFormatted() {
        final int maxPoints = info.getMaxPoints();
        String percent = maxPoints == 0 ? "" : " (" + (reachedPoints * 100 / maxPoints) + "%)";
        return reachedPoints + "/" + maxPoints + percent;
    }
}
