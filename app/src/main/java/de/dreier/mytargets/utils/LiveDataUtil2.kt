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

package de.dreier.mytargets.utils

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.annotation.MainThread

class LiveDataUtil2<A, B, Y> {

    private var source: LiveData<Y>? = null

    @MainThread
    public fun switchMap(
        trigger1: LiveData<A>,
        trigger2: LiveData<B>,
        thunk: (A?, B?) -> LiveData<Y>
    ): LiveData<Y> {
        val result: MediatorLiveData<Y> = MediatorLiveData()

        result.addSource(trigger1, { a ->
            val newLiveData: LiveData<Y> = thunk.invoke(a, trigger2.value)
            if (source != newLiveData) {
                if (source != null) {
                    result.removeSource(source!!)
                }

                source = newLiveData
                if (source != null) {
                    result.addSource(source!!, { y ->
                        result.setValue(y)
                    })
                }
            }
        })

        result.addSource(trigger2, { b ->
            val newLiveData: LiveData<Y> = thunk.invoke(trigger1.value, b)
            if (source != newLiveData) {
                if (source != null) {
                    result.removeSource(source!!)
                }

                source = newLiveData
                if (source != null) {
                    result.addSource(source!!, { y ->
                        result.setValue(y)
                    })
                }
            }
        })

        return result
    }

    fun map(
        source1: LiveData<A>, source2: LiveData<B>,
        thunk: (A, B) -> Y
    ): LiveData<Y> {
        val result = MediatorLiveData<Y>()
        var value1: A? = null
        var value2: B? = null
        fun setResult(value1: A?, value2: B?) {
            if (value1 != null && value2 != null) {
                result.value = thunk.invoke(value1, value2)
            }
        }
        result.addSource(source1, { a ->
            value1 = a
            setResult(value1, value2)
        })
        result.addSource(source2, { b ->
            value2 = b
            setResult(value1, value2)
        })
        return result
    }
}
