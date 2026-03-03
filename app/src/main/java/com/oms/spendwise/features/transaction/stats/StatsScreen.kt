package com.oms.spendwise.features.transaction.stats

import android.content.Context
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.oms.spendwise.R
import com.oms.spendwise.features.transaction.TransactionViewModel
import com.oms.spendwise.model.entity.Category
import com.oms.spendwise.model.enum.TransactionType
import com.oms.spendwise.ui.theme.Dimens
import com.oms.spendwise.ui.theme.ExpenseRed
import com.oms.spendwise.ui.theme.IncomeGreen
import com.oms.spendwise.ui.theme.PrimaryBlue
import com.oms.spendwise.ui.theme.TextPrimary
import com.oms.spendwise.ui.theme.TextSecondary
import java.util.Currency
import kotlin.math.round

@Composable
fun StatsScreen(
    modifier: Modifier = Modifier,
    onCalendarClick: () -> Unit = {},
    currency: Currency,
    statsScreenViewModel: StatsScreenViewModel,
    transactionViewModel: TransactionViewModel
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopBar(
                modifier = Modifier
                    .statusBarsPadding(),
                onCalenderClick = onCalendarClick,
                selectedTab = statsScreenViewModel.selectedTab,
                tabs = statsScreenViewModel.topBarTabs,
                onTabClick = {statsScreenViewModel.onTopBarItemChange(it)}

            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            AnimatedContent(
                targetState = statsScreenViewModel.selectedTab,
                transitionSpec = {
                    slideInHorizontally { width -> width } + fadeIn() togetherWith
                            slideOutHorizontally { width -> -width } + fadeOut()
                },
                label = ""
            ) { target ->
                when(target){
                    StatsScreenViewModel.TopBarTab.DAY ->
                        Content(
                            currency = currency,
                            type = target.value,
                            balance = transactionViewModel.getTodayTotalBalance(),
                            balanceIncrementPercentage = transactionViewModel.getDayBalanceIncrementFromLastDay(),
                            income = transactionViewModel.getDayTotalIncome(),
                            incomeIncrementPercentage = transactionViewModel.getDayTotalIncomeFromLastDay(),
                            expense = transactionViewModel.getDayTotalExpense(),
                            expenseIncrementPercentage = transactionViewModel.getDayTotalExpenseFromLastDay(),
                            expenseCategoryList = transactionViewModel.getDayExpenseCategoryList()
                        )
                    StatsScreenViewModel.TopBarTab.WEEK ->
                        Content(
                            currency = currency,
                            type = target.value,
                            balance = transactionViewModel.getCurrentWeekTotalBalance(),
                            balanceIncrementPercentage = transactionViewModel.getWeekBalanceIncrementFromLastWeek(),
                            income = transactionViewModel.getWeekTotalIncome(),
                            incomeIncrementPercentage = transactionViewModel.getWeekTotalIncomeFromLastWeek(),
                            expense = transactionViewModel.getWeekTotalExpense(),
                            expenseIncrementPercentage = transactionViewModel.getWeekTotalExpenseFromLastWeek(),
                            expenseCategoryList = transactionViewModel.getWeekExpenseCategoryList()
                        )
                    StatsScreenViewModel.TopBarTab.MONTH ->
                        Content(
                            currency = currency,
                            type = target.value,
                            balance = transactionViewModel.getCurrentMonthTotalBalance(),
                            balanceIncrementPercentage = transactionViewModel.getMonthBalanceIncrementFromLastMonth(),
                            income = transactionViewModel.getMonthTotalIncome(),
                            incomeIncrementPercentage = transactionViewModel.getMonthTotalIncomeFromLastMonth(),
                            expense = transactionViewModel.getMonthTotalExpense(),
                            expenseIncrementPercentage = transactionViewModel.getMonthTotalExpenseFromLastMonth(),
                            expenseCategoryList = transactionViewModel.getMonthExpenseCategoryList()
                        )
                    StatsScreenViewModel.TopBarTab.YEAR ->
                        Content(
                            currency = currency,
                            type = target.value,
                            balance = transactionViewModel.getCurrentYearTotalBalance(),
                            balanceIncrementPercentage = transactionViewModel.getYearBalanceIncrementFromLastYear(),
                            income = transactionViewModel.getYearTotalIncome(),
                            incomeIncrementPercentage = transactionViewModel.getYearTotalIncomeFromLastYear(),
                            expense = transactionViewModel.getYearTotalExpense(),
                            expenseIncrementPercentage = transactionViewModel.getYearTotalExpenseFromLastYear(),
                            expenseCategoryList = transactionViewModel.getYearExpenseCategoryList()
                        )

                }
            }
        }
    }
}

