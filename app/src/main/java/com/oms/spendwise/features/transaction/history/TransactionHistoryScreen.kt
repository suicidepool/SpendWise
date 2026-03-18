package com.oms.spendwise.features.transaction.history

import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.oms.spendwise.R
import com.oms.spendwise.features.transaction.TransactionViewModel
import com.oms.spendwise.features.transaction.components.Overview
import com.oms.spendwise.features.transaction.components.TransactionItem
import com.oms.spendwise.model.enum.TransactionType
import com.oms.spendwise.ui.theme.TextHint
import com.oms.spendwise.utils.AmountFormatter
import com.oms.spendwise.utils.formatDate
import java.util.Currency

@Composable
fun TransactionHistoryScreen(
    modifier: Modifier = Modifier,
    transactionVM: TransactionViewModel,
    currency: Currency,
    context: Context,
    transactionHistoryVm: TransactionHistoryViewModel,
    onItemClick: (transactionId: Long) -> Unit,
    onBack: () -> Unit
) {

    BackHandler() {
        transactionHistoryVm.reset()
        onBack()
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopBar(
                modifier = Modifier
                    .statusBarsPadding(),
                transactionTypeFilter = transactionHistoryVm.transactionTypeFilter,
                onTransactionTypeFilterChange = transactionHistoryVm.onTransactionTypeFilterChange,
                searchBarVisibility = transactionHistoryVm.searchVisibility,
                toggleSearchBarVisibility = {transactionHistoryVm.onSearchVisibilityChange(!transactionHistoryVm.searchVisibility)},
                searchText = transactionHistoryVm.searchText,
                onSearchTextChange = transactionHistoryVm.onSearchTextChange,
                onBack = {
                    transactionHistoryVm.reset()
                    onBack()
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
        ) {
            if(transactionHistoryVm.searchVisibility && transactionHistoryVm.searchText.isNotEmpty()){
                SearchedTransactions(
                    transactionVM = transactionVM,
                    currency = currency,
                    searchedText = transactionHistoryVm.searchText,
                    context = context,
                    onClick = onItemClick
                )
            } else {
                FilteredTransactions(
                    transactionVM = transactionVM,
                    currency = currency,
                    transactionTypeFilter = transactionHistoryVm.transactionTypeFilter,
                    context = context,
                    onClick = onItemClick
                )
            }
        }
    }
}

@Composable
fun SearchedTransactions(
    modifier: Modifier = Modifier,
    transactionVM: TransactionViewModel,
    searchedText: String,
    currency: Currency,
    context: Context,
    onClick: (transactionId: Long) -> Unit
) {
    LazyColumn(
        modifier = modifier.fillMaxWidth()
    ) {
        items(transactionVM.distinctDates){ date ->
            transactionVM.transactions[date]?.let { transactions ->

                val filteredItemCnt = transactions.count{ transaction ->
                    val transactionCategory = transactionVM.categories.find { it.categoryId == transaction.categoryId}!!
                    transactionCategory.name.lowercase().contains(searchedText.trim().lowercase())
                }

                if(filteredItemCnt > 0){
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = formatDate(date),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.labelLarge
                    )
                    Spacer(Modifier.height(8.dp))
                    Overview(
                        totalIncome = "+${currency.symbol}${AmountFormatter.formatAmount(transactionVM.getTotalIncome(date))}",
                        totalExpense = "-${currency.symbol}${AmountFormatter.formatAmount(transactionVM.getTotalExpense(date))}"
                    )
                    Spacer(Modifier.height(14.dp))
                }


                transactions.forEach { transaction ->
                    val transactionCategory = transactionVM.categories.find { it.categoryId == transaction.categoryId }!!

                    if(transactionCategory.name.lowercase().contains(searchedText.trim().lowercase())) {
                        TransactionItem(
                            transaction = transaction,
                            category = transactionCategory,
                            currency = currency,
                            context = context,
                            onClick = {onClick(transaction.transactionId)}
                        )
                        Spacer(Modifier.height(14.dp))
                    }
                }
            }
        }
        items(1){
            Spacer(Modifier.height(110.dp))
        }
    }
}

