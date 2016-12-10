package de.dreier.mytargets.shared.models.db;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.ForeignKeyAction;
import com.raizlabs.android.dbflow.annotation.ForeignKeyReference;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.parceler.Parcel;

import de.dreier.mytargets.shared.AppDatabase;
import de.dreier.mytargets.shared.models.Dimension;
import de.dreier.mytargets.shared.models.IIdSettable;
import de.dreier.mytargets.shared.models.Target;
import de.dreier.mytargets.shared.utils.DimensionConverter;

@Parcel
@Table(database = AppDatabase.class)
public class RoundTemplate extends BaseModel implements IIdSettable {

    @Column(name = "_id")
    @PrimaryKey(autoincrement = true)
    Long id;

    @ForeignKey(tableClass = StandardRound.class, references = {
            @ForeignKeyReference(columnName = "standardRound", columnType = Long.class, foreignKeyColumnName = "_id")},
            onDelete = ForeignKeyAction.CASCADE)
    public Long standardRound;

    @Column
    public int index;

    @Column
    public int shotsPerEnd;

    @Column
    public int endCount;

    @Column(typeConverter = DimensionConverter.class)
    public Dimension distance;

    @Column
    int targetId;

    @Column
    int targetScoringStyle;

    @Column(typeConverter = DimensionConverter.class)
    Dimension targetDiameter;

    public static RoundTemplate get(long sid, int index) {
        return SQLite.select()
                .from(RoundTemplate.class)
                .where(RoundTemplate_Table.standardRound.eq(sid))
                .and(RoundTemplate_Table.index.eq(index))
                .querySingle();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object another) {
        return another instanceof RoundTemplate &&
                getClass().equals(another.getClass()) &&
                id.equals(((RoundTemplate) another).id);
    }

    public Target getTargetTemplate() {
        return new Target(targetId, targetScoringStyle, targetDiameter);
    }

    public void setTargetTemplate(Target targetTemplate) {
        targetId = targetTemplate.id;
        targetScoringStyle = targetTemplate.scoringStyle;
        targetDiameter = targetTemplate.size;
    }
}