@Composable
private fun Content(
    modifier: Modifier = Modifier,
    currency: Currency,
    type: String,
    balance: Double,
    balanceIncrementPercentage: Double,
    income: Double,
    incomeIncrementPercentage: Double,
    expense: Double,
    expenseIncrementPercentage: Double,
    expenseCategoryList: List<Pair<Category, Double>>
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = Dimens.HorizontalScreenPadding)
    ) {
        Spacer(Modifier.height(18.dp))
        TotalBalanceCard(
            balance = balance,
            balanceIncrementPercentage = balanceIncrementPercentage,
            type = type,
            currency = currency

        )
        Spacer(Modifier.height(18.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            IncomeExpenseCard(
                modifier = Modifier.weight(1f),
                titleText = "TOTAL INCOME",
                amount = income,
                amountIncrementPercentage = incomeIncrementPercentage,
                currency = currency,
                type = TransactionType.INCOME
            )
            IncomeExpenseCard(
                modifier = Modifier.weight(1f),
                titleText = "TOTAL EXPENSE",
                amount = expense,
                amountIncrementPercentage = expenseIncrementPercentage,
                currency = currency,
                type = TransactionType.EXPENSE
            )
        }
        Spacer(Modifier.height(18.dp))

        PieChart(
            data = expenseCategoryList,
            type = TransactionType.EXPENSE,
            currency = currency
        )

        Spacer(Modifier.height(100.dp))

    }
}

@Composable
fun IncomeExpenseCard(
    modifier: Modifier = Modifier,
    titleText: String,
    amount: Double,
    amountIncrementPercentage: Double,
    currency: Currency,
    type: TransactionType
){
    val formattedAmount = "${if(amount<0) "-" else ""}${currency.symbol}${if(amount<0) amount*(-1) else amount}"
    val bottomLabelText = if(amountIncrementPercentage.isNaN() || amountIncrementPercentage.isInfinite()) "N/A"
    else "${if(amountIncrementPercentage > 0) "+" else ""}${amountIncrementPercentage}%"
    Card(
        modifier = modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(Dimens.CardCornerRadius),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(22.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = titleText,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.labelMedium,
                    color = TextSecondary
                )
                Text(
                    text = formattedAmount,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleLarge,
                    color = TextPrimary
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val iconTint = if(type == TransactionType.EXPENSE){
                    if(amountIncrementPercentage > 0) ExpenseRed else IncomeGreen
                } else {
                    if(amountIncrementPercentage > 0) IncomeGreen else ExpenseRed
                }
                Icon(
                    painter = if(amountIncrementPercentage < 0) painterResource(R.drawable.icon_arrow_down)
                    else painterResource(R.drawable.icon_arrow_up),
                    contentDescription = null,
                    modifier = Modifier.size(12.dp),
                    tint = iconTint
                )
                Text(
                    text = bottomLabelText,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.labelMedium,
                    color = iconTint
                )
            }
        }
    }
}

@Composable
private fun TotalBalanceCard(
    modifier: Modifier = Modifier,
    balance: Double,
    balanceIncrementPercentage: Double,
    type: String,
    currency: Currency,
) {
    val formattedBalance = "${if(balance<0) "-" else ""}${currency.symbol}${if(balance<0) balance*(-1) else balance}"
    val bottomLabelText = if(balanceIncrementPercentage.isNaN() || balanceIncrementPercentage.isInfinite()) "N/A"
    else "${if(balanceIncrementPercentage > 0) "+" else ""}${balanceIncrementPercentage}%"
    Card(
        modifier = modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(Dimens.CardCornerRadius),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(22.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "NET BALANCE",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.labelMedium,
                    color = TextSecondary
                )
                Text(
                    text = formattedBalance,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.displaySmall,
                    color = TextPrimary
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = if(balanceIncrementPercentage < 0) painterResource(R.drawable.icon_arrow_trend_down)
                    else painterResource(R.drawable.icon_arrow_trend_up),
                    contentDescription = null,
                    modifier = Modifier.size(12.dp),
                    tint = if(balanceIncrementPercentage > 0) IncomeGreen else ExpenseRed
                )
                Text(
                    text = bottomLabelText,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.labelMedium,
                    color = if(balanceIncrementPercentage > 0) IncomeGreen else ExpenseRed
                )
                Text(
                    text = "From last $type",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.labelMedium,
                    color = if(balanceIncrementPercentage > 0) IncomeGreen else ExpenseRed
                )
            }
        }
    }
}

@Composable
private fun TopBar(
    modifier: Modifier = Modifier,
    title: String = "Statistics",
    onCalenderClick: () -> Unit,
    selectedTab: StatsScreenViewModel.TopBarTab,
    tabs: List<StatsScreenViewModel.TopBarTab>,
    onTabClick: (tab: StatsScreenViewModel.TopBarTab) -> Unit
) {
    Column(
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = Dimens.HorizontalScreenPadding)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        )
        {

            Spacer(Modifier.size(40.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            IconButton(
                onClick = onCalenderClick
            ) {
                Icon(
                    painter = painterResource(R.drawable.icon_calendar),
                    contentDescription = "calendar",
                    modifier = Modifier
                        .size(18.dp)
                )
            }

        }

        Spacer(Modifier.height(12.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Dimens.HorizontalScreenPadding)
                .height(32.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            tabs.forEach { item ->
                TopBarTabItem(
                    modifier = Modifier
                        .weight(1f),
                    text = item.name,
                    isSelected = selectedTab == item,
                    onClick = {
                        onTabClick(item)
                    }
                )
            }
        }
        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            thickness = 0.5.dp,
            color = Color.LightGray
        )
        Spacer(Modifier.height(6.dp))
    }

}

