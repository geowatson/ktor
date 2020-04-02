/*
 * Copyright 2014-2020 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package io.ktor.utils.io.concurrent

import kotlinx.atomicfu.*
import kotlin.native.concurrent.*
import kotlin.properties.*
import kotlin.reflect.*

public actual inline fun <T> shared(value: T): ReadWriteProperty<Any, T> = object : ReadWriteProperty<Any, T> {
    private var reference = atomic(value)

    init {
        freeze()
    }

    override fun getValue(thisRef: Any, property: KProperty<*>): T {
        return reference.value
    }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: T) {
        reference.value = value
    }
}
