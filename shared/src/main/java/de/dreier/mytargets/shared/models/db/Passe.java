package de.dreier.mytargets.shared.models.db;

import com.raizlabs.android.dbflow.annotation.Column;
import org.joda.time.DateTime;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.ForeignKeyReference;
import com.raizlabs.android.dbflow.annotation.OneToMany;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.dreier.mytargets.shared.AppDatabase;
import de.dreier.mytargets.shared.models.IIdSettable;
import de.dreier.mytargets.shared.models.Target;

@Table(database = AppDatabase.class, name = "PASSE")
public class Passe extends BaseModel implements IIdSettable {

    //@Column(name = "index") TODO migration, does no yet exist in db
    public int index;
    @Column(name = "image")
    public String image;
    @ForeignKey(tableClass = Round.class, references = {
            @ForeignKeyReference(columnName = "round", columnType = Long.class, foreignKeyColumnName = "_id")})
    public Long roundId;
    @Column(name = "exact")
    public boolean exact;

    public List<Shot> shots = new ArrayList<>();
    @Column(name = "_id")
    @PrimaryKey(autoincrement = true)
    Long id;

    public Shot[] shot;
    
    public DateTime saveDate = new DateTime();

    public Passe() {
    }

    public Passe(int ppp) {
        for (int i = 0; i < ppp; i++) {
            shots.add(new Shot(i));
            shots.get(i).index = i;
        }
    }

    public Passe(Passe p) {
        id = p.id;
        roundId = p.roundId;
        index = p.index;
        exact = p.exact;
        shots = p.shots;
        //shots = p.shots.clone(); TODO clone
    }

    @OneToMany(methods = {OneToMany.Method.ALL}, variableName = "shots")
    public List<Shot> getShots() {
        if (shots == null || shots.isEmpty()) {
            shots = SQLite.select()
                    .from(Shot.class)
                    //.where(Shot_Table.passeId__id.eq(id))
                    .queryList();
        }
        return shots;
    }

    public void sort() {
        Collections.sort(shots);
        for (int i = 0; i < shots.size(); i++) {
            shots.get(i).index = i;
        }
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
        for (Shot s : shots) {
            s.passe = id;
        }
    }

    @Override
    public boolean equals(Object another) {
        return another instanceof Passe &&
                getClass().equals(another.getClass()) &&
                id.equals(((Passe) another).id);
    }

    public int getReachedPoints(Target target) {
        return target.getReachedPoints(this);
    }
}
