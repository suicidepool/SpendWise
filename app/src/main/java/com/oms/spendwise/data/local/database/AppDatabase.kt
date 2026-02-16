package com.oms.spendwise.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.oms.spendwise.data.local.dao.BudgetCategoryDao
import com.oms.spendwise.data.local.dao.BudgetDao
import com.oms.spendwise.data.local.dao.CategoryDao
import com.oms.spendwise.data.local.dao.TransactionDao
import com.oms.spendwise.data.local.dao.UserDao
import com.oms.spendwise.model.entity.Budget
import com.oms.spendwise.model.entity.BudgetCategory
import com.oms.spendwise.model.entity.Category
import com.oms.spendwise.model.entity.Transaction
import com.oms.spendwise.model.entity.User
import com.oms.spendwise.model.typeConverter.LocalDbConverters

@Database(
    entities = [
        Category::class,
        User::class,
        Transaction::class,
        Budget::class,
        BudgetCategory::class
    ],
    version = 1
)
@TypeConverters(LocalDbConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun categoryDao(): CategoryDao
    abstract fun userDao(): UserDao
    abstract fun transactionDao(): TransactionDao
    abstract fun budgetDao(): BudgetDao
    abstract fun budgetCategoryDao(): BudgetCategoryDao
}