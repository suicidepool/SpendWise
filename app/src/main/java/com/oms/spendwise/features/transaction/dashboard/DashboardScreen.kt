package com.oms.spendwise.features.transaction.dashboard


import android.content.Context
import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.oms.spendwise.features.transaction.TransactionViewModel
import com.oms.spendwise.ui.theme.Dimens
import com.oms.spendwise.ui.theme.PrimaryBlue
import com.oms.spendwise.ui.theme.TextPrimary
import com.oms.spendwise.ui.theme.TextSecondary
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.Currency
import com.oms.spendwise.R
import com.oms.spendwise.model.entity.Category
import com.oms.spendwise.model.entity.Transaction
import com.oms.spendwise.model.enum.TransactionType
import com.oms.spendwise.navigation.BottomNavigationItem
import com.oms.spendwise.ui.theme.BackgroundPrimary
import com.oms.spendwise.ui.theme.ExpenseRed
import com.oms.spendwise.ui.theme.IncomeGreen
import com.oms.spendwise.ui.theme.PrimaryBlueLight
import com.oms.spendwise.utils.formatTime
import java.time.format.DateTimeFormatter
import kotlin.math.exp

@Composable
fun DashboardScreen(
    modifier: Modifier = Modifier,
    transactionViewModel: TransactionViewModel,
    currency: Currency,
    onTransactionItemClick: (id: Long) -> Unit,
    onSeeAllClick: () -> Unit,
    context: Context
) {
    val localDateTime = LocalDateTime.now()
    val month = localDateTime.monthValue
    val year = localDateTime.year
    val incomeComparedToLastMonth = transactionViewModel.getIncomeIncrementFromLastMonth(month,year)
    val expenseComparedToLastMonth = transactionViewModel.getExpenseIncrementFromLastMonth(month,year)


    Column(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(Modifier.height(12.dp))
        TotalBalanceCard(
            modifier = Modifier.padding(horizontal = Dimens.HorizontalScreenPadding),
            balance = transactionViewModel.getTotalBalance(),
            incrementFromLastMonth = transactionViewModel.getBalanceIncrementFromLastMonth(),
            currency = currency
        )
        Spacer(Modifier.height(28.dp))
        ThisMonthSummerySection(
            modifier = Modifier.padding(horizontal = Dimens.HorizontalScreenPadding),
            month = localDateTime.format(DateTimeFormatter.ofPattern("MMM yyyy")),
            income = transactionViewModel.getMonthlyIncome(month,year),
            expense = transactionViewModel.getMonthlyExpense(month,year),
            currency = currency,
            incomeComparedToLastMonth = incomeComparedToLastMonth,
            expenseComparedToLastMonth = expenseComparedToLastMonth
        )
        Spacer(Modifier.height(22.dp))
        TodayTransactionSection(
            modifier = Modifier.padding(horizontal = Dimens.HorizontalScreenPadding),
            transactions = transactionViewModel.transactions[LocalDate.now()]
                ?: emptyList<Transaction>(),
            onTransactionItemClick = onTransactionItemClick,
            onSeeAllClick = onSeeAllClick,
            context = context,
            currency = currency,
            categories = transactionViewModel.categories
        )
        Spacer(Modifier.height(110.dp))

    }
}

@Composable
fun TodayTransactionSection(
    modifier: Modifier = Modifier,
    transactions: List<Transaction>,
    onTransactionItemClick: (id: Long) -> Unit,
    categories: List<Category>,
    onSeeAllClick: () -> Unit,
    currency: Currency,
    context: Context
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Today's Transactions",
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary,
                fontWeight = FontWeight.Black
            )
            Text(
                text = "See All",
                style = MaterialTheme.typography.labelMedium,
                color = PrimaryBlue,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .clickable(
                        onClick = onSeeAllClick
                    )
            )
        }

        Spacer(Modifier.height(12.dp))

        transactions.forEach { transaction ->
            val transactionCategory = categories.find { it.categoryId == transaction.categoryId }!!
            TransactionItem(
                transaction = transaction,
                category = transactionCategory,
                context = context,
                currency = currency,
                onClick = {
                    onTransactionItemClick(transaction.transactionId)
                }
            )
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun TransactionItem(
    modifier: Modifier = Modifier,
    transaction: Transaction,
    category: Category,
    context: Context,
    currency: Currency,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .padding(2.dp)
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(2.dp),
        onClick = onClick
    ){
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        )
        {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                val iconId = context.resources.getIdentifier(
                    category.icon,
                    "drawable",
                    context.packageName
                )

                val iconColor = Color(0xFF000000 + category.colorHex.toLong(16))

                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(iconColor.copy(0.17f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(iconId),
                        contentDescription = "Confirm",
                        tint = iconColor,
                        modifier = Modifier.size(16.dp)
                    )
                }

                Column(
                    verticalArrangement = Arrangement.spacedBy(0.dp),
                ) {
                    Text(
                        text = category.name,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = formatTime(transaction.transactionDateTime.toLocalTime()),
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary
                    )
                }
            }

            Text(
                text = "${if(transaction.type == TransactionType.INCOME.value) "+" else "-"}${currency.symbol}${transaction.amount}",
                style = MaterialTheme.typography.bodyLarge,
                color = if(transaction.type == TransactionType.INCOME.value) IncomeGreen else ExpenseRed,
                fontWeight = FontWeight.Bold
            )
        }
    }

}

