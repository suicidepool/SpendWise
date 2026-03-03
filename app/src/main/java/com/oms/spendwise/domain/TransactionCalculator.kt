package com.oms.spendwise.domain

import android.util.Log
import com.oms.spendwise.model.entity.Transaction
import com.oms.spendwise.model.enum.TransactionType
import java.time.LocalDate
import javax.inject.Inject
import kotlin.math.round
import java.time.*
import java.time.temporal.*

class TransactionCalculator @Inject constructor(){

    fun calculateTotalBalance(
        transactions: List<Transaction>,
        month: Int = LocalDate.now().monthValue,
        year: Int = LocalDate.now().year
    ): Double {
        val targetYearMonth = YearMonth.of(year, month)
        var sum = 0.0
        transactions.forEach { transaction ->
            val transactionYearMonth = YearMonth.from(transaction.transactionDateTime)
            if(transactionYearMonth <= targetYearMonth){
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
        val targetYearMonth = YearMonth.of(year, month)
        transactions.forEach { transaction ->
            val transactionYearMonth = YearMonth.from(transaction.transactionDateTime)
            if(transaction.type == TransactionType.INCOME.value && transactionYearMonth == targetYearMonth){
                income += transaction.amount
            }
        }
        return income
    }

    fun calculateBalanceIncrementFromLastMonth(
        transactions: List<Transaction>
    ): Double{
        val currentMonthTotalBalance = calculateTotalBalance(transactions)
        val lastMonth = LocalDate.now().minusMonths(1)
        val prevMonthTotalBalance = calculateTotalBalance(transactions, lastMonth.monthValue,
            lastMonth.year)
        val increment = (currentMonthTotalBalance-prevMonthTotalBalance) * (100 / prevMonthTotalBalance)
        return round(increment * 100) / 100
    }

    fun calculateMonthlyExpense(
        transactions: List<Transaction>,
        month: Int,
        year: Int
    ): Double{
        var expense = 0.0
        val targetYearMonth = YearMonth.of(year, month)
        transactions.forEach { transaction ->
            val transactionYearMonth = YearMonth.from(transaction.transactionDateTime)
            if(transaction.type == TransactionType.EXPENSE.value && transactionYearMonth == targetYearMonth){
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
        val lastMonth = LocalDate.of(year, month, 1).minusMonths(1)
        val lastMonthIncome = calculateMonthlyIncome(
            transactions = transactions,
            month = lastMonth.monthValue,
            year = lastMonth.year
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
        val lastMonth = LocalDate.of(year, month, 1).minusMonths(1)
        val lastMonthExpense = calculateMonthlyExpense(
            transactions = transactions,
            month = lastMonth.monthValue,
            year = lastMonth.year
        )

        increment = (thisMonthExpense - lastMonthExpense) * (100 / lastMonthExpense)

        return round(increment * 100) / 100
    }

    fun calculateDayTotalBalance(
        transactions: List<Transaction>,
        date: LocalDate = LocalDate.now()
    ): Double{
        var balance = 0.0
        transactions.forEach { transaction ->
            if(transaction.transactionDateTime.toLocalDate() == date){
                if(transaction.type == TransactionType.INCOME.value)
                    balance += transaction.amount
                else
                    balance -= transaction.amount
            }
        }
        return balance
    }

    fun calculateDayBalanceIncrementFromLastDay(
        transactions: List<Transaction>
    ): Double{
        val current = calculateDayTotalBalance(transactions)
        val prev = calculateDayTotalBalance(transactions, LocalDate.now().minusDays(1))
        val increment = (current-prev) * (100 / prev)
        return round(increment * 100) / 100
    }

    fun calculateDayTotalIncome(
        transactions: List<Transaction>,
        date: LocalDate = LocalDate.now()
    ): Double{
        var income = 0.0
        transactions.forEach { transaction ->
            if(transaction.type == TransactionType.INCOME.value && transaction.transactionDateTime.toLocalDate() == date){
                income += transaction.amount
            }
        }
        return income
    }

    fun calculateDayTotalIncomeFromLastDay(
        transactions: List<Transaction>
    ): Double{
        val current = calculateDayTotalIncome(transactions)
        val prev = calculateDayTotalIncome(transactions, LocalDate.now().minusDays(1))
        val increment = (current-prev) * (100 / prev)
        return round(increment * 100) / 100
    }

    fun calculateDayTotalExpense(
        transactions: List<Transaction>,
        date: LocalDate = LocalDate.now()
    ): Double{
        var expense = 0.0
        transactions.forEach { transaction ->
            if(transaction.type == TransactionType.EXPENSE.value && transaction.transactionDateTime.toLocalDate() == date){
                expense += transaction.amount
            }
        }
        return expense
    }

    fun calculateDayTotalExpenseFromLastDay(
        transactions: List<Transaction>
    ): Double{
        val current = calculateDayTotalExpense(transactions)
        val prev = calculateDayTotalExpense(transactions, LocalDate.now().minusDays(1))
        val increment = (current-prev) * (100 / prev)
        return round(increment * 100) / 100
    }


    fun calculateWeekTotalBalance(
        transactions: List<Transaction>,
        date: LocalDate = LocalDate.now()
    ): Double{
        var balance = 0.0
        val firstDayOfWeek = date
            .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))

        val lastDayOfWeek = date
            .with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))

        transactions.forEach { transaction ->
            val transactionDate = transaction.transactionDateTime.toLocalDate()
            if(transactionDate in firstDayOfWeek..lastDayOfWeek){
                if(transaction.type == TransactionType.INCOME.value)
                    balance += transaction.amount
                else
                    balance -= transaction.amount
            }
        }
        return balance
    }

    fun calculateWeekBalanceIncrementFromLastWeek(
        transactions: List<Transaction>
    ): Double{
        val current = calculateWeekTotalBalance(transactions)
        val prev = calculateWeekTotalBalance(transactions, LocalDate.now().minusDays(7))
        val increment = (current-prev) * (100 / prev)
        return round(increment * 100) / 100
    }

    fun calculateWeekTotalIncome(
        transactions: List<Transaction>,
        date: LocalDate = LocalDate.now()
    ): Double{
        var income = 0.0
        val firstDayOfWeek = date
            .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))

