package com.syed.jetpacktwo.data.local.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "expected_items")
data class ExpectedItemEntity(
    @PrimaryKey val epc: String,
    val description: String,
    val department: String,
    val timestamp: Long = System.currentTimeMillis()
)
