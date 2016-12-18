package de.dreier.mytargets.shared.models.db;

import android.support.annotation.NonNull;

import com.annimon.stream.Stream;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.ForeignKeyAction;
import com.raizlabs.android.dbflow.annotation.ForeignKeyReference;
import com.raizlabs.android.dbflow.annotation.OneToMany;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.joda.time.LocalDate;
import org.parceler.Parcel;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;

import de.dreier.mytargets.shared.AppDatabase;
import de.dreier.mytargets.shared.models.EWeather;
import de.dreier.mytargets.shared.models.Environment;
import de.dreier.mytargets.shared.models.IIdSettable;
import de.dreier.mytargets.shared.models.Score;
import de.dreier.mytargets.shared.utils.typeconverters.EWeatherConverter;
import de.dreier.mytargets.shared.utils.typeconverters.LocalDateConverter;

@Parcel
@Table(database = AppDatabase.class)
public class Training extends BaseModel implements IIdSettable, Comparable<Training> {

    @Column(name = "_id")
    @PrimaryKey(autoincrement = true)
    Long id;

    @Column
    public String title = "";

    @Column(typeConverter = LocalDateConverter.class)
    public LocalDate date = new LocalDate();

    @ForeignKey(tableClass = StandardRound.class, references = {
            @ForeignKeyReference(columnName = "standardRound", columnType = Long.class, foreignKeyColumnName = "_id")},
            onDelete = ForeignKeyAction.SET_NULL)
    public Long standardRoundId;

    @ForeignKey(tableClass = Bow.class, references = {
            @ForeignKeyReference(columnName = "bow", columnType = Long.class, foreignKeyColumnName = "_id")},
            onDelete = ForeignKeyAction.SET_NULL)
    public Long bowId;

    @ForeignKey(tableClass = Arrow.class, references = {
            @ForeignKeyReference(columnName = "arrow", columnType = Long.class, foreignKeyColumnName = "_id")},
            onDelete = ForeignKeyAction.SET_NULL)
    public Long arrowId;

    @Column
    public boolean arrowNumbering;

    @Column
    public int timePerEnd;

    @Column
    public boolean indoor;

    @Column(typeConverter = EWeatherConverter.class)
    public EWeather weather;

    @Column
    public int windDirection;

    @Column
    public int windSpeed;

    @Column
    public String location = "";

    public List<Round> rounds = new ArrayList<>();

    public static Training get(Long id) {
        return SQLite.select()
                .from(Training.class)
                .where(Training_Table._id.eq(id))
                .querySingle();
    }

    public static List<Training> getAll() {
        return SQLite.select().from(Training.class).queryList();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object another) {
        return another instanceof Training &&
                getClass().equals(another.getClass()) &&
                id.equals(((Training) another).id);
    }

    public Environment getEnvironment() {
        return new Environment(weather, windSpeed, windDirection, location);
    }

    public void setEnvironment(Environment env) {
        weather = env.weather;
        windDirection = env.windDirection;
        windSpeed = env.windSpeed;
        location = env.location;
    }

    @OneToMany(methods = {OneToMany.Method.DELETE}, variableName = "rounds")
    public List<Round> getRounds() {
        if (rounds == null || rounds.isEmpty()) {
            rounds = SQLite.select()
                    .from(Round.class)
                    .where(Round_Table.training.eq(id))
                    .queryList();
        }
        return rounds;
    }

    public StandardRound getStandardRound() {
        return SQLite.select()
                .from(StandardRound.class)
                .where(StandardRound_Table._id.eq(standardRoundId))
                .querySingle();
    }

    public Bow getBow() {
        return SQLite.select()
                .from(Bow.class)
                .where(Bow_Table._id.eq(bowId))
                .querySingle();
    }

    public Arrow getArrow() {
        return SQLite.select()
                .from(Arrow.class)
                .where(Arrow_Table._id.eq(arrowId))
                .querySingle();
    }

    public String getFormattedDate() {
        return DateFormat.getDateInstance().format(date.toDate());
    }

    public Score getReachedScore() {
        return Stream.of(getRounds())
                .map(Round::getReachedScore)
                .reduce(new Score(), Score::add);
    }

    @Override
    public int compareTo(@NonNull Training training) {
        if (date.equals(training.date)) {
            return (int) (id - training.id);
        }
        return date.compareTo(training.date);
    }


    @Override
    public void save() {
        super.save();
        // TODO Replace this super ugly workaround by stubbed Relationship in version 4 of dbFlow
        for (Round s : getRounds()) {
            s.trainingId = id;
            s.save();
        }
    }
}