@Composable
private fun TopBarTabItem(
    modifier: Modifier = Modifier,
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .clickable(
                onClick = onClick
            ),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = if(isSelected) PrimaryBlue else TextSecondary
        )

        if(isSelected)
            HorizontalDivider(
                modifier = Modifier.fillMaxWidth(),
                thickness = 2.dp,
                color = PrimaryBlue)
    }
}


@Composable
fun PieChart(
    data: List<Pair<Category, Double>>,
    type: TransactionType,
    currency: Currency,
    radiusOuter: Dp = 235.dp,
    chartBarWidth: Dp = 22.dp,
    animDuration: Int = 1000
) {

    val totalSum = data.sumOf { it.second }
    val floatValue = mutableListOf<Float>()

    data.forEachIndexed { index, values ->
        floatValue.add(index, 360 * values.second.toFloat() / totalSum.toFloat())
    }

    val colors = data.map {
        Color(0xFF000000 + it.first.colorHex.toLong(16))
    }

    var animationPlayed by remember { mutableStateOf(false) }

    var lastValue = 0f


    val animateRotation by animateFloatAsState(
        targetValue = if (animationPlayed) 90f * 11f else 0f,
        animationSpec = tween(
            durationMillis = animDuration,
            delayMillis = 0,
            easing = LinearOutSlowInEasing
        )
    )

    // to play the animation only once when the function is Created or Recomposed
    LaunchedEffect(key1 = true) {
        animationPlayed = true
    }

    Card(
        modifier = Modifier.fillMaxWidth()
            .padding(horizontal = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {

        Column(
            modifier = Modifier.fillMaxWidth()
                .padding(
                    top = 32.dp,
                    bottom = 32.dp,
                    start = 18.dp,
                    end = 18.dp
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = if(type == TransactionType.EXPENSE) "Expense Breakdown" else "Income Breakdown",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Spacer(Modifier.height(24.dp))

            // Pie Chart using Canvas Arc
            Box(
                modifier = Modifier.size(radiusOuter),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if(type == TransactionType.EXPENSE) "Total Spending" else "Total Income",
                        style = MaterialTheme.typography.labelLarge,
                        color = TextSecondary,
                        fontWeight = FontWeight.Normal
                    )
                    Text(
                        text = currency.symbol + totalSum,
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }
                Canvas(
                    modifier = Modifier
                        .size(radiusOuter * 2f)
                        .rotate(animateRotation)
                ) {
                    // draw each Arc for each data entry in Pie Chart
                    floatValue.forEachIndexed { index, value ->
                        drawArc(
                            color = colors[index],
                            lastValue,
                            value,
                            useCenter = false,
                            style = Stroke(chartBarWidth.toPx(), cap = StrokeCap.Butt)
                        )
                        lastValue += value
                    }
                }
            }

            // To see the data in more structured way
            // Compose Function in which Items are showing data
            DetailsPieChart(
                data = data,
                colors = colors,
                currency = currency
            )

        }
    }


}

@Composable
fun DetailsPieChart(
    data: List<Pair<Category, Double>>,
    colors: List<Color>,
    currency: Currency
) {
    val total = data.sumOf { it.second }
    Column(
        modifier = Modifier
            .padding(top = 80.dp)
            .fillMaxWidth()
    ) {
        // create the data items
        data.forEachIndexed { index, value ->
            DetailsPieChartItem(
                data = value,
                color = colors[index],
                total = total,
                currency = currency
            )
            Spacer(Modifier.height(22.dp))
        }

    }
}

@Composable
fun DetailsPieChartItem(
    data: Pair<Category, Double>,
    currency: Currency,
    total: Double,
    height: Dp = 12.dp,
    color: Color
) {

    Surface(
        modifier = Modifier,
        color = Color.Transparent
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Row (
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .background(
                            color = color,
                            shape = RoundedCornerShape(6.dp)
                        )
                        .size(height)
                )
                Text(
                    modifier = Modifier.padding(start = 15.dp),
                    text = data.first.name,
                    fontWeight = FontWeight.Medium,
                    style = MaterialTheme.typography.labelLarge,
                    color = TextSecondary
                )
            }

            Text(
                text = currency.symbol + data.second.toString() + "(${round(((data.second / total) * 100) * 100) / 100}%)",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodyLarge,
                color = TextPrimary
            )

        }

    }

}