package com.oms.spendwise.navigation

import android.content.Context
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.oms.spendwise.features.budget.BudgetScreen
import com.oms.spendwise.features.budget.BudgetViewModel
import com.oms.spendwise.features.budget.add.SetBudgetScreen
import com.oms.spendwise.features.transaction.dashboard.DashboardScreen
import com.oms.spendwise.features.profile.ProfileViewModel
import com.oms.spendwise.features.profile.complete.CompleteProfileScreen
import com.oms.spendwise.features.profile.edit.EditProfileScreen
import com.oms.spendwise.features.profile.profile.ProfileScreen
import com.oms.spendwise.features.transaction.stats.StatsScreen
import com.oms.spendwise.features.transaction.TransactionViewModel
import com.oms.spendwise.features.transaction.add.AddTransactionScreen
import com.oms.spendwise.features.transaction.add.AddTransactionViewModel
import com.oms.spendwise.features.transaction.calendar.CalendarScreen
import com.oms.spendwise.features.transaction.calendar.CalendarViewModel
import com.oms.spendwise.features.transaction.calendar.DateDetailsScreen
import com.oms.spendwise.features.transaction.details.TransactionDetailsScreen
import com.oms.spendwise.features.transaction.history.TransactionHistoryScreen
import com.oms.spendwise.features.transaction.history.TransactionHistoryViewModel
import com.oms.spendwise.features.transaction.stats.StatsScreenViewModel
import com.oms.spendwise.model.enum.TransactionType
import com.oms.spendwise.ui.theme.Dimens
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.Currency

