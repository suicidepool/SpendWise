package com.oms.spendwise.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime

@Entity(tableName = "user")
data class User(
    @PrimaryKey
    val userId: Long = 0,
    val name: String,
    val profilePic: String,
    val currency: String,
    val weekStart: DayOfWeek,
    val dateOfBirth: LocalDate,
    val createdAt: LocalDateTime

)