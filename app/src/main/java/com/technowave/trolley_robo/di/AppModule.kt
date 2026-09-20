package com.technowave.trolley_robo.di
import com.technowave.trolley_robo.data.repository.DynamicRfidRepository
import com.technowave.trolley_robo.domain.repository.RfidRepository
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

    @Provides
    fun provideBeeper(): com.technowave.trolley_robo.util.Beeper {
        return com.technowave.trolley_robo.util.Beeper()
    }
}
