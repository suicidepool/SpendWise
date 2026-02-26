package com.oms.spendwise.features.transaction.history

import android.content.Context
import android.icu.text.StringSearch
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.oms.spendwise.R
import com.oms.spendwise.features.transaction.TransactionViewModel
import com.oms.spendwise.model.entity.Category
import com.oms.spendwise.model.entity.Transaction
import com.oms.spendwise.model.enum.TransactionType
import com.oms.spendwise.ui.theme.ExpenseRed
import com.oms.spendwise.ui.theme.IncomeGreen
import com.oms.spendwise.ui.theme.InputFieldBorder
import com.oms.spendwise.ui.theme.PrimaryBlue
import com.oms.spendwise.ui.theme.TextHint
import com.oms.spendwise.ui.theme.TextPrimary
import com.oms.spendwise.ui.theme.TextSecondary
import com.oms.spendwise.utils.formatDate
import com.oms.spendwise.utils.formatTime
import java.util.Currency

@Composable
fun TransactionHistoryScreen(
    modifier: Modifier = Modifier,
    transactionVM: TransactionViewModel,
    currency: Currency,
    context: Context
) {

    var transactionTypeFilter by remember { mutableStateOf<TransactionType?>(null) }
    var searchVisibility by remember {mutableStateOf(false)}
    var searchText by remember { mutableStateOf("") }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopBar(
                modifier = Modifier
                    .statusBarsPadding(),
                transactionTypeFilter = transactionTypeFilter,
                onTransactionTypeFilterChange = {transactionTypeFilter = it},
                searchBarVisibility = searchVisibility,
                toggleSearchBarVisibility = {searchVisibility = !searchVisibility},
                searchText = searchText,
                onSearchTextChange = {searchText = it}
            ) { }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
        ) {
            if(searchVisibility && searchText.isNotEmpty()){
                SearchedTransactions(
                    transactionVM = transactionVM,
                    currency = currency,
                    searchedText = searchText,
                    context = context
                )
            } else {
                FilteredTransactions(
                    transactionVM = transactionVM,
                    currency = currency,
                    transactionTypeFilter = transactionTypeFilter,
                    context = context
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
    context: Context
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
                        color = TextSecondary,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.labelLarge
                    )
                    Spacer(Modifier.height(8.dp))
                    Overview(
                        totalIncome = "+${currency.symbol}${transactionVM.getTotalIncome(date)}",
                        totalExpense = "-${currency.symbol}${transactionVM.getTotalExpense(date)}"
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
                            onClick = {}
                        )
                        Spacer(Modifier.height(14.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun FilteredTransactions(
    modifier: Modifier = Modifier,
    transactionVM: TransactionViewModel,
    transactionTypeFilter: TransactionType?,
    currency: Currency,
    context: Context
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
                        color = TextSecondary,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.labelLarge
                    )
                    Spacer(Modifier.height(8.dp))
                    Overview(
                        totalIncome = "+${currency.symbol}${transactionVM.getTotalIncome(date)}",
                        totalExpense = "-${currency.symbol}${transactionVM.getTotalExpense(date)}"
                    )
                    Spacer(Modifier.height(14.dp))
                } else {
                    val filteredItemCnt = transactions.count{it.type == transactionTypeFilter!!.value}
                    if(filteredItemCnt > 0){
                        Spacer(Modifier.height(2.dp))
                        Text(
                            text = formatDate(date),
                            color = TextSecondary,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.labelLarge
                        )
                        Spacer(Modifier.height(8.dp))
                        Overview(
                            totalIncome = "+${currency.symbol}${transactionVM.getTotalIncome(date)}",
                            totalExpense = "-${currency.symbol}${transactionVM.getTotalExpense(date)}"
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
                            onClick = {}
                        )
                        Spacer(Modifier.height(14.dp))
                    } else if(transactionTypeFilter!!.value == transactionCategory.type) {
                        TransactionItem(
                            transaction = transaction,
                            category = transactionCategory,
                            currency = currency,
                            context = context,
                            onClick = {}
                        )
                        Spacer(Modifier.height(14.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun Overview(
    modifier: Modifier = Modifier,
    totalIncome: String,
    totalExpense: String
) {
    Row(
        modifier = modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OverviewItemCard(
            modifier = Modifier.weight(1f),
            amount = totalIncome,
            type = TransactionType.INCOME
        )
        OverviewItemCard(
            modifier = Modifier.weight(1f),
            amount = totalExpense,
            type = TransactionType.EXPENSE
        )
    }
}

@Composable
private fun OverviewItemCard(
    modifier: Modifier = Modifier,
    amount: String,
    type: TransactionType
) {
    Card(
        modifier = modifier
            .padding(2.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
       Column(
           modifier = Modifier
               .fillMaxWidth()
               .padding(12.dp),
           verticalArrangement = Arrangement.spacedBy(4.dp)
       ) {
           Text(
               text = "TOTAL ${if(type == TransactionType.INCOME) "INCOME" else "EXPENSE"}",
               color = TextSecondary,
               fontWeight = FontWeight.Bold,
               style = MaterialTheme.typography.bodyMedium
           )

           Text(
               text = amount,
               color = if(type == TransactionType.INCOME) IncomeGreen else ExpenseRed,
               fontWeight = FontWeight.Bold,
               style = MaterialTheme.typography.bodyLarge,
           )
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
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(
                onClick = onClick
            ),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
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
                    .size(48.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(iconColor.copy(0.17f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(iconId),
                    contentDescription = "Confirm",
                    tint = iconColor,
                    modifier = Modifier.size(18.dp)
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
                color = TextPrimary
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
                        .size(16.dp)
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
                        color = TextHint
                    )
                },
                leadingIcon = {
                    Icon(
                        painter = painterResource(R.drawable.icon_search),
                        contentDescription = "note",
                        tint = TextHint,
                        modifier = Modifier.size(16.dp)
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = InputFieldBorder,
                    unfocusedBorderColor = InputFieldBorder,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                ),
                trailingIcon = {
                    IconButton(
                        onClick = {
                            onSearchTextChange("")
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
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
                Button(
                    onClick = {
                        onTransactionTypeFilterChange(null)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if(transactionTypeFilter == null) PrimaryBlue else Color.White,
                        contentColor = if(transactionTypeFilter == null) Color.White else TextPrimary
                    ),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(
                        width = if(transactionTypeFilter == null) 0.dp else 1.dp,
                        color = InputFieldBorder
                    ),
                    modifier = Modifier.height(36.dp)
                ) {
                    Text(
                        text = "All",
                        style = MaterialTheme.typography.labelMedium
                    )
                }

                Button(
                    onClick = {
                        onTransactionTypeFilterChange(TransactionType.INCOME)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if(transactionTypeFilter == TransactionType.INCOME) PrimaryBlue else Color.White,
                        contentColor = if(transactionTypeFilter == TransactionType.INCOME) Color.White else TextPrimary
                    ),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(
                        width = if(transactionTypeFilter == TransactionType.INCOME) 0.dp else 1.dp,
                        color = InputFieldBorder
                    ),
                    modifier = Modifier.height(36.dp)
                ) {
                    Text(
                        text = "Income",
                        style = MaterialTheme.typography.labelMedium
                    )
                }

                Button(
                    onClick = {
                        onTransactionTypeFilterChange(TransactionType.EXPENSE)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if(transactionTypeFilter == TransactionType.EXPENSE) PrimaryBlue else Color.White,
                        contentColor = if(transactionTypeFilter == TransactionType.EXPENSE) Color.White else TextPrimary
                    ),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(
                        width = if(transactionTypeFilter == TransactionType.EXPENSE) 0.dp else 1.dp,
                        color = InputFieldBorder
                    ),
                    modifier = Modifier.height(36.dp)
                ) {
                    Text(
                        text = "Expense",
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }
        }
        Spacer(Modifier.height(6.dp))
    }
}