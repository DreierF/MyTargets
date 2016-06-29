package de.dreier.mytargets.shared.models.db;

import com.raizlabs.android.dbflow.annotation.Column;
import org.joda.time.DateTime;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.util.Arrays;
import java.util.List;

import de.dreier.mytargets.shared.AppDatabase;
import de.dreier.mytargets.shared.models.IIdSettable;
import de.dreier.mytargets.shared.models.Target;

@Table(database = AppDatabase.class)
public class Passe extends BaseModel implements IIdSettable {

    @PrimaryKey(autoincrement = true)
    Long id;
    @Column
    public int index;
    @ForeignKey(tableClass = Round.class)
    public Long roundId;
    @Column
    public boolean exact;

    public Shot[] shot;
    
    public DateTime saveDate = new DateTime();

    public Passe() {
    }

    public Passe(int ppp) {
        shot = new Shot[ppp];
        for (int i = 0; i < ppp; i++) {
            shot[i] = new Shot(i);
            shot[i].index = i;
        }
    }

    public Passe(Passe p) {
        id = p.id;
        roundId = p.roundId;
        index = p.index;
        exact = p.exact;
        shot = p.shot.clone();
    }

    public List<Shot> shotList() {
        return Arrays.asList(shot);
    }

    public void sort() {
        Arrays.sort(shot);
        for (int i = 0; i < shot.length; i++) {
            shot[i].index = i;
        }
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
        for (Shot s : shot) {
            s.passe = id;
        }
    }

    @Override
    public boolean equals(Object another) {
        return another instanceof Passe &&
                getClass().equals(another.getClass()) &&
                id == ((Passe) another).id;
    }

    public int getReachedPoints(Target target) {
        return target.getReachedPoints(this);
    }
}
