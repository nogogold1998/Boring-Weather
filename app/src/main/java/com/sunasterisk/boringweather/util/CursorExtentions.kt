package com.sunasterisk.boringweather.util

import android.database.Cursor
import androidx.core.database.getDoubleOrNull
import androidx.core.database.getFloatOrNull
import androidx.core.database.getIntOrNull
import androidx.core.database.getLongOrNull
import androidx.core.database.getShortOrNull
import androidx.core.database.getStringOrNull

inline fun <reified R> Cursor.get(colName: String): R? {
    val colIndex = getColumnIndex(colName)
    val result: Any? = when (R::class) {
        Short::class -> getShortOrNull(colIndex)
        Int::class -> getIntOrNull(colIndex)
        Long::class -> getLongOrNull(colIndex)
        Float::class -> getFloatOrNull(colIndex)
        Double::class -> getDoubleOrNull(colIndex)
        String::class -> getStringOrNull(colIndex)
        else -> null
    }
    return result as? R
}

inline fun <reified R> Cursor.getOrElse(colName: String, default: R): R = get(colName) ?: default

inline fun <R> Cursor.map(transform: (cursor: Cursor) -> R): List<R> = mutableListOf<R>().also {
    if (moveToFirst()) {
        do {
            it.add(transform(this))
        } while (moveToNext())
    }
}
