package de.dreier.mytargets.shared.models.db;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import de.dreier.mytargets.shared.AppDatabase;
import de.dreier.mytargets.shared.models.Dimension;
import de.dreier.mytargets.shared.models.IIdSettable;
import de.dreier.mytargets.shared.models.Target;
import de.dreier.mytargets.shared.utils.DimensionConverter;

@Table(database = AppDatabase.class)
public class RoundTemplate extends BaseModel implements IIdSettable {

    @PrimaryKey(autoincrement = true)
    Long id;
    @ForeignKey(tableClass = StandardRound.class)
    public Long standardRound;
    @Column
    public int index;
    @Column
    public int arrowsPerPasse;
    public Target target;
    @Column
    public int passes;
    @Column(typeConverter = DimensionConverter.class)
    public Dimension distance;
    @Column
    int targetTemplateId;
    @Column
    int targetTemplateScoringStyle;
    @Column(typeConverter = DimensionConverter.class)
    Dimension targetTemplateSize;

    public int getMaxPoints() {
        return target.getEndMaxPoints(arrowsPerPasse) * passes;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object another) {
        return another instanceof RoundTemplate &&
                getClass().equals(another.getClass()) &&
                id == ((RoundTemplate) another).id;
    }

    public Target getTargetTemplate() {
        return new Target(targetTemplateId, targetTemplateScoringStyle, targetTemplateSize);
    }

    public void setTargetTemplate(Target targetTemplate) {
        targetTemplateId = targetTemplate.id;
        targetTemplateScoringStyle = targetTemplate.scoringStyle;
        targetTemplateSize = targetTemplate.size;
    }
}
