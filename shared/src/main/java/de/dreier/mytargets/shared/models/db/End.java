package de.dreier.mytargets.shared.models.db;

import android.support.annotation.NonNull;
import android.support.v4.util.Pair;

import com.annimon.stream.Collectors;
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

import org.joda.time.DateTime;
import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.dreier.mytargets.shared.AppDatabase;
import de.dreier.mytargets.shared.models.IIdSettable;
import de.dreier.mytargets.shared.models.SelectableZone;
import de.dreier.mytargets.shared.models.Target;
import de.dreier.mytargets.shared.utils.DateTimeConverter;

@Parcel
@Table(database = AppDatabase.class)
public class End extends BaseModel implements IIdSettable, Comparable<End> {

    @Column(name = "_id")
    @PrimaryKey(autoincrement = true)
    Long id;

    @Column
    public int index;

    //TODO prepare for multiple images
    @Column(name = "image")
    public String image;

    @ForeignKey(tableClass = Round.class, references = {
            @ForeignKeyReference(columnName = "round", columnType = Long.class, foreignKeyColumnName = "_id")},
            onDelete = ForeignKeyAction.CASCADE)
    public Long roundId;

    @Column
    public boolean exact;

    @Column(typeConverter = DateTimeConverter.class)
    public DateTime saveTime = new DateTime();

    List<Shot> shots = new ArrayList<>();

    public End() {
    }

    public End(int shotCount, int index) {
        this.index = index;
        for (int i = 0; i < shotCount; i++) {
            shots.add(new Shot(i));
        }
    }

    @NonNull
    private static Map<SelectableZone, Integer> getRoundScores(List<Round> rounds) {
        final Target t = rounds.get(0).getTarget();
        Map<SelectableZone, Integer> scoreCount = getAllPossibleZones(t);
        for (Round round : rounds) {
            for (End end : round.getEnds()) {
                for (Shot s : end.shots) {
                    SelectableZone tuple = new SelectableZone(s.scoringRing,
                            t.getModel().getZone(s.scoringRing),
                            t.zoneToString(s.scoringRing, s.index),
                            t.getPointsByZone(s.scoringRing, s.index));
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

    /**
     * Compound 9ers are already collapsed to one SelectableZone.
     */
    public static List<Map.Entry<SelectableZone, Integer>> getSortedScoreDistribution(List<Round> rounds) {
        Map<SelectableZone, Integer> scoreCount = getRoundScores(rounds);
        return Stream.of(scoreCount)
                .sorted((lhs, rhs) -> lhs.getKey().compareTo(rhs.getKey()))
                .collect(Collectors.toList());
    }

    public static void deleteAll() {
        SQLite.delete(End.class).execute();
    }

    @OneToMany(methods = {OneToMany.Method.ALL}, variableName = "shots")
    public List<Shot> getShots() {
        if (shots == null || shots.isEmpty()) {
            shots = SQLite.select()
                    .from(Shot.class)
                    .where(Shot_Table.end.eq(id))
                    .queryList();
        }
        return shots;
    }

    public void setShots(List<Shot> shots) {
        this.shots = shots;
    }

    public List<Shot> getSortedShotList() {
        final List<Shot> shots = getShots();
        Collections.sort(shots);
        return shots;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public void save() {
        super.save();
        // TODO Replace this super ugly workaround by stubbed Relationship in version 4 of dbFlow
        for (Shot s : getShots()) {
            s.endId = id;
        }
        super.save();
    }

    @Override
    public boolean equals(Object another) {
        return another instanceof End &&
                getClass().equals(another.getClass()) &&
                id.equals(((End) another).id);
    }

    public int getReachedPoints(Target target) {
        return target.getReachedPoints(this);
    }

    @Override
    public int compareTo(@NonNull End end) {
        return index - end.index;
    }

}
