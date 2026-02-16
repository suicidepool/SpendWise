package com.oms.spendwise.model.entity

import android.adservices.adid.AdId
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey


@Entity(
    tableName = "budgetCategory",
    foreignKeys = [
        ForeignKey(
            entity = Budget::class,
            parentColumns = ["budgetId"],
            childColumns = ["budgetId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Category::class,
            parentColumns = ["categoryId"],
            childColumns = ["categoryId"]
        )
    ]
)
data class BudgetCategory(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val budgetId: Long,
    val categoryId: Long,
    val amountLimit: Double
)
