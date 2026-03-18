package com.oms.spendwise

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.oms.spendwise.data.repository.BudgetRepository
import com.oms.spendwise.features.app.App
import com.oms.spendwise.features.app.ScreenNavigationViewModel
import com.oms.spendwise.features.budget.BudgetViewModel
import com.oms.spendwise.features.profile.ProfileViewModel
import com.oms.spendwise.features.transaction.TransactionViewModel
import com.oms.spendwise.features.transaction.add.AddTransactionViewModel
import com.oms.spendwise.features.transaction.calendar.CalendarViewModel
import com.oms.spendwise.features.transaction.history.TransactionHistoryViewModel
import com.oms.spendwise.features.transaction.stats.StatsScreenViewModel
import com.oms.spendwise.navigation.Screen
import com.oms.spendwise.ui.theme.SpendWiseTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    lateinit var profileViewModel: ProfileViewModel
    lateinit var transactionViewModel: TransactionViewModel
    @Inject
    lateinit var budgetRepository: BudgetRepository
    lateinit var addTransactionViewModel: AddTransactionViewModel
    lateinit var transactionHistoryViewModel: TransactionHistoryViewModel
    lateinit var screenNavigationViewModel: ScreenNavigationViewModel
    lateinit var statsScreenViewModel: StatsScreenViewModel
    lateinit var calendarViewModel: CalendarViewModel
    lateinit var budgetViewModel: BudgetViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            profileViewModel = hiltViewModel()
            transactionViewModel = hiltViewModel()
            transactionViewModel.loadTransactions()
            addTransactionViewModel = hiltViewModel()
            transactionHistoryViewModel = hiltViewModel()
            screenNavigationViewModel = hiltViewModel()
            statsScreenViewModel = hiltViewModel()
            calendarViewModel = hiltViewModel()
            budgetViewModel = hiltViewModel()
            val context = applicationContext


            SpendWiseTheme {
                if(!profileViewModel.isLoading && profileViewModel.user == null){
                    navController.navigate(Screen.CompleteProfileScreen.route)
                }
                App(
                    navController = navController,
                    profileViewModel = profileViewModel,
                    transactionViewModel = transactionViewModel,
                    addTransactionViewModel = addTransactionViewModel,
                    transactionHistoryViewModel = transactionHistoryViewModel,
                    screenNavigationViewModel = screenNavigationViewModel,
                    statsScreenViewModel = statsScreenViewModel,
                    calendarViewModel = calendarViewModel,
                    budgetViewModel = budgetViewModel,
                    context = context
                )
            }
        }
    }
}