package com.wattswatcher.app.data.local

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import android.content.Context
import com.wattswatcher.app.data.local.dao.DeviceDao
import com.wattswatcher.app.data.local.dao.LiveDataDao
import com.wattswatcher.app.data.local.entity.DeviceEntity
import com.wattswatcher.app.data.local.entity.LiveDataEntity

@Database(
    entities = [DeviceEntity::class, LiveDataEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class WattsWatcherDatabase : RoomDatabase() {
    
    abstract fun deviceDao(): DeviceDao
    abstract fun liveDataDao(): LiveDataDao
    
    companion object {
        @Volatile
        private var INSTANCE: WattsWatcherDatabase? = null
        
        fun getDatabase(context: Context): WattsWatcherDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    WattsWatcherDatabase::class.java,
                    "wattswatcher_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}