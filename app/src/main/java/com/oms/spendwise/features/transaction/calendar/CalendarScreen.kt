package com.oms.spendwise.features.transaction.calendar

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.oms.spendwise.R
import com.oms.spendwise.features.transaction.TransactionViewModel
import com.oms.spendwise.ui.theme.Dimens
import com.oms.spendwise.ui.theme.ExpenseRed
import com.oms.spendwise.ui.theme.IncomeGreen
import com.oms.spendwise.utils.AmountFormatter
import java.time.DayOfWeek
import java.time.LocalDate
import java.util.Currency
import java.util.Locale

@Composable
fun CalendarScreen(
    modifier: Modifier = Modifier,
    transactionVM: TransactionViewModel,
    calendarViewModel: CalendarViewModel,
    currency: Currency,
    onDateClick: (LocalDate) -> Unit,
    onBack: () -> Unit
) {

    var isForwardAnimation by remember { mutableStateOf(false) }

    BackHandler() {
        calendarViewModel.resetYearMonth()
        onBack()
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopBar(
                modifier = Modifier.padding(horizontal = Dimens.HorizontalScreenPadding),
                yearMonth = calendarViewModel.getFormatedYearMonth(),
                nextMonth = {
                    isForwardAnimation = true
                    calendarViewModel.shiftNextMonth()
                },
                prevMonth = {
                    isForwardAnimation = false
                    calendarViewModel.shiftPrevMonth()
                },
                onBack = {
                    calendarViewModel.resetYearMonth()
                    onBack()
                }
            )
        }

    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(Modifier.height(18.dp))
            AnimatedContent(
                modifier = Modifier
                    .fillMaxWidth(),
                targetState = calendarViewModel.yearMonth,
                transitionSpec = {
                    if(isForwardAnimation){
                        slideInHorizontally { width -> width } + fadeIn() togetherWith
                                slideOutHorizontally { width -> -width } + fadeOut()
                    }
                    else{
                        slideInHorizontally { width -> -width } + fadeIn() togetherWith
                                slideOutHorizontally { width -> width } + fadeOut()
                    }

                },
                label = "calendarSlideAnimation"
            ) { yearMonth ->
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Calendar(
                        modifier = Modifier.padding(horizontal = Dimens.HorizontalScreenPadding),
                        currency = currency,
                        calendar = calendarViewModel.calendar,
                        onDateClick = onDateClick,
                        getTotalIncome = { date: LocalDate ->
                            transactionVM.getTotalIncome(date)
                        },
                        getTotalExpense = { date: LocalDate ->
                            transactionVM.getTotalExpense(date)
                        },
                        shortFormat = transactionVM::shortFormat
                    )
                    Spacer(Modifier.height(22.dp))
                    MonthlyOverviewCard(
                        modifier = Modifier.padding(horizontal = Dimens.HorizontalScreenPadding),
                        currency = currency,
                        income = transactionVM.getMonthTotalIncome(yearMonth.monthValue, yearMonth.year),
                        expense = transactionVM.getMonthTotalExpense(yearMonth.monthValue,yearMonth.year)
                    )
                    Spacer(Modifier.height(110.dp))
                }
            }

        }
    }
}