        val lastDayOfWeek = date
            .with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))

        transactions.forEach { transaction ->
            val transactionDate = transaction.transactionDateTime.toLocalDate()
            if(transaction.type == TransactionType.INCOME.value && transactionDate in firstDayOfWeek..lastDayOfWeek){
                income += transaction.amount
            }
        }
        return income
    }

    fun calculateWeekTotalIncomeFromLastWeek(
        transactions: List<Transaction>
    ): Double{
        val current = calculateWeekTotalIncome(transactions)
        val prev = calculateWeekTotalIncome(transactions, LocalDate.now().minusDays(7))
        val increment = (current-prev) * (100 / prev)
        return round(increment * 100) / 100
    }

    fun calculateWeekTotalExpense(
        transactions: List<Transaction>,
        date: LocalDate = LocalDate.now()
    ): Double{
        var expense = 0.0
        val firstDayOfWeek = date
            .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))

        val lastDayOfWeek = date
            .with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))

        transactions.forEach { transaction ->
            val transactionDate = transaction.transactionDateTime.toLocalDate()
            if(transaction.type == TransactionType.EXPENSE.value && transactionDate in firstDayOfWeek..lastDayOfWeek){
                expense += transaction.amount
            }
        }
        return expense
    }

    fun calculateWeekTotalExpenseFromLastWeek(
        transactions: List<Transaction>
    ): Double{
        val current = calculateWeekTotalExpense(transactions)
        val prev = calculateWeekTotalExpense(transactions, LocalDate.now().minusDays(7))
        val increment = (current-prev) * (100 / prev)
        return round(increment * 100) / 100
    }

    fun calculateMonthTotalBalance(
        transactions: List<Transaction>,
        month: Int = LocalDate.now().monthValue,
        year: Int = LocalDate.now().year
    ): Double{
        var balance = 0.0
        val targetYearMonth = YearMonth.of(year, month)
        transactions.forEach { transaction ->
            val transactionYearMonth = YearMonth.from(transaction.transactionDateTime)
            if(transactionYearMonth == targetYearMonth){
                if(transaction.type == TransactionType.INCOME.value)
                    balance += transaction.amount
                else
                    balance -= transaction.amount
            }
        }
        return balance
    }

    fun calculateMonthBalanceIncrementFromLastMonth(
        transactions: List<Transaction>
    ): Double{
        val current = calculateMonthTotalBalance(transactions)
        val prevMonth = LocalDate.now().minusMonths(1)
        val prev = calculateMonthTotalBalance(transactions, prevMonth.monthValue, prevMonth.year )
        val increment = (current-prev) * (100 / prev)
        return round(increment * 100) / 100
    }

    fun calculateMonthTotalIncome(
        transactions: List<Transaction>,
        month: Int = LocalDate.now().monthValue,
        year: Int = LocalDate.now().year
    ): Double{
        var amount = 0.0
        val targetYearMonth = YearMonth.of(year, month)
        transactions.forEach { transaction ->
            val transactionYearMonth = YearMonth.from(transaction.transactionDateTime)
            if(transactionYearMonth == targetYearMonth){
                if(transaction.type == TransactionType.INCOME.value)
                    amount += transaction.amount
            }
        }
        return amount
    }

    fun calculateMonthTotalIncomeFromLastMonth(
        transactions: List<Transaction>
    ): Double{
        val current = calculateMonthTotalIncome(transactions)
        val prevMonth = LocalDate.now().minusMonths(1)
        val prev = calculateMonthTotalIncome(transactions, prevMonth.monthValue, prevMonth.year )
        val increment = (current-prev) * (100 / prev)
        return round(increment * 100) / 100
    }

    fun calculateMonthTotalExpense(
        transactions: List<Transaction>,
        month: Int = LocalDate.now().monthValue,
        year: Int = LocalDate.now().year
    ): Double{
        var amount = 0.0
        val targetYearMonth = YearMonth.of(year, month)
        transactions.forEach { transaction ->
            val transactionYearMonth = YearMonth.from(transaction.transactionDateTime)
            if(transactionYearMonth == targetYearMonth){
                if(transaction.type == TransactionType.EXPENSE.value)
                    amount += transaction.amount
            }
        }
        return amount
    }

    fun calculateMonthTotalExpenseFromLastMonth(
        transactions: List<Transaction>
    ): Double{
        val current = calculateMonthTotalExpense(transactions)
        val prevMonth = LocalDate.now().minusMonths(1)
        val prev = calculateMonthTotalExpense(transactions, prevMonth.monthValue, prevMonth.year )
        val increment = (current-prev) * (100 / prev)
        return round(increment * 100) / 100
    }

    fun calculateYearTotalBalance(
        transactions: List<Transaction>,
        year: Int = LocalDate.now().year
    ): Double{
        var balance = 0.0
        transactions.forEach { transaction ->
            if(transaction.transactionDateTime.year == year){
                if(transaction.type == TransactionType.INCOME.value)
                    balance += transaction.amount
                else
                    balance -= transaction.amount
            }
        }
        return balance
    }

    fun calculateYearBalanceIncrementFromLastYear(
        transactions: List<Transaction>
    ): Double{
        val current = calculateYearTotalBalance(transactions)
        val prev = calculateYearTotalBalance(transactions, LocalDate.now().year-1 )
        val increment = (current-prev) * (100 / prev)
        return round(increment * 100) / 100
    }

    fun calculateYearTotalIncome(
        transactions: List<Transaction>,
        year: Int = LocalDate.now().year
    ): Double{
        var income = 0.0
        transactions.forEach { transaction ->
            if(transaction.type == TransactionType.INCOME.value && transaction.transactionDateTime.year == year){
                income += transaction.amount
            }
        }
        return income
    }

    fun calculateYearTotalIncomeFromLastYear(
        transactions: List<Transaction>
    ): Double{
        val current = calculateYearTotalIncome(transactions)
        val prev = calculateYearTotalIncome(transactions, LocalDate.now().year-1 )
        val increment = (current-prev) * (100 / prev)
        return round(increment * 100) / 100
    }

    fun calculateYearTotalExpense(
        transactions: List<Transaction>,
        year: Int = LocalDate.now().year
    ): Double{
        var expense = 0.0
        transactions.forEach { transaction ->
            if(transaction.type == TransactionType.EXPENSE.value && transaction.transactionDateTime.year == year){
                expense += transaction.amount
            }
        }
        return expense
    }

    fun calculateYearTotalExpenseFromLastYear(
        transactions: List<Transaction>
    ): Double{
        val current = calculateYearTotalExpense(transactions)
        val prev = calculateYearTotalExpense(transactions, LocalDate.now().year-1 )
        val increment = (current-prev) * (100 / prev)
        return round(increment * 100) / 100
    }
}