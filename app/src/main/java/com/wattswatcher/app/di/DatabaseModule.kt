package com.wattswatcher.app.di

import android.content.Context
import androidx.room.Room
import com.wattswatcher.app.data.local.WattsWatcherDatabase
import com.wattswatcher.app.data.local.dao.DeviceDao
import com.wattswatcher.app.data.local.dao.LiveDataDao
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
    fun provideWattsWatcherDatabase(@ApplicationContext context: Context): WattsWatcherDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            WattsWatcherDatabase::class.java,
            "wattswatcher_database"
        ).build()
    }
    
    @Provides
    fun provideDeviceDao(database: WattsWatcherDatabase): DeviceDao {
        return database.deviceDao()
    }
    
    @Provides
    fun provideLiveDataDao(database: WattsWatcherDatabase): LiveDataDao {
        return database.liveDataDao()
    }
}