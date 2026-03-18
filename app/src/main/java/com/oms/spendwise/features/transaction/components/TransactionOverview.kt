package com.oms.spendwise.features.transaction.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.oms.spendwise.model.enum.TransactionType
import com.oms.spendwise.ui.theme.ExpenseRed
import com.oms.spendwise.ui.theme.IncomeGreen

@Composable
fun Overview(
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
            containerColor = MaterialTheme.colorScheme.surface
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
                color = MaterialTheme.colorScheme.onSurfaceVariant,
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