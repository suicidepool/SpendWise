package com.oms.spendwise.model.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(
    tableName = "transaction",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["userId"],
            childColumns = ["userId"]
        ),
        ForeignKey(
            entity = Category::class,
            parentColumns = ["categoryId"],
            childColumns = ["categoryId"]
        )
    ]
)
data class Transaction (
    @PrimaryKey(autoGenerate = true)
    val transactionId: Long = 0,
    val userId: Long,
    val categoryId: Long,
    val amount: Double,
    val type: String,
    val note: String = "",
    val transactionDateTime: LocalDateTime,
    val createdAt: LocalDateTime
)