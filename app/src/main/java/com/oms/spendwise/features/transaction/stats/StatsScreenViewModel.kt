package com.oms.spendwise.features.transaction.stats

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class StatsScreenViewModel @Inject constructor() : ViewModel() {

    var selectedTab by mutableStateOf(TopBarTab.MONTH)

    enum class TopBarTab(val value: String, val weight: Int) {
        DAY("Day", 1),
        WEEK("Week", 2),
        MONTH("Month", 3),
        YEAR("Year", 4)
    }

    val topBarTabs = listOf(
        TopBarTab.DAY,
        TopBarTab.WEEK,
        TopBarTab.MONTH,
        TopBarTab.YEAR,
    )

    fun onTopBarItemChange(
        topBarItem: TopBarTab,
    ){
        if(selectedTab != topBarItem){
            selectedTab = topBarItem
        }
    }
}