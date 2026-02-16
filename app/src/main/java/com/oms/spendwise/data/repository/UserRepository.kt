package com.oms.spendwise.data.repository

import com.oms.spendwise.data.local.dao.UserDao
import com.oms.spendwise.model.entity.User
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.jvm.Throws

@Singleton
class UserRepository @Inject constructor(
    private val userDao: UserDao
) {
    suspend fun getUser(): User{
        return userDao.getUser() ?: throw Exception("user not registered")
    }

    suspend fun addUser(
        name: String,
        profilePic: String,
        currency: String,
        weekStart: DayOfWeek,
        dateOfBirth: LocalDate
    ) {
        val currentUser = userDao.getUser()

        if(currentUser == null){
            userDao.insertUser(
                User(
                    name = name,
                    profilePic = profilePic,
                    currency = currency,
                    weekStart = weekStart,
                    dateOfBirth = dateOfBirth,
                    createdAt = LocalDateTime.now()
                )
            )
            return
        }

        throw Exception("user already registered")
    }

    suspend fun updateUser(user: User){
        val currentUser = userDao.getUser()
        if (currentUser != null){
            userDao.updateUser(user)
            return
        }

        throw Exception("user not registered")
    }

    suspend fun deleteUser(user: User){
        userDao.deleteUser(user)
    }
}