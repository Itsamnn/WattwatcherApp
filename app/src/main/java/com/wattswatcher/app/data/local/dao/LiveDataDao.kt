package com.wattswatcher.app.data.local.dao

import androidx.room.*
import com.wattswatcher.app.data.local.entity.LiveDataEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LiveDataDao {
    
    @Query("SELECT * FROM live_data WHERE id = 1")
    fun getCurrentLiveData(): Flow<LiveDataEntity?>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLiveData(liveData: LiveDataEntity)
    
    @Query("DELETE FROM live_data")
    suspend fun deleteAllLiveData()
}