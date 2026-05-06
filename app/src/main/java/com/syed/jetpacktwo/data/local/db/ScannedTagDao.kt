package com.syed.jetpacktwo.data.local.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ScannedTagDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTags(tags: List<ScannedTag>)

    @Query("SELECT * FROM scanned_tags ORDER BY timestamp DESC")
    fun getAllTags(): Flow<List<ScannedTag>>

    @Query("SELECT COUNT(*) FROM scanned_tags")
    fun getTagCount(): Flow<Int>

    @Delete
    suspend fun deleteTags(tags: List<ScannedTag>)

    @Query("DELETE FROM scanned_tags")
    suspend fun clearAll()
}
