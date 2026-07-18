package com.syed.jetpacktwo.data.local.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpectedItemDao {
    @Query("SELECT * FROM expected_items")
    fun getAllExpectedItems(): Flow<List<ExpectedItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItems(items: List<ExpectedItemEntity>)

    @Query("DELETE FROM expected_items")
    suspend fun deleteAll()

    @Transaction
    suspend fun replaceAll(items: List<ExpectedItemEntity>) {
        deleteAll()
        insertItems(items)
    }
}
