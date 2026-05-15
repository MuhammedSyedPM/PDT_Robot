package com.syed.jetpacktwo.di

import com.syed.jetpacktwo.data.repository.RfidRepositoryImpl
import com.syed.jetpacktwo.data.repository.ChainwayRfidRepositoryImpl
import com.syed.jetpacktwo.data.repository.ZebraRfidRepositoryImpl
import com.syed.jetpacktwo.domain.repository.RfidRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideRfidRepository(
        dynamicRepo: com.syed.jetpacktwo.data.repository.DynamicRfidRepository
    ): RfidRepository {
        return dynamicRepo
    }
}
