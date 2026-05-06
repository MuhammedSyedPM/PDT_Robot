package com.syed.jetpacktwo.di
import com.syed.jetpacktwo.data.repository.DynamicRfidRepository
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
        dynamicRepo: DynamicRfidRepository
    ): RfidRepository {
        return dynamicRepo
    }
}
