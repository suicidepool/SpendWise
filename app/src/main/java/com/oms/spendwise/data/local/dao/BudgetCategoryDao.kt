package com.oms.spendwise.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.oms.spendwise.model.entity.BudgetCategory

@Dao
interface BudgetCategoryDao {
    @Insert
    suspend fun insertBudgetCategory(budgetCategory: BudgetCategory)

    @Update
    suspend fun updateBudgetCategory(budgetCategory: BudgetCategory)

    @Delete
    suspend fun deleteBudgetCategory(budgetCategory: BudgetCategory)

    @Query("DELETE FROM budgetCategory WHERE budgetId = :budgetId")
    suspend fun deleteBudgetCategories(budgetId: Long)

    @Query("SELECT * FROM budgetCategory WHERE budgetId = :budgetId")
    suspend fun getBudgetCategories(budgetId: Long): List<BudgetCategory>
}