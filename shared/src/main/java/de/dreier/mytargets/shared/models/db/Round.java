package de.dreier.mytargets.shared.models.db;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.OneToMany;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

import de.dreier.mytargets.shared.AppDatabase;
import de.dreier.mytargets.shared.models.IIdSettable;

@Parcel
@Table(database = AppDatabase.class, name = "ROUND")
public class Round extends BaseModel implements IIdSettable {

    @Column(name = "_id")
    @PrimaryKey(autoincrement = true)
    Long id;

    //@ForeignKey(tableClass = Training.class, references = {
    //        @ForeignKeyReference(columnName = "training", columnType = Long.class, foreignKeyColumnName = "_id")})
    public Long trainingId;

    //@ForeignKey(tableClass = RoundTemplate.class, references = {
    //        @ForeignKeyReference(columnName = "template", columnType = Long.class, foreignKeyColumnName = "_id")})
    public RoundTemplate info;

    @Column(name = "comment")
    public String comment;

    public int reachedPoints;

    public Long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object another) {
        return another instanceof Round &&
                getClass().equals(another.getClass()) &&
                id.equals(((Round) another).id);
    }


    public static Round get(Long id) {
        return SQLite.select()
                .from(Round.class)
                //.where(Round_Table.id__id.eq(id))
                .querySingle();
    }

    public List<Passe> passes = new ArrayList<>();

    @OneToMany(methods = {OneToMany.Method.ALL}, variableName = "passes")
    public List<Passe> getPasses() {
        if (passes == null || passes.isEmpty()) {
            passes = SQLite.select()
                    .from(Passe.class)
                    //.where(Passe_Table.roundId__id.eq(id))
                    .queryList();
        }
        return passes;
    }

    public String getReachedPointsFormatted() {
        final int maxPoints = info.getMaxPoints();
        String percent = maxPoints == 0 ? "" : " (" + (reachedPoints * 100 / maxPoints) + "%)";
        return reachedPoints + "/" + maxPoints + percent;
    }

    public static void deleteAll() {
        SQLite.delete(Round.class).execute();
    }
}
