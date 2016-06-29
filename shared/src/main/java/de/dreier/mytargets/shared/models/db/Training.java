package de.dreier.mytargets.shared.models.db;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.joda.time.LocalDate;

import java.text.DateFormat;

import de.dreier.mytargets.shared.AppDatabase;
import de.dreier.mytargets.shared.models.Environment;
import de.dreier.mytargets.shared.models.IIdSettable;
import de.dreier.mytargets.shared.utils.LocalDateConverter;
import java.util.List;
import java.util.Locale;

@Table(database = AppDatabase.class)
public class Training extends BaseModel implements IIdSettable {
    @PrimaryKey(autoincrement = true)
    Long id;
    @Column
    public String title = "";
    @Column(typeConverter = LocalDateConverter.class)
    public LocalDate date = new LocalDate();

    public Environment environment;
    @ForeignKey(tableClass = StandardRound.class)
    public Long standardRoundId;
    @ForeignKey(tableClass = Bow.class)
    public Long bow;
    @ForeignKey(tableClass = Arrow.class)
    public Long arrow;
    @Column
    public boolean arrowNumbering;
    @Column
    public int timePerPasse;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object another) {
        return another instanceof Training &&
                getClass().equals(another.getClass()) &&
                id.equals(((Training) another).id);
    }

    public String getFormattedDate() {
        return DateFormat.getDateInstance().format(date.toDate());
    }

    public String getReachedPoints(List<Round> rounds) {
        int maxPoints = 0;
        int reachedPoints = 0;
        for (Round r : rounds) {
            maxPoints += r.info.getMaxPoints();
            reachedPoints += r.reachedPoints;
        }
        return String.format(Locale.ENGLISH, "%d/%d", reachedPoints, maxPoints);
    }
}