@Composable
private fun MonthlyOverviewCard(
    modifier: Modifier = Modifier,
    currency: Currency,
    income: Double,
    expense: Double,
) {
    Card(
        modifier = modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        val balance = income - expense
        val balanceMod = if(balance < 0) balance * (-1) else balance

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = "INCOME",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 10.sp
                )
                Text(
                    text = "${currency.symbol}${AmountFormatter.formatAmount(income)}",
                    color = IncomeGreen,
                    style = MaterialTheme.typography.labelLarge,
                    textAlign = TextAlign.Center
                )
            }
            VerticalDivider(
                modifier = Modifier
                    .height(36.dp)
                    .padding(horizontal = 8.dp),
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant
            )
            Column(
                modifier = Modifier
                    .weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = "EXPENSE",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 10.sp
                )
                Text(
                    text = "${currency.symbol}${AmountFormatter.formatAmount(expense)}",
                    color = ExpenseRed,
                    style = MaterialTheme.typography.labelLarge,
                    textAlign = TextAlign.Center
                )
            }
            VerticalDivider(
                modifier = Modifier
                    .height(36.dp)
                    .padding(horizontal = 8.dp),
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant
            )
            Column(
                modifier = Modifier
                    .weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = "BALANCE",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 10.sp
                )
                Text(
                    text = "${if(balance < 0) "-" else ""}${currency.symbol}${AmountFormatter.formatAmount(balanceMod)}",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.labelLarge,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun Calendar(
    modifier: Modifier = Modifier,
    currency: Currency,
    calendar: List<LocalDate?>,
    onDateClick: (LocalDate) -> Unit,
    shortFormat: (Double) -> String,
    getTotalIncome: (date:LocalDate) -> Double,
    getTotalExpense: (date:LocalDate) -> Double,
) {

    val daysOfWeek = listOf(
        DayOfWeek.SUNDAY,
        DayOfWeek.MONDAY,
        DayOfWeek.TUESDAY,
        DayOfWeek.WEDNESDAY,
        DayOfWeek.THURSDAY,
        DayOfWeek.FRIDAY,
        DayOfWeek.SATURDAY
    )


    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(520.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
        ) {
            daysOfWeek.forEach { day ->
                val label =  day.getDisplayName(java.time.format.TextStyle.SHORT, Locale.getDefault())
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .width(38.dp),
                    contentAlignment = Alignment.Center
                ){
                    Text(
                        text = label,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }

        LazyHorizontalGrid(
            modifier = Modifier,
            rows = GridCells.Fixed(7)
        ) {
            items(calendar) { date ->
                if(date != null){

                    val expense = getTotalExpense(date)
                    val income = getTotalIncome(date)

                    CalendarDateCard(
                        date = date,
                        onClick = {
                            onDateClick(date)
                        },
                        income = if(income != 0.0) "-${currency.symbol}${shortFormat(income)}" else ""
                        ,
                        expense = if(expense != 0.0) "-${currency.symbol}${shortFormat(expense)}" else ""

                    )
                } else {
                    Spacer(
                        modifier = Modifier.width(56.dp)
                    )
                }
            }
        }

    }

}

@Composable
fun CalendarDateCard(
    modifier: Modifier = Modifier,
    date: LocalDate,
    onClick: () -> Unit,
    income: String,
    expense: String,
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
            .background(shape = RoundedCornerShape(12.dp), color = Color.Transparent)
            .width(56.dp)
            .padding(2.dp),
        elevation = CardDefaults.cardElevation(1.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(
            width = 1.dp,
            color = if(date == LocalDate.now()) MaterialTheme.colorScheme.primary else Color.Transparent
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .clickable(
                    onClick = onClick
                )
                .padding(6.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = date.dayOfMonth.toString(),
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            if(income.isNotEmpty())
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(16.dp)
                        .background(
                            color = IncomeGreen.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(10.dp)
                        ),
                    contentAlignment = Alignment.Center
                ){
                    Text(
                        text = income,
                        style = TextStyle(
                            platformStyle = PlatformTextStyle(includeFontPadding = false)
                        ),
                        fontWeight = FontWeight.Bold,
                        fontSize = 8.sp,
                        color = IncomeGreen
                    )
                }
            if(expense.isNotEmpty())
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(16.dp)
                        .background(
                            color = ExpenseRed.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(10.dp)
                        ),
                    contentAlignment = Alignment.Center
                ){
                    Text(
                        text = expense,
                        style = TextStyle(
                            platformStyle = PlatformTextStyle(includeFontPadding = false)
                        ),
                        fontWeight = FontWeight.Bold,
                        fontSize = 8.sp,
                        color = ExpenseRed
                    )
                }
        }
    }
}

@Composable
private fun TopBar(
    modifier: Modifier = Modifier,
    yearMonth: String,
    nextMonth: () -> Unit,
    prevMonth: () -> Unit,
    onBack: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(82.dp)
            .statusBarsPadding(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(
            onClick = onBack
        ) {
            Icon(
                painter = painterResource(R.drawable.icon_back_arrow),
                contentDescription = "back",
                modifier = Modifier.size(28.dp),
                tint = MaterialTheme.colorScheme.onBackground
            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = prevMonth
            ) {
                Icon(
                    painter = painterResource(R.drawable.icon_back),
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Text(
                text = yearMonth,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            IconButton(
                onClick = nextMonth
            ) {
                Icon(
                    painter = painterResource(R.drawable.icon_forward),
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(Modifier.size(22.dp))
    }
}