package com.oms.spendwise.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.oms.spendwise.model.entity.Budget

@Dao
interface BudgetDao {
    @Insert
    suspend fun insertBudget(budget: Budget)

    @Update
    suspend fun updateBudget(budget: Budget)

    @Delete
    suspend fun deleteBudget(budget: Budget)

    @Query("DELETE FROM budget")
    suspend fun deleteAllBudget()

    @Query("SELECT * FROM budget ORDER BY startDate DESC")
    suspend fun getAllBudgets(): List<Budget>
}