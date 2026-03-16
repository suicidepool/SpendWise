package com.oms.spendwise.features.budget

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.oms.spendwise.data.repository.BudgetRepository
import com.oms.spendwise.data.repository.CategoryRepository
import com.oms.spendwise.data.repository.TransactionRepository
import com.oms.spendwise.domain.BudgetCalculator
import com.oms.spendwise.model.entity.Budget
import com.oms.spendwise.model.entity.BudgetCategory
import com.oms.spendwise.model.entity.Category
import com.oms.spendwise.model.entity.Transaction
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import javax.inject.Inject
import kotlin.math.round

@HiltViewModel
class BudgetViewModel @Inject constructor(
    private val budgetRepository: BudgetRepository,
    private val categoryRepository: CategoryRepository,
    private val budgetCalculator: BudgetCalculator
): ViewModel() {

    var budget by mutableStateOf<Budget?>(null)
    var budgetCategories by mutableStateOf<List<BudgetCategory>>(emptyList())
    var isLoading by mutableStateOf(true)


    private fun loadBudgetData(){
        isLoading = true
        viewModelScope.launch {
            loadBudget()
            loadBudgetCategories()
            isLoading = false
        }
    }


    private suspend fun loadBudget(){
        budget = budgetRepository.getCurrentBudget()
    }

    fun addBudget(
        userId: Long,
        startDate: LocalDate,
        endDate: LocalDate,
        amount: Double,
        categories: SnapshotStateList<Pair<Category, String>>
    ){
            if(budget == null){
                viewModelScope.launch {
                    budgetRepository.addBudget(
                        userId = userId,
                        startDate = startDate,
                        endDate = endDate,
                        amount = amount
                    )
                    loadBudget()
                    budget?.let { budget ->
                        addBudgetCategories(
                            budgetId = budget.budgetId,
                            categories = categories
                        )
                    }
                    loadBudgetCategories()
                }
            }
    }

    fun updateBudget(
        startDate: LocalDate,
        endDate: LocalDate,
        amount: Double,
        categories: SnapshotStateList<Pair<Category, String>>
    ){
        budget?.let { budget ->
            Log.d("BUDGET","inside update")
            viewModelScope.launch {
                budgetRepository.updateBudget(
                    budgetId = budget.budgetId,
                    userId = budget.userId,
                    startDate = startDate,
                    endDate = endDate,
                    amount = amount
                )
                deleteBudgetCategories()
                addBudgetCategories(
                    budgetId = budget.budgetId,
                    categories = categories
                )
                loadBudgetData()
            }
        }
    }

    fun deleteBudget(){
        budget?.let { budget ->
            viewModelScope.launch {
                budgetRepository.deleteBudget(budget)
                loadBudgetData()
            }
        }
    }

    fun deleteAllBudgets(){
        viewModelScope.launch {
            budgetRepository.deleteAllBudget()
            loadBudgetData()
        }
    }

    suspend fun loadBudgetCategories(){
        budget?.let {
            budgetCategories = budgetRepository.getBudgetCategories(it.budgetId)
        }
    }

    suspend fun addBudgetCategories(
        budgetId: Long,
        categories: SnapshotStateList<Pair<Category, String>>
    ){
        categories.forEach { (category, amount) ->
            budgetRepository.addBudgetCategory(
                budgetId = budgetId,
                categoryId = category.categoryId,
                amountLimit = amount.toDouble()
            )
        }
    }

    private suspend fun deleteBudgetCategories(){
        budget?.let { budget ->
            budgetRepository.deleteBudgetCategories(budget.budgetId)
        }
    }


    init {
        loadBudgetData()
    }

    fun getDays(
        start: LocalDate,
        end: LocalDate
    ): Int {
        return ChronoUnit.DAYS.between(start, end).toInt()
    }

    fun getAmountSpent(
        transactions: List<Transaction>
    ) : Double{
        var amountSpent = 0.0
        budget?.let {
            amountSpent = budgetCalculator.calculateAmountSpent(
                transactions = transactions,
                startDate = it.startDate,
                endDate = it.endDate
            )
        }
        return amountSpent
    }

    fun getAmountSpent(
        categoryId: Long,
        transactions: List<Transaction>
    ) : Double{
        var amountSpent = 0.0
        budget?.let {
            amountSpent = budgetCalculator.calculateAmountSpent(
                categoryId = categoryId,
                transactions = transactions,
                startDate = it.startDate,
                endDate = it.endDate
            )
        }
        return amountSpent
    }

    fun getPercentage(
        spent: Double,
        total: Double,
    ):Double{
        val percentage = (spent / total) * 100
        return round(percentage * 100) / 100
    }
}