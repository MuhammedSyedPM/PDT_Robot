package com.syed.jetpacktwo.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [ScannedTag::class, ExpectedItemEntity::class], version = 3, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun scannedTagDao(): ScannedTagDao
    abstract fun expectedItemDao(): ExpectedItemDao
}
