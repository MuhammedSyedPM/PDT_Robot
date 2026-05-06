package com.syed.jetpacktwo.di

import android.content.Context
import androidx.room.Room
import com.syed.jetpacktwo.data.local.db.AppDatabase
import com.syed.jetpacktwo.data.local.db.ScannedTagDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "jetpack_two_db"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    fun provideScannedTagDao(database: AppDatabase): ScannedTagDao {
        return database.scannedTagDao()
    }
}
