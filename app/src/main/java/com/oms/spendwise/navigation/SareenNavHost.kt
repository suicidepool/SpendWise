package com.oms.spendwise.navigation

import android.content.Context
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.oms.spendwise.features.profile.ProfileViewModel
import com.oms.spendwise.features.profile.complete.CompleteProfileScreen
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
    profileViewModel: ProfileViewModel,
    transactionViewModel: TransactionViewModel,
    addTransactionViewModel: AddTransactionViewModel,
    transactionHistoryViewModel: TransactionHistoryViewModel,
    context: Context
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.TransactionHistoryScreen.route,
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
                context = context
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
    }
}