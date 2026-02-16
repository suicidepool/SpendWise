package com.oms.spendwise.core.di

import android.content.Context
import androidx.room.Room
import com.oms.spendwise.data.local.dao.BudgetCategoryDao
import com.oms.spendwise.data.local.dao.BudgetDao
import com.oms.spendwise.data.local.dao.CategoryDao
import com.oms.spendwise.data.local.dao.TransactionDao
import com.oms.spendwise.data.local.dao.UserDao
import com.oms.spendwise.data.local.database.AppDatabase
import com.oms.spendwise.model.entity.BudgetCategory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): AppDatabase{
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "appDB"
        ).allowMainThreadQueries().build()
    }

    @Provides
    @Singleton
    fun provideCategoryDao(
        database: AppDatabase
    ): CategoryDao{
        return  database.categoryDao()
    }

    @Provides
    @Singleton
    fun provideUserDao(
        database: AppDatabase
    ): UserDao{
        return database.userDao()
    }

    @Provides
    @Singleton
    fun provideTransactionDao(
        database: AppDatabase
    ): TransactionDao{
        return database.transactionDao()
    }

    @Provides
    @Singleton
    fun provideBudgetDao(
        database: AppDatabase
    ): BudgetDao{
        return database.budgetDao()
    }

    @Provides
    @Singleton
    fun provideBudgetCategoryDao(
        database: AppDatabase
    ): BudgetCategoryDao{
        return database.budgetCategoryDao()
    }
}