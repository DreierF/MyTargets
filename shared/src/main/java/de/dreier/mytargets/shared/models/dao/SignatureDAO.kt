/*
 * Copyright (C) 2018 Florian Dreier
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

package de.dreier.mytargets.shared.models.dao

import com.raizlabs.android.dbflow.kotlinextensions.delete
import com.raizlabs.android.dbflow.kotlinextensions.save
import com.raizlabs.android.dbflow.sql.language.SQLite
import de.dreier.mytargets.shared.models.db.Signature
import de.dreier.mytargets.shared.models.db.Signature_Table

object SignatureDAO {

    fun loadSignature(id: Long): Signature = SQLite.select()
            .from(Signature::class.java)
            .where(Signature_Table._id.eq(id))
            .querySingle() ?: throw IllegalStateException("Signature $id does not exist")

    fun loadSignatureOrNull(id: Long): Signature? = SQLite.select()
            .from(Signature::class.java)
            .where(Signature_Table._id.eq(id))
            .querySingle()

    fun saveSignature(signature: Signature) {
        signature.save()
    }

    fun deleteSignature(signature: Signature) {
        signature.delete()
    }
}
