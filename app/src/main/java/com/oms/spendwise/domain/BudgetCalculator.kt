package com.oms.spendwise.domain

import com.oms.spendwise.model.entity.Transaction
import com.oms.spendwise.model.enum.TransactionType
import java.time.LocalDate
import javax.inject.Inject

class BudgetCalculator @Inject constructor() {

    fun calculateAmountSpent(
        transactions: List<Transaction>,
        startDate: LocalDate,
        endDate: LocalDate
    ): Double {
        var amountSpent = 0.0
        transactions.forEach { transaction ->
            val transactionDate = transaction.transactionDateTime.toLocalDate()
            if((transaction.type == TransactionType.EXPENSE.value) && (transactionDate == startDate) || (transactionDate == endDate) || (transactionDate.isAfter(startDate) && transactionDate.isBefore(endDate))){
                amountSpent += transaction.amount
            }
        }
        return  amountSpent
    }

    fun calculateAmountSpent(
        categoryId: Long,
        transactions: List<Transaction>,
        startDate: LocalDate,
        endDate: LocalDate
    ): Double {
        var amountSpent = 0.0
        transactions.forEach { transaction ->
            val transactionDate = transaction.transactionDateTime.toLocalDate()
            if(transaction.categoryId == categoryId && (transactionDate == startDate) || (transactionDate == endDate) || (transactionDate.isAfter(startDate) && transactionDate.isBefore(endDate))){
                amountSpent += transaction.amount
            }
        }
        return  amountSpent
    }
}