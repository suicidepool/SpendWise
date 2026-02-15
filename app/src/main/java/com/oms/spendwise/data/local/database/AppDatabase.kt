package com.oms.spendwise.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.oms.spendwise.data.local.dao.CategoryDao
import com.oms.spendwise.model.entity.Category

@Database(
    entities = [
        Category::class
    ],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun categoryDao(): CategoryDao
}