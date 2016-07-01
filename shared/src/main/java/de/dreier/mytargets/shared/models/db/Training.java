package de.dreier.mytargets.shared.models.db;

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

import de.dreier.mytargets.shared.AppDatabase;
import de.dreier.mytargets.shared.models.EWeather;
import de.dreier.mytargets.shared.models.Environment;
import de.dreier.mytargets.shared.models.IIdSettable;
import de.dreier.mytargets.shared.utils.LocalDateConverter;
import java.util.List;
import java.util.Locale;

@Parcel
@Table(database = AppDatabase.class, name = "TRAINING")
public class Training extends BaseModel implements IIdSettable {

    @Column(name = "_id")
    @PrimaryKey(autoincrement = true)
    Long id;
    @Column(name = "title")
    public String title = "";
    @Column(typeConverter = LocalDateConverter.class, name = "datum")
    public LocalDate date = new LocalDate();
    @Column(name = "weather")
    public EWeather weather;
    @Column(name = "wind_direction")
    public int windDirection;
    @Column(name = "wind_speed")
    public int windSpeed;
    @Column(name = "location")
    public String location;
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

    public Long getId() {
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

    public Environment getEnvironment() {
        return new Environment(weather, windSpeed, windDirection, location);
    }

    public void setEnvironment(Environment env) {
        weather = env.weather;
        windDirection = env.windDirection;
        windSpeed = env.windSpeed;
        location = env.location;
    }

    public static Training get(Long id) {
        return SQLite.select()
                .from(Training.class)
                //.where(Training_Table.id__id.eq(id))
                .querySingle();
    }

    public static List<Training> getAll() {
        return SQLite.select().from(Training.class).queryList();
    }

    public List<Round> rounds = new ArrayList<>();

    @OneToMany(methods = {OneToMany.Method.ALL}, variableName = "rounds")
    public List<Round> getRounds() {
        if (rounds == null || rounds.isEmpty()) {
            rounds = SQLite.select()
                    .from(Round.class)
                    //.where(Round_Table.trainingId__id.eq(id))
                    .queryList();
        }
        return rounds;
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