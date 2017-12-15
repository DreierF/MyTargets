/*
 * Copyright (C) 2017 Florian Dreier
 *
 * This file is part of MyTargets.
 *
 * MyTargets is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2
 * as published by the Free Software Foundation.
 *
 * MyTargets is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */

package de.dreier.mytargets.shared.models.db

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Parcelable
import android.text.TextUtils
import com.raizlabs.android.dbflow.annotation.Column
import com.raizlabs.android.dbflow.annotation.OneToMany
import com.raizlabs.android.dbflow.annotation.PrimaryKey
import com.raizlabs.android.dbflow.annotation.Table
import com.raizlabs.android.dbflow.config.FlowManager
import com.raizlabs.android.dbflow.sql.language.SQLite
import com.raizlabs.android.dbflow.structure.BaseModel
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper
import de.dreier.mytargets.shared.AppDatabase
import de.dreier.mytargets.shared.models.*
import de.dreier.mytargets.shared.utils.typeconverters.EBowTypeConverter
import de.dreier.mytargets.shared.utils.typeconverters.ThumbnailConverter
import kotlinx.android.parcel.Parcelize

@SuppressLint("ParcelCreator")
@Parcelize
@Table(database = AppDatabase::class)
data class Bow(
        @Column(name = "_id")
        @PrimaryKey(autoincrement = true)
        override var id: Long? = 0,

        @Column
        override var name: String = "",

        @Column(typeConverter = EBowTypeConverter::class)
        var type: EBowType? = EBowType.RECURVE_BOW,

        @Column
        var brand: String? = "",

        @Column
        var size: String? = "",

        @Column
        var braceHeight: String? = "",

        @Column
        var tiller: String? = "",

        @Column
        var limbs: String? = "",

        @Column
        var sight: String? = "",

        @Column
        var drawWeight: String? = "",

        @Column
        var stabilizer: String? = "",

        @Column
        var clicker: String? = "",

        @Column
        var button: String? = "",

        @Column
        var string: String? = "",

        @Column
        var nockingPoint: String? = "",

        @Column
        var letoffWeight: String? = "",

        @Column
        var arrowRest: String? = "",

        @Column
        var restHorizontalPosition: String? = "",

        @Column
        var restVerticalPosition: String? = "",

        @Column
        var restStiffness: String? = "",

        @Column
        var camSetting: String? = "",

        @Column
        var scopeMagnification: String? = "",

        @Column
        var description: String? = "",

        @Column(typeConverter = ThumbnailConverter::class)
        @JvmField //DBFlow bug
        var thumbnail: Thumbnail? = null
) : BaseModel(), IImageProvider, IIdSettable, Comparable<Bow>, IRecursiveModel, Parcelable {

    @Transient
    var images: List<BowImage>? = null

    @Transient
    var sightMarks: MutableList<SightMark>? = null

    val drawable: Drawable
        get() = thumbnail!!.roundDrawable

    @OneToMany(methods = [], variableName = "sightMarks")
    fun loadSightMarks(): MutableList<SightMark>? {
        if (sightMarks == null) {
            sightMarks = if (id == null) mutableListOf() else SQLite.select()
                    .from(SightMark::class.java)
                    .where(SightMark_Table.bow.eq(id))
                    .queryList()
                    .sortedBy { sightMark -> sightMark.distance }
                    .toMutableList()
        }
        return sightMarks
    }

    @OneToMany(methods = [], variableName = "images")
    fun loadImages(): List<BowImage>? {
        if (images == null) {
            images = if (id == null) mutableListOf() else SQLite.select()
                    .from(BowImage::class.java)
                    .where(BowImage_Table.bow.eq(id!!))
                    .queryList()
        }
        return images
    }

    override fun getDrawable(context: Context): Drawable {
        return drawable
    }

    fun loadSightSetting(distance: Dimension): SightMark? {
        return loadSightMarks()?.firstOrNull { s -> s.distance == distance }
    }

    override fun save() {
        FlowManager.getDatabase(AppDatabase::class.java).executeTransaction({ this.save(it) })
    }

    override fun save(databaseWrapper: DatabaseWrapper) {
        super.save(databaseWrapper)
        if (images != null) {
            SQLite.delete(BowImage::class.java)
                    .where(BowImage_Table.bow.eq(id!!))
                    .execute(databaseWrapper)
            // TODO Replace this super ugly workaround by stubbed Relationship in version 4 of dbFlow
            for (image in images!!) {
                image.bowId = id
                image.save(databaseWrapper)
            }
        }
        if (sightMarks != null) {
            SQLite.delete(SightMark::class.java)
                    .where(SightMark_Table.bow.eq(id))
                    .execute(databaseWrapper)
            // TODO Replace this super ugly workaround by stubbed Relationship in version 4 of dbFlow
            for (sightMark in sightMarks!!) {
                sightMark.bowId = id
                sightMark.save(databaseWrapper)
            }
        }
    }

    override fun delete() {
        FlowManager.getDatabase(AppDatabase::class.java).executeTransaction({ this.delete(it) })
    }

    override fun delete(databaseWrapper: DatabaseWrapper) {
        loadSightMarks()?.forEach { it.delete(databaseWrapper) }
        loadImages()?.forEach { it.delete(databaseWrapper) }
        super.delete(databaseWrapper)
    }

    override fun compareTo(other: Bow) = compareBy(Bow::name, Bow::id).compare(this, other)

    fun areAllPropertiesSet(): Boolean {
        return !TextUtils.isEmpty(size) &&
                !TextUtils.isEmpty(drawWeight) &&
                (!type!!.showLetoffWeight() || !TextUtils.isEmpty(letoffWeight)) &&
                (!type!!.showArrowRest() || !TextUtils.isEmpty(arrowRest)) &&
                (!type!!.showArrowRest() || !TextUtils.isEmpty(restVerticalPosition)) &&
                (!type!!.showArrowRest() || !TextUtils.isEmpty(restHorizontalPosition)) &&
                (!type!!.showArrowRest() || !TextUtils.isEmpty(restStiffness)) &&
                (!type!!.showCamSetting() || !TextUtils.isEmpty(camSetting)) &&
                (!type!!.showTiller() || !TextUtils.isEmpty(tiller)) &&
                (!type!!.showBraceHeight() || !TextUtils.isEmpty(braceHeight)) &&
                (!type!!.showLimbs() || !TextUtils.isEmpty(limbs)) &&
                (!type!!.showSight() || !TextUtils.isEmpty(sight)) &&
                (!type!!.showScopeMagnification() || !TextUtils.isEmpty(scopeMagnification)) &&
                (!type!!.showStabilizer() || !TextUtils.isEmpty(stabilizer)) &&
                (!type!!.showClicker() || !TextUtils.isEmpty(clicker)) &&
                (!type!!.showNockingPoint() || !TextUtils.isEmpty(nockingPoint)) &&
                !TextUtils.isEmpty(string) &&
                (!type!!.showButton() || !TextUtils.isEmpty(button)) &&
                !TextUtils.isEmpty(description)
    }

    override fun saveRecursively() {
        save()
    }

    override fun saveRecursively(databaseWrapper: DatabaseWrapper) {
        save(databaseWrapper)
    }

    companion object {

        val all: List<Bow>
            get() = SQLite.select().from(Bow::class.java).queryList()

        operator fun get(id: Long?): Bow? {
            return SQLite.select()
                    .from(Bow::class.java)
                    .where(Bow_Table._id.eq(id))
                    .querySingle()
        }
    }
}
