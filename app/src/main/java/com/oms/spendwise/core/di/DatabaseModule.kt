package com.oms.spendwise.core.di

import android.content.Context
import androidx.room.Room
import com.oms.spendwise.data.local.dao.CategoryDao
import com.oms.spendwise.data.local.database.AppDatabase
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
        ).build()
    }

    @Provides
    fun provideCategoryDao(
        database: AppDatabase
    ): CategoryDao{
        return  database.categoryDao()
    }
}