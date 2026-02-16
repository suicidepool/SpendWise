package com.oms.spendwise.data.repository

import com.oms.spendwise.data.local.dao.BudgetCategoryDao
import com.oms.spendwise.data.local.dao.BudgetDao
import com.oms.spendwise.model.entity.Budget
import com.oms.spendwise.model.entity.BudgetCategory
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BudgetRepository @Inject constructor(
    private val budgetDao: BudgetDao,
    private val budgetCategoryDao: BudgetCategoryDao
) {

    suspend fun addBudget(
        userId: Long,
        startDate: LocalDate,
        endDate: LocalDate,
        amount: Double
    ){
        budgetDao.insertBudget(
            Budget(
                userId = userId,
                startDate = startDate,
                endDate = endDate,
                amount = amount
            )
        )
    }

    suspend fun updateBudget(budget: Budget){
        budgetDao.updateBudget(budget)
    }

    suspend fun deleteBudget(budget: Budget){
        budgetDao.deleteBudget(budget)
    }

    suspend fun deleteAllBudget(){
        budgetDao.deleteAllBudget()
    }

    suspend fun getCurrentBudget(): Budget?{
        val allBudgets = budgetDao.getAllBudgets()

        if(allBudgets.isNotEmpty()){
            val currentDate = LocalDate.now()
            val recentBudget = allBudgets[0]
            val isCurrentBudget = currentDate.isAfter(recentBudget.startDate) && currentDate.isBefore(recentBudget.endDate)
            if(isCurrentBudget) return recentBudget
        }
        return null
    }

    suspend fun getBudgetCategories(budgetId: Long): List<BudgetCategory>{
        return budgetCategoryDao.getBudgetCategories(budgetId)
    }

}