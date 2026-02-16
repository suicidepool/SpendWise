package com.oms.spendwise.model.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(
    tableName = "budget",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["userId"],
            childColumns = ["userId"]
        )
    ]
)
data class Budget(
    @PrimaryKey(autoGenerate = true)
    val budgetId: Long = 0,
    val userId: Long,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val amount: Double
)