package com.oms.spendwise.domain

import android.util.Log
import com.oms.spendwise.model.entity.Transaction
import com.oms.spendwise.model.enum.TransactionType
import java.time.LocalDate
import java.time.LocalDateTime
import javax.inject.Inject
import kotlin.math.round

class TransactionCalculator @Inject constructor(){

    fun calculateTotalBalance(
        transactions: List<Transaction>,
        month: Int = LocalDate.now().monthValue,
        year: Int = LocalDate.now().year
    ): Double{
        var sum = 0.0
        transactions.forEach { transaction ->
            if(transaction.transactionDateTime.monthValue <= month && transaction.transactionDateTime.year <= year){
                if(transaction.type == TransactionType.INCOME.value)
                    sum += transaction.amount
                else
                    sum -= transaction.amount
            }
            }
        return round(sum * 100) / 100
    }

    fun calculateMonthlyIncome(
        transactions: List<Transaction>,
        month: Int,
        year: Int
    ): Double{
        var income = 0.0
        transactions.forEach { transaction ->
            val transactionDateTime = transaction.transactionDateTime
            if(transaction.type == TransactionType.INCOME.value && transactionDateTime.monthValue == month && transactionDateTime.year == year){
                income += transaction.amount
            }
        }
        return income
    }

    fun calculateBalanceIncrementFromLastMonth(
        transactions: List<Transaction>
    ): Double{
        val currentMonthTotalBalance = calculateTotalBalance(transactions)
        val prevMonthTotalBalance = calculateTotalBalance(transactions, LocalDate.now().monthValue-1,
            LocalDate.now().year)
        Log.d("BALANCE",currentMonthTotalBalance.toString())
        Log.d("BALANCE",prevMonthTotalBalance.toString())
        val increment = (currentMonthTotalBalance-prevMonthTotalBalance) * (100 / prevMonthTotalBalance)
        return round(increment * 100) / 100
    }

    fun calculateMonthlyExpense(
        transactions: List<Transaction>,
        month: Int,
        year: Int
    ): Double{
        var expense = 0.0
        transactions.forEach { transaction ->
            val transactionDateTime = transaction.transactionDateTime
            if(transaction.type == TransactionType.EXPENSE.value && transactionDateTime.monthValue == month && transactionDateTime.year == year){
                expense += transaction.amount
            }
        }
        return expense
    }

    fun calculateIncomeIncrementFromLastMonth(
        transactions: List<Transaction>,
        month: Int,
        year: Int
    ): Double{
        var increment = 0.0
        val thisMonthIncome = calculateMonthlyIncome(
            transactions = transactions,
            month = month,
            year = year
        )
        val lastMonthIncome = calculateMonthlyIncome(
            transactions = transactions,
            month = month-1,
            year = year
        )

        increment = (thisMonthIncome - lastMonthIncome) * (100 / lastMonthIncome)

        return round(increment * 100) / 100
    }

    fun calculateExpenseIncrementFromLastMonth(
        transactions: List<Transaction>,
        month: Int,
        year: Int
    ): Double{
        var increment = 0.0
        val thisMonthExpense = calculateMonthlyExpense(
            transactions = transactions,
            month = month,
            year = year
        )
        val lastMonthExpense = calculateMonthlyExpense(
            transactions = transactions,
            month = month-1,
            year = year
        )

        increment = (thisMonthExpense - lastMonthExpense) * (100 / lastMonthExpense)

        return round(increment * 100) / 100
    }
}