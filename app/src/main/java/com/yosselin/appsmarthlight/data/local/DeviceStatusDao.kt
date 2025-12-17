package com.yosselin.appsmarthlight.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface DeviceStatusDao {
    @Insert
    suspend fun insertDeviceStatus(deviceStatus: DeviceStatus)

    @Query("SELECT * FROM device_status ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLatestDeviceStatus(): DeviceStatus?
}