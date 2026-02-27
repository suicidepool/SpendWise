package com.oms.spendwise

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.room.util.TableInfo
import coil.compose.AsyncImage
import coil.size.Dimension
import com.oms.spendwise.data.repository.BudgetRepository
import com.oms.spendwise.features.app.App
import com.oms.spendwise.features.app.ScreenNavigationViewModel
import com.oms.spendwise.features.profile.ProfileViewModel
import com.oms.spendwise.features.profile.complete.CompleteProfileScreen
import com.oms.spendwise.features.transaction.TransactionViewModel
import com.oms.spendwise.features.transaction.add.AddTransactionScreen
import com.oms.spendwise.features.transaction.add.AddTransactionViewModel
import com.oms.spendwise.features.transaction.details.TransactionDetailsScreen
import com.oms.spendwise.features.transaction.history.TransactionHistoryScreen
import com.oms.spendwise.features.transaction.history.TransactionHistoryViewModel
import com.oms.spendwise.model.enum.TransactionType
import com.oms.spendwise.navigation.BottomNavigationItem
import com.oms.spendwise.navigation.Screen
import com.oms.spendwise.navigation.ScreenNavHost
import com.oms.spendwise.ui.theme.Dimens
import com.oms.spendwise.ui.theme.SpendWiseTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.Currency
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
                    context = context
                )
            }
        }
    }
}