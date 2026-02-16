package com.oms.spendwise.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class Category(
    @PrimaryKey(autoGenerate = true)
    val categoryId: Long,
    val name: String,
    val type: String,
    val icon: String,
    val colorHex: String
)