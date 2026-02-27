package com.oms.spendwise.navigation

sealed class Screen(val route: String) {
    object CompleteProfileScreen : Screen("completeProfileScreen")
    object AddTransactionScreen : Screen("addTransactionScreen/{transactionId}"){
        fun createRoute(transactionId: Long) = "addTransactionScreen/$transactionId"
    }
    object TransactionHistoryScreen : Screen("transactionHistoryScreen")
    object TransactionDetailsScreen : Screen("transactionDetailsScreen/{transactionId}"){
        fun createRoute(transactionId: Long) = "transactionDetailsScreen/$transactionId"
    }
}