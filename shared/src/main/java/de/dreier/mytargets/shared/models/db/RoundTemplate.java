package de.dreier.mytargets.shared.models.db;

import com.raizlabs.android.dbflow.annotation.Column;
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

    //@ForeignKey(tableClass = StandardRound.class, references = {
    //        @ForeignKeyReference(columnName = "sid", columnType = Long.class, foreignKeyColumnName = "_id")})
    public Long standardRound;
    @Column(name = "r_index")
    public int index;
    @Column(name = "arrows")
    public int arrowsPerEnd;
    public Target target;
    @Column(name = "passes")
    public int endCount;
    @Column(typeConverter = DimensionConverter.class, name = "distance")
    public Dimension distance;
    @Column(name = "_id")
    @PrimaryKey(autoincrement = true)
    Long id;
    @Column(name = "target")
    int targetTemplateId;
    @Column(name = "scoring_style")
    int targetTemplateScoringStyle;
    @Column(typeConverter = DimensionConverter.class, name = "size")
    Dimension targetTemplateSize;

    public int getMaxPoints() {
        return target.getEndMaxPoints(arrowsPerEnd) * endCount;
    }

    public Long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object another) {
        return another instanceof RoundTemplate &&
                getClass().equals(another.getClass()) &&
                id.equals(((RoundTemplate) another).id);
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
                //.where(RoundTemplate_Table.sid.eq(sid))
                //.and(RoundTemplate_Table.r_index.eq(index))
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
