package de.dreier.mytargets.shared.models.db;

import android.support.annotation.NonNull;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.ForeignKeyReference;
import com.raizlabs.android.dbflow.annotation.OneToMany;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

import de.dreier.mytargets.shared.AppDatabase;
import de.dreier.mytargets.shared.models.Dimension;
import de.dreier.mytargets.shared.models.IIdSettable;
import de.dreier.mytargets.shared.models.Target;
import de.dreier.mytargets.shared.utils.DimensionConverter;
import de.dreier.mytargets.shared.utils.LongUtils;

@Parcel
@Table(database = AppDatabase.class, name = "ROUND")
public class Round extends BaseModel implements IIdSettable, Comparable<Round> {

    @Column(name = "_id")
    @PrimaryKey(autoincrement = true)
    Long id;

    @ForeignKey(tableClass = Training.class, references = {
            @ForeignKeyReference(columnName = "training", columnType = Long.class, foreignKeyColumnName = "_id")})
    public Long trainingId;

    @ForeignKey(tableClass = RoundTemplate.class, references = {
            @ForeignKeyReference(columnName = "template", columnType = Long.class, foreignKeyColumnName = "_id", referencedGetterName = "getId", referencedSetterName = "setId")})
    public RoundTemplate info;

    @Column(name = "comment")
    public String comment;

    @Column(name = "target")
    int targetId;

    @Column(name = "scoring_style")
    int targetScoringStyle;

    @Column(typeConverter = DimensionConverter.class, name = "size")
    Dimension targetSize;

    public int reachedPoints;
    public List<Passe> passes = new ArrayList<>();

    public static Round get(Long id) {
        return SQLite.select()
                .from(Round.class)
                .where(Round_Table._id.eq(id))
                .querySingle();
    }

    public Target getTarget() {
        return new Target(targetId, targetScoringStyle, targetSize);
    }

    public void setTarget(Target targetTemplate) {
        targetId = targetTemplate.id;
        targetScoringStyle = targetTemplate.scoringStyle;
        targetSize = targetTemplate.size;
    }

    public static void deleteAll() {
        SQLite.delete(Round.class).execute();
    }

    public static List<Round> getAll(long[] roundIds) {
        return SQLite.select()
                .from(Round.class)
                .where(Round_Table._id.in(LongUtils.toList(roundIds)))
                .queryList();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object another) {
        return another instanceof Round &&
                getClass().equals(another.getClass()) &&
                id.equals(((Round) another).id);
    }

    @OneToMany(methods = {OneToMany.Method.ALL}, variableName = "passes")
    public List<Passe> getPasses() {
        if (passes == null || passes.isEmpty()) {
            passes = SQLite.select()
                    .from(Passe.class)
                    .where(Passe_Table.round.eq(id))
                    .queryList();
        }
        return passes;
    }

    public int getMaxPoints() {
        return getTarget().getEndMaxPoints(info.arrowsPerEnd) * info.endCount;
    }

    public String getReachedPointsFormatted() {
        return reachedPoints + "/" + getMaxPoints();
    }

    @Override
    public int compareTo(@NonNull Round round) {
        return info.index - round.info.index;
    }
}
