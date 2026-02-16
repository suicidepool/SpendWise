package com.oms.spendwise.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.oms.spendwise.model.entity.User

@Dao
interface UserDao {
    @Insert
    suspend fun insertUser(user: User)

    @Update
    suspend fun updateUser(user: User)

    @Delete
    suspend fun deleteUser(user: User)

    @Query("SELECT * FROM user LIMIT 1")
    suspend fun getUser(): User?

    @Query("SELECT * FROM user WHERE userId = :id")
    suspend fun getUserById(id: Long): User?
}