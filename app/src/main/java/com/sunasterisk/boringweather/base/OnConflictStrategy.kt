package com.sunasterisk.boringweather.base

import android.database.sqlite.SQLiteDatabase
import androidx.annotation.IntDef

@IntDef(
    value = [SQLiteDatabase.CONFLICT_ABORT,
        SQLiteDatabase.CONFLICT_IGNORE,
        SQLiteDatabase.CONFLICT_REPLACE,
        SQLiteDatabase.CONFLICT_ROLLBACK,
        SQLiteDatabase.CONFLICT_FAIL,
        SQLiteDatabase.CONFLICT_NONE]
)
annotation class OnConflictStrategy
