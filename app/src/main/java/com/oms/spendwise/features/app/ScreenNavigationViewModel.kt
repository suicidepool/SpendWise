package com.oms.spendwise.features.app

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.oms.spendwise.navigation.BottomNavigationItem
import com.oms.spendwise.navigation.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ScreenNavigationViewModel @Inject constructor() : ViewModel() {

    var selectedTab by mutableStateOf<BottomNavigationItem>(BottomNavigationItem.Dashboard)

    val containingBottomBarScreenRoutes = listOf(
        Screen.DashboardScreen.route,
        Screen.StatsScreen.route,
        Screen.BudgetScreen.route,
        Screen.ProfileScreen.route,
        Screen.TransactionHistoryScreen.route,
        Screen.CalendarScreen.route
    )

    fun switchSelectedTab(bottomNavigationItem: BottomNavigationItem){
        selectedTab = bottomNavigationItem
    }

}