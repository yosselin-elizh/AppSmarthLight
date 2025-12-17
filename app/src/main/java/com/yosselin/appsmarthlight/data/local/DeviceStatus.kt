package com.yosselin.appsmarthlight.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "device_status")
data class DeviceStatus(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val isConnected: Boolean,
    val isLedOn: Boolean,
    val pirDetected: Boolean,
    val timestamp: Long
)