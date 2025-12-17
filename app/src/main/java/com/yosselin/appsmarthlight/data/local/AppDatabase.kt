package com.yosselin.appsmarthlight.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [User::class, DeviceStatus::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun deviceStatusDao(): DeviceStatusDao
}