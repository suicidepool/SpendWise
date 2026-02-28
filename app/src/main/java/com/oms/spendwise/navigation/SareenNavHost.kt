package com.oms.spendwise.navigation

import android.content.Context
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.oms.spendwise.features.budget.BudgetScreen
import com.oms.spendwise.features.transaction.dashboard.DashboardScreen
import com.oms.spendwise.features.profile.ProfileViewModel
import com.oms.spendwise.features.profile.complete.CompleteProfileScreen
import com.oms.spendwise.features.profile.profile.ProfileScreen
import com.oms.spendwise.features.stats.StatsScreen
import com.oms.spendwise.features.transaction.TransactionViewModel
import com.oms.spendwise.features.transaction.add.AddTransactionScreen
import com.oms.spendwise.features.transaction.add.AddTransactionViewModel
import com.oms.spendwise.features.transaction.details.TransactionDetailsScreen
import com.oms.spendwise.features.transaction.history.TransactionHistoryScreen
import com.oms.spendwise.features.transaction.history.TransactionHistoryViewModel
import com.oms.spendwise.ui.theme.Dimens
import java.util.Currency

@Composable
fun ScreenNavHost(
    navController: NavHostController,
    profileViewModel: ProfileViewModel,
    transactionViewModel: TransactionViewModel,
    addTransactionViewModel: AddTransactionViewModel,
    transactionHistoryViewModel: TransactionHistoryViewModel,
    context: Context
) {

    NavHost(
        navController = navController,
        startDestination = Screen.DashboardScreen.route,
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
        ){
            StatsScreen()
        }

        composable(
            route = Screen.BudgetScreen.route
        ){
            BudgetScreen()
        }

        composable(
            route = Screen.ProfileScreen.route
        ){
            ProfileScreen()
        }
    }
}