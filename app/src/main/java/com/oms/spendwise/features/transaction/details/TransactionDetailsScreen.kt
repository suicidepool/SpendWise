package com.oms.spendwise.features.transaction.details

import android.annotation.SuppressLint
import android.content.Context
import androidx.annotation.DrawableRes
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.Group
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.size.Dimension
import com.oms.spendwise.R
import com.oms.spendwise.features.transaction.TransactionViewModel
import com.oms.spendwise.model.entity.Category
import com.oms.spendwise.model.entity.Transaction
import com.oms.spendwise.model.enum.TransactionType
import com.oms.spendwise.ui.theme.BackgroundElevated
import com.oms.spendwise.ui.theme.Dimens
import com.oms.spendwise.ui.theme.ExpenseRed
import com.oms.spendwise.ui.theme.IncomeGreen
import com.oms.spendwise.ui.theme.InputFieldBorder
import com.oms.spendwise.ui.theme.PrimaryBlue
import com.oms.spendwise.ui.theme.RedButton
import com.oms.spendwise.ui.theme.TextHint
import com.oms.spendwise.ui.theme.TextPrimary
import com.oms.spendwise.ui.theme.TextSecondary
import com.oms.spendwise.utils.formatDateTime
import kotlinx.coroutines.launch
import java.util.Currency

@SuppressLint("DiscouragedApi")
@Composable
fun TransactionDetailsScreen(
    modifier: Modifier = Modifier,
    transactionId: Long,
    transactionVM: TransactionViewModel,
    currency: Currency,
    context: Context,
    onEditClick: () -> Unit,
    onBack: () -> Unit
) {
    var transaction by remember { mutableStateOf<Transaction?>(null) }
    var category by remember { mutableStateOf<Category?>(null) }
    var showDialog by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    LaunchedEffect(Unit) {
        transaction = transactionVM.getTransaction(transactionId)
        transaction?.let {
            category = transactionVM.getCategory(it.categoryId)
        }
    }
    Scaffold(
        modifier = modifier,
        topBar = {
            TopBar(
                modifier = Modifier.statusBarsPadding(),
                onBack = onBack
            )
        },
        bottomBar = {
            BottomBar(
                modifier = Modifier
                    .navigationBarsPadding(),
                onEditClick = onEditClick,
                onDeleteClick = {
                    showDialog = true
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = Dimens.HorizontalScreenPadding)
        ) {
            transaction?.let{ transaction ->
                AmountSection(
                    transactionAmount = transaction.amount.toString(),
                    transactionType = transaction.type,
                    currency = currency
                )
                category?.let { category ->
                    val categoryIcon = context.resources.getIdentifier(
                        category.icon,
                        "drawable",
                        context.packageName
                    )
                    InformationSection(
                        transaction = transaction,
                        categoryIcon = categoryIcon,
                        categoryName = category.name
                    )
                }
                if(showDialog)
                    DeleteDialog(
                        onConfirm = {
                            scope.launch {
                                transaction.let {
                                    transactionVM.deleteTransaction(it)
                                }
                                showDialog = false
                                onBack()
                            }
                        },
                        onDismiss = {
                            showDialog = false
                        }
                    )
            }
        }
    }
}

@Composable
private fun DeleteDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = BackgroundElevated,
        shape = RoundedCornerShape(22.dp),
        title = {
            Text(
                text = "Delete Transaction?",
                color = TextPrimary
            )
        },
        text = {
            Text(
                text = "This action cannot be undone.",
                color = TextSecondary
            )
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = RedButton
                )
            ) {
                Text("Delete")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = Color.Gray
                )
            ) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun BottomBar(
    modifier: Modifier = Modifier,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            thickness = 0.7.dp,
            color = Color.LightGray.copy(alpha = 0.5f)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                modifier = Modifier
                    .weight(3f)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                onClick = onEditClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryBlue.copy(.1f),
                    contentColor = PrimaryBlue
                ),
                contentPadding = PaddingValues(vertical = 16.dp)
            ){
                Icon(
                    imageVector = Icons.Default.Create,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = PrimaryBlue
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text = "Edit Transaction",
                    style = MaterialTheme.typography.bodyLarge,
                    color = PrimaryBlue,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Button(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                onClick = onDeleteClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = RedButton.copy(.1f),
                    contentColor = RedButton
                ),
                contentPadding = PaddingValues(vertical = 16.dp)
            ){
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = null,
                    modifier = Modifier.size(22.dp)
                )
            }

        }
    }
}

@Composable
private fun InformationSection(
    modifier: Modifier = Modifier,
    transaction: Transaction,
    @DrawableRes categoryIcon: Int,
    categoryName: String,
) {


    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(
            text = "INFORMATION",
            color = TextSecondary,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 4.dp)
        )

        InformationItem(
            icon = categoryIcon,
            title = "CATEGORY",
            description = categoryName
        )

        InformationItem(
            icon = R.drawable.icon_clock,
            title = "DATE & TIME",
            description = formatDateTime(transaction.transactionDateTime)
        )

        InformationItem(
            icon = R.drawable.icon_note,
            title = "NOTE",
            description = transaction.note
        )
    }
}

@Composable
fun InformationItem(
    modifier: Modifier = Modifier,
    @DrawableRes icon: Int,
    title: String,
    description: String
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 2.dp),
        shape = RoundedCornerShape(Dimens.CardCornerRadius),
        elevation = CardDefaults.elevatedCardElevation(2.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .background(
                        color = PrimaryBlue.copy(.1f),
                        shape = RoundedCornerShape(Dimens.CardCornerRadius)
                    )
                    .size(48.dp),
                contentAlignment = Alignment.Center
            ){
                Icon(
                    painter = painterResource(icon),
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = PrimaryBlue
                )
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium,
                    color = TextSecondary
                )

                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextPrimary,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun AmountSection(
    modifier: Modifier = Modifier,
    transactionAmount: String,
    transactionType: String,
    currency: Currency
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(50.dp))
                .background(
                    color = if (transactionType == TransactionType.INCOME.value)
                        IncomeGreen.copy(alpha = 0.3f)
                    else
                        ExpenseRed.copy(alpha = 0.3f)
                )
                .padding(
                    horizontal = 16.dp,
                    vertical = 4.dp
                )
        ) {
            Text(
                text = transactionType,
                style = MaterialTheme.typography.labelLarge,
                color = if(transactionType == TransactionType.INCOME.value)
                    IncomeGreen
                else
                    ExpenseRed
            )
        }

        Spacer(Modifier.height(8.dp))

        Text(
            text = "${if(transactionType == TransactionType.INCOME.value) "+" else "-"}${currency.symbol}${transactionAmount}",
            style = MaterialTheme.typography.displayMedium,
            color = TextPrimary,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun TopBar(
    modifier: Modifier = Modifier,
    title: String = "Transaction Details",
    onBack: () -> Unit
) {
    Row(
        modifier = modifier
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


        Spacer(Modifier.size(40.dp))

    }
    Spacer(Modifier.height(6.dp))
}