@Composable
fun FilteredTransactions(
    modifier: Modifier = Modifier,
    transactionVM: TransactionViewModel,
    transactionTypeFilter: TransactionType?,
    currency: Currency,
    context: Context,
    onClick: (transactionId: Long) -> Unit
) {
    LazyColumn(
        modifier = modifier.fillMaxWidth()
    ) {
        items(transactionVM.distinctDates){ date ->
            transactionVM.transactions[date]?.let { transactions ->
                if(transactionTypeFilter ==  null){
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = formatDate(date),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.labelLarge
                    )
                    Spacer(Modifier.height(8.dp))
                    Overview(
                        totalIncome = "+${currency.symbol}${AmountFormatter.formatAmount(transactionVM.getTotalIncome(date))}",
                        totalExpense = "-${currency.symbol}${AmountFormatter.formatAmount(transactionVM.getTotalExpense(date))}"
                    )
                    Spacer(Modifier.height(14.dp))
                } else {
                    val filteredItemCnt = transactions.count{it.type == transactionTypeFilter.value}
                    if(filteredItemCnt > 0){
                        Spacer(Modifier.height(2.dp))
                        Text(
                            text = formatDate(date),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.labelLarge
                        )
                        Spacer(Modifier.height(8.dp))
                        Overview(
                            totalIncome = "+${currency.symbol}${AmountFormatter.formatAmount(transactionVM.getTotalIncome(date))}",
                            totalExpense = "-${currency.symbol}${AmountFormatter.formatAmount(transactionVM.getTotalExpense(date))}"
                        )
                        Spacer(Modifier.height(14.dp))
                    }
                }



                transactions.forEach { transaction ->
                    val transactionCategory = transactionVM.categories.find { it.categoryId == transaction.categoryId }!!
                    if(transactionTypeFilter == null){
                        TransactionItem(
                            transaction = transaction,
                            category = transactionCategory,
                            currency = currency,
                            context = context,
                            onClick = {onClick(transaction.transactionId)}
                        )
                        Spacer(Modifier.height(14.dp))
                    } else if(transactionTypeFilter.value == transactionCategory.type) {
                        TransactionItem(
                            transaction = transaction,
                            category = transactionCategory,
                            currency = currency,
                            context = context,
                            onClick = {onClick(transaction.transactionId)}
                        )
                        Spacer(Modifier.height(14.dp))
                    }
                }
            }
        }
        items(1){
            Spacer(Modifier.height(110.dp))
        }
    }
}

@Composable
private fun TopBar(
    modifier: Modifier = Modifier,
    title: String = "Transaction History",
    transactionTypeFilter: TransactionType? = null,
    onTransactionTypeFilterChange: (TransactionType?) -> Unit,
    searchBarVisibility: Boolean,
    toggleSearchBarVisibility: () -> Unit,
    searchText: String,
    onSearchTextChange: (String) -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth(),
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
                        .size(16.dp)
                )
            }

            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )


            IconButton(
                modifier = Modifier,
                onClick = {
                    onTransactionTypeFilterChange(null)
                    toggleSearchBarVisibility()
                }
            ) {
                Icon(
                    painter = if(searchBarVisibility) painterResource(R.drawable.icon_cross) else painterResource(R.drawable.icon_search),
                    contentDescription = "search",
                    modifier = Modifier
                        .size(16.dp),
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }

        }

        if(searchBarVisibility){
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth(),
                value = searchText,
                onValueChange = onSearchTextChange,
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyLarge,
                shape = RoundedCornerShape(22.dp),
                placeholder = {
                    Text(
                        text = "e.g. Income",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                leadingIcon = {
                    Icon(
                        painter = painterResource(R.drawable.icon_search),
                        contentDescription = "note",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.outline,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface
                ),
                trailingIcon = {
                    IconButton(
                        onClick = {
                            onSearchTextChange("")
                        }
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.icon_cross),
                            contentDescription = "note",
                            tint = TextHint,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            )
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterButton(
                    onClick = {
                        onTransactionTypeFilterChange(null)
                    },
                    text = "All",
                    transactionTypeFilter = transactionTypeFilter,
                    filterType = null
                )
                FilterButton(
                    onClick = {
                        onTransactionTypeFilterChange(TransactionType.INCOME)
                    },
                    text = "Income",
                    transactionTypeFilter = transactionTypeFilter,
                    filterType = TransactionType.INCOME
                )
                FilterButton(
                    onClick = {
                        onTransactionTypeFilterChange(TransactionType.EXPENSE)
                    },
                    text = "Expense",
                    transactionTypeFilter = transactionTypeFilter,
                    filterType = TransactionType.EXPENSE
                )
            }
        }
        Spacer(Modifier.height(6.dp))
    }
}

@Composable
private fun FilterButton(
    modifier: Modifier = Modifier,
    text: String,
    transactionTypeFilter: TransactionType?,
    filterType: TransactionType?,
    onClick: (TransactionType?) -> Unit
) {
    Button(
        onClick = {
            onClick(filterType)
        },
        colors = ButtonDefaults.buttonColors(
            containerColor = if(transactionTypeFilter == filterType) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
            contentColor = if(transactionTypeFilter == filterType) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
        ),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(
            width = if(transactionTypeFilter == null) 0.dp else 1.dp,
            color = MaterialTheme.colorScheme.outline
        ),
        modifier = modifier.height(36.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium
        )
    }
}