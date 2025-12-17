package com.yosselin.appsmarthlight.data.repository

import android.util.Base64
import com.yosselin.appsmarthlight.data.local.User
import com.yosselin.appsmarthlight.data.local.UserDao
import java.security.SecureRandom
import java.security.spec.KeySpec
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec
import javax.inject.Inject

class AuthRepository @Inject constructor(private val userDao: UserDao) {

    private val PBE_ITERATION_COUNT = 65536
    private val PBE_KEY_LENGTH = 256 // in bits
    private val PBE_ALGORITHM = "PBKDF2WithHmacSHA256"

    suspend fun register(username: String, password: String): Boolean {
        if (username.isBlank() || password.isBlank()) {
            throw IllegalArgumentException("El nombre de usuario y la contraseña no pueden estar vacíos")
        }

        if (userDao.getUserByUsername(username) != null) {
            return false // User already exists
        }

        val salt = generateSalt()
        val passwordHash = hashPassword(password, salt)

        val newUser = User(
            username = username,
            passwordHash = Base64.encodeToString(passwordHash, Base64.NO_WRAP),
            salt = Base64.encodeToString(salt, Base64.NO_WRAP)
        )
        userDao.insertUser(newUser)
        return true
    }

    suspend fun login(username: String, password: String): Boolean {
        if (username.isBlank() || password.isBlank()) {
            return false
        }
        
        val user = userDao.getUserByUsername(username) ?: return false

        val storedHash = Base64.decode(user.passwordHash, Base64.NO_WRAP)
        val salt = Base64.decode(user.salt, Base64.NO_WRAP)

        val newHash = hashPassword(password, salt)

        return storedHash.contentEquals(newHash)
    }

    private fun generateSalt(): ByteArray {
        val random = SecureRandom()
        val salt = ByteArray(16)
        random.nextBytes(salt)
        return salt
    }

    private fun hashPassword(password: String, salt: ByteArray): ByteArray {
        val spec: KeySpec = PBEKeySpec(password.toCharArray(), salt, PBE_ITERATION_COUNT, PBE_KEY_LENGTH)
        val factory = SecretKeyFactory.getInstance(PBE_ALGORITHM)
        return factory.generateSecret(spec).encoded
    }
}