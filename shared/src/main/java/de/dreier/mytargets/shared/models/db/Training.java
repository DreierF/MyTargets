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

import org.joda.time.LocalDate;
import org.parceler.Parcel;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import de.dreier.mytargets.shared.AppDatabase;
import de.dreier.mytargets.shared.models.EWeather;
import de.dreier.mytargets.shared.models.Environment;
import de.dreier.mytargets.shared.models.IIdSettable;
import de.dreier.mytargets.shared.utils.LocalDateConverter;

@Parcel
@Table(database = AppDatabase.class, name = "TRAINING")
public class Training extends BaseModel implements IIdSettable, Comparable<Training> {

    @Column(name = "_id")
    @PrimaryKey(autoincrement = true)
    Long id;

    @Column(name = "title")
    public String title = "";

    @Column(typeConverter = LocalDateConverter.class, name = "datum")
    public LocalDate date = new LocalDate();

    @ForeignKey(tableClass = StandardRound.class, references = {
            @ForeignKeyReference(columnName = "standard_round", columnType = Long.class, foreignKeyColumnName = "_id")})
    public Long standardRoundId;

    @ForeignKey(tableClass = Bow.class, references = {
            @ForeignKeyReference(columnName = "bow", columnType = Long.class, foreignKeyColumnName = "_id")})
    public Long bow;

    @ForeignKey(tableClass = Arrow.class, references = {
            @ForeignKeyReference(columnName = "arrow", columnType = Long.class, foreignKeyColumnName = "_id")})
    public Long arrow;

    @Column(name = "arrow_numbering")
    public boolean arrowNumbering;

    @Column(name = "time")
    public int timePerPasse;

    @Column(name = "weather")
    public EWeather weather;

    @Column(name = "wind_direction")
    public int windDirection;

    @Column(name = "wind_speed")
    public int windSpeed;

    @Column(name = "location")
    public String location;

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

    public static void deleteAll() {
        SQLite.delete(Training.class).execute();
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

    @OneToMany(methods = {OneToMany.Method.ALL}, variableName = "rounds")
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

    public String getFormattedDate() {
        return DateFormat.getDateInstance().format(date.toDate());
    }

    public String getReachedPointsFormatted(List<Round> rounds, boolean appendPercent) {
        int maxPoints = 0;
        int reachedPoints = 0;
        for (Round r : rounds) {
            maxPoints += r.getMaxPoints();
            reachedPoints += r.reachedPoints;
        }
        if (appendPercent && maxPoints > 0) {
            return String.format(Locale.ENGLISH, "%d/%d (%d)", reachedPoints, maxPoints,
                    (reachedPoints * 100 / maxPoints));
        } else {
            return String.format(Locale.ENGLISH, "%d/%d", reachedPoints, maxPoints);
        }
    }

    @Override
    public int compareTo(@NonNull Training training) {
        if (date.equals(training.date)) {
            return (int) (id - training.id);
        }
        return date.compareTo(training.date);
    }
}
