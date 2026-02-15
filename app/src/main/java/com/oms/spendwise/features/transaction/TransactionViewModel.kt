package com.oms.spendwise.features.transaction

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.oms.spendwise.data.repository.CategoryRepository
import com.oms.spendwise.model.entity.Category
import com.oms.spendwise.model.enum.TransactionType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TransactionViewModel @Inject constructor(
    val categoryRepository: CategoryRepository
): ViewModel() {
    var categories = mutableStateListOf<Category>()


    private suspend fun loadCategories(){
        categories.addAll(categoryRepository.getCategories())
        if(categories.isEmpty()){
            saveAllCategoriesInRoomDb()
            categories.addAll(categoryRepository.getCategories())
        }
        Log.d("TAG",categories.size.toString())
    }

    private suspend fun saveAllCategoriesInRoomDb(){
        val defaultCategories = listOf(
            Category(0, "Salary", TransactionType.INCOME.value, "category_income_salary", "#2E7D32"),
            Category(0, "Bonus", TransactionType.INCOME.value, "category_income_bonus", "#388E3C"),
            Category(0, "Business", TransactionType.INCOME.value, "category_income_business", "#43A047"),
            Category(0, "Commission", TransactionType.INCOME.value, "category_income_commission", "#4CAF50"),
            Category(0, "Crypto", TransactionType.INCOME.value, "category_income_crypto", "#66BB6A"),
            Category(0, "Gift", TransactionType.INCOME.value, "category_income_gift", "#81C784"),
            Category(0, "Interest", TransactionType.INCOME.value, "category_income_interest", "#A5D6A7"),
            Category(0, "Investment", TransactionType.INCOME.value, "category_income_investment", "#2E7D32"),
            Category(0, "Prize", TransactionType.INCOME.value, "category_income_prize", "#388E3C"),
            Category(0, "Refund", TransactionType.INCOME.value, "category_income_refund", "#43A047"),
            Category(0, "Rent", TransactionType.INCOME.value, "category_income_rent", "#4CAF50"),
            Category(0, "Royalty", TransactionType.INCOME.value, "category_income_royality", "#66BB6A"),
            Category(0, "Cashback", TransactionType.INCOME.value, "category_income_cashback", "#81C784"),
            Category(0, "SideHustle", TransactionType.INCOME.value, "category_income_sidehustle", "#A5D6A7"),
            Category(0, "Accessory", TransactionType.EXPENSE.value, "category_expense_accessory", "#C62828"),
            Category(0, "Books", TransactionType.EXPENSE.value, "category_expense_books", "#D84315"),
            Category(0, "Donation", TransactionType.EXPENSE.value, "category_expense_donation", "#E64A19"),
            Category(0, "Entertainment", TransactionType.EXPENSE.value, "category_expense_entertainment", "#F4511E"),
            Category(0, "Fees", TransactionType.EXPENSE.value, "category_expense_fees", "#BF360C"),
            Category(0, "Food", TransactionType.EXPENSE.value, "category_expense_food", "#C62828"),
            Category(0, "Fuel", TransactionType.EXPENSE.value, "category_expense_fuel", "#D84315"),
            Category(0, "Groceries", TransactionType.EXPENSE.value, "category_expense_groceries", "#E64A19"),
            Category(0, "Gym", TransactionType.EXPENSE.value, "category_expense_gym", "#F4511E"),
            Category(0, "Hobby", TransactionType.EXPENSE.value, "category_expense_hobby", "#BF360C"),
            Category(0, "Household", TransactionType.EXPENSE.value, "category_expense_household", "#C62828"),
            Category(0, "Insurance", TransactionType.EXPENSE.value, "category_expense_insurance", "#D84315"),
            Category(0, "Internet", TransactionType.EXPENSE.value, "category_expense_internet", "#E64A19"),
            Category(0, "Maintenance", TransactionType.EXPENSE.value, "category_expense_maintenance", "#F4511E"),
            Category(0, "Medical", TransactionType.EXPENSE.value, "category_expense_medical", "#BF360C"),
            Category(0, "Movie", TransactionType.EXPENSE.value, "category_expense_movie", "#C62828"),
            Category(0, "Parking", TransactionType.EXPENSE.value, "category_expense_parking", "#D84315"),
            Category(0, "Pharmacy", TransactionType.EXPENSE.value, "category_expense_pharmacy", "#E64A19"),
            Category(0, "Phone", TransactionType.EXPENSE.value, "category_expense_phone", "#F4511E"),
            Category(0, "Rent", TransactionType.EXPENSE.value, "category_expense_rent", "#BF360C"),
            Category(0, "Repairs", TransactionType.EXPENSE.value, "category_expense_repairs", "#C62828"),
            Category(0, "Shopping", TransactionType.EXPENSE.value, "category_expense_shopping", "#D84315"),
            Category(0, "Subscription", TransactionType.EXPENSE.value, "category_expense_subscription", "#E64A19"),
            Category(0, "Toll", TransactionType.EXPENSE.value, "category_expense_toll", "#F4511E"),
            Category(0, "Transport", TransactionType.EXPENSE.value, "category_expense_transport", "#BF360C"),
            Category(0, "Travel", TransactionType.EXPENSE.value, "category_expense_travel", "#C62828"),
            Category(0, "Utilities", TransactionType.EXPENSE.value, "category_expense_utilities", "#D84315")
        )
        categoryRepository.insertAll(defaultCategories.toMutableStateList())
    }

    init {
        viewModelScope.launch {
            loadCategories()
        }
    }
}