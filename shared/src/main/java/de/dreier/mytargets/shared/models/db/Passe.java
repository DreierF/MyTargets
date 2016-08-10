package de.dreier.mytargets.shared.models.db;

import android.support.annotation.NonNull;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.ForeignKeyReference;
import com.raizlabs.android.dbflow.annotation.OneToMany;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.joda.time.DateTime;
import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.dreier.mytargets.shared.AppDatabase;
import de.dreier.mytargets.shared.models.IIdSettable;
import de.dreier.mytargets.shared.models.Target;
import de.dreier.mytargets.shared.targets.SelectableZone;
import de.dreier.mytargets.shared.utils.DateTimeConverter;
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
    public Shot[] shot;
    @Column(typeConverter = DateTimeConverter.class, name = "save_time")
    public DateTime saveDate = new DateTime();
    @Column(name = "_id")
    @PrimaryKey(autoincrement = true)
    Long id;

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

    public List<Shot> getSortedShotList() {
        final List<Shot> shots = getShots();
        Collections.sort(shots);
        return shots;
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

    public static List<Pair<Target, List<Round>>> groupByTarget(List<Round> rounds) {
        return Stream.of(rounds)
                .groupBy(value -> new Pair<>(value.info.target.getId(), value.info.target.scoringStyle))
                .map(value1 -> new Pair<>(value1.getValue().get(0).info.target, value1.getValue()))
                .collect(Collectors.toList());
    }

    @NonNull
    private static Map<SelectableZone, Integer> getRoundScores(List<Round> rounds) {
        final Target t = rounds.get(0).info.target;
        Map<SelectableZone, Integer> scoreCount = getAllPossibleZones(t);
        for (Round round : rounds) {
            for (Passe p : round.getPasses()) {
                for (Shot s : p.shot) {
                    SelectableZone tuple = new SelectableZone(s.zone, t.getModel().getZone(s.zone),
                            t.zoneToString(s.zone, s.index), t.getPointsByZone(s.zone, s.index));
                    final Integer integer = scoreCount.get(tuple);
                    if (integer != null) {
                        int count = integer + 1;
                        scoreCount.put(tuple, count);
                    }
                }
            }
        }
        return scoreCount;
    }

    @NonNull
    private static Map<SelectableZone, Integer> getAllPossibleZones(Target t) {
        Map<SelectableZone, Integer> scoreCount = new HashMap<>();
        for (int arrow = 0; arrow < 3; arrow++) {
            final List<SelectableZone> zoneList = t.getSelectableZoneList(arrow);
            for (SelectableZone selectableZone : zoneList) {
                scoreCount.put(selectableZone, 0);
            }
            if (!t.getModel().dependsOnArrowIndex()) {
                break;
            }
        }
        return scoreCount;
    }

    public static List<Pair<String, Integer>> getTopScoreDistribution(List<Map.Entry<SelectableZone, Integer>> sortedScore) {
        final List<Pair<String, Integer>> result = Stream.of(sortedScore)
                .map(value -> new Pair<>(value.getKey().text, value.getValue()))
                .collect(Collectors.toList());

        // Collapse first two entries if they yield the same score points,
        // e.g. 10 and X => {X, 10+X, 9, ...}
        if (sortedScore.size() > 1) {
            Map.Entry<SelectableZone, Integer> first = sortedScore.get(0);
            Map.Entry<SelectableZone, Integer> second = sortedScore.get(1);
            if (first.getKey().points == second.getKey().points) {
                final String newTitle = second.getKey().text + "+" + first.getKey().text;
                result.set(1, new Pair<>(newTitle, second.getValue() + first.getValue()));
            }
        }
        return result;
    }

    public static List<Map.Entry<SelectableZone, Integer>> getSortedScoreDistribution(List<Round> rounds) {
        Map<SelectableZone, Integer> scoreCount = getRoundScores(rounds);
        return Stream.of(scoreCount)
                .sorted((lhs, rhs) -> lhs.getKey().compareTo(rhs.getKey()))
                .collect(Collectors.toList());
    }

    public static void deleteAll() {
        SQLite.delete(Passe.class).execute();
    }
}
