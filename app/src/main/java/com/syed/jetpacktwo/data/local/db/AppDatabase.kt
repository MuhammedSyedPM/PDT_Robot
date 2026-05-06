package com.syed.jetpacktwo.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [ScannedTag::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun scannedTagDao(): ScannedTagDao
}
