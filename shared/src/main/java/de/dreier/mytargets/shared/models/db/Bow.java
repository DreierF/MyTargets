package de.dreier.mytargets.shared.models.db;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;

import com.annimon.stream.Stream;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.OneToMany;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

import de.dreier.mytargets.shared.AppDatabase;
import de.dreier.mytargets.shared.models.Dimension;
import de.dreier.mytargets.shared.models.EBowType;
import de.dreier.mytargets.shared.models.IIdSettable;
import de.dreier.mytargets.shared.models.IImageProvider;
import de.dreier.mytargets.shared.models.Thumbnail;
import de.dreier.mytargets.shared.utils.typeconverters.EBowTypeConverter;
import de.dreier.mytargets.shared.utils.typeconverters.StringListConverter;
import de.dreier.mytargets.shared.utils.typeconverters.ThumbnailConverter;

@Parcel
@Table(database = AppDatabase.class)
public class Bow extends BaseModel implements IImageProvider, IIdSettable, Comparable<Bow> {

    @Column(name = "_id")
    @PrimaryKey(autoincrement = true)
    public Long id = -1L;

    @Column
    public String name = "";

    @Column(typeConverter = EBowTypeConverter.class)
    public EBowType type = EBowType.RECURVE_BOW;

    @Column
    public String brand = "";

    @Column
    public String size = "";

    @Column
    public String braceHeight = "";

    @Column
    public String tiller = "";

    @Column
    public String limbs = "";

    @Column
    public String sight = "";

    @Column
    public String drawWeight = "";

    @Column
    public String stabilizer = "";

    @Column
    public String clicker = "";

    @Column
    public String description = "";

    @Column(typeConverter = ThumbnailConverter.class)
    public Thumbnail thumbnail;

    @Column(typeConverter = StringListConverter.class)
    public List<String> images;

    public List<SightMark> sightMarks = new ArrayList<>();

    public static List<Bow> getAll() {
        return SQLite.select().from(Bow.class).queryList();
    }

    public static Bow get(Long id) {
        return SQLite.select()
                .from(Bow.class)
                .where(Bow_Table._id.eq(id))
                .querySingle();
    }

    @OneToMany(methods = {OneToMany.Method.ALL}, variableName = "sightMarks")
    public List<SightMark> getSightMarks() {
        if (sightMarks == null || sightMarks.isEmpty()) {
            sightMarks = SQLite.select()
                    .from(SightMark.class)
                    .where(SightMark_Table.bow.eq(id))
                    .queryList();
        }
        return sightMarks;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Drawable getDrawable() {
        return thumbnail.getRoundDrawable();
    }

    @Override
    public Drawable getDrawable(Context context) {
        return getDrawable();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object another) {
        return another instanceof Bow &&
                getClass().equals(another.getClass()) &&
                id.equals(((Bow) another).id);
    }

    public SightMark getSightSetting(Dimension distance) {
        return Stream.of(getSightMarks())
                .filter(s -> s.distance.equals(distance))
                .findFirst()
                .orElse(null);
    }

    @Override
    public int compareTo(@NonNull Bow another) {
        final int result = getName().compareTo(another.getName());
        return result == 0 ? (int) (id - another.id) : result;
    }
}
