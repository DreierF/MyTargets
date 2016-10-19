package de.dreier.mytargets.shared.models.db;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
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
@Table(database = AppDatabase.class, name = "ROUND_TEMPLATE")
public class RoundTemplate extends BaseModel implements IIdSettable {

    @Column(name = "_id")
    @PrimaryKey(autoincrement = true)
    Long _id;
    @ForeignKey(tableClass = StandardRound.class, references = {
            @ForeignKeyReference(columnName = "sid", columnType = Long.class, foreignKeyColumnName = "_id")})
    public Long standardRound;
    @Column(name = "r_index")
    public int index;
    @Column(name = "arrows")
    public int arrowsPerEnd;
    @Column(name = "passes")
    public int endCount;
    @Column(typeConverter = DimensionConverter.class, name = "distance")
    public Dimension distance;
    @Column(name = "target")
    int targetTemplateId;
    @Column(name = "scoring_style")
    int targetTemplateScoringStyle;
    @Column(typeConverter = DimensionConverter.class, name = "size")
    Dimension targetTemplateSize;

    public Long getId() {
        return _id;
    }

    public void setId(Long id) {
        this._id = id;
    }

    @Override
    public boolean equals(Object another) {
        return another instanceof RoundTemplate &&
                getClass().equals(another.getClass()) &&
                _id.equals(((RoundTemplate) another)._id);
    }

    public Target getTargetTemplate() {
        return new Target(targetTemplateId, targetTemplateScoringStyle, targetTemplateSize);
    }

    public void setTargetTemplate(Target targetTemplate) {
        targetTemplateId = targetTemplate.id;
        targetTemplateScoringStyle = targetTemplate.scoringStyle;
        targetTemplateSize = targetTemplate.size;
    }

    public static RoundTemplate get(long sid, int index) {
        return SQLite.select()
                .from(RoundTemplate.class)
                .where(RoundTemplate_Table.sid.eq(sid))
                .and(RoundTemplate_Table.r_index.eq(index))
                .querySingle();
    }

    void deletePasse() {
        endCount--;
        update();
    }

    public void addPasse() {
        endCount++;
        update();
    }
}
