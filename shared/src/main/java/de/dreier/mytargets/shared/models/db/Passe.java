package de.dreier.mytargets.shared.models.db;

import android.database.Cursor;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.raizlabs.android.dbflow.annotation.Column;
import org.joda.time.DateTime;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.ForeignKeyReference;
import com.raizlabs.android.dbflow.annotation.OneToMany;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.dreier.mytargets.shared.AppDatabase;
import de.dreier.mytargets.shared.models.IIdSettable;
import de.dreier.mytargets.shared.models.Target;
import de.dreier.mytargets.shared.utils.Pair;

@Parcel
@Table(database = AppDatabase.class, name = "PASSE")
public class Passe extends BaseModel implements IIdSettable {

    //@Column(name = "index") TODO migration, does no yet exist in db
    public int index;
    @Column(name = "image")
    public String image;
    @ForeignKey(tableClass = Round.class, references = {
            @ForeignKeyReference(columnName = "round", columnType = Long.class, foreignKeyColumnName = "_id")})
    public Long roundId;
    @Column(name = "exact")
    public boolean exact;

    public List<Shot> shots = new ArrayList<>();
    @Column(name = "_id")
    @PrimaryKey(autoincrement = true)
    Long id;

    public Shot[] shot;
    
    public DateTime saveDate = new DateTime();

    public Passe() {
    }

    public Passe(int ppp) {
        for (int i = 0; i < ppp; i++) {
            shots.add(new Shot(i));
            shots.get(i).index = i;
        }
    }

    public Passe(Passe p) {
        id = p.id;
        roundId = p.roundId;
        index = p.index;
        exact = p.exact;
        shots = p.shots;
        //shots = p.shots.clone(); TODO clone
    }

    @OneToMany(methods = {OneToMany.Method.ALL}, variableName = "shots")
    public List<Shot> getShots() {
        if (shots == null || shots.isEmpty()) {
            shots = SQLite.select()
                    .from(Shot.class)
                    //.where(Shot_Table.passeId__id.eq(id))
                    .queryList();
        }
        return shots;
    }

    public void sort() {
        Collections.sort(shots);
        for (int i = 0; i < shots.size(); i++) {
            shots.get(i).index = i;
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
        for (Shot s : shots) {
            s.passe = id;
        }
    }

    @Override
    public boolean equals(Object another) {
        return another instanceof Passe &&
                getClass().equals(another.getClass()) &&
                id.equals(((Passe) another).id);
    }

    public int getReachedPoints(Target target) {
        return target.getReachedPoints(this);
    }

    private static Map<Pair<Integer, String>, Integer> getScoreDistribution(long training) {
        Cursor cursor = FlowManager.getWritableDatabase(AppDatabase.class)
                .rawQuery("SELECT a.target, a.scoring_style, s.points, s.arrow_index, COUNT(*) " +
                        "FROM ROUND r " +
                        "LEFT JOIN ROUND_TEMPLATE a ON r.template=a._id " +
                        "LEFT JOIN PASSE p ON r._id = p.round " +
                        "LEFT JOIN SHOOT s ON p._id = s.passe " +
                        "WHERE r.training=" + training + " " +
                        "GROUP BY a.target, a.scoring_style, s.points, s.arrow_index", null);

        if (!cursor.moveToFirst()) {
            cursor.close();
            throw new IllegalStateException("There must be at least one round!");
        }
        Target t = new Target(cursor.getInt(0), cursor.getInt(1));
        Map<Pair<Integer, String>, Integer> scoreCount = new HashMap<>();
        for (int arrow = 0; arrow < 3; arrow++) {
            for (int zone = -1; zone < t.getModel().getZoneCount(); zone++) {
                scoreCount.put(new Pair<>(t.getPointsByZone(zone, arrow),
                        t.zoneToString(zone, arrow)), 0);
            }
            if (!t.getModel().dependsOnArrowIndex()) {
                break;
            }
        }
        do {
            if (cursor.isNull(2)) {
                continue;
            }
            int zone = cursor.getInt(2);
            int arrow = cursor.getInt(3);
            int count = cursor.getInt(4);
            Pair<Integer, String> tuple = new Pair<>(t.getPointsByZone(zone, arrow),
                    t.zoneToString(zone, arrow));
            count += scoreCount.get(tuple);
            scoreCount.put(tuple, count);
        } while (cursor.moveToNext());
        cursor.close();
        return scoreCount;
    }


    public static List<Pair<String, Integer>> getTopScoreDistribution(long training) {
        Map<Pair<Integer, String>, Integer> scoreCount = getScoreDistribution(training);
        List<Map.Entry<Pair<Integer, String>, Integer>> sortedScore = Stream.of(scoreCount)
                .sorted((lhs, rhs) -> {
                    if (lhs.getKey().getFirst().equals(rhs.getKey().getFirst())) {
                        return -lhs.getKey().getSecond().compareTo(rhs.getKey().getSecond());
                    }
                    return rhs.getKey().getFirst() - lhs.getKey().getFirst();
                })
                .collect(Collectors.toList());

        // Collapse first two entries if they yield the same score points,
        // e.g. 10 and X => {X, 10+X, 9, ...}
        if (sortedScore.size() > 1) {
            Map.Entry<Pair<Integer, String>, Integer> first = sortedScore.get(0);
            Map.Entry<Pair<Integer, String>, Integer> second = sortedScore.get(1);
            if (first.getKey().getFirst().equals(second.getKey().getFirst())) {
                final String newTitle = second.getKey().getSecond() + "+" + first.getKey()
                        .getSecond();
                final int newValue = second.getValue() + first.getValue();
                sortedScore.get(1).getKey().setSecond(newTitle);
                sortedScore.get(1).setValue(newValue);
                second.setValue(newValue);
            }
        }
        return Stream.of(sortedScore)
                .map(value -> new Pair<>(value.getKey().getSecond(), value.getValue()))
                .collect(Collectors.toList());
    }

    public static float getAverageScore(long training) {
        Cursor res = FlowManager.getWritableDatabase(AppDatabase.class).rawQuery(
                "SELECT s.points, a.target, a.scoring_style, s.arrow_index " +
                        "FROM ROUND r " +
                        "LEFT JOIN PASSE p ON r._id = p.round " +
                        "LEFT JOIN SHOOT s ON p._id = s.passe " +
                        "LEFT JOIN ROUND_TEMPLATE a ON a._id = r.template " +
                        "WHERE r.training = " + training + " " +
                        "ORDER BY r._id ASC, p._id ASC, s._id ASC", null);
        int count = 0;
        int sum = 0;
        if (res.moveToFirst()) {
            do {
                count++;
                sum += new Target(res.getInt(1), res.getInt(2))
                        .getPointsByZone(res.getInt(0), res.getInt(3));
            } while (res.moveToNext());
        }
        res.close();
        float average = 0;
        if (count > 0) {
            average = ((sum * 100) / count) / 100.0f;
        }
        return average;
    }


}
