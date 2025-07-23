package com.wattswatcher.app.data.local.dao

import androidx.room.*
import com.wattswatcher.app.data.local.entity.DeviceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DeviceDao {
    
    @Query("SELECT * FROM devices ORDER BY name ASC")
    fun getAllDevices(): Flow<List<DeviceEntity>>
    
    @Query("SELECT * FROM devices WHERE id = :deviceId")
    suspend fun getDeviceById(deviceId: String): DeviceEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDevice(device: DeviceEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDevices(devices: List<DeviceEntity>)
    
    @Update
    suspend fun updateDevice(device: DeviceEntity)
    
    @Delete
    suspend fun deleteDevice(device: DeviceEntity)
    
    @Query("DELETE FROM devices")
    suspend fun deleteAllDevices()
}