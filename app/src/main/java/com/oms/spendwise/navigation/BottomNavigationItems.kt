package com.oms.spendwise.navigation

import androidx.annotation.DrawableRes
import com.oms.spendwise.R
import okhttp3.Route

sealed class BottomNavigationItem (val name: String, val icon: Int, val route: String) {
    object Dashboard : BottomNavigationItem(
        name = "Dashboard",
        icon = R.drawable.icon_dashboard,
        route = Screen.DashboardScreen.route
    )
    object Stats : BottomNavigationItem(
        name = "Stats",
        icon = R.drawable.icon_stats,
        route = Screen.StatsScreen.route
    )
    object Add : BottomNavigationItem(
        name = "Add",
        icon = R.drawable.icon_add,
        route = Screen.AddTransactionScreen.createRoute(-1)
    )
    object Budget : BottomNavigationItem(
        name = "Budget",
        icon = R.drawable.icon_budget,
        route = Screen.BudgetScreen.route
    )
    object Profile : BottomNavigationItem(
        name = "Profile",
        icon = R.drawable.icon_profile,
        route = Screen.ProfileScreen.route
    )
}