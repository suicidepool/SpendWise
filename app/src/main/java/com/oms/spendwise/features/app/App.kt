package com.oms.spendwise.features.app

import android.content.Context
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.oms.spendwise.features.profile.ProfileViewModel
import com.oms.spendwise.features.transaction.TransactionViewModel
import com.oms.spendwise.features.transaction.add.AddTransactionViewModel
import com.oms.spendwise.features.transaction.history.TransactionHistoryViewModel
import com.oms.spendwise.navigation.BottomNavigationItem
import com.oms.spendwise.navigation.ScreenNavHost
import com.oms.spendwise.ui.theme.BackgroundPrimary
import com.oms.spendwise.ui.theme.Dimens
import com.oms.spendwise.ui.theme.PrimaryBlue
import com.oms.spendwise.ui.theme.PrimaryBlueLight
import com.oms.spendwise.ui.theme.TextPrimary
import kotlinx.coroutines.launch

@Composable
fun App(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    profileViewModel: ProfileViewModel,
    transactionViewModel: TransactionViewModel,
    addTransactionViewModel: AddTransactionViewModel,
    transactionHistoryViewModel: TransactionHistoryViewModel,
    screenNavigationViewModel: ScreenNavigationViewModel,
    context: Context,
) {
    val scope = rememberCoroutineScope()


    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val current = navBackStackEntry?.destination?.route
    LaunchedEffect(current) {
        when(current){
            BottomNavigationItem.Dashboard.route -> screenNavigationViewModel.switchSelectedTab(BottomNavigationItem.Dashboard)
            BottomNavigationItem.Stats.route -> screenNavigationViewModel.switchSelectedTab(BottomNavigationItem.Stats)
            BottomNavigationItem.Budget.route -> screenNavigationViewModel.switchSelectedTab(BottomNavigationItem.Budget)
            BottomNavigationItem.Profile.route -> screenNavigationViewModel.switchSelectedTab(BottomNavigationItem.Profile)
        }
    }



    Box(
        modifier = modifier
            .fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        ScreenNavHost(
            navController = navController,
            profileViewModel = profileViewModel,
            transactionViewModel = transactionViewModel,
            addTransactionViewModel = addTransactionViewModel,
            transactionHistoryViewModel = transactionHistoryViewModel,
            context = context
        )

        if(screenNavigationViewModel.containingBottomBarScreenRoutes.contains(navBackStackEntry?.destination?.route)){
            BottomBar(
                screenNavigationVM = screenNavigationViewModel,
                switchScreen = { route ->
                    scope.launch {
                        navController.navigate(route)
                    }
                },
                navBackStackEntry = navBackStackEntry,
                popBackStack = {
                    navController.popBackStack(
                        navController.graph.startDestinationId,
                        true
                    )
                }
            )
        }
    }
}

@Composable
private fun BottomBar(
    modifier: Modifier = Modifier,
    screenNavigationVM: ScreenNavigationViewModel,
    switchScreen: (route: String) -> Unit,
    navBackStackEntry: NavBackStackEntry?,
    popBackStack: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding(),
    ) {
        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            thickness = 0.7.dp,
            color = Color.LightGray.copy(alpha = 0.5f)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .background(color = Color.White),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomNavigationItem(
                modifier = Modifier.weight(1f),
                isSelected = screenNavigationVM.selectedTab.route == BottomNavigationItem.Dashboard.route,
                icon = BottomNavigationItem.Dashboard.icon,
                name = BottomNavigationItem.Dashboard.name,
                onClick = {
                    if(screenNavigationVM.selectedTab.route != navBackStackEntry?.destination?.route || screenNavigationVM.selectedTab != BottomNavigationItem.Dashboard){
                        screenNavigationVM.switchSelectedTab(BottomNavigationItem.Dashboard)
                        switchScreen(BottomNavigationItem.Dashboard.route)
                        popBackStack()
                    }
                }
            )
            BottomNavigationItem(
                modifier = Modifier.weight(1f),
                isSelected = screenNavigationVM.selectedTab.route == BottomNavigationItem.Stats.route,
                icon = BottomNavigationItem.Stats.icon,
                name = BottomNavigationItem.Stats.name,
                onClick = {
                    if(screenNavigationVM.selectedTab.route != navBackStackEntry?.destination?.route || screenNavigationVM.selectedTab != BottomNavigationItem.Stats) {
                        screenNavigationVM.switchSelectedTab(BottomNavigationItem.Stats)
                        switchScreen(BottomNavigationItem.Stats.route)
                    }
                }
            )
            Box(Modifier.weight(1f))
            BottomNavigationItem(
                modifier = Modifier.weight(1f),
                isSelected = screenNavigationVM.selectedTab.route == BottomNavigationItem.Budget.route,
                icon = BottomNavigationItem.Budget.icon,
                name = BottomNavigationItem.Budget.name,
                onClick = {
                    if(screenNavigationVM.selectedTab.route != navBackStackEntry?.destination?.route || screenNavigationVM.selectedTab != BottomNavigationItem.Budget) {
                        screenNavigationVM.switchSelectedTab(BottomNavigationItem.Budget)
                        switchScreen(BottomNavigationItem.Budget.route)
                    }
                }
            )

            BottomNavigationItem(
                modifier = Modifier.weight(1f),
                isSelected = screenNavigationVM.selectedTab.route == BottomNavigationItem.Profile.route,
                icon = BottomNavigationItem.Profile.icon,
                name = BottomNavigationItem.Profile.name,
                onClick = {
                    if(screenNavigationVM.selectedTab.route != navBackStackEntry?.destination?.route || screenNavigationVM.selectedTab != BottomNavigationItem.Profile) {
                        screenNavigationVM.switchSelectedTab(BottomNavigationItem.Profile)
                        switchScreen(BottomNavigationItem.Profile.route)
                    }
                }
            )
        }

    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .height(92.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        Box(
            modifier = Modifier
                .background(
                    color = PrimaryBlue,
                    shape = RoundedCornerShape(Dimens.CardCornerRadius)
                )
                .size(52.dp)
                .shadow(
                    elevation = 8.dp,
                    spotColor = PrimaryBlueLight,
                    ambientColor = PrimaryBlueLight,
                    shape = RoundedCornerShape(Dimens.CardCornerRadius)
                )
                .clickable(
                    onClick = {
                        switchScreen(BottomNavigationItem.Add.route)
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        color = PrimaryBlue,
                        shape = RoundedCornerShape(Dimens.CardCornerRadius)
                    ),
                contentAlignment = Alignment.Center
            ){
                Icon(
                    painter = painterResource(BottomNavigationItem.Add.icon),
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = BackgroundPrimary
                )
            }
        }
    }
}

@Composable
private fun BottomNavigationItem(
    modifier: Modifier = Modifier,
    isSelected: Boolean,
    @DrawableRes icon: Int,
    name: String,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = null,
            modifier = Modifier
                .size(18.dp),
            tint = if(isSelected) PrimaryBlue else PrimaryBlue.copy(alpha = 0.5f)
        )
        Text(
            text = name,
            fontSize = 11.sp,
            color = if(isSelected) PrimaryBlue else PrimaryBlue.copy(alpha = 0.5f),
            fontWeight = if(isSelected) FontWeight.Bold else FontWeight.SemiBold
        )
    }
}