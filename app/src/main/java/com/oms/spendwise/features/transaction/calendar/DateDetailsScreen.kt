package com.oms.spendwise.features.transaction.calendar

import android.content.Context
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.oms.spendwise.R
import com.oms.spendwise.features.transaction.TransactionViewModel
import com.oms.spendwise.features.transaction.components.Overview
import com.oms.spendwise.features.transaction.components.TransactionItem
import com.oms.spendwise.navigation.BottomNavigationItem
import com.oms.spendwise.ui.theme.Dimens
import com.oms.spendwise.utils.AmountFormatter
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Currency

@Composable
fun DateDetailsScreen(
    modifier: Modifier = Modifier,
    date: LocalDate,
    transactionViewModel: TransactionViewModel,
    currency: Currency,
    context: Context,
    onItemClick: (Long) -> Unit,
    onAddTransactionClick: (LocalDate) -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopBar(
                modifier = Modifier.padding(horizontal = Dimens.HorizontalScreenPadding),
                title = date.format(DateTimeFormatter.ofPattern("d MMM yyyy")),
                onBack = onBack
            )
        },
        floatingActionButton = {
            AddTransactionFab(
                onClick = {
                    onAddTransactionClick(date)
                }
            )
        }
    ) { innerPadding ->

        Column(
            modifier = modifier.padding(innerPadding)
        ) {
            val transactions = transactionViewModel.transactions[date]
            if(transactions != null){
                Overview(
                    modifier = Modifier.padding(horizontal = Dimens.HorizontalScreenPadding),
                    totalIncome = "+${currency.symbol}${AmountFormatter.formatAmount(transactionViewModel.getTotalIncome(date))}",
                    totalExpense = "-${currency.symbol}${AmountFormatter.formatAmount(transactionViewModel.getTotalExpense(date))}"
                )
                Spacer(Modifier.height(14.dp))
                LazyColumn(
                    modifier = modifier
                        .fillMaxWidth()
                        .padding(horizontal = Dimens.HorizontalScreenPadding)
                ) {
                    items(transactions) { transaction ->
                        val transactionCategory = transactionViewModel.categories.find { it.categoryId == transaction.categoryId }!!
                        TransactionItem(
                            transaction = transaction,
                            category = transactionCategory,
                            currency = currency,
                            context = context,
                            onClick = {onItemClick(transaction.transactionId)}
                        )
                            Spacer(Modifier.height(14.dp))
                    }
                    items(1){
                        Spacer(Modifier.height(110.dp))
                    }
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        painter = painterResource(R.drawable.icon_scroll),
                        contentDescription = null,
                        modifier = Modifier.size(36.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "No Transactions",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Normal
                    )
                }
            }
        }
    }
}

@Composable
fun AddTransactionFab(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .background(
                color = MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(Dimens.CardCornerRadius)
            )
            .size(52.dp)
            .shadow(
                elevation = 8.dp,
                spotColor = MaterialTheme.colorScheme.secondary,
                ambientColor = MaterialTheme.colorScheme.secondary,
                shape = RoundedCornerShape(Dimens.CardCornerRadius)
            )
            .clickable(
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(Dimens.CardCornerRadius)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(BottomNavigationItem.Add.icon),
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }

    }
}

@Composable
private fun TopBar(
    modifier: Modifier = Modifier,
    title: String,
    onBack: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding(),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack
            ) {
                Icon(
                    painter = painterResource(R.drawable.icon_back),
                    contentDescription = "back",
                    modifier = Modifier
                        .size(16.dp),
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }

            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(Modifier.size(20.dp))
        }
        Spacer(Modifier.height(6.dp))
    }
}