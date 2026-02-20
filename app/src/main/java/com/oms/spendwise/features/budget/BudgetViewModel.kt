package com.oms.spendwise.features.budget

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.oms.spendwise.data.repository.BudgetRepository
import com.oms.spendwise.data.repository.CategoryRepository
import com.oms.spendwise.data.repository.TransactionRepository
import com.oms.spendwise.model.entity.Budget
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class BudgetViewModel @Inject constructor(
    private val budgetRepository: BudgetRepository,
    private val categoryRepository: CategoryRepository,
    private val transactionRepository: TransactionRepository
): ViewModel() {

    var budget by mutableStateOf<Budget?>(null)
    var isLoading by mutableStateOf(true)

    fun addBudget(
        userId: Long,
        startDate: LocalDate,
        endDate: LocalDate,
        amount: Double
    ){
        viewModelScope.launch {
            budgetRepository.addBudget(
                userId = userId,
                startDate = startDate,
                endDate = endDate,
                amount = amount
            )
        }
    }

    fun editBudget(budget: Budget){
        viewModelScope.launch {
            budgetRepository.updateBudget(budget)
        }
    }

    fun loadBudget(){
        isLoading = true
        viewModelScope.launch {
           budget = budgetRepository.getCurrentBudget()
            isLoading = false
        }
    }

    init {
        loadBudget()
    }
}