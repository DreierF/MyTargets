package de.dreier.mytargets.shared.models.db;

import android.content.Context;
import android.graphics.drawable.Drawable;

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
import de.dreier.mytargets.shared.utils.ThumbnailConverter;

@Parcel
@Table(database = AppDatabase.class, name = "BOW")
public class Bow extends BaseModel implements IImageProvider, IIdSettable {

    @Column(name = "_id")
    @PrimaryKey(autoincrement = true)
    public Long id = -1L;

    @Column(name = "name")
    public String name = "";

    @Column(name = "type")
    public EBowType type = EBowType.RECURVE_BOW;

    @Column(name = "brand")
    public String brand = "";

    @Column(name = "size")
    public String size = "";

    @Column(name = "height")
    public String braceHeight = "";

    @Column(name = "tiller")
    public String tiller = "";

    @Column(name = "limbs")
    public String limbs = "";

    @Column(name = "sight")
    public String sight = "";

    @Column(name = "draw_weight")
    public String drawWeight = "";

    @Column(name = "stabilizer")
    public String stabilizer = "";

    @Column(name = "clicker")
    public String clicker = "";

    @Column(name = "description")
    public String description = "";

    @Column(typeConverter = ThumbnailConverter.class, name = "thumbnail")
    public Thumbnail thumbnail;

    @Column(name = "image")
    public String imageFile;

    public List<SightSetting> sightSettings = new ArrayList<>();

    public static List<Bow> getAll() {
        return SQLite.select().from(Bow.class).queryList();
    }

    public static Bow get(Long id) {
        return SQLite.select()
                .from(Bow.class)
                //.where(Bow_Table.id__id.eq(id))
                .querySingle();
    }

    public static void deleteAll() {
        SQLite.delete(Bow.class).execute();
    }

    @OneToMany(methods = {OneToMany.Method.ALL}, variableName = "sightSettings")
    public List<SightSetting> getSightSettings() {
        if (sightSettings == null || sightSettings.isEmpty()) {
            sightSettings = SQLite.select()
                    .from(SightSetting.class)
                    //.where(SightSetting_Table.bowId__id.eq(id))
                    .queryList();
        }
        return sightSettings;
    }

    public Long getId() {
        return id;
    }

    public void setId(long id) {
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

    public SightSetting getSightSetting(Dimension distance) {
        return Stream.of(getSightSettings())
                .filter(s -> s.distance.equals(distance))
                .findFirst()
                .orElse(null);
    }
}
