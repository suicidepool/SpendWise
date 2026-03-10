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
    object DashboardScreen : Screen("dashboardScreen")
    object StatsScreen : Screen("statsScreen")
    object BudgetScreen : Screen("budgetScreen")
    object ProfileScreen : Screen("profileScreen")
    object CalendarScreen: Screen("calendarScreen")
    object DateDetailsScreen: Screen("dateDetailsScreen/{dateString}"){
        fun createRoute(dateString: String) = "dateDetailsScreen/$dateString"
    }
    object EditProfileScreen: Screen("editProfileScreen")
}