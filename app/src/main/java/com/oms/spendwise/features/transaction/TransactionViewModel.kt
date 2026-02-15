package com.oms.spendwise.features.transaction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.oms.spendwise.data.repository.CategoryRepository
import com.oms.spendwise.model.entity.Category
import kotlinx.coroutines.launch

class TransactionViewModel(
    val categoryRepository: CategoryRepository
): ViewModel() {
    var categories: List<Category> = emptyList()
        private set

    private suspend fun loadCategories(){
        categories = categoryRepository.getCategories()
        if(categories.isEmpty()){
            saveAllCategoriesInRoomDb()
            categories = categoryRepository.getCategories()
        }
    }

    private suspend fun saveAllCategoriesInRoomDb(){
        val defaultCategories = listOf(

            // -------------------- INCOME --------------------

            Category(0, "Salary", "income", "category_income_salary", "#2E7D32"),
            Category(0, "Bonus", "income", "category_income_bonus", "#388E3C"),
            Category(0, "Business", "income", "category_income_business", "#43A047"),
            Category(0, "Commission", "income", "category_income_commission", "#4CAF50"),
            Category(0, "Crypto", "income", "category_income_crypto", "#66BB6A"),
            Category(0, "Gift", "income", "category_income_gift", "#81C784"),
            Category(0, "Interest", "income", "category_income_interest", "#A5D6A7"),
            Category(0, "Investment", "income", "category_income_investment", "#2E7D32"),
            Category(0, "Prize", "income", "category_income_prize", "#388E3C"),
            Category(0, "Refund", "income", "category_income_refund", "#43A047"),
            Category(0, "Rent", "income", "category_income_rent", "#4CAF50"),
            Category(0, "Royalty", "income", "category_income_royality", "#66BB6A"),
            Category(0, "Cashback", "income", "category_income_cashback", "#81C784"),
            Category(0, "SideHustle", "income", "category_income_sidehustle", "#A5D6A7"),

            // -------------------- EXPENSE --------------------

            Category(0, "Accessory", "expense", "category_expense_accessory", "#C62828"),
            Category(0, "Books", "expense", "category_expense_books", "#D84315"),
            Category(0, "Donation", "expense", "category_expense_donation", "#E64A19"),
            Category(0, "Entertainment", "expense", "category_expense_entertainment", "#F4511E"),
            Category(0, "Fees", "expense", "category_expense_fees", "#BF360C"),
            Category(0, "Food", "expense", "category_expense_food", "#C62828"),
            Category(0, "Fuel", "expense", "category_expense_fuel", "#D84315"),
            Category(0, "Groceries", "expense", "category_expense_groceries", "#E64A19"),
            Category(0, "Gym", "expense", "category_expense_gym", "#F4511E"),
            Category(0, "Hobby", "expense", "category_expense_hobby", "#BF360C"),
            Category(0, "Household", "expense", "category_expense_household", "#C62828"),
            Category(0, "Insurance", "expense", "category_expense_insurance", "#D84315"),
            Category(0, "Internet", "expense", "category_expense_internet", "#E64A19"),
            Category(0, "Maintenance", "expense", "category_expense_maintenance", "#F4511E"),
            Category(0, "Medical", "expense", "category_expense_medical", "#BF360C"),
            Category(0, "Movie", "expense", "category_expense_movie", "#C62828"),
            Category(0, "Parking", "expense", "category_expense_parking", "#D84315"),
            Category(0, "Pharmacy", "expense", "category_expense_pharmacy", "#E64A19"),
            Category(0, "Phone", "expense", "category_expense_phone", "#F4511E"),
            Category(0, "Rent", "expense", "category_expense_rent", "#BF360C"),
            Category(0, "Repairs", "expense", "category_expense_repairs", "#C62828"),
            Category(0, "Shopping", "expense", "category_expense_shopping", "#D84315"),
            Category(0, "Subscription", "expense", "category_expense_subscription", "#E64A19"),
            Category(0, "Toll", "expense", "category_expense_toll", "#F4511E"),
            Category(0, "Transport", "expense", "category_expense_transport", "#BF360C"),
            Category(0, "Travel", "expense", "category_expense_travel", "#C62828"),
            Category(0, "Utilities", "expense", "category_expense_utilities", "#D84315")
        )
        categoryRepository.insertAll(defaultCategories)
    }

    init {
        viewModelScope.launch {
            loadCategories()
        }
    }
}