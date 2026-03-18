package com.oms.spendwise.features.transaction.components

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.oms.spendwise.model.entity.Category
import com.oms.spendwise.model.entity.Transaction
import com.oms.spendwise.model.enum.TransactionType
import com.oms.spendwise.ui.theme.ExpenseRed
import com.oms.spendwise.ui.theme.IncomeGreen
import com.oms.spendwise.utils.AmountFormatter
import com.oms.spendwise.utils.formatTime
import java.util.Currency

@Composable
fun TransactionItem(
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
                    .background(iconColor.copy(0.2f)),
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
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = formatTime(transaction.transactionDateTime.toLocalTime()),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Text(
            text = "${if(transaction.type == TransactionType.INCOME.value) "+" else "-"}${currency.symbol}${AmountFormatter.formatAmount(transaction.amount)}",
            style = MaterialTheme.typography.bodyLarge,
            color = if(transaction.type == TransactionType.INCOME.value) IncomeGreen else ExpenseRed,
            fontWeight = FontWeight.Bold
        )
    }
}