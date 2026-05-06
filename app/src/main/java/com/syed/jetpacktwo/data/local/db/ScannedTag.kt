package com.syed.jetpacktwo.data.local.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "scanned_tags")
data class ScannedTag(
    @PrimaryKey val epc: String,
    val schedulerId: String,
    val timestamp: Long = System.currentTimeMillis()
)
