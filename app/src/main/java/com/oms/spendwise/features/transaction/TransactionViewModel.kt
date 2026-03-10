package com.oms.spendwise.features.transaction

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.util.fastCbrt
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.oms.spendwise.data.repository.CategoryRepository
import com.oms.spendwise.data.repository.TransactionRepository
import com.oms.spendwise.domain.TransactionCalculator
import com.oms.spendwise.model.entity.Category
import com.oms.spendwise.model.entity.Transaction
import com.oms.spendwise.model.enum.TransactionType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.YearMonth
import java.time.temporal.TemporalAdjusters
import javax.inject.Inject
import kotlin.math.abs

@HiltViewModel
class TransactionViewModel @Inject constructor(
    private val categoryRepository: CategoryRepository,
    private val transactionRepository: TransactionRepository,
    private val transactionCalculator: TransactionCalculator

): ViewModel() {
    var isLoading by mutableStateOf(true)
    var categories = mutableStateListOf<Category>()
    var transactions by mutableStateOf<Map<LocalDate, List<Transaction>>>(emptyMap())
        private set
    var distinctDates by mutableStateOf<List<LocalDate>>(emptyList())


    private suspend fun loadCategories(){
        categories.addAll(categoryRepository.getCategories())
        if(categories.isEmpty()){
            saveAllCategoriesInRoomDb()
            categories.addAll(categoryRepository.getCategories())
        }
    }

    fun getTransactionList(): List<Transaction> {
        return transactions.values.flatMap { it }
    }

    fun addTransaction(
        userId: Long,
        categoryId: Long,
        amount: Double,
        type: String,
        note: String = "",
        transactionDateTime: LocalDateTime,
        createdAt: LocalDateTime
    ){
        isLoading = true
        viewModelScope.launch {
            transactionRepository.addTransaction(
                userId = userId,
                categoryId = categoryId,
                amount = amount,
                type = type,
                note = note,
                transactionDateTime = transactionDateTime,
                createdAt = createdAt
            )
            isLoading = false
            loadTransactions()
        }
    }

    fun getTotalIncome(date: LocalDate): Double{
        var total = 0.0
        transactions[date]?.let {
            it.forEach {
                if(it.type == TransactionType.INCOME.value) total += it.amount
            }
        }
        return total
    }

    fun getTotalExpense(date: LocalDate): Double{
        var total = 0.0
        transactions[date]?.let {
            it.forEach { transaction ->
                if(transaction.type == TransactionType.EXPENSE.value) total += transaction.amount
            }
        }
        return total
    }

    fun editTransaction(transaction: Transaction){
        isLoading = true
        viewModelScope.launch {
            transactionRepository.updateTransaction(transaction)
            isLoading = false
            loadTransactions()
        }
    }

    fun deleteTransaction(transaction: Transaction){
        isLoading = true
        viewModelScope.launch {
            transactionRepository.deleteTransaction(transaction)
            isLoading = false
            loadTransactions()
        }
    }

    fun deleteAllTransactions(){
        viewModelScope.launch {
            transactionRepository.deleteAllTransactions()
            loadTransactions()
        }
    }

    fun loadTransactions(){
        isLoading = true
        viewModelScope.launch {
            val allTransactions = transactionRepository.getAllTransactions()
            transactions = allTransactions.groupBy { it.transactionDateTime.toLocalDate() }
            distinctDates = allTransactions
                .map { it.transactionDateTime.toLocalDate() }
                .distinct()
                .sortedDescending()
            isLoading = false
        }
    }

    suspend fun getTransaction(id: Long): Transaction{
        return transactionRepository.getTransaction(id)
    }

    fun getCategory(categoryId: Long): Category?{
        return categories.find { it.categoryId == categoryId }
    }

    fun getTotalBalance() : Double{
        return transactionCalculator.calculateTotalBalance(getTransactionList())
    }

    fun getMonthlyIncome(
        month: Int,
        year: Int
    ) : Double{
        return transactionCalculator.calculateMonthlyIncome(
            transactions = getTransactionList(),
            month = month,
            year = year
        )
    }

    fun getMonthlyExpense(
        month: Int,
        year: Int
    ) : Double{
        return transactionCalculator.calculateMonthlyExpense(
            transactions = getTransactionList(),
            month = month,
            year = year
        )
    }

    fun getIncomeIncrementFromLastMonth(
        month: Int,
        year: Int
    ): Double{
        return transactionCalculator.calculateIncomeIncrementFromLastMonth(
            transactions = getTransactionList(),
            month = month,
            year = year
        )
    }

    fun getExpenseIncrementFromLastMonth(
        month: Int,
        year: Int
    ): Double{
        return transactionCalculator.calculateExpenseIncrementFromLastMonth(
            transactions = getTransactionList(),
            month = month,
            year = year
        )
    }

    fun getBalanceIncrementFromLastMonth(): Double{
        return transactionCalculator.calculateBalanceIncrementFromLastMonth(getTransactionList())
    }

    fun getTodayTotalBalance(): Double{
        return transactionCalculator.calculateDayTotalBalance(getTransactionList())
    }

    fun getCurrentWeekTotalBalance(): Double{
        return transactionCalculator.calculateWeekTotalBalance(getTransactionList())
    }
    fun getCurrentMonthTotalBalance(): Double{
        return transactionCalculator.calculateMonthTotalBalance(getTransactionList())
    }

    fun getCurrentYearTotalBalance(): Double{
        return transactionCalculator.calculateYearTotalBalance(getTransactionList())
    }

    fun getDayBalanceIncrementFromLastDay(): Double{
        return transactionCalculator.calculateDayBalanceIncrementFromLastDay(getTransactionList())
    }

    fun getWeekBalanceIncrementFromLastWeek(): Double{
        return transactionCalculator.calculateWeekBalanceIncrementFromLastWeek(getTransactionList())
    }

    fun getMonthBalanceIncrementFromLastMonth(): Double{
        return transactionCalculator.calculateMonthBalanceIncrementFromLastMonth(getTransactionList())
    }

    fun getYearBalanceIncrementFromLastYear(): Double{
        return transactionCalculator.calculateYearBalanceIncrementFromLastYear(getTransactionList())
    }

    fun getDayTotalIncome() : Double{
        return transactionCalculator.calculateDayTotalIncome(getTransactionList())
    }

    fun getDayTotalIncomeFromLastDay() : Double{
        return  transactionCalculator.calculateDayTotalIncomeFromLastDay(getTransactionList())
    }

    fun getDayTotalExpense() : Double{
        return transactionCalculator.calculateDayTotalExpense(getTransactionList())
    }

    fun getDayTotalExpenseFromLastDay() : Double{
        return transactionCalculator.calculateDayTotalExpenseFromLastDay(getTransactionList())
    }

    fun getWeekTotalIncome(): Double{
        return transactionCalculator.calculateWeekTotalIncome(getTransactionList())
    }

    fun getWeekTotalIncomeFromLastWeek(): Double{
        return transactionCalculator.calculateWeekTotalIncomeFromLastWeek(getTransactionList())
    }

    fun getWeekTotalExpense(): Double{
        return transactionCalculator.calculateWeekTotalExpense(getTransactionList())
    }

    fun getWeekTotalExpenseFromLastWeek(): Double{
        return transactionCalculator.calculateWeekTotalExpenseFromLastWeek(getTransactionList())
    }

    fun getMonthTotalIncome(): Double{
        return transactionCalculator.calculateMonthTotalIncome(getTransactionList())
    }

    fun getMonthTotalIncome(
        month: Int,
        year: Int
    ): Double {
        return transactionCalculator.calculateMonthTotalIncome(
            transactions = getTransactionList(),
            month = month,
            year = year
        )
    }

    fun getMonthTotalIncomeFromLastMonth(): Double{
        return transactionCalculator.calculateMonthTotalIncomeFromLastMonth(getTransactionList())
    }

    fun getMonthTotalExpense(): Double{
        return transactionCalculator.calculateMonthTotalExpense(getTransactionList())
    }

    fun getMonthTotalExpense(
        month: Int,
        year: Int
    ): Double {
        return transactionCalculator.calculateMonthTotalExpense(
            transactions = getTransactionList(),
            month = month,
            year = year
        )
    }

    fun getMonthTotalExpenseFromLastMonth(): Double{
        return transactionCalculator.calculateMonthTotalExpenseFromLastMonth(getTransactionList())
    }

    fun getYearTotalIncome(): Double{
        return transactionCalculator.calculateYearTotalIncome(getTransactionList())
    }

    fun getYearTotalIncomeFromLastYear(): Double{
        return transactionCalculator.calculateYearTotalIncomeFromLastYear(getTransactionList())
    }

    fun getYearTotalExpense(): Double{
        return transactionCalculator.calculateYearTotalExpense(getTransactionList())
    }

    fun getYearTotalExpenseFromLastYear(): Double{
        return transactionCalculator.calculateYearTotalExpenseFromLastYear(getTransactionList())
    }

    fun getDayExpenseCategoryList(): List<Pair<Category, Double>>{
        return getTransactionList()
            .filter { it.transactionDateTime.toLocalDate() == LocalDate.now() && it.type == TransactionType.EXPENSE.value }
            .groupBy { it.categoryId }
            .map { (categoryId, list) ->
                categories.find { category ->
                    category.categoryId == categoryId
                }!! to list.sumOf { it.amount }
            }
            .sortedByDescending { it.second }
    }

    fun getWeekExpenseCategoryList(): List<Pair<Category, Double>>{
        val date = LocalDate.now()
        val firstDayOfWeek = date
            .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))

        val lastDayOfWeek = date
            .with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))
        return getTransactionList()
            .filter { it.transactionDateTime.toLocalDate() in firstDayOfWeek..lastDayOfWeek && it.type == TransactionType.EXPENSE.value }
            .groupBy { it.categoryId }
            .map { (categoryId, list) ->
                categories.find { category ->
                    category.categoryId == categoryId
                }!! to list.sumOf { it.amount }
            }
            .sortedByDescending { it.second }
    }

    fun getMonthExpenseCategoryList(): List<Pair<Category, Double>>{
        val date = LocalDate.now()
        val targetYearMonth = YearMonth.of(date.year, date.monthValue)
        return getTransactionList()
            .filter {
                val transactionYearMonth = YearMonth.from(it.transactionDateTime)
                targetYearMonth == transactionYearMonth && it.type == TransactionType.EXPENSE.value
            }
            .groupBy { it.categoryId }
            .map { (categoryId, list) ->
                categories.find { category ->
                    category.categoryId == categoryId
                }!! to list.sumOf { it.amount }
            }
            .sortedByDescending { it.second }
    }

    fun getYearExpenseCategoryList(): List<Pair<Category, Double>>{
        return getTransactionList()
            .filter { it.transactionDateTime.year == LocalDate.now().year && it.type == TransactionType.EXPENSE.value }
            .groupBy { it.categoryId }
            .map { (categoryId, list) ->
                categories.find { category ->
                    category.categoryId == categoryId
                }!! to list.sumOf { it.amount }
            }
            .sortedByDescending { it.second }
    }

    fun shortFormat(amount: Double): String {
        val df = DecimalFormat("#.##")
        val absValue = abs(amount)

        return when {
            absValue >= 1_000_000_000 -> "${df.format(amount / 1_000_000_000)}B"
            absValue >= 1_000_000 -> "${df.format(amount / 1_000_000)}M"
            absValue >= 1_000 -> "${df.format(amount / 1_000)}K"
            else -> df.format(amount)
        }
    }