@Composable
fun ScreenNavHost(
    navController: NavHostController,
    profileViewModel: ProfileViewModel,
    transactionViewModel: TransactionViewModel,
    addTransactionViewModel: AddTransactionViewModel,
    transactionHistoryViewModel: TransactionHistoryViewModel,
    statsScreenViewModel: StatsScreenViewModel,
    calendarViewModel: CalendarViewModel,
    budgetViewModel: BudgetViewModel,
    context: Context
) {

    val startDestinationScreenRoute = remember { mutableStateOf(Screen.DashboardScreen.route) }
    if(!profileViewModel.isLoading && profileViewModel.user == null)
        startDestinationScreenRoute.value = Screen.CompleteProfileScreen.route

    NavHost(
        navController = navController,
        startDestination = startDestinationScreenRoute.value,
        enterTransition = {
            slideIntoContainer(
                AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween(200)
            )
        },
        exitTransition = {
            slideOutOfContainer(
                AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween(200)
            )
        },
        popEnterTransition = {
            slideIntoContainer(
                AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(200)
            )
        },
        popExitTransition = {
            slideOutOfContainer(
                AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(200)
            )
        }
    ){
        composable(
            route = Screen.CompleteProfileScreen.route
        ){
            CompleteProfileScreen(
                profileViewModel = profileViewModel,
                context = context,
                onContinue = {
                    navController.navigate(Screen.TransactionHistoryScreen.route)
                }
            )
        }

        composable(
            route = Screen.AddTransactionScreen.route,
            arguments = listOf(
                navArgument(
                    name = "transactionId"
                ){
                    type = NavType.LongType
                }
            )
        ){ backStackEntry ->
            val id = backStackEntry.arguments?.getLong("transactionId")
            AddTransactionScreen(
                addTransactionViewModel = addTransactionViewModel,
                context = context,
                transactionViewModel = transactionViewModel,
                profileViewModel = profileViewModel,
                transactionId = id,
                onBack = {navController.popBackStack()}
            )
        }

        composable(
            route = Screen.TransactionHistoryScreen.route
        ){
            if(profileViewModel.user != null)
                TransactionHistoryScreen(
                    modifier = Modifier.padding(horizontal = Dimens.HorizontalScreenPadding),
                    transactionVM = transactionViewModel,
                    currency = Currency.getInstance(profileViewModel.user!!.currency),
                    context = context,
                    onItemClick = { transactionId ->
                        navController.navigate(Screen.TransactionDetailsScreen.createRoute(transactionId))
                    },
                    transactionHistoryVm = transactionHistoryViewModel
                )
        }

        composable(
            route = Screen.TransactionDetailsScreen.route,
            arguments = listOf(
                navArgument(
                    name = "transactionId"
                ){
                    type = NavType.LongType
                }
            )
        ){ backStackEntry ->
            val id = backStackEntry.arguments?.getLong("transactionId")
            TransactionDetailsScreen(
                transactionId = id!!,
                transactionVM = transactionViewModel,
                currency = Currency.getInstance(profileViewModel.user!!.currency),
                onBack = {navController.popBackStack()},
                onEditClick = {navController.navigate(Screen.AddTransactionScreen.createRoute(id))},
                context = context
            )
        }

        composable(
            route = Screen.DashboardScreen.route
        ){
            if(profileViewModel.user != null)
                DashboardScreen(
                    transactionViewModel = transactionViewModel,
                    currency =  Currency.getInstance(profileViewModel.user!!.currency),
                    onTransactionItemClick = { id ->
                        navController.navigate(Screen.TransactionDetailsScreen.createRoute(id))
                    },
                    onSeeAllClick = {
                        navController.navigate(Screen.TransactionHistoryScreen.route)
                    },
                    context = context
                )
        }

        composable(
            route = Screen.StatsScreen.route
        ) {
            if (profileViewModel.user != null)
                StatsScreen(
                    currency = Currency.getInstance(profileViewModel.user!!.currency),
                    statsScreenViewModel = statsScreenViewModel,
                    transactionViewModel = transactionViewModel,
                    onCalendarClick = {
                        navController.navigate(Screen.CalendarScreen.route)
                    }
                )
        }

        composable(
            route = Screen.CalendarScreen.route
        ){
            if (profileViewModel.user != null)
                CalendarScreen(
                    calendarViewModel = calendarViewModel,
                    transactionVM = transactionViewModel,
                    onBack = {
                        navController.popBackStack()
                    },
                    currency = Currency.getInstance(profileViewModel.user!!.currency),
                    onDateClick = {date: LocalDate ->
                        navController.navigate(Screen.DateDetailsScreen.createRoute(date.toString()))
                    }
                )
        }

        composable(
            route = Screen.BudgetScreen.route
        ){
            BudgetScreen()
        }

        composable(
            route = Screen.ProfileScreen.route
        ){
            ProfileScreen(
                profileVM = profileViewModel,
                onEditProfileClick = {
                    if(profileViewModel.user != null)
                        navController.navigate(Screen.EditProfileScreen.route)
                },
                resetAllData = {
                    transactionViewModel.deleteAllTransactions()
                    profileViewModel.resetAllData()
                    navController.navigate(Screen.CompleteProfileScreen.route)
                }
            )
        }

        composable(
            route = Screen.DateDetailsScreen.route,
            arguments = listOf(
                navArgument(
                    name = "dateString"
                ){
                    type = NavType.StringType
                }
            )
        ){ backStackEntry ->
            val dateString = backStackEntry.arguments?.getString("dateString")
            val date = LocalDate.parse(dateString)
            if (profileViewModel.user != null)
                DateDetailsScreen(
                    date = date,
                    onBack = {
                        navController.popBackStack()
                    },
                    transactionViewModel = transactionViewModel,
                    context = context,
                    currency = Currency.getInstance(profileViewModel.user!!.currency),
                    onItemClick = { id ->
                        navController.navigate(Screen.TransactionDetailsScreen.createRoute(id))
                    },
                    onAddTransactionClick = { date ->
                        addTransactionViewModel.onTransactionDateTimeChange(LocalDateTime.now().with(date))
                        navController.navigate(Screen.AddTransactionScreen.createRoute(-1L))
                    }
                )
        }

        composable(
            route = Screen.EditProfileScreen.route
        ){
            EditProfileScreen(
                onSave = {
                    navController.popBackStack()
                },
                profileViewModel = profileViewModel,
                onBack = {
                    navController.popBackStack()
                },
                context = context,
            )
        }

        composable(
            route = Screen.SetBudgetScreen.route
        ){
            if(profileViewModel.user != null)
                SetBudgetScreen(
                    budgetVM = budgetViewModel,
                    userId = profileViewModel.user!!.userId,
                    currency = Currency.getInstance(profileViewModel.user!!.currency),
                    categories = transactionViewModel.categories.filter { it.type == TransactionType.EXPENSE.value },
                    context = context,
                    onBack = {
                        navController.popBackStack()
                    }
                )
        }
    }
}