@Composable
private fun ThisMonthSummerySection(
    modifier: Modifier = Modifier,
    month: String,
    income: Double,
    expense: Double,
    currency: Currency,
    incomeComparedToLastMonth: Double,
    expenseComparedToLastMonth: Double
)
{
    val bottomIncomeLabelText = if(incomeComparedToLastMonth.isNaN() || incomeComparedToLastMonth.isInfinite()) "N/A"
    else "${if(incomeComparedToLastMonth > 0) "+" else ""}${incomeComparedToLastMonth}%"

    val bottomExpanseLabelText = if(expenseComparedToLastMonth.isNaN() || expenseComparedToLastMonth.isInfinite()) "N/A"
    else "${if(expenseComparedToLastMonth > 0) "+" else ""}${expenseComparedToLastMonth}%"

    Column(
        modifier = modifier
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "This month summery",
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary,
                fontWeight = FontWeight.Black
            )
            Text(
                text = month,
                style = MaterialTheme.typography.labelMedium,
                color = PrimaryBlue,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(Modifier.height(18.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            MonthlySummeryCard(
                modifier = Modifier.weight(1f),
                titleText = "INCOME",
                amountText = "${currency.symbol}${income}",
                bottomLabelText = bottomIncomeLabelText,
                bottomLabelColor = if(incomeComparedToLastMonth > 0) IncomeGreen else ExpenseRed,
                topIcon = R.drawable.icon_arrow_up,
                iconColor = IncomeGreen
            )
            MonthlySummeryCard(
                modifier = Modifier.weight(1f),
                titleText = "EXPENSE",
                amountText = "${currency.symbol}${expense}",
                bottomLabelText = bottomExpanseLabelText,
                bottomLabelColor = if(expenseComparedToLastMonth > 0) ExpenseRed else IncomeGreen,
                topIcon = R.drawable.icon_arrow_down,
                iconColor = ExpenseRed
            )
        }

    }
}

@Composable
fun MonthlySummeryCard(
    modifier: Modifier = Modifier,
    titleText: String,
    amountText: String,
    bottomLabelText: String,
    bottomLabelColor: Color,
    @DrawableRes topIcon: Int,
    iconColor: Color
) {
    Card(
        modifier = modifier
            .padding(2.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(2.dp)
    )
    {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(
                            color = iconColor.copy(alpha = 0.1f),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ){
                    Icon(
                        painter = painterResource(topIcon),
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = iconColor
                    )
                }
                Text(
                    text = titleText,
                    style = MaterialTheme.typography.labelMedium,
                    color = PrimaryBlueLight,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Text(
                text = amountText,
                style = MaterialTheme.typography.titleLarge,
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = bottomLabelText,
                style = MaterialTheme.typography.labelMedium,
                color = bottomLabelColor,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun TotalBalanceCard(
    modifier: Modifier = Modifier,
    balance: Double,
    incrementFromLastMonth: Double,
    currency: Currency
) {
    val formattedBalance = "${if(balance<0) "-" else ""}${currency.symbol}${if(balance<0) balance*(-1) else balance}"
    val bottomLabelText = if(incrementFromLastMonth.isNaN() || incrementFromLastMonth.isInfinite()) "N/A"
    else "${if(incrementFromLastMonth > 0) "+" else ""}${incrementFromLastMonth}%"
    Card(
        modifier = modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(Dimens.CardCornerRadius),
        colors = CardDefaults.cardColors(
            containerColor = PrimaryBlue
        ),
    ) {
        Column(
            modifier = Modifier
                .padding(22.dp),
            verticalArrangement = Arrangement.spacedBy(22.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "Total Balance",
                    fontWeight = FontWeight.Normal,
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.White.copy(alpha = 0.7f)
                )
                Text(
                    text = formattedBalance,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.displaySmall,
                    color = Color.White
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier
                        .background(
                            color = PrimaryBlueLight,
                            shape = RoundedCornerShape(20.dp)
                        ),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Spacer(Modifier.width(8.dp))
                    Icon(
                        painter = if(incrementFromLastMonth < 0) painterResource(R.drawable.icon_arrow_trend_down)
                        else painterResource(R.drawable.icon_arrow_trend_up),
                        contentDescription = null,
                        modifier = Modifier.size(12.dp),
                        tint = Color.White
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = bottomLabelText,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White
                    )
                    Spacer(Modifier.width(8.dp))
                }
                Text(
                    text = "From last month",
                    fontWeight = FontWeight.Normal,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.7f)
                )
            }
        }
    }
}