//    fun loadTodayTransactions(){
//        isLoading = true
//        viewModelScope.launch {
//            val tempTransactions = transactionRepository.getAllTransactions(LocalDate.now())
//            transactions.addAll(tempTransactions)
//            isLoading = false
//        }
//    }

//    fun loadTransactions(date: LocalDate){
//        isLoading = true
//        viewModelScope.launch {
//            val tempTransactions = transactionRepository.getAllTransactions(date)
//            transactions.addAll(tempTransactions)
//            isLoading = false
//        }
//    }



    private suspend fun saveAllCategoriesInRoomDb(){
        val defaultCategories = listOf(
            Category(0, "Salary", TransactionType.INCOME.value, "category_income_salary", "3F51B5"),
            Category(0, "Bonus", TransactionType.INCOME.value, "category_income_bonus", "FF6F00"),
            Category(0, "Business", TransactionType.INCOME.value, "category_income_business", "009688"),
            Category(0, "Commission", TransactionType.INCOME.value, "category_income_commission", "8E24AA"),
            Category(0, "Crypto", TransactionType.INCOME.value, "category_income_crypto", "00ACC1"),
            Category(0, "Gift", TransactionType.INCOME.value, "category_income_gift", "F06292"),
            Category(0, "Interest", TransactionType.INCOME.value, "category_income_interest", "7CB342"),
            Category(0, "Investment", TransactionType.INCOME.value, "category_income_investment", "3949AB"),
            Category(0, "Prize", TransactionType.INCOME.value, "category_income_prize", "C2185B"),
            Category(0, "Refund", TransactionType.INCOME.value, "category_income_refund", "0288D1"),
            Category(0, "Rent", TransactionType.INCOME.value, "category_income_rent", "6D4C41"),
            Category(0, "Royalty", TransactionType.INCOME.value, "category_income_royality", "5E35B1"),
            Category(0, "Cashback", TransactionType.INCOME.value, "category_income_cashback", "00897B"),
            Category(0, "SideHustle", TransactionType.INCOME.value, "category_income_sidehustle", "F4511E"),
            Category(0, "Other", TransactionType.INCOME.value, "other", "455A74"),

            Category(0, "Accessory", TransactionType.EXPENSE.value, "category_expense_accessory", "D81B60"),
            Category(0, "Books", TransactionType.EXPENSE.value, "category_expense_books", "1E88E5"),
            Category(0, "Donation", TransactionType.EXPENSE.value, "category_expense_donation", "FB8C00"),
            Category(0, "Entertainment", TransactionType.EXPENSE.value, "category_expense_entertainment", "AB47BC"),
            Category(0, "Fees", TransactionType.EXPENSE.value, "category_expense_fees", "43A047"),
            Category(0, "Food", TransactionType.EXPENSE.value, "category_expense_food", "FF7043"),
            Category(0, "Fuel", TransactionType.EXPENSE.value, "category_expense_fuel", "5C6BC0"),
            Category(0, "Groceries", TransactionType.EXPENSE.value, "category_expense_groceries", "26C6DA"),
            Category(0, "Gym", TransactionType.EXPENSE.value, "category_expense_gym", "8D6E63"),
            Category(0, "Hobby", TransactionType.EXPENSE.value, "category_expense_hobby", "EC407A"),
            Category(0, "Household", TransactionType.EXPENSE.value, "category_expense_household", "7E57C2"),
            Category(0, "Insurance", TransactionType.EXPENSE.value, "category_expense_insurance", "00ACC1"),
            Category(0, "Internet", TransactionType.EXPENSE.value, "category_expense_internet", "FBC02D"),
            Category(0, "Maintenance", TransactionType.EXPENSE.value, "category_expense_maintenance", "6A1B9A"),
            Category(0, "Medical", TransactionType.EXPENSE.value, "category_expense_medical", "E53935"),
            Category(0, "Movie", TransactionType.EXPENSE.value, "category_expense_movie", "9CCC65"),
            Category(0, "Parking", TransactionType.EXPENSE.value, "category_expense_parking", "039BE5"),
            Category(0, "Pharmacy", TransactionType.EXPENSE.value, "category_expense_pharmacy", "C0CA33"),
            Category(0, "Phone", TransactionType.EXPENSE.value, "category_expense_phone", "3949AB"),
            Category(0, "Rent", TransactionType.EXPENSE.value, "category_expense_rent", "FF5722"),
            Category(0, "Repairs", TransactionType.EXPENSE.value, "category_expense_repairs", "00838F"),
            Category(0, "Shopping", TransactionType.EXPENSE.value, "category_expense_shopping", "AD1457"),
            Category(0, "Subscription", TransactionType.EXPENSE.value, "category_expense_subscription", "512DA8"),
            Category(0, "Toll", TransactionType.EXPENSE.value, "category_expense_toll", "FFA000"),
            Category(0, "Transport", TransactionType.EXPENSE.value, "category_expense_transport", "00796B"),
            Category(0, "Travel", TransactionType.EXPENSE.value, "category_expense_travel", "0288D1"),
            Category(0, "Utilities", TransactionType.EXPENSE.value, "category_expense_utilities", "455A64"),
            Category(0, "Other", TransactionType.EXPENSE.value, "other", "455A74"),

        )
        categoryRepository.insertAll(defaultCategories.toMutableStateList())
    }

    init {
        viewModelScope.launch {
            loadCategories()
        }
    }
}