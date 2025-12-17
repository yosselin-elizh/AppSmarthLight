package com.yosselin.appsmarthlight

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yosselin.appsmarthlight.data.local.AppDatabase
import com.yosselin.appsmarthlight.data.local.DeviceStatus
import com.yosselin.appsmarthlight.data.local.User
import com.yosselin.appsmarthlight.data.local.UserDao
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class DatabaseTest {

    private lateinit var db: AppDatabase
    private lateinit var userDao: UserDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, AppDatabase::class.java).build()
        userDao = db.userDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun writeUserAndReadInList() = runBlocking {
        val user = User(username = "test", passwordHash = "hash", salt = "salt")
        userDao.insertUser(user)
        val byName = userDao.getUserByUsername("test")
        assertEquals(byName?.username, "test")
    }

    @Test
    @Throws(Exception::class)
    fun writeDeviceStatusAndRead() = runBlocking {
        val deviceStatusDao = db.deviceStatusDao()
        val status = DeviceStatus(isConnected = true, isLedOn = false, pirDetected = true, timestamp = System.currentTimeMillis())
        deviceStatusDao.insertDeviceStatus(status)
        val latestStatus = deviceStatusDao.getLatestDeviceStatus()
        assertEquals(latestStatus?.isConnected, true)
    